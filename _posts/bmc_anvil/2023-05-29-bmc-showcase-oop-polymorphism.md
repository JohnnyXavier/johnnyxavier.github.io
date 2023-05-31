---
title: OOP polymorphism - the “implemented as / like” feature
excerpt: This note is about oop - polymorphism in the application
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

This note is about polymorphism usage tips and tricks on the app.

---

the app is written in **java {{ site.showcase.java-version }}** and on this note we are going to tackle a few places where OOP techniques
were used.

I will not define oop. For a formal definition and discussion here I leave
the [wikipedia entry for OOP](https://en.wikipedia.org/wiki/Object-oriented_programming)

each OOP section will tackle some OOP feature present in the app.

---

[polymorphism](https://en.wikipedia.org/wiki/Polymorphism_(computer_science)) is supported in java by overriding / overloading.<br>
overloading can be considered outside polymorphism, but I will leave controversy to philosophers and include overloading in this note.

## overriding a method

simple inheritance examples in {{ site.showcase.name }} refer to the abstract class `BasicPersistenceService.java` mentioned
in [OOP - inheritance](/bmc-showcase-oop-inheritance) note.

that base class serves part as a contract and part as implementation code to be reused.

regarding the implementation code, our basic CRUD operations are:

* **create**: different for each entity
* **read**: we could abstract that functionality at it's base
* **update**: different for every entity
* **delete**: we could abstract that functionality at it's base

we covered the abstraction we could achieve for reading (`findById()`, `findAll()`) and deleting (`deleteById()`) at the inheritance note
already.

having also a `create` and an `update` method on the `BasicPersistenceService.java` is what enables us to use any service extending from it
within the REST services, and calling all those methods regardless of the entity involved.

we know a commonly implemented code will be called for `deleteById()` and `findById()`, but it is not possible to use a single `create()`
or `update()` method as each entity has different fields and gets created and updated on its own way.

similar to an interface the `create()` and `update()` methods are present in the `BasicPersistenceService.java` with no implementation as
follows:

```java
public abstract class BasicPersistenceService<D, E> {
    public abstract Uni<D> create(@Valid final D fromDto);
}

```

the keyword `abstract` is marking this method as to be implemented by its extending classes, and as you can see has no body.

the extending classes are the ones responsible to provide the proper implementation on how to create the entity they are in charge of.

for example:

```java
public class AccountService extends BasicPersistenceService<AccountDto, AccountEntity> {

    @Override
    @WithTransaction
    public Uni<AccountDto> create(@Valid final AccountDto accountDto) {
        UserEntity accountCreator = new UserEntity();
        accountCreator.setId(accountDto.getCreatedBy());

        AccountEntity newAccount = new AccountEntity();
        newAccount.setId(randomUUID());
        newAccount.setName(accountDto.getName());
        newAccount.setDescription(accountDto.getDescription());
        newAccount.setCreatedBy(accountCreator);

        return accountRepo.persist(newAccount)
                .replaceWith(findById(newAccount.getId()));
    }

}
```

I chose a simple service to illustrate that the `create()` method body is not reusable for other entities.

the `@Override` keyword indicates that this method is overriding a method from a parent class, in our case, the `abstract create()` from
the `BasicPersistenceService.java`.

other `create()` methods do have more in common... as it is the case with `catalog entities` and will allow us for simpler creation.

> **general note:** <br>
> a catalog table or entity is one that groups data that either hardly changes or that it can be a tag / description / label widely used on
> an application. If you think you can replace it with an `enum`, then probably it's a catalog. ie:<br>
> * developer seniority
> * countries
> * phone prefix codes
> * a country's provinces
> * tags / labels

an example of a catalog could be a card type (bug, task, story, epic, to-do, etc.):

```java
public class CardTypeService extends BasicPersistenceService<CardTypeDto, CardTypeEntity> {

    @Override
    @WithTransaction
    public Uni<CardTypeDto> create(@Valid final CardTypeDto cardTypeDto) {
        CardTypeEntity newCardType = new CardTypeEntity();
        CreationUtils.createBaseCatalogEntity(newCardType, cardTypeDto);

        return cardTypeRepo.persist(newCardType)
                .replaceWith(findById(newCardType.getId()));
    }
}
```

here we can use code for creating catalog entities as they have all basically the same properties. For illustration, here is the catalog
creation code:

```java
public class CreationUtils {

    private CreationUtils() {
    }

    public static void createBaseCatalogEntity(BaseCatalogEntity CatalogEntity, BaseCatalogDto CatalogDto) {
        UserEntity creator = new UserEntity();
        creator.setId(CatalogDto.getCreatedBy());

        CatalogEntity.setId(randomUUID());
        CatalogEntity.setName(CatalogDto.getName());
        CatalogEntity.setDescription(CatalogDto.getDescription());
        CatalogEntity.setCreatedBy(creator);
        CatalogEntity.setIsSystem(FALSE);
    }
}
```

as catalog entities inherit from BaseCatalogEntity we can use this utility to create the base of them. If a given catalog declares more
fields, we just add them to their `create()` method as follows:

```java
public class LabelService extends BasicPersistenceService<LabelDto, LabelEntity> {
    @Override
    @WithTransaction
    public Uni<LabelDto> create(@Valid final LabelDto labelDto) {
        LabelEntity newLabel = new LabelEntity();
        CreationUtils.createBaseCatalogEntity(newLabel, labelDto);
        newLabel.setColorHex(labelDto.getColorHex());

        return labelRepo.persist(newLabel)
                .replaceWith(findById(newLabel.getId()));
    }
}
```

here, a label has all the common catalog properties and also defines a color in hexadecimal, the `colorHex` field. We just add that missing
property after generating the main label ones via our helper method.

> **teaching / showcase note:**<br>
> we covered 'OOP - inheritance' elsewhere, but I did not mention anything about entities inheriting from each other...<br>
> as this is a topic closely related with hibernate and database tables it will be covered on its own hibernate inheritance section<br>
> <br>
> care to guess how much code is reused by inheriting from base entities?

## overloading a method

if overriding implemented a parent method differently, overloading achieves it by reusing the same method with different parameters.

using the same `BasicPersistenceService` as before let's examine the `update()` method.

```java
public abstract class BasicPersistenceService<D, E> {

    public Uni<Void> update(final UUID id, final String key, final String value) {
        return repository.findById(id)
                .onItem().ifNull().fail()
                .chain(entityToUpdate -> update(entityToUpdate, key, value));
    }

    protected abstract Uni<Void> update(final E toUpdate, final String key, final String value);
}

```

what we have here is a little inheritance and polymorphism in a single feature to show them together.

when we want to change a field's value, for example a label's hexColor, that field may be unique for each entity, so as with the `create()`
method we need dedicated `update()` implementations for each. But... something we want to do before updating any record's field, is making
sure that the record itself exists, and that is true to every entity in the app.<br>

> **teaching note:** <br>
> method declaration will only refer to the method's name and parameters <br>
> [oracle docs - methods](https://docs.oracle.com/javase/tutorial/java/javaOO/methods.html)

let's check the first `update()` method declaration:

> `public Uni<Void> update(final UUID id, final String key, final String value)`

it receives from the caller, the id of the record to update, the key (field) to update, and it's new value. From there, it tries to find the
given record and fails if it can't or moves on to update the entity.<br>
that's where the second `update()` method comes in play:

> `protected abstract Uni<Void> update(final E toUpdate, final String key, final String value)`

this method is overloading the first one, it also receives the key/value pair to update but instead of an id it receives the entity found on
the previous step.<br>
but we're not done, this method is also abstract and without implementation, which means that it has to be implemented by any class
extending the `BasicPersistenceService`.

let see an example of one of those extending classes:

```java
public class AccountService extends BasicPersistenceService<AccountDto, AccountEntity> {

    @Override
    protected Uni<Void> update(final AccountEntity toUpdate, final String key, final String value) {
        return switch (key) {
            case "name" -> updateInPlace(toUpdate, SET_NAME, value);
            case "description" -> updateInPlace(toUpdate, SET_DESCRIPTION, value);
            case "coverImage" -> updateInPlace(toUpdate, SET_COVER_IMAGE, value);

            default -> throw new IllegalStateException("Unexpected value: " + key);
        };
    }
}

```

the `AccountService` has its own code for updating its own fields. Each service overrides the `update()` and `create()` methods as they see
fit for their purpose.

## OOP techniques used so far

with what we've seen so far, a REST resource for example, does not need to worry calling specific methods such
as `createUser()`, `createLabel()`
or `deleteCommentById()`

with the `update()` method we used overloading and overriding at the same time for a double gain.

when overriding, each service updates its own entities as required and by overloading we can use a coherent naming for the same operation
and redirect the code just by changing the first parameter from a UUID to a generic entity.<br>
because of that combination every service inherits the common code for searching an existing record prior to update and can implement their
own update as required.

inheritance, polymorphism, code reuse and code implementation all at once.

> **showcase / teaching note:**<br>
> you will likely see overloading methods
> in [simpler more straightforward scenarios](https://docs.oracle.com/javase/tutorial/java/javaOO/methods.html). I thought the way described
> above could make a more interesting use of it for a clear rounded example of inheritance polymorphism as it is an overloaded version of a
> method that is overridden on each service. 

