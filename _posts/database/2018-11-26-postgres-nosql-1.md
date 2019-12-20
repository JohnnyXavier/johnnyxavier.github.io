---
layout:     post
title:      PostgreSQL no SQL Series Note 1
author:     Johnny Xavier
image:      PostgreSQL_logo.png
updated: 
categories: database
extract:    This note is the 1st on a series on playing around <strong>noSQL</strong> capabilities of the tried and true <strong>PostgreSQL</strong> db 
tags:       postgresql postgres sql nosql jsonb spring-jdbc spring-data document-store configuration
---

# /the_one_with_the_database_setup
some time ago we started exploring the possibility of using `PostgreSQL` as a document store while keeping the traditional sql approach. So... *sql and no sql* at the same time.

We needed a doc approach for parts of the app and there were other parts that just made sense to keep under  `sql`. Plus, a lot of the data was already on `postgres`. Having 2 different databases to relate to the same data... mmm smelled like a lot of sync problems, so that was not an option at all...

so the question went down to...

can we mix and match docs and columns on the same DB on different or same tables and have decent performance on reads and writes?

to answer that I had to read a lot of `postgres` docs, stackO7s, and ultimately just try by myself as this was 
relatively unexploited yet.

it was hard to gather info about all the topics I needed to create a working solution, so I am putting what I found 
in a single place to be able to go back quickly to the very disperse documentation about this subject and expand on it if possible.

these series of notes will go from installing `postgres` on `Linux` (`Ubuntu 19.10 Eoan Ermine`) to creating 
doc columns on `postgres` to doing some jolly operations over `jsonb` columns and testing some indexes.

I will use `linux` as OS, `java` ecosystem as supporting framework, and of course, `PostgreSQL` as database.

let's get this started!

---

## /Installing_postgres_on_Ubuntu
let's install the DB to get our feet wet.

* Ubuntu version: 19.10 *Eoan Ermine*
    * Linux Kernel: 5.3.0-23-generic
* postgres version: 12.1 (Ubuntu 12.1-1.pgdg19.10+1)

This is my current setup and yours may differ, the minimum requirement thou, is `postgres` <strong>10</strong>.<br>
You can install `postgres` over many OSs but the instructions here apply for `Debian` based OSs.

