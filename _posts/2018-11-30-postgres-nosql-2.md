---
layout:     post
title:      PostgreSQL no SQL Series Note 2
author:     Johnny Xavier
image:      Ubuntu-18-04-LTS-Bionic-Beaver.png
updated: 
categories: database
extract:    This note is the 2nd on a series on playing around <strong>noSQL</strong> capabilities of the tried and true <strong>PostgreSQL</strong> db 
tags:       postgresql postgres sql nosql jsonb spring-jdbc spring-data document-store basics
---

# /Note_2_PostgreSQL_no_sql
the first note on the postgresSQL noSql series was about installing and doing some basic setup for us to get going. In this note we're diving a little bit into postgres jsonB

## /to column or not to column
storing data in columns is actually very easy and convenient.

let's say you have a list of books at home and want to produce a catalog of them. you can define a book like this:
* `book: ( id, author, title)`

then someone comes with a really neat international system to catalog the books... ISBN. So it's time to add that to your book definition like this
* `book: ( id, author, title, ISBN)`
    
and then you lend some books to your friends. As you already have a db with all your books, you can easy track which ones are lent and to whom by adding a simple column...
* `book: ( id, author, title, ISBN, lent_to)`

this is how this will look like with data
###### books
```bash
id  | author    | title     | ISBN    | lent_to
---------------------------------------------
1   | tolkien   | the hobbit| someISBN| mike
```
the `lent_to` field will have the name of you friend
and then you realize you want to capture the name and last name of your friend...<br>
easy, let's make another definition... the people you lent those books too...
* `book: ( id, author, title, ISBN, lent_to)`
* `borrower: ( id, first_name, last_name)`

now things get a bit complicated, but not much really.<br>
the `lent_to` field on the books definition will now store your friend's `id` and all of your friend's data will be stored somewhere else<br>

this is how this will look like with data with 2 tables
###### books and borrowers
```bash
#book:
id  | author    | title     | ISBN    | lent_to
-----------------------------------------------
1   | tolkien   | the hobbit| someISBN| 2

#borrower:
id  | first_name| last_name 
----------------------------
1   | jake      | smith
----------------------------
2   | mike      | johnson
```

thi is far from the end right? Where is actually that book? Alright let's add your friend's address. As we did before it is handy to have the addresses on a different table and reference the address with an `id`.
we will have now 3 tables like this:
* `book: ( id, author, title, ISBN, lent_to)`
* `borrower: ( id, first_name, last_name, address)`
* `address: ( id, street_name, street_number, postal_code)`

and this is how it will look like with data on all 3 tables
###### books and borrowers and addresses
```bash
#book:
id  | author    | title     | ISBN    | lent_to
-----------------------------------------------
1   | tolkien   | the hobbit| someISBN| 2

#borrower:
id  | first_name| last_name | address
-------------------------------------
1   | jake      | smith     | 1
-------------------------------------
2   | mike      | johnson   | 1

#address:
id  | street_name| street_number| postal_code
---------------------------------------------
1   | rose road  | 123          | QA23RR
---------------------------------------------
2   | mistle lane| 98           | QA23RR
```

this is a very simple an classical approach for this simple example.
### **B U T**

to get all data about our book `id = 1` we need to check 3 tables and relate the data appropriately.<br>
why would they call this databases ***`RELATIONAL`*** databases right?

we will need to tell the db to `join` tables and to tell the db that what you actually want is not the borrower `id` (*`lent_to`*) but to go and fetch into the borrowers table your actual friend's data related to that `id` and again the same for your friend's address `id`.

let's see another way to approach this. How about storing everything as document on a single field on a single table?

defining the book, the borrower and the address will be exactly the same but will look very different loaded with data:
###### books
```json
{
    "id": 1,
    "author": "tolkien",
    "title": "the hobbit",
    "lent_to": {
        "id": 2,
        "first_name": "mike",
        "last_name": "johnson",
        "address": {
         "id": 1,
         "street_name": "rose road",
         "street_number": "123",
         "postal_code": "QA23RR"        
        }
    }
}
```

now that's something different!

when we get the book we want by asking for it's `id` or `title` we get the entire piece of data we wanted to fetch.<br>
dandy!

##### some incredibly short and abbreviated evolutionary pill
traditionally, databases will store data in columns, each column representing a field such as name, age, birthday, price, etc, and text such as articles, biographies, comments, etc were stored in "text fields", ranging from a few chars to big blobs.

that meant that as searching is performed on a column basis, querying the blobs of text was not as efficient as querying another field.<br>
the text will be just that, text.<br>
Meaning that the json above will have no meaning at all for a traditional database. You could not store the above json and tell the db something like, ***from the text blob column bring me the records for `author = 'tolkien'`***. It just would not make any sense. You will need to make a less efficient string search. For toy examples it would be ok, for big news articles it would be really not ok.

adding columns and normalizing a toy database as the one above when it grows and evolves is trivial, doing the same thing on a real working company live database with lots of hits is completely not trivial. Additions and changes can be difficult or painful to do down the road.

