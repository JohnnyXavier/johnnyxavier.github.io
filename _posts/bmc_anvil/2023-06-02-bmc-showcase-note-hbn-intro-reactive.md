---
title: Hibernate reactive and persistence introduction
excerpt: This note is an introduction to the application's data stack
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

This note is an introduction to the application's data stack.

references:

{% include_relative hibernate-references.md %}

---

## intro to **{{ site.showcase.name }}**'s persistence stack

**{{ site.showcase.name }}**'s data access stack is built
around `hibernate reactive {{ showcase.hibernate-version }}` + `panache` + `reactive postgreSQL client` +
`postgeSQL {{ site.showcase.postgres-version }}` db

as stated on other places everything that can be reactive was chosen over the classic flavour.

### Panache

panache could be seen as the `Quarkus` equivalent for `SpringData`. It simplifies boilerplate code and offers 2 flavours for data access

* the repository pattern:
    * abstracts all data access logic in an interface away from the domain objects
    * [martinfowler's repository definition](https://martinfowler.com/eaaCatalog/repository.html)
* the active record pattern:
    * implements the data access on the domain object itself
    * [martinfowler's repository definition](https://www.martinfowler.com/eaaCatalog/activeRecord.html)

I chose for the data access layer the repository approach, it extracts all logic to repositories making very clear and clean the data
modeling by the entity and the data access by the repository itself.<br>

> **showcase note**:<br>
> I will not have a specific showcase for the active record approach on the app itself as it will be very disruptive, and services and
> resources will all have to be coded differently for the active record `Entity`.

the example below from quarkus guides illustrates how the `active record` approach works with `panache` library:

```java
import io.quarkus.hibernate.reactive.panache.PanacheEntity;

@Entity
public class Person extends PanacheEntity {
    public String    name;
    public LocalDate birth;
    public Status    status;

    public static Uni<Person> findByName(String name) {
        return find("name", name).firstResult();
    }

    public static Uni<List<Person>> findAlive() {
        return list("status", Status.Alive);
    }

    public static Uni<Long> deleteStefs() {
        return delete("name", "Stef");
    }
}
```

a given `entity` extends a `PanacheEntity` which is the one encapsulating all basic **CRUD** data access.

## hibernate 6.x reactive

for the
moment, `Quarkus` [claims hibernate reactive to be the only Jakarta (JPA) implementation](https://quarkus.io/guides/hibernate-reactive-panache)

it is basically the same as the classic implementation with a few minor differences regarding access to the `EntityManager` and how loading
lazy collections is achieved.

we are going to see both peculiarities in other notes.

if you are coming from `hibernate 5.x` series you will see some core changes:

* JPA3 - Jakarta: moving to eclipse's foundation specification means everything under `javax.persistence` now lives
  under `jakarta.persistence`
* no more hibernate own criteria API
* `ResultTransformer` split into 2

there are a few more that are non-breaking changes like the above.

if you are thinking about updating from `hibernate` 5.x to 6.x try
reading [thorben jansen's note on migrating to hibernate 6](https://thorben-janssen.com/things-to-know-when-migrating-to-hibernate-6-x/)
first

## postgreSQL

in the free relational databases, I used `MySQL` for years with fantastic results, support and great features. I think in the benchmark
world it also ranks among the fastest ones if not the fastest.

so, why postgreSQL?

at early stages I used both databases, and at some point I started trying `Quarkus`'s live reload (dev mode) and found it awesome to iterate
and experiment and debug. It turns out to be that when reloading the slowest part of the bootstrap was dropping and recreating all the DB
with the seeded data. While MySQL took several seconds, PostgreSQL takes 2 seconds at most. That leaned me to PostgreSQL, you can hook
up any DB of your choice.

### and then using a DB specific feature...

I read a while ago that using everything in an agnostic ultra compatible fashion is paramount to be able to switch implementations,
databases in this case.<br>
if I were to use a `MySQL` specific functionality, I won't be able to just swap databases...

I read a while ago that using everything in an agnostic ultra compatible fashion is at some point throwing away the benefits a specific
implementation offers.<br>
like having a tow truck and not loading more than xxx bags because they would not fit in a ferrari.

in this case, I did just that... I used a `postgreSQL` specific DataType
the [UUID data type](https://www.postgresql.org/docs/current/datatype-uuid.html)

I wanted to use `UUID`s as entities PKs instead of auto generated sequences of numbers.

the advantage is that UUIDs are native to `PostgreSQL`, and they are fast to operate with... blazing fast compared to uuids stored as
characters. Not only they are fast, but they are also footprint storage small. A char version of a `UUID` will be considerably larger
(2x +1 byte) than the 128-bit(16 bytes) size of `PostgreSQL` `UUID` type!

it is disputed that generating a `UUID` is slower than increasing an int in a sequence on large sets, but I am not fan of using
autoincrement sequences, nor having hibernate generate the sequence for me, which will be commented on a later note.

with `MySQL`, `UUID` support is achieved by means of [supporting UUID functions](https://dev.mysql.com/blog-archive/mysql-8-0-uuid-support/)

if you like `MySQL` there is the [UUID data Type offered by MariaDB](https://mariadb.com/kb/en/uuid-data-type/)