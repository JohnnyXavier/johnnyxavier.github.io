---
title: Hibernate inheritance
excerpt: This note is about using inheritance on entities with hibernate
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

This note is about using inheritance on entities.

references:

{% include_relative hibernate-references.md %}

---

## intro

we already saw inheritance in the [OOP - inheritance note](/bmc-showcase-note-oop-inheritance), and inheritance in the entities world is
about the same, with a few twists to consider.

## inheritance in the realm of orm

let's dive into how I use inheritance in {{ site.showcase.name }} with an example.

### the base class

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

this is an abstract class, and we already know from the [OOP - inheritance note](/bmc-showcase-note-oop-inheritance) that abstract classes
cannot be instantiated. This is exactly our purpose here as we do not want rogue instances of `BaseEntity` running around. The only purpose
of this class is to serve as basis for others.

not only that... this class is not marked as an `@Entity` itself so there won't be a table for it on the database, instead this class is
annotated as `@MappedSuperClass`.

`@MappedSuperClass` indicates that mapping information from this class is applied to the inheriting entities. This means that each entity
class that extends from this `BaseEntity` inherits all 4 fields:

* **UUID id**: as the id
* **UserEntity createdBy**: as the creator of a given record
* **LocalDateTime createdAt**: as the date-time of creation
* **LocalDateTime updated**: as the date-time of update

when you think about this approach, I wanted every table to have creation and update info, the user responsible for creating a given record
and the id type shared across all records will be of the uuid kind.

let's check how it is used by examining a class that extends from this one.

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

the `CommentEntity` extends the `BaseEntity` and that is it, nothing else is required from the classes that inherit from `BaseEntity`.<br>
when looking at the db, this class will have id, createdBy, createdAt and updatedAt fields.

## inheritance purpose on entities

as stated on the [OOP - inheritance note](/bmc-showcase-note-oop-inheritance), code reusability is a big topic when using inheritance. It is
true in general, and it is true here too. When you care for the amount of code we save ourselves from repeating on each entity, it can be in
the hundreds.

there is something that is subtle in the case of entities and inheritance. It is not just avoiding repetition per se or making contracts for
inheritors.<br>
it is modeling our tables with shared properties and commonality. What I meant to convey by using this approach is that I want every table
to have a creator, and timestamps. A database inheritance of sorts.

## cascading inheritance on entities

just like with common inheritance, cascading inheritance is supported.

in {{ site.showcase.name }} we have 3 very distinct entity types.

* **catalogs**: these are common datasets used by the application and by the record entities, ie: labels, statuses, seniority, etc.
* **records**: this is the data we actually want to track, the meat of the app, ie: accounts, cards, projects, etc.
* **other**: entities that won't fall into any of the above 2 categories, ie: changelogs, time tracking, configuration, comments, etc.

catalogs will have in common:

* name
* description
* isSystem: a boolean indicating if a record is isSystem reserved or not

records will have in common:

* name
* description
* coverImage

let's see how cascading inheritance will work with an example.

```java

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class BaseCatalogEntity extends BaseEntity {

    @Column(unique = true)
    private String name;

    @Column(columnDefinition = "boolean default false")
    private Boolean isSystem;

    private String description;

}
```

this class extends the `BaseEntity` as we said before and is itself annotated with `@MappedSuperClass`. It means its associations will pass
down to any class that extends from it

```java

@Entity
@Table(name = "seniority")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class SeniorityEntity extends BaseCatalogEntity {

    @OneToMany(mappedBy = "seniority", cascade = ALL)
    public Set<UserEntity> users = new HashSet<>();

    private short level;
}
```

here a `SeniorityEntity` class extends the `BaseCatalogEntity`, declaring its own fields and inheriting then:

* from `BaseEntity`:
    * id
    * createdBy
    * createdAt
    * updatedAt
* from `BaseCatalogEntity`:
    * name
    * description
    * isSystem

this catalog entity and all catalogs will now have that shape on the database, the same will apply to records with their own inherited
fields.

## note on multiple cascading inheritance

as u can see from above, `BaseCatalogEntity` share **name** and **description** with the base records entity... we cannot move them to
the `BaseEntity` or it will be specialized beyond what we want from it. Why not extract those 2 fields into another class and inherit from
that new one before creating the `BaseCatalogEntity` or the base records one?

as stated in [OOP - inheritance note](/bmc-showcase-note-oop-inheritance), probably a cascade of 3 classes is enough and I am respecting
that for readability’s sake.

