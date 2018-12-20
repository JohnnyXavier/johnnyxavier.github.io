---
layout:     post
title:      PostgreSQL no SQL Series Note 4
author:     Johnny Xavier
image:      PostgreSQL_logo.png
updated: 
categories: database
extract:    This note is the 4th on a series on playing around <strong>noSQL</strong> capabilities of the tried and true <strong>PostgreSQL</strong> db 
tags:       postgresql postgres sql nosql jsonb spring-jdbc spring-data document-store basics
---

# /the_one_on_generating_jsons_from_columns
the first 3 notes helped introduce our subject and setup the db and the pieces to support a working project.<br>
this note will dive into the json functionality we can get with postgres to help build json data. This will be really useful later on when we switch over to full nosql with `json` and `jsonb` data types

### /differences_between_`json`_and_`jsonb`_data_types
[postgresql.org -> datatype-json](https://www.postgresql.org/docs/current/datatype-json.html)

postgres supports `json` and `jsonb` data types to store and query `json` data.<br>
`json` stands for `j`ava`s`cript `o`bject `n`otation, and `jsonb` is a `b`inary representation of a `json`.

the main difference between both other than the way it is stored internally, are the operations we can do on each and the performance you can get out of them.

`both` data types support:
* json grammar check
    * it will prevent to store an incorrectly constructed json
* querying fields whether top level, nested, array etc

`json` characteristics:
    * stores an exact copy of the input
        * makes it faster to store
        * ordering and even white spaces are respected
        * repeated keys are respected (but only last one will be the *"working one"*)
    * needs reparsing for functions to process the input
        * makes it a little slower to work with 
    * no indexing support 

`jsonb` characteristics:
    * stores a decomposed binary format of the original input
        * makes it a bit slower to store due to the preprocessing before insert
        * makes it significantly faster to work with as it does not need to be reparsed
    * can be indexed
    * ordering of fields is not respected, white space is trimmed
    * repeated keys are not kept, just the last one is kept
    
these are **`C-R-U-C-I-A-L`** differences to take into consideration when choosing a data type over the other, and they can have some saying in the way we decide our data structures.

check this out
###### selecting same data as `json` vs `jsonb`
```bash
postgres=# SELECT '{"name":"john",   "last_name":"doe","last_name":"pierce"}'::json, '{"name":"john",   "last_name":"doe","last_name":"pierce"}'::jsonb;

                           json                            |                  jsonb                  
-----------------------------------------------------------+-----------------------------------------
 {"name":"john",   "last_name":"doe","last_name":"pierce"} | {"name": "john", "last_name": "pierce"}
(1 row)
```

`json` will select back exactly what we inserted, spaces and all, `jsonb` will change things and eliminate repeated keys keeping the last one only

I am not mentioning `UTF-8` encoding of the `json` format and how that relates to the DB as a whole but it is also something to be aware of.

## /from_columns_to_json
we're gonna play in this note with the operations in postgres that allow to create jsons, and json arrays from column data.

### /setting_up_a_supporting_project
as advertised, we're using `java's` ecosystem to support an imaginary project to store, query, and present data.<br>

I find more engaging to use lifelike demos than toy examples, so we're gonna try and build a tiny piece of a real project, with millions of rows and hitting the db as hard as we want to reach to push it to it's limits.

we're seeing lately the rise of phone controlled top up debit cards, that show a lot more info on screen than our own banks do. We can get info broken by purchase type, seller, dates, country, etc. <br>
we are going to build a web based super charged version of one of those apps that would show the info you can get in vendors like `Revolut` or `Monzo` or a service of the like, using the versatility of postgres to act as pure sql, nosql and hybrid database.

#### /the_architecture_we_will_be_exploring
we're going to explore a small part of one of those top up card services. Mainly the part storing and querying the users and transactions data.

here we go!

#### /starting_relational
this type of apps provide a service to a `user`.<br>
a `user` then, is an important entity for these apps, thou the `user`'s data itself is not the core of the service... (that we know of...)

let's try a first attempt of modeling our user, with the basic data we need to capture

* USER
    * user_uuid
    * user_id_type (passport, national id, etc)
    * user_id
    * first_name
    * last_name
    * age
    * credit_card 
    * debit_card 
    * status
    * address
    * joining_date
    * last_login
    
now... this is pseudocode for a DB table
a few things to notice, this user model will sink very quickly. The main data we need is there, but it's difficult to access. Storing all the data from a credit card into a single field makes no sense... limiting a user to just have 2 cards makes no sense either.

1st refinement
* USER
    * user_uuid
    * user_id_type (passport, national id, etc)
    * user_id
    * first_name
    * last_name
    * age 
    * status
    * address_personal_id
    * address_work_id
    * last_login
    * created_at
    * modified_at

* CREDIT / DEBIT CARD
    * card_uuid
    * type      (credit, debit)
    * internal  (is this one of our cards?)
    * issuer    (a bank? a card itself)
    * processor (visa, amex, master)
    * number
    * country
    * holder_name
    * valid_from_date
    * expiration_date
    * security_code
    * created_at
    * modified_at
    
* Address
    * address_uuid
    * country
    * city
    * street_name
    * street_number
    * building_type (house, apartment)
    * floor
    * apartment
    * created_at
    * modified_at
    
now, the model for our user, is far better than the original but still needs some massaging.<br>
the things to notice here is that we are limiting the amount of addresses a user can have. Well, addresses are not likely to change very often and we won't keep historics on the users addresses table itself, so the limit is not terrible. Regarding the cards user can have, that's another story as our users can choose to add many cards, and at some point cards will expire so limiting the amount of cards won't be the best approach.<br>
enters an intermediate table that will relate a user to his cards... a single user can have many cards, and maybe a family will share a single card among it's members so we need to add a table that can allow more than one user to relate to more than one card, a many to many table.

* USERS AND CARDS
    * user_uuid
    * card_uuid
    * status (in_use, discarded)
    * created_at
    * modified_at

can this be refined further?<br>
**YES!** extensively...<br>
users can use bank accounts to top up their account for example, but the core of this example is to show postgres no sql more that achieving a perfect user model and we have a fine set of table to start playing with.<br>
So we will leave our user, and it's immediate satellite data like this for the time being.

let's give this a SQL spin
###### user.sql
```sql
--create the DB
--creating user and satellite data

set search_path to postgres_no_sql;

create table if not exists users
(
  id                  serial8 primary key,
  user_id_type        varchar(25) check ( user_id_type in ('driver_licence', 'passport', 'national id')),
  user_id             varchar(25),
  first_name          varchar(25),
  last_name           varchar(25),
  age                 smallint,
  status              varchar(25) check ( status in ('active', 'inactive', 'suspended') ),
  address_personal_id integer,
  address_work_id     integer,
  last_login          time with time zone,
  created_at          time with time zone,
  modified_at         time with time zone
);
```

###### banking_card.sql
```sql
create table if not exists banking_card
(
  id              serial8 primary key,
  type            varchar(20) check (type in ('credit', 'debit')),
  internal        boolean,
  issuer          varchar(20) check (internal in ('bank', 'another_card')),
  processor       varchar(20) check (processor in ('visa', 'amex', 'master')),
  number          integer,
  country         varchar(20),
  holder_name     varchar(40),
  valid_from_date timestamp,
  expiration_date timestamp,
  security_code   smallint,
  created_at      time with time zone,
  modified_at     time with time zone
);
```
###### address.sql
```sql
create table if not exists address
(
  id            serial primary key,
  country       varchar(20),
  city          varchar(20),
  street_name   varchar(50),
  street_number varchar(7),
  post_code     varchar(10),
  building_type varchar(20) check ( building_type in ((house, apartment)) ),
  floor         smallint,
  apartment     varchar(10),
  created_at    time with time zone,
  modified_at   time with time zone
);
```
###### users_cards.sql
```sql
create table if not exists users_and_cards
(
  user_id         integer,
  banking_card_id integer,
  status          varchar(20) check (status in ('in_use', 'discarded')),
  created_at      time with time zone,
  modified_at     time with time zone
);
```
---

this is all for now on the fourth note on postgres noSql.