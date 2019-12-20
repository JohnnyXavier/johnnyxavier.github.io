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

# /the_one_on_setting_up_a_supporting_java_project
on the previous notes I showed a few small examples, which I referred to, as toy examples

those toy examples are good to understand a concept without being overwhelmed by detail and not to loose focus on the subject being explained. But real world examples are nothing like that...

Reality is messy, drags many versions and many years and generations of developers and changing in management, and good ideas poorly implemented and also, sadly, bad ideas incredibly well implemented...<br>
the next examples will start with toy DBs that I might not publish per their simplicity, but we will play in our playground schemas like big fellas with DBs with millions of rows...

being a `jvm` dev mostly, I build many things with `java` tools.<br>
as the topic here is `postgres` no Sql, `java` does not matter much and you can build the same as myself in the language of your own preference.<br>
having said that, if you are also a `jvm` developer you will benefit a lot as many things might solve problems you currently have.

I will use `jdbc`, `spring` and many of the ecosystem's usual suspects.Translating the examples to `goLang` or `.NET` or any other language for which you have a postgresql driver might be kind of simple if you know your way around your lang (possibly?)

### /so_postgresql_can_do_json
alright, so postgres can do json, but what can we do with it?

from now on, each note will try to answer a problem or chat a bit about a topic related to no sql in json, and a few sporty functions that will handle or produce jsons.

the first thing we are going to do is to setup a project around postgres to use as starting point or service for the operations we're going to perform.

### /setup
we'll setup a java project to handle some web containers, rest endpoints, jdbc, connection pools, populating databases with test data, etc

you can download all of it from github from the link below

[PostgreSQL no sql repo]({{ site.repourl }})

#### /the_suspects
* OS:
    * GNU / Linux -> Ubuntu 19.10 ***-Eoan Ermine-***
* Lang:
    * java 13 (>= J8 is OK)
* frameworks / libs / etc:
    * spring boot (2.2.2.RELEASE)
        * hikari
        * log4j2
        * undertow
        * liquibase
        * jackson
        * JPA
        * JDBC
* database:
    * PostgreSQL 12.1

##### /installing_the_os
if you need help installing the OS on a laptop, this guide below can point you into the right direction as linux and laptops with discrete cards are not the best friends at the moment
[Ubuntu 18.10 Cosmic Cuttlefish Setup - on Dell 9560]({{ site.baseurl }}{% post_url gnu_linux/2018-11-01-dell-9560-setup-ubuntu1810 %})

interestingly, installing `Ubuntu 19.10 Eoan Ermine` was straightforward without any low level tweak needed.

installing linux other than in a laptop with a discrete GPU is straightforward so I won't cover that here.

##### /installing_java_on_Linux
you can install `openjdk` or `Oracle's jdk`<br>
`Ubuntu` has the `openjdk` packages already on it's own repos, `Oracle` ones need to be added.

```bash
# for the java openjdk 13
sudo apt install openjdk-13-jdk

# confirm you have the Openjdk installed correctly
javac -version

# you should get you something similar to
$ javac 13

# and the runtime
java -version

# for the Openjdk should display something similar to
$ openjdk version "13" 2019-09-17
$ OpenJDK Runtime Environment (build 13+33-Ubuntu-1)
$ OpenJDK 64-Bit Server VM (build 13+33-Ubuntu-1, mixed mode)
```

##### /creating_a_new_spring_boot_project
let's create a new [spring boot](http://spring.io/boot) project that will allow to put things to practice

