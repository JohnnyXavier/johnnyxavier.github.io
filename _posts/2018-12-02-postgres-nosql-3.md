---
layout:     post
title:      PostgreSQL no SQL Series Note 3
author:     Johnny Xavier
image:      PostgreSQL_logo.png
updated: 
categories: database
extract:    This note is the 3rd on a series on playing around <strong>noSQL</strong> capabilities of the tried and true <strong>PostgreSQL</strong> db 
tags:       postgresql postgres sql nosql jsonb spring-jdbc spring-data document-store basics
---

# /Note_3_PostgreSQL_no_sql
on the previous notes I showed a few small examples, which I referred to, as toy examples

those toy examples are good to understand a concept without being overwhelmed by detail and not to loose focus on the subject being explained. But real world examples are nothing like that...

Reality is messy, drags many versions and many years and generations of developers and changing in management, and good ideas poorly implemented and also, sadly, bad ideas incredibly well implemented...<br>
the next examples will start with toy DBs that I might not publish per their simplicity, but we will play in our playground schemas like big fellas with DBs with millions of rows...

being a `jvm` dev mostly, I will build many things with `java` tools.<br>
as the topic here is `postgres` no Sql, `java` does not matter much and you can build the same as myself in the language of your own preference.<br>
having said that, if you are also a `jvm` developer you will benefit a lot as many things might solve problems you currently have.

I will use `jdbc`, `spring` and many of the ecosystem's usual suspects. Apart from the setup a `for` is a `for` in every language and translating the examples to `goLang` or `.NET` or any other language for which you have a postgresql driver might be kind of simple if you know your way around your lang (possibly?)

## /So_postgresql_can_do_json
alright, so postgres can do json, but what can we do with it?

from now on, each note will try to answer a problem or chat a bit about a topic related to no sql in json, and a few sporty functions that will handle or produce jsons.

we will see in this one how to produce json strings from traditional columns and where can we use them for.

## /postgresql_going_to_json
besides having these `json` and `jsonb` data types we also have a few functions to produce `json`. This is very handy when you already have a working db in a `relational` model and want to produce jsons to insert into a `jsonb` `document`.

###setup
we will use a toy db as we just want to understand the functions that can produce and aggregate `json` 

let create some tables.


---

this is all for now on the third note on postgres noSql.
