---
layout:     post
title:      PostgreSQL no SQL Series Note 1
author:     Johnny Xavier
image:      Ubuntu-18-04-LTS-Bionic-Beaver.png
updated: 
categories: database
extract:    This note is the 1st on a series on playing around <strong>noSQL</strong> capabilities of the tried and true <strong>PostgreSQL</strong> db 
tags:       postgresql nosql jsonb spring-jdbc spring-data document-store 
---

# /Note_1_PostgreSQL_no_sql
somme time ago we started exploring the possibility of using `PostgreSQL` as a document store db while keeping the 
traditional sql approach. So... *sql and no sql*

We needed a doc approach for parts of the app and there were other parts that just made sense to keep under
 `sql`. Plus a lot of the data was already on `PostgreSQL`. Having 2 different databases to relate to the same data..
 . mmm smelled like a lot of problems

so the question goes down to...

can we mix and match docs and columns on the same DB on different or same tables and have decent performance while 
reading and saving?

to answer that I had to read a lot of PostgreSQL docs, stackO7s, and ultimately just try by myself as this was 
relatively unexploited yet.

it was hard to gather info about all topics I needed to create a working solution so I am putting what I found in a 
single place to be able to go back quickly to the very disperse documentation about this subject.

these series of notes will go from installing `PostgreSQL` on `Linux` (`Ubuntu 18.10 Cosmic Cuttlefish`) to creating 
doc columns on `PostgreSQL` to do some jolly operations over jsonb columns and indexes.

let's get this started!

## /Installing_PostgreSQL_on_Ubuntu
* Hardware: Dell XPS 15 9560
    * CPU:
    * RAM:
    * HD:
* Ubuntu version: 18.10 *Cosmic CuttleFish*
    * Linux Kernel: 4.18.0-11-generic
* PostgreSQL version: 11