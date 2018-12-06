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

## /the_one_on_setting_up_a_supporting_java_project
on the previous notes I showed a few small examples, which I referred to, as toy examples

those toy examples are good to understand a concept without being overwhelmed by detail and not to loose focus on the subject being explained. But real world examples are nothing like that...

Reality is messy, drags many versions and many years and generations of developers and changing in management, and good ideas poorly implemented and also, sadly, bad ideas incredibly well implemented...<br>
the next examples will start with toy DBs that I might not publish per their simplicity, but we will play in our playground schemas like big fellas with DBs with millions of rows...

being a `jvm` dev mostly, I will build many things with `java` tools.<br>
as the topic here is `postgres` no Sql, `java` does not matter much and you can build the same as myself in the language of your own preference.<br>
having said that, if you are also a `jvm` developer you will benefit a lot as many things might solve problems you currently have.

I will use `jdbc`, `spring` and many of the ecosystem's usual suspects. Apart from the setup a `for` is a `for` in every language and translating the examples to `goLang` or `.NET` or any other language for which you have a postgresql driver might be kind of simple if you know your way around your lang (possibly?)

### /so_postgresql_can_do_json
alright, so postgres can do json, but what can we do with it?

from now on, each note will try to answer a problem or chat a bit about a topic related to no sql in json, and a few sporty functions that will handle or produce jsons.

the first thing we are going to do is to setup a project around the postgres to use as starting point or service for the operations we're going to perform.

### /setup
we'll setup a java project to handle some web containers, rest endpoints, jdbc, connection pools, etc

you can download all of it from github from the link below
[postgreSQL no sql repo]()

#### /the_suspects
* OS:
    * GNU / Linux -> Ubuntu 18.10 ***-Cosmic Cuttlefish-***
* Lang:
    * java 11 (>= J8 is OK)
* frameworks / libs / etc:
    * spring boot (2.1.0.RELEASE)
        * hikari
        * log4j2
        * wildfly
        * liquibase
        * jackson
* database:
    * PostgreSQL 11

the important versions to respect are spring boot one and postgresSQL, the rest you can have your own setup and everything will work just fine.
spring boot will handle the versions of the components I listed under it

##### /installing_the_os
if you need help installing the OS on a laptop, these guide below can point you into the right direction as linux and laptops with discrete cards are not the best friends by the moment
[Ubuntu 18.10 Cosmic Cuttlefish Setup - on Dell 9560]({{ site.baseurl }}{% post_url 2018-11-01-dell-9560-setup-ubuntu1810 %})


---

this is all for now on the third note on postgres noSql.