the easiest way to do it, is to go to [spring initializr](https://start.spring.io) and create a new project that you can download and use as starting point.<br>
You can then import it to your IDE, or wherever you feel comfortable working in.

if you have `intelliJ IDEA Ultimate` or `STS` (`Eclipse` based **S**pring **T**ool **S**uite) you can create a new spring boot project directly from the IDE.

using one of the tools of your choice, select the following components from the `spring boot initializr`

* springboot: 2.2.2.RELEASE (spring boot project that will handle our below dependencies and build)
* general:
    * lombok (utility to avoid the ceremony of getters setters and the like)
* SQL:
    * postgreSQL (the database driver)
    * JDBC (spring jdbc module to handle DB connections et als)
    * JPA (spring JPA module to handle DB connections et als)
    * Liquibase (database maintenance)
* Web:
    * web (spring project that provides infrastructure for rest, mappings et als)
    
this will create a starting point for our project, choose any name you like for it.<br>
if you downloaded it from the `initializr` web, unzip and import it to your IDE

once you have the project on your IDE, we're gonna tweak a few things.
* change the default `tomcat` and use `wildfly` as web container
* change `logback` for `log4j2`
* configure `PostgreSQL` and datasource (`hikariDS`)
* minor tweaks to logging and `Spring Boot` interface

`springboot` offers an easy way to perform the above tweaks without much fuzz.

your `pom.xml` should look like this
###### postgresnosql pom file
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.postgresnosql</groupId>
    <artifactId>postgresnosql</artifactId>
    <version>1.0-SNAPSHOT</version>

    <packaging>jar</packaging>

    <name>postgresnosql</name>
    <description>Demo project for postgresnosql jsonb</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>13</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <!--removing tomcat web container-->
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <!--removing logback as logging engine-->
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <!--adding log4j2 as logging engine-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j2</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <!--adding undertow web container-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-undertow</artifactId>
            <exclusions>
                <exclusion>
                    <artifactId>xnio-api</artifactId>
                    <groupId>org.jboss.xnio</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>xnio-nio</artifactId>
                    <groupId>org.jboss.xnio</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <artifactId>xnio-api</artifactId>
            <exclusions>
                <exclusion>
                    <artifactId>jboss-threads</artifactId>
                    <groupId>org.jboss.threads</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>wildfly-common</artifactId>
                    <groupId>org.wildfly.common</groupId>
                </exclusion>
            </exclusions>
            <groupId>org.jboss.xnio</groupId>
            <version>3.7.3.Final</version>
        </dependency>
        <dependency>
            <artifactId>xnio-nio</artifactId>
            <groupId>org.jboss.xnio</groupId>
            <version>3.7.3.Final</version>
        </dependency>
        <dependency>
            <artifactId>jboss-threads</artifactId>
            <version>3.0.0.Final</version>
            <groupId>org.jboss.threads</groupId>
        </dependency>
        <dependency>
            <groupId>org.liquibase</groupId>
            <artifactId>liquibase-core</artifactId>
            <!--removing logback again from liquibase-->
            <exclusions>
                <exclusion>
                    <artifactId>logback-classic</artifactId>
                    <groupId>ch.qos.logback</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--adding libs that were removed since java 8 / 9 -->
        <dependency>
            <groupId>javax.xml.bind</groupId>
            <artifactId>jaxb-api</artifactId>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

then we will tweak the project's properties file.
```properties
#springboot tweaks
spring.main.banner-mode=off

#hikari settings without too much tweaking
spring.datasource.hikari.username=john
spring.datasource.hikari.password=john
spring.datasource.url=jdbc:postgresql://localhost:5432/playground

#liquibase setup
spring.liquibase.liquibase-schema=postgres_no_sql

#logging Levels -> we may want to switch these at some point
logging.level.liquibase=info
logging.level.root=info
```

`liquibase` handles your db maintenance in an incremental way with a few advantages over using `hibernate`'s automatic creation (ddl-auto).

let's create a `liquibase` basic setup to get some tables created and add some data.

for `liquibase` to work smoothly under springboot it's changelog file needs to be placed in a particular path like this:<br>

**src -> main -> resources -> db -> changelog**<br>

now let's create a changelog file.<br>
inside the changelog folder create a file and name it `db.changelog-master.yaml`. `Liquibase` also supports `xml` and `json` formats if you're not very fond of `yaml` syntax.<br>
I will use `yaml` but post here the examples on the other 2 formats so you choose the one that suits you better<br>

This file will have every modification and update we want to perform to the database and will look similar to this
###### liquibase changelog in `YAML` format
```yaml
databaseChangeLog:
- changeSet:
    id: 1
    author: Johnny Xavier
    changes:
    - sqlFile:
        path: ../sql/dbCreation.sql
        relativeToChangelogFile: true
- changeSet:
    id: 2
    author: Johnny Xavier
    changes:
    - sqlFile:
        path: ../sql/db_first_users_seed.sql
        relativeToChangelogFile: true
```

or if you prefer the xml syntax, the equivalent would be
###### liquibase changelog in `XML` format
```xml
<?xml version="1.0" encoding="UTF-8"?>

<databaseChangeLog
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
            http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.0.xsd
            http://www.liquibase.org/xml/ns/dbchangelog-ext
            http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd">

    <changeSet id="1" author="Johnny Xavier">
        <sqlFile path="../sql/dbCreation.sql" relativeToChangelogFile="true"/>
    </changeSet>
    <changeSet id="2" author="Johnny Xavier">
        <sqlFile path="../sql/db_first_users_seed.sql" relativeToChangelogFile="true"/>
    </changeSet>
</databaseChangeLog>
```
or the json equivalent
###### liquibase changelog in `Json` format
```json
{
  "databaseChangeLog": [
    {
      "changeSet": {
        "id": "1",
        "author": "Johnny Xavier",
        "changes": [
          {
            "sqlFile": {
              "path": "../sql/dbCreation.sql",
              "relativeToChangelogFile": true
            }
          }
        ]
      }
    },
    {
      "changeSet": {
        "id": "2",
        "author": "Johnny Xavier",
        "changes": [
          {
            "sqlFile": {
              "path": "../sql/db_first_users_seed.sql",
              "relativeToChangelogFile": true
            }
          }
        ]
      }
    }
  ]
}
```
every `changeset` has a ***set of changes*** to apply to the db at a given time.<br>
you can see that what I am doing here is to load the different `.sql` files that contain those changes. Those are 100% standard sql files with sql statements.

`Liquibase` itself supports a syntax to operate on databases, but there is no upside using something different than sql, and the .sql files will not tie you to `liquibase` nor this `java` project, in case you just want the PostgreSQL knowledge.

in the end you should have something like this:
<img style="width: 30%" src="{{ site.baseurl }}/public/images/Selection_012.png">

we haven't created any data nor put anything into the db, so let do that just to make sure everything is working just ok.

below you can see 2 **.sql** files, one to create the tables and the other one to seed some data in it.

those are 2 toy .sql examples to test that our setup works fine.

let's then create a table

###### dbCreation.sql
```sql
--create the DB
set search_path to postgres_no_sql;


create table if not exists users_sql
(
  id         serial8 primary key,
  first_name varchar(30),
  last_name  varchar(50),
  email      varchar(50),
  address_id integer
);

create table if not exists users_doc
(
  id            serial8 primary key,
  user_document jsonb
);

create table if not exists address
(
  id            serial primary key,
  user_id       integer references users_sql (id),
  street_name   varchar(50),
  street_number varchar(7),
  city          integer,
  pos_code      varchar(10)

)
```

###### db_first_users_seed.sql
```sql
set search_path to postgres_no_sql;

insert into postgres_no_sql.users_doc (user_document) values ('{"id":1,"first_name":"Michel","last_name":"Etchell","email":"metchell0@engadget.com","gender":"Female","ip_address":"37.109.70.52","slogan":"embrace end-to-end experiences"}');
insert into postgres_no_sql.users_doc (user_document) values ('{"id":2,"first_name":"Caspar","last_name":"Corradi","email":"ccorradi1@nationalgeographic.com","gender":"Male","ip_address":"110.8.108.121","slogan":"grow holistic technologies"}');
insert into postgres_no_sql.users_doc (user_document) values ('{"id":3,"first_name":"Eyde","last_name":"Dorro","email":"edorro2@illinois.edu","gender":"Female","ip_address":"162.33.32.197","slogan":"transform web-enabled action-items"}');
insert into postgres_no_sql.users_doc (user_document) values ('{"id":4,"first_name":"Kiley","last_name":"Perelli","email":"kperelli3@g.co","gender":"Male","ip_address":"176.62.18.22","slogan":"transition vertical experiences"}');
insert into postgres_no_sql.users_doc (user_document) values ('{"id":5,"first_name":"Giuditta","last_name":"Themann","email":"gthemann4@dailymotion.com","gender":"Female","ip_address":"96.231.28.203","slogan":"facilitate seamless portals"}');

[...] --a few more inserts here

```
those test values were created using ***[mokaroo](https://mockaroo.com/)***

to test all went fine, just run the app

the 1st time it runs it will setup the tables, and insert some data on the `users_doc` table. The other 2 tables, the ones we didn't seed any data into, are to check that we can create all sorts of tables, as one would expect, by running `liquibase`.

if you run it again and again it won't redo the db work unless you add something to Liquibase's changelog.<br>
`Liquibase` will create 2 tables for internal use, where it stores which changes have already been made. These two tables are ***`databasechangelog`*** and ***`databasechangeloglock`***.<br>
we can just ignore them

this should be all for setting up a Java SpringBoot project to support our `PostgreSQL` series of notes.

##### /Note_on_using_spring-data_/_hibernate
regarding database tables creation with hibernate:

there is an option that will allow hibernate ORM to scan our entities and generate tables out of them.
It has a few modes, to create, to create and drop, to update, etc.

The good and bad things leaving the db creation to hibernate are many... BUT, you should avoid leaving the DB handling to hibernate unless it's a test or toy database or demo or POC.

What can, and eventually `WILL` happen, is that you'll update some  entity fields and that will propagate automatically to the DB.<br>
if those entities are used elsewhere something might, and eventually will break.<br>
if those entities are part of a "commons" or "shared" or an often used module something might and eventually will break.

on a small codebase it is not likely to happen but as you grow, it will.

of course this won't prevent you breaking things, but it will prevent avoidable mistakes.

one can also argue that a few tweaks, indexes, sequences, triggers etc are better handled directly with SQL and we also keep jpa annotations to a minimum

to save me some future trouble I found better to control the DB with plain SQL.

---

reviewing these first 3 notes, we have introduced the `nosql` topic with `posgres`, installed the `database`, setup our `IDEs`, installed `java`, setup a supporting project and checked out a few examples.

**with everything setup, we are now ready to dive into the features and possibilities of using `PostgreSQL` as a noSQL database!**

---

this is all for now on the third note on postgres noSql.