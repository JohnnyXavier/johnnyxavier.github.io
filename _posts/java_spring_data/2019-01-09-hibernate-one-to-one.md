---
layout: post
title: Hibernate - one to one relation
author: Johnny Xavier
image: java-logo.png
updated:
categories: java.spring.data
extract: This note is about building a <strong>one to one</strong> relationship between 2 entities 
tags: spring data hibernate
---
gitHub Repo for this note: [java-data.git](https://github.com/JohnnyXavier/java-data.git)

### Intro
I find myself doing this from time to time, so I'll write a tiny bit about how you can relate 2 tables in a one to one relationship with the `Spring data` stack using `hibernate`.

### Definition of **one to one**
a nice way to understand this relationship is to imagine the relation you have with your passport as seen by your home country.

from your own point of vue, you can have more than one passport.<br>
I will use here the single country point of vue and leave for another note the perspective were you can have more than one passport.

there is only one passport number tied to an individual, and an individual has only one passport number for a given country.
so, one person per passport and one passport per person... 
 
**one to one**
  
if you have a database of users you might not want to add all their passport info on the user's table<br>
let's try a simple citizen with a simple passport

### Possible representations
with 2 objects tied this way you can have more than one way to relate them.

the `citizen` can include a `passport_id` as field. This `passport_id` field on the `citizen` would be the `passport`'s `id` field.

you can decide you want the `passport` to drive the relation instead and have a `citizen_id` field inside your `passport` represented by the `id` from the `citizen` table.

as many object relations, this one too will be described as a parent and a child. The child can also be described as the **target** entity as well.<br>
I've chosen the citizen to be the one driving the relation which has an implication in the hibernate world. It means there should be a column on the table to store the passport id, this will allow later to retrieve the passport for this citizen and you'll be able to use that passport_id field if you are querying passports and want to know which citizen they correspond to.

### Project
we're using `java` as dev language<br>
we're using `Spring Boot Data JPA` which uses hibernate as `ORM`<br>
we're using in mem `H2` as database
we're using automatic creation of db tables by the framework to focus on the relationship mapping.

#### creating a project:
you can go to [start.spring.io](https://start.spring.io) to create a springboot app. From the components available select:
* jpa
* jdbc
* web
* H2
* lombok

download the project and import it to your favourite IDE, anyone will work ok<br>

my `pom.xml` today looks like this (over time versions will change)

###### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.1.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>io.ioforge</groupId>
    <artifactId>java-data</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>java-data</name>
    <description>here and there with data management and java</description>

    <properties>
        <java.version>11</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
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

this will give us a spring boot skeleton to start with.

let's model both our classes

the Citizen
###### CitizenOneOne.java
```java
package io.ioforge.javadata;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(indexes = {
        @Index(columnList = "passport_id", name = "passport_id_idx")
})
public class CitizenOneOne {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String firstName;
    private String lastName;
    private int age;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    private PassportOneOne passport;
}
```
#### walking through the code
```java
@Getter
@Setter
@Entity
@Table(indexes = {
        @Index(columnList = "passport_id", name = "passport_id_idx")
})
```
the `@Getter` and `@Setter` are `lombok` annotations that will generate the **getters** and **setters** for us.<br>
you might see sometime the `@Data` instead of the 2 above. That annotation will generate way more things such as toString(), which we want to avoid as it will stackOverflow unnecessarily like this (if used on both entities).
<img src="{{ site.baseurl }}/public/images/one_to_one_to_string_stacktrace.png">

the `@Entity` is a javax persistence annotation that specifies that the class is an `Entity`, an object oriented data representation. 

the `@Table` line and the one below tell hibernate that we want to generate an index on the `passport_id` column named `passport_id_idx`

so where is that field...? It is the `PassportOneOne passport` field!

given that the `Citizen` will be driving the relation as you will see later, hibernate by convention will append the passport column with an **'_id'** at he end, and create it for us in the database. The **'_id'** at the end of the field name will be appended always so, a field named **'passport_id'** will end up as **'passport_id_id'** on the database.<br>

```java
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
```
here above we specify which field on this entity will be the primary key on the table with the `@Id` annotation and we also tell hibernate that this column's values are generated by the database automatically with the `@GeneratedValue` annotation. The `strategy = GenerationType.Auto` instructs hibernate to automatically choose the generator for a given DB, in this case it will be an autoincrement (the same will be for most DBs).

```java
    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    private PassportOneOne passport;
```
here above is where we define the `one to one` relationship. The `CascadeType.ALL` tells hibernate to propagate or "*cascade*" ALL operations down to the passport target entity.

Something that help me understand not only this relation but the rest of them too, is to put the keyword in context like this:<br>
there is **ONE** Citizen related **TO ONE** Passport.<br>
the first **ONE** relates to the Class, the second **ONE** relates to the field, in our case the class is citizen and the field passport

when you see relation annotations this will always work. For example if we have many passports, we could say that there is **ONE** citizen related **TO MANY** passports... the relation there? ***one to many***!

the `@JsonManagedReference` has nothing to do with persistence.<br>
it is an `jackson` annotation that will help us avoid getting into a recursive loop when creating a json out of this entities as they contain each other and that will cause a stackOverflow

the Passport
###### PassportOneOne.java
```java
package io.ioforge.javadata.relations.onetoone.withindex.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class PassportOneOne {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String nationality;
    private Timestamp creation_date;
    private Timestamp expiration_date;
    @OneToOne(mappedBy = "passport", cascade = CascadeType.ALL)
    @JsonBackReference
    private CitizenOneOne citizen;
}
```
#### walking through the code
`@Getter`, `@Setter`,`@Entity`, `Id`, `@GeneratedValue` have been covered for the citizen class already

as you can see from above, the objects have one another in their fields, but if we check the database only the citizen has a field relating to the passport

the `@OneToOne(mappedBy = "passport", cascade = CascadeType.ALL)` is a little different than the one in the citizen class. This one has an extra parameter called `mappedBy`.<br>
this parameter tells hibernate who is driving the relation. In our case it tells hibernate that the `citizen` Entity is in charge via it's `passport` field. You will see this annotation only on the non-owning side of a relation.

---

#### accessing the database
you can run the project from the command line with 
```bash
mvn spring-boot:run
```

or directly from the IDE

with the project running, point your web browser to [http://localhost:8081/h2-console](http://localhost:8081/h2-console) and you  will see the web interface for the H2 db.
the configuration should be as follows:
* **Saved Settings:** Generic H2 (Embedded)
* **JDBC URL:** jdbc:h2:mem:testdb
* **user Name:** sa
* **password** is not necessary

you will see something like this when hitting the H2 login url
<img style="width: 50%" src="{{ site.baseurl }}/public/images/H2_console_login.png">

once you access you should see something like this
<img style="width: 60%" src="{{ site.baseurl }}/public/images/H2_console_interface.png">

### On efficiency.
hibernate has a few tweaks that can be made to improve performance on your queries but you can also do a few simple things without much tinkering.<br>

there are a few things to consider:
laziness: as in not loading the passport eagerly each time we fetch the citizen.

ftis is maybe more relevant in the `to many` relationships when maybe you will load a LOT of info and maybe you just needed to query for the citizen info only. Still for the sake of completeness, here below is how you would do this lazy fetch

```java
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private PassportOneOne passport;
```
#### walking through the code
you just add `fetch = FetchType.LAZY` to the relation annotation and it tells hibernate not to query that field eagerly

this is what you will see on a debugger with a lazy fetchtype in place for our citizen
<img style="width: 80%" src="{{ site.baseurl }}/public/images/one_to_one_lazy_load.png">

if you had many records, this can save you a lot of time. But this comes with some needed workaround when displaying the results as jackson deserializer will fail on the empty passport. This can be circumvented and will be left for a different note. What we can take from the lazy loading is that it will save us time querying for a passport we might not need.

another tip sits on the database itself.

Indexes are key on this tip.<br>
the citizen id is a primary key and will be indexed automatically<br>
the passport id is another primary key and will also be indexed<br>

this leaves us with the situation in which we have a passport and want to get the citizen it belongs to.

we would query the passport id from the citizens table for this, but we never indexed that column, so selection can be less performant.

we have a few options:
#### making the `passport_id` column on the `citizen` table a `foreign key`
this will automatically index it on some DBs like mySQL -> [13.1.20.6 Using FOREIGN KEY Constraints](https://dev.mysql.com/doc/refman/8.0/en/create-table-foreign-keys.html)
but you are recommended to do it yourself on postgreSQL for example -> [5.3.5. Foreign Keys](https://www.postgresql.org/docs/current/ddl-constraints.html#DDL-CONSTRAINTS-FK)

apart from the index, it has another implication.<br>
being a foreign key means here, that the data stored on that column must exist on the table->column it points to<br>
think of it as `existing` data from a `foreign` table.column<br>
on our example, if we choose to use `citizen.passport_id` as foreign key, every `passport_id` we assign must exist on the passport table. If we try to save a citizen with a passport_id that does not exists on the passport table, the DB will complain.<br>
Hibernate will take care of that for us when saving the entity but we need to be aware of that constrain if we do it directly on the db

#### simply indexing the `passport_id` column on the `citizen` table
we get the efficiency of an indexed column without the constrains of the foreign key.<br>
this is what we have done already on the code above.

#### sharing the primary key
sharing the primary key means 2 things<br>
from a db perspective, you create you primary key on the parent and use that same one on the child as it's primary key.<br>
from a hibernate perspective it means that you dont need to generate a passport id on it's own, and now, you just share one index if you annotate your classes correctly.

this will need tweaking both entities to tell hibernate not to try to generate a value for the passport's id and that we're going to be using the citizen's id as shared primary key

let's see how the same classes will look like, if we decide to share the primary key.

###### Citizen.java
```java
package io.ioforge.javadata.relations.onetoone.withmapsid.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class CitizenOneOneMapsId {
    @Id
    private int id;
    private String firstName;
    private String lastName;
    private int age;

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JsonManagedReference
    private PassportOneOneMapsId passport;
}
```

and

###### Passport.java
```java
package io.ioforge.javadata.relations.onetoone.withmapsid.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class PassportOneOneMapsId {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String nationality;
    private Timestamp creationDate;
    private Timestamp expirationDate;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "passport")
    @JsonBackReference
    private CitizenOneOneMapsId citizen;
}
```
#### walking through the code
the `citizen` class has 2 differences
* it's no longer generating his own Id
* has a `@MapsId` annotation means that this field now serves as both PK and FK

the `passport` class now has to generate the PK value as it will be used in the citizen.

with this technique we know instantly which citizen correspond to which passport and vice versa as their ID's are mirrored.

it makes me a little uncomfortable that if instead of just having a passport field we add a driver's licence field this will get complicated to think about.

check how the DB looks for this scenario
<img style="width: 50%" src="{{ site.baseurl }}/public/images/one_to_one_citizen_maps_id.png">
as you can see above we don't have an id generated at all for our citizen, maybe that's not what we want.

if we query for our full entity on a db we might do something similar to this:
<img style="width: 60%" src="{{ site.baseurl }}/public/images/one_to_one_citizen_maps_id_full.png">
the id is generated on the passport table and shared with the citizen table on it's corresponding column.

we could invert the relation and have the passport inherit the id from the citizen but we will be in the same pickle as here on the other side of the coin.


#### which one to choose.
per Vlad Mihalcea's blog (absolutely **great** insights on hibernate), the `@MapsId` is the recommended one to one recipe in a hibernate ecosystem. check it here [The best way to map a @OneToOne relationship with JPA and Hibernate](https://vladmihalcea.com/the-best-way-to-map-a-onetoone-relationship-with-jpa-and-hibernate)<br>

there is no silver bullet that will be a best fit for all scenarios.

I might go with indexing and keeping PKs separated and ids on both tables like this
<img style="width: 60%" src="{{ site.baseurl }}/public/images/one_to_one_citizen_full.png">

but maybe I cannot control the DB in that regard, or the tables were already setup, or a special situation can make good use of Vlad's recommendations, or you want to keep your code more hibernate friendly than db friendly, or... or...

As usual... it depends...

Hope this note gives you a bit more info on entity relations to allow you solve a problem with more options on your toolbox.
If you have another way of doing this, let me know! The more we learn the better we can choose.