---
title: Hibernate simple entities
excerpt: This note is about managing simple entities with hibernate
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

This note is about hibernate simple entities.

references:

{% include_relative hibernate-references.md %}

---

## intro: Entities

`entities` typically represent a table on a database, and each instance of an entity a row on that table. They can have logic code as seen
on the active record pattern.

an `entity` in **{{ site.showcase.name }}** is a `POJO` representing a table on a database, and except for accessors done via `lombok` it has no
logic.<br>

in this application there is no code on the `entities` as I chose to use a repository approach instead of an active record approach to
decouple the data access code from the data modeling.

when looking at the `entities` in **{{ site.showcase.name }}** you are looking at the table structure of the application.

the `entities` can be very simple representations of a table of very complex representations of tables, relations, fields data types, etc.

## simple Entities

I do not have plain simple entities in the application, so let's first see a proper simple entity en then a simple entity in {{
site.showcase.name }}

### simple entities in general

a simple entity en general will look something like this:

example modified from [quarkus.io orm with panache guide](https://quarkus.io/guides/hibernate-orm-panache#defining-your-entity-2)

```java

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long      id;
    private String    name;
    private LocalDate birth;

    //get / set omitted for brevity 
}
```

via the `@Entity` annotation, the above means that `Person` is a class that will represent a table of the same name in a database

it has 4 fields with different data types that will represent the columns on the database.

the `id` `Long` field is annotated with `@Id` and `@GeneratedValue`, meaning that it will act as primary key and its value will be
autogenerated leaving to hibernate to decide which strategy to use, hence the `strategy = GenerationType.AUTO`.

hibernate will take care of mapping the proper data type back and forth from the database to match `Long`, `String` and `LocalDate` to the
underlying database's data types.

### simple entities in {{ site.showcase.name }}

the actual showcase application uses a few tricks to benefit from more advanced JPA features like inheritance.

I'll show here how a simple entity looks like in the application.

```java

@Entity
@Table(name = "comment")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class CommentEntity extends BaseEntity {

  @Column(columnDefinition = "text")
  private String comment;

  @ManyToOne
  private CardEntity card;

}
```

let's ignore `lombok`'s annotations as [they are addressed elsewhere](/bmc-showcase-note-utils-lombok)

this `CommentEntity` has the `@Entity` annotation along with `@Table`. The table one tell hibernate how do we want to call the table on
the DB.

this entity es very simple even if we start seen some annotations on the fields. It just has 2 fields, a `String` and a `CardEntity`.
let's check these 2 JPA annotations:

* `@Column(columnDefinition = "text")`: a String type maps by default to a `varchar(255)` on the database, but we want our comment column to
  have no limit. By means of the `@Column` annotation we can configure different aspects of the column in the database, and in this case we
  tell the orm to create this `String` field as a text column on the db.
* `@ManyToOne`: this is an association mapping that tells hibernate that there are many comments per card. We are going to look deeper into
  mappings at their own articles.

seems that there is all to it, but we are missing an id of some sorts.

this simple `entity` class is made even simpler by reusing code. The `CommentEntity` extends a `BaseEntity` class. Let's quickly check it!

```java
@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {
  @Id
  @EqualsAndHashCode.Include
  private UUID id;

  @ManyToOne(fetch = LAZY)
  private UserEntity createdBy;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

}
```

aha! the parent class has the `id` we were missing. By extending from this class our `CommentEntity` inherits everything from
the `BaseEntity`.

we are going to see how it all works on the [hibernate inheritance note](/bmc-showcase-note-hbn-inheritance). See you there!