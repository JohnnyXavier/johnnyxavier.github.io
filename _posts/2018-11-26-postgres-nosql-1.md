---
layout:     post
title:      PostgreSQL no SQL Series Note 1
author:     Johnny Xavier
image:      Ubuntu-18-04-LTS-Bionic-Beaver.png
updated: 
categories: database
extract:    This note is the 1st on a series on playing around <strong>noSQL</strong> capabilities of the tried and true <strong>PostgreSQL</strong> db 
tags:       postgresql postgres nosql jsonb spring-jdbc spring-data document-store 
---

# /Note_1_PostgreSQL_no_sql
somme time ago we started exploring the possibility of using `PostgreSQL` as a document store db while keeping the 
traditional sql approach. So... *sql and no sql*

We needed a doc approach for parts of the app and there were other parts that just made sense to keep under
 `sql`. Plus a lot of the data was already on `postgres`. Having 2 different databases to relate to the same data... mmm smelled like a lot of problems

so the question goes down to...

can we mix and match docs and columns on the same DB on different or same tables and have decent performance while 
reading and saving?

to answer that I had to read a lot of `postgres` docs, stackO7s, and ultimately just try by myself as this was 
relatively unexploited yet.

it was hard to gather info about all the topics I needed to create a working solution, so I am putting what I found 
in a single place to be able to go back quickly to the very disperse documentation about this subject.

these series of notes will go from installing `postgres` on `Linux` (`Ubuntu 18.10 Cosmic Cuttlefish`) to creating 
doc columns on `postgres` to do some jolly operations over `jsonb` columns and testing some indexes.

let's get this started!

## /Installing_postgres_on_Ubuntu
* Ubuntu version: 18.10 *Cosmic CuttleFish*
    * Linux Kernel: 4.18.0-11-generic
* postgres version: 11

This is my current setup and yours may differ, the minimum requirement thou, is `postgres` 10. You can install 
`postgres` over many OSs but the instructions here apply for a `Debian` based OSs although installing over 
different systems are almost identical.

### /Getting_and_installing_postgres
head to `postgres` website download page.<br>
[https://www.postgres.org/download/linux/ubuntu/](https://www.postgres.org/download/linux/ubuntu/)

there you will see the  different `Ubuntu` versions the Database is available for. You can follow instructions 
there or keep reading here.

###### Bash
```bash
# add the postgres repo to you repo list
nano /etc/apt/sources.list.d/pgdg.list # use vi vim emacs etc as you fancy

# once inside your editor, add this line.
# this works just fine on ubuntu cosmic
deb http://apt.postgres.org/pub/repos/apt/ bionic-pgdg main

# save and close the text editor

# add the repo key
wget --quiet -O - https://www.postgres.org/media/keys/ACCC4CF8.asc | sudo apt-key add -

# update you packages list
sudo apt update

# install the meta package and a few db extensions
sudo apt install postgres postgres-contrib
```

### /configuring_postgres_the_basics
we will cover here how to access `postgres` from the console and from an IDE.
I personally use `IntelliJ IDEA` and sometimes `DataGrip` both from `jetbrains`, but I will cover an `Eclipse` based 
option called `dbBeaver` as it is free and runs everywhere too. I will just cover basic setup as SQL commands are the 
same regardless which IDE you use.

##### on how to access postgres from the terminal
there are other ways that this one but I find this one very convenient.<br>
during installation the db creates a user, the **postgres** user, and we will use it to get into the db CLI
###### Bash
```bash
sudo -iu postgres

#you should land into postgres users's shell
#let's call the cli command
psql

# you should get this prompt from psql with a similar version
$ psql (11.1 (Ubuntu 11.1-1.pgdg18.04+1))
$ Type "help" for help.

$ postgres=#

# "help" should be your best friend here as postgres is loaded with features and it is easy to get lost
# similar to "help" is to type "\?", so lets see what they show
# I will omit the "$ postgres=#" prompt from now

\h
# \h will show help for different SQL commands and output something like this
Available help:
  ABORT                            CHECKPOINT                       CREATE USER                      DROP TRIGGER
  ALTER AGGREGATE                  CLOSE                            CREATE USER MAPPING              DROP TYPE
  ALTER COLLATION                  CLUSTER                          CREATE VIEW                      DROP USER
  ALTER CONVERSION                 COMMENT                          DEALLOCATE                       DROP USER MAPPING
  ALTER DATABASE                   COMMIT                           DECLARE                          DROP VIEW
  ALTER DEFAULT PRIVILEGES         COMMIT PREPARED                  DELETE                           END
[...]

\?
# \? will show help for different psql cli commands and output something like this
General
  \copyright             show PostgreSQL usage and distribution terms
  \crosstabview [COLUMNS] execute query and display results in crosstab
  \errverbose            show most recent error message at maximum verbosity
  \g [FILE] or ;         execute query (and send results to file or |pipe)
  \gdesc                 describe result of query, without executing it
  \gexec                 execute query, then execute each value in its result
  \gset [PREFIX]         execute query and store results in psql variables
  \gx [FILE]             as \g, but forces expanded output mode
[...]
Informational
  (options: S = show system objects, + = additional detail)
  \d[S+]                 list tables, views, and sequences
  \d[S+]  NAME           describe table, view, sequence, or index
  \da[S]  [PATTERN]      list aggregates
  \dA[+]  [PATTERN]      list access methods
  \db[+]  [PATTERN]      list tablespaces
  \dc[S+] [PATTERN]      list conversions
  \dC[+]  [PATTERN]      list casts
  \dd[S]  [PATTERN]      show object descriptions not displayed elsewhere
  \dD[S+] [PATTERN]      list domains
  \ddp    [PATTERN]      list default privileges
  \dE[S+] [PATTERN]      list foreign tables
  \det[+] [PATTERN]      list foreign tables
  \des[+] [PATTERN]      list foreign servers
  \deu[+] [PATTERN]      list user mappings
[...]

# those 2 commands should be good place to go when you need info


```