enters [MongoDB](https://www.mongodb.com/)... this db, a document store, could handle the above json in a way that each field has a meaning and could be queried as if it was a column from the traditional approach. Others, as [Amazon's DynamoDB](https://aws.amazon.com/dynamodb/) work similarly, but for now the one to beat is still mongo.

the above json is a single field with meaning and queryable in mongo. Mongo offers a flexibility that the traditional schema could not. If you do additions or modifications to the json you store, mongo cannot care less. It will keep working fine, and now maintenance and breaking changes are reduced considerably.

the key word here is -***reduced***- as maintenance effort will not disappear. If you drive any approach to the limit or you choose the wrong tool for the given job, they will all offer the poorest performance possible.<br>

again... the key word here is ***`reduced`***.
So this means that neither approach will be a silver bullet for every case and every need.

to the question of which of these 2 approaches to choose the answer is of course...<br>
***it depends...***

## /Relational meet Document
you will read a lot of praise to mongodb as well as a lot of criticism, and the same will go to postgresql or relational model in general.

among the data types supported by postgres you will find the usual ones that support text. The most conspicuous being `character varying aka varchar(n)`. You also have the bigger `text` one and you can store a json there but again it will mean only text for the db.

enters PostgreSQL `JSON` and `JSONB` data types...

those two types will store json strings, but now, those text strings will have meaning for postgres and you will get a few things out of the box. One of them is grammar checking. If you try to store an incorrectly constructed json, postgres will let you know.

check this:
```sql
-- creating a temporary db with json data type
create table postgres_no_sql.temp
(
  id            serial primary key,
  json_document json
);

--insert a valid json {"name": "john", "last_name": "smith"}
sql> insert into postgres_no_sql.temp(json_document)
     values ('{"name": "john", "last_name": "smith"}')
[2018-12-01 21:29:04] 1 row affected in 10 ms --SUCCESS!

--insert an invalid json {"name": "john", "last_name": "smith} -> forgot to close the double quote after smith
sql> insert into postgres_no_sql.temp(json_document)
     values ('{"name": "john", "last_name": "smith}')
[2018-12-01 21:29:24] [22P02] ERROR: invalid input syntax for type json
[2018-12-01 21:29:24] Detail: Token ""smith}" is invalid.
[2018-12-01 21:29:24] Position: 52
[2018-12-01 21:29:24] Where: JSON data, line 1: {"name": "john", "last_name": "smith}
```

so, this is not just characters for the database now, it is a string with meaning that needs to make sense. Let's insert a few more records in our temporary table and try to query something inside that json field.

```sql
insert into postgres_no_sql.temp(json_document) values ('{"name": "john", "last_name": "smith"}');
insert into postgres_no_sql.temp(json_document) values ('{"name": "john", "last_name": "doe"}');
insert into postgres_no_sql.temp(json_document) values ('{"name": "mike", "last_name": "johnson"}');
insert into postgres_no_sql.temp(json_document) values ('{"name": "arnie", "last_name": "mc manus"}');
insert into postgres_no_sql.temp(json_document) values ('{"name": "tommy", "last_name": "riordan"}');

select * from postgres_no_sql.temp where document ->> name = 'john';

--this is what we get!
 id |             json_document              
----+----------------------------------------
  1 | {"name": "john", "last_name": "smith"}
  2 | {"name": "john", "last_name": "doe"}
```
and now to add complexity, let's expand our document to add `middle name` and `nationality` without modifying our previous records and let's repeat the same query
```sql
-- our new inserts
insert into postgres_no_sql.temp(json_document) values ('{"name": "john", "last_name": "trent", "country": "france", "nationality": "french"}');
insert into postgres_no_sql.temp(json_document) values ('{"name": "laura", "last_name": "tedesco", "country": "france", "nationality": "italian"}');
insert into postgres_no_sql.temp(json_document) values ('{"name": "mike", "last_name": "clark", "country": "england"}');
insert into postgres_no_sql.temp(json_document) values ('{"name": "hamish", "last_name": "daly", "nationality": "scottish"}');

select * from postgres_no_sql.temp where document ->> name = 'john';

--this is what we get now
 id |                                    json_document                                     
----+--------------------------------------------------------------------------------------
  1 | {"name": "john", "last_name": "smith"}
  2 | {"name": "john", "last_name": "doe"}
  6 | {"name": "john", "last_name": "trent", "country": "france", "nationality": "french"}
(3 rows)
```

we changed our document, some times coherently, some other records with missing json keys and the query run just fine regardless...

you might be wondering.. AHA! but the query was on a field all the documents have!<br>
let's query a field just a few documents have, `country` for example...
```sql
--just a few records have country in their json document
select * from postgres_no_sql.temp where json_document ->> 'country' = 'france';

--and we get...
 id |                                      json_document                                       
----+------------------------------------------------------------------------------------------
  6 | {"name": "john", "last_name": "trent", "country": "france", "nationality": "french"}
  7 | {"name": "laura", "last_name": "tedesco", "country": "france", "nationality": "italian"}

```
exactly what you would expected!

so... this is postgresql right? **right!**
and this is sql right? *mmm kinda...*

I have not explained much but you can see a new player here... the **`->>`** operator after the `where` clause.

well... this was just getting our feet wet on a tiny toy example for a toy query about the features of `PostgreSQL` as a document store database...

on the next notes of the series we will start answering the many question on how to use this new `json` and `jsonb` fields. How to merge add delete combine data to and from these documents, how to optimize searches mixing SQL and noSQL, what to index and how, and what to avoid...

---

this is all for now on the second note on postgres noSql.
