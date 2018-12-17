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
postgres supports `json` and `jsonb` data types to store and query `json` data.<br>
`json` stands for javascript object notation, and `jsonb` is a binary representation of a `json`.

the main difference between both other than the way it is stored internally, are the operations we can do on each and the performance you can get out of them.

both support:
* json grammar check
    * it will prevent to store an incorrectly constructed json
* querying fields whether top level, nested, array etc
* indexing

jsonb additionally support:




---

this is all for now on the fourth note on postgres noSql.