### /Getting_and_installing_postgres
head to `postgres` website download page.<br>
[https://www.postgresql.org/download/linux/ubuntu/](https://www.postgresql.org/download/linux/ubuntu/)

there you will see the  different `Ubuntu` versions the Database is available for. You can follow instructions 
there for the ready to go OSs versions or keep reading here for latest Ubuntu 19.10 instructions taken from [https://wiki.postgresql.org/wiki/Apt](https://wiki.postgresql.org/wiki/Apt)

###### Bash
```bash
# be sure you have all required packages on you distro for postgresql to install correctly
sudo apt install curl ca-certificates gnupg

# add the repo key
# be sure to check the postgresql wiki linked above for any change on keys
wget --quiet -O - https://www.postgres.org/media/keys/ACCC4CF8.asc | sudo apt-key add -

# add the postgres repo to you repo list
sudo vim /etc/apt/sources.list.d/pgdg.list # use vi vim emacs etc as you fancy

# once inside your editor, add this line.
# for the time being postgres offers only 64 bit repo for Ubuntu 19.10 eoan
deb [arch=amd64] http://apt.postgresql.org/pub/repos/apt/ eoan-pgdg main

# save and close the text editor
:wq

# update you packages list
sudo apt update

# install the meta package with the latest version and a few db extensions
sudo apt install postgres postgres-contrib
```

### /configuring_postgres_the_basics
we will cover here how to access `postgres` from the console and from an IDE.

I personally use [IntelliJ IDEA](https://www.jetbrains.com/idea/) / [DataGrip](https://www.jetbrains.com/datagrip/) both from **[jetbrains](https://www.jetbrains.com/)**, but I will also cover an **[Eclipse](https://www.eclipse.org/eclipseide/)** based option called [dBeaver](https://dbeaver.io/) as it is free and runs everywhere too.<br>
I will just cover basic setup as SQL commands are the same regardless which IDE you use.

##### on how to access postgres from the terminal
there are other ways than this one but I find this one very convenient.<br>
during installation the db creates a user, the **postgres** user, and we will use it to get into the db CLI
###### Bash
```bash
# switch to the postgres user (-u) using its shell and settings (-i)
sudo -iu postgres

# you should land into postgres users's shell
# let's call postgresql cli command
psql

# you should get this prompt from psql with a similar version
$ psql (12.1 (Ubuntu 12.1-1.pgdg19.10+1))
$ Type "help" for help.

$ postgres=#

# "help" should be your best friend here as postgres is loaded with features and it is easy to get lost
# I will omit the "$ postgres=#" prompt from now
help

# help just display this small help menu,
You are using psql, the command-line interface to PostgreSQL.
Type:  \copyright for distribution terms
       \h for help with SQL commands
       \? for help with psql commands
       \g or terminate with semicolon to execute query

# let's check \h
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

# let's check \?
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

so this is what you get after installing the database. It will also setup postgres as a service.<br>

---
`this note focuses more on the database as a developer tool than from a dba devops perspective, so I will avoid getting into detailed settings to go to the core of our topic.`

---
##### on how to configure postgres with a new user and creating our playground database and schemas
having said that, we will need to go through a few setup steps to access postgres from outside the CLI. Back to bash
###### bash
```bash
# let's create a database and a user other than the main db and the root user so we can break a few things, but not 
# everything.
# back to psql
$ postgres=#

# let's create a user.
CREATE USER john PASSWORD `john`;

# let's create a new database.
CREATE DATABASE playground;

# let's give our user john control of that database.
GRANT ALL ON DATABASE playground TO john;

# let's connect to database playground
\c playground;

# let' s create a schema in which our no_sql tables will live
# and give access to our user
# we need to give access to the schema itself and all its tables and functions
CREATE SCHEMA postgres_no_sql;
GRANT ALL ON SCHEMA postgres_no_sql TO john;
GRANT ALL ON ALL TABLES IN SCHEMA postgres_no_sql TO john;
GRANT ALL ON ALL FUNCTIONS IN SCHEMA postgres_no_sql TO john;

# let' s create a schema in which our sql tables will live
# and give access to our user
# we need to give access to the schema itself and all its tables and functions
CREATE SCHEMA postgres_sql;
GRANT ALL ON SCHEMA postgres_sql TO john;
GRANT ALL ON ALL TABLES IN SCHEMA postgres_sql TO john;
GRANT ALL ON ALL FUNCTIONS IN SCHEMA postgres_sql TO john;
```
summing up:<br>
we created a set of 2 schemas inside a new database called playground. This should make sure anything we break is 
self contained. The grants give our user the permissions to create tables and use functions. After all this if you 
list your databases and schemas you should see this

###### bash
```bash
# list all databases with "\l"
\l

# you should see
                                  List of databases
    Name    |  Owner   | Encoding |   Collate   |    Ctype    |   Access privileges   
------------+----------+----------+-------------+-------------+-----------------------
 playground | postgres | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =Tc/postgres         +
            |          |          |             |             | postgres=CTc/postgres+
            |          |          |             |             | john=CTc/postgres
 postgres   | postgres | UTF8     | en_US.UTF-8 | en_US.UTF-8 | 
 template0  | postgres | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =c/postgres          +
            |          |          |             |             | postgres=CTc/postgres
 template1  | postgres | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =c/postgres          +
            |          |          |             |             | postgres=CTc/postgres
(4 rows)

# it may be the case that the collate and ctype of your database are different, maybe en_GB or es_ES or another language
# it is completely fine
# if you are curious how many and which collations your postgresql installation supports you can execute the following query:
# select * from pg_collation; 

# list all schemas in playground with "\dn"
\dn

# you should see
      List of schemas
      Name       |  Owner   
-----------------+----------
 postgres_no_sql | postgres
 postgres_sql    | postgres
 public          | postgres
(3 rows)

# we are done here so let's exit the postgres cli
# exit or quit or \q will get us out
\q

# we may want to exit the postgres user's shell too
postgres@your_computer:~$ exit
```
this setup will allow the created user `john` to connect to `playground` database with the chosen password and to operate on those schemas.

### /configuring_postgres_the_IDEs
with the above we installed the database and configured it to our needs. Now let's leave the CLI and configure an IDE to access the dbs and schemas and start getting into the dandy stuff.
 
***note that this is completely optional and you can keep using the CLI, or jump into `emacs` or `vi`or any tool you love***

#### configuring Jetbrains family of apps
although having a cost, `intelliJ Ultimate` is widely used and chances are it's your IDE too.<br>
the version I use for this tutorial is the `2019.3` one

what we need to do is to add a `DataSource`
* go to `view -> tool windows -> database` or you can find the tab usually to the right edge of the app
* click the `+` sign
* from the drop down select `datasource` -> `postgreSQL`

<img style="width: 35%" src="{{ site.baseurl }}/public/images/Menu_003.png">

* you will see the `datasource` popup where you have to input your DB's settings
    * it is hosted on our own computer, so `localhost` for the host
    * `playground` is our db name
    * user/password are the ones you created before
    * driver is downloaded by the IDE for you

you should get a screen similar to this one
<img style="width: 70%" src="{{ site.baseurl }}/public/images/DataSourcesAndDrivers_004.png">

if you check the schemas tabs you should see something similar to this showing the schemas we just created
<img style="width: 70%" src="{{ site.baseurl }}/public/images/DataSourcesAndDrivers_011.png">

finally on your main IDE layout your new datasource should show like this
<img style="width: 35%" src="{{ site.baseurl }}/public/images/Selection_006.png">

#### configuring dBeaver
this tool was among my favourites. It's community edition is loaded with features and it's eclipse based, which makes it 
very familiar for many devs. Eclipse can be configured in almost the same way

* from the menus choose `Database` -> `new connection`
* you should see a window to choose which db to setup. choose `PostgreSQL`
<img style="width: 45%" src="{{ site.baseurl }}/public/images/CreateNewConnection_008.png">

* you will see the `connection settings` popup where you have to input your DB's settings
    * it is hosted on out own computer so `localhost` for the host
    * `playground` is our db name
    * user/password are the ones you created before
    * driver is downloaded by the IDE

you should get a screen similar to this one
<img style="width: 45%" src="{{ site.baseurl }}/public/images/CreateNewConnection_009.png">

finally on your main IDE layout your new database connection should show like this
<img style="width: 30%" src="{{ site.baseurl }}/public/images/Selection_010.png">

---

that's it!<br>
you have should have postgresql running on you computer and configured for these series!

this is all for now on the first note on postgres noSql.