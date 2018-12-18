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
`json` stands for javascript object notation, and `jsonb` is a binary representation of a `json`.

the main difference between both other than the way it is stored internally, are the operations we can do on each and the performance you can get out of them.

both data types support:
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
    
these are `**C-R-U-C-I-A-L**` differences to take into consideration when choosing a data type over the other, and they can have some saying in the way we decide our data structures.

## /from_columns_to_json
we're gonna play in this note with the operations in postgres that allow to create jsons, and json arrays from column data.

### /setting_up_a_supporting_project
as advertised, we're using `java's` ecosystem to support an imaginary project to store, query, and present data.<br>

I find more engaging to use lifelike demos than toy examples, so we're gonna try and build a tiny piece of a real project, with millions of rows and hitting the db as hard as we want to reach to push it to it's limits.

we're seeing lately the rise of phone controlled top up debit cards, that show a lot more info on screen than our own banks do. We can get info broken by purchase type, seller, dates, country, etc. <br>
we are going to build a web based super charged version of one of those apps that would show the info you can get in vendors like `Revolut` or `Monzo` or a service of the like, using the versatility of postgres to act as pure sql, nosql and hybrid database.

#### /the_architecture_we_will_be_exploring
we're going to explore a small part of the whole of one of those services. Mainly the part storing and querying the users and transactions data.

here we go!




---

this is all for now on the fourth note on postgres noSql.