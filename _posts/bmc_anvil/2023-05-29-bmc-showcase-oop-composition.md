---
title: OOP composition - the "has a..." feature
excerpt: This note is about oop - composition in the application
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

This note is about composition usage tips and tricks on the app.

---

the app is written in **java {{ site.showcase.java-version }}** and on this note we are going to tackle a few places where OOP techniques
were used.

I will not define oop. For a formal definition and discussion here I leave
the [wikipedia entry for OOP](https://en.wikipedia.org/wiki/Object-oriented_programming)

each OOP section will tackle some OOP feature present in the app.

---

[composition](https://en.wikipedia.org/wiki/Object_composition) is straightforward in java. It's "just" combining various objects to create
a compound one.

## simplest example:

we've already seen a REST resource in [OOP - inheritance](bmc-showcase-oop-inheritance) note.

```java

@Path("/v1/seniority")
@Produces("application/json")
public class SeniorityResource extends BasicCatalogResource<SeniorityDto, SeniorityEntity> {

    private final SeniorityService seniorityService;

    public SeniorityResource(final SeniorityService seniorityService) {
        super(seniorityService);
        this.seniorityService = seniorityService;
    }

}
```

the `SeniorityResource` has a `SeniorityService`.<br>
we created the rest resource by "combining" just a single object in this simple example.

## regular example:

probably better examples are our dtos. let's check the following one:

```java

public class CardSimpleDto extends BaseRecordDto {

    private       Boolean           isCompleted;
    private       LocalDateTime     dueDate;
    private       LocalDateTime     updatedAt;
    private       Long              position;
    private       UUID              boardId;
    private       UUID              boardColumnId;
    private       UUID              cardDifficultyId;
    private       UUID              cardStatusId;
    private       UUID              cardTypeId;
    private final Set<CardLabelDto> labels = new ArrayList<>();

    // constructor omitted

}
```

this dto is the sum of many Objects and even a collection of Objects. The `CardSimpleDto` is a combination of all it's constituents.

it _has a_ due date <br>
it _has a_ position <br>
it _has a_ collection of labels <br>

## on composition

compared to `inheritance` or `polymorphism`, `composition` feels like the innocent feature of the family. It is true that it's less daunting
that the others but when you are composing objects, you are bringing to your data type everything from the objects you add to it. When
working with certain frameworks like hibernate or jackson composition has to be handled with some care.

let's see.

on {{ site.showcase.name }}, each entity has a `UserEntity` that acts as the creator. Even a` UserEntity` has a creator itself.<br>
when you create a `CardEntity` for example, that `CardEntity` has a `UserEntity` field named `createdBy`, and also many other fields such
as `CardTypeEntity` or `StatusEntity` who themselves have their own `createdBy` field.<br>
In addition, a card also has a set of `watchers` and a set of `assignees`, which... you guessed correctly... are also `UserEntities`.

a `UserEntity` itself has a sets of cards assigned to it, sets of labels created by it etc...

this can generate cyclic references that will stack overflow jackson when trying to generate a json from a given Object.

we will dive more into this topic when digging into hibernate as bidirectional associations are usually the recommended approach, which can
be the source of a few headaches.
