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
as the topic here is `postgres`, `java` does not matters much and you can build the same as myself in the language of your own preference.<br>
having said that, if you are also a `jvm` developer you will benefit a lot as many things might solve problems you currently have.

I will use `jdbc` and `spring` and many of the ecosystem's usual suspects, so apart from the setup a `for` is a `for` in every language and translating the examples to `goLang` or `.NET` might be kind of simple if you know your lang (maybe?)

## /So_postgresql_can_do_json

---

this is all for now on the third note on postgres noSql.
