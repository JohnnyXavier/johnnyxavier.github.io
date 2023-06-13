---
title: OOP inheritance - the "is a kind of..." feature
excerpt: This note is about oop - inheritance in the application
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

This note is about inheritance usage tips and tricks on the app.

---

the app is written in **java {{ site.showcase.java-version }}** and on this note we are going to tackle a few places where OOP techniques
were used.

I will not define oop, nor what a class or interface are. For a formal definition and discussion here I leave
the [wikipedia entry for OOP](https://en.wikipedia.org/wiki/Object-oriented_programming)

each OOP section will tackle some OOP feature present in the app.

---

[inheritance](https://en.wikipedia.org/wiki/Inheritance_(object-oriented_programming)), or subclassing, or deriving, or basing... is
supported in java by extending or implementing classes, abstract classes and interfaces.

with java 8, the line between abstract classes and interfaces became blurry.

once upon a time... interfaces used to have no implementation code at all and would only provide "contracts" or "shapes" that the
inheriting classes would implement. One could say a pre java 8 interface was a "pure abstract" class. With retro compatibility in mind,
interfaces from java 8 onwards, can define `default methods` that can have implementation code.

I'll show here a nice "feature" of inheritance: **code reusability**

## abstraction for code reusability

code reusability, or ***"avoid repeating yourself"***... is an efficient way to tackle medium to big applications.<br>
if you have a small codebase and 2 or 3 times a given method has to be implemented for a common functionality, you might not want to reuse
it, but if that functionality is widespread, reusing a method is not only an efficient way of avoiding repetition, but to increase
productivity and quality.

### the API REST access commonality

CRUD applications that have a web interface can have some common ops. We usually see many REST resources (controllers) getting / creating /
updating / deleting the structure they respond for.

if we have 5 endpoints, well, we may not care into thinking to abstract those common ops... if you have many endpoints it seems like a good
candidate to practice some oop!

### the basic ops resource

in {{ site.showcase.name }} you will see REST endpoints to access for labels, users, status, cards, comments, projects, accounts, etc.

each one of those will have a `findById()` method as follows:

```java
private final PersistenceService persistenceService;

@GET
@Path("{id}")
public Uni<Response> findById(final UUID id){
        return persistenceService.findById(id)
        .map(resultDto->Response.ok(resultDto).build())
        .onFailure(NoResultException.class).recoverWithItem(Response.status(NOT_FOUND)::build)
        .onFailure().recoverWithItem(ResponseUtils::failToServerError);
        }
```

the above code is as simple as it can be, we receive a request with an **id**, we call the corresponding persistence service and return the
result. Finding a single result by id is not going to change whether it is a label, a user or a comment.

but that's not the only thing we need in general... we also want:

* deleteById()
* create()
* update()

if we agree that we want the minimum CRUD combo for most entities, this is 4 methods x 10 lines of almost the same code, on every
resource (controller) class.

say you have 10 entities... 400 lines of almost the same code for minimum shared CRUD...

let's check how grouping the common CRUD in an abstract class can help reuse code.

`BasicOpsResource.java` class

```java

@Produces("application/json")
@JBossLog
@WithSession
public abstract class BasicOpsResource<D, E> {

    private final BasicPersistenceService<D, E> basicPersistenceService;

    protected BasicOpsResource(BasicPersistenceService<D, E> service) {
        this.basicPersistenceService = service;
    }

    @GET
    @Path("{id}")
    public Uni<Response> findById(final UUID id) {
        return basicPersistenceService.findById(id)
                .map(resultDto -> Response.ok(resultDto).build())
                .onFailure(NoResultException.class).recoverWithItem(Response.status(NOT_FOUND)::build)
                .onFailure().recoverWithItem(ResponseUtils::failToServerError);
    }

    @POST
    @Consumes("application/json")
    public Uni<Response> create(final D fromDto) {
        return basicPersistenceService.create(fromDto)
                .map(newlyCreatedDto -> Response.ok(newlyCreatedDto).status(CREATED).build())
                .onFailure(ConstraintViolationException.class).recoverWithItem(ResponseUtils::violationsToResponse)
                .onFailure(PgException.class).recoverWithItem(ResponseUtils::processPgException)
                .onFailure().recoverWithItem(ResponseUtils::failToServerError);
    }

    @PUT
    @Path("{idToUpdate}")
    @Consumes("application/x-www-form-urlencoded")
    public Uni<Response> update(@NotNull final UUID idToUpdate,
                                @FormParam("field") final String field,
                                @FormParam("value") final String value) {
        return basicPersistenceService.update(idToUpdate, field, value)
                .replaceWith(Response.accepted()::build)
                .onFailure(PersistenceException.class).recoverWithItem(ResponseUtils::persistenceExResponse)
                .onFailure(NoSuchElementException.class).recoverWithItem(Response.status(NOT_FOUND)::build)
                .onFailure(PgException.class).recoverWithItem(ResponseUtils::processPgException)
                .onFailure().recoverWithItem(ResponseUtils::failToServerError);
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> deleteById(final UUID id) {
        return basicPersistenceService.deleteById(id)
                .map(isDeleted -> {
                    Response.Status status = isDeleted ? OK : NOT_FOUND;
                    return Response.ok().status(status).build();
                });
    }

}
```

a few things to ignore as they are out of **oop** scope:

* `@WithSession` is about reactive hibernate 6
* `@JbossLog` is a lombok annotation for adding logging
* `Uni<xxx>`, chaining of `.map()`, `.onFailure()` etc().
* `ResponseUtils` is a helper class that will create a proper `Response` based on the error to handle

examining the class, the first thing to note is that it is `abstract`, means it can not be instantiated on its own. We will extend it by
the other rest resource classes to reuse its code.

the second thing to note is that it uses a `persistence service` that is initialized on the constructor. More on that later...

so this class has a persistence service, and the 4 CRUD methods, and that's it... and it powers every single endpoint of the application.

let's do a quick dive into the `findById()` to understand what happens and introduce briefly Mutiny and its reactive style.

```java
@GET
@Path("{id}")
public Uni<Response> findById(final UUID id){
        return basicPersistenceService.findById(id)
        .map(resultDto->Response.ok(resultDto).build())
        .onFailure(NoResultException.class).recoverWithItem(Response.status(NOT_FOUND)::build)
        .onFailure().recoverWithItem(ResponseUtils::failToServerError);
        }
```

the `@GET` annotation indicates the method responds to HTTP GET requests.<br>
the `@Path("{id}")` indicates that there is a path variable the request will be serving for.<br>

the method signature `public Uni<Response> findById(final UUID id)` requires a little explanation.

a `Uni` represents a lazy async action, and it's part of the Mutiny reactive stack, while a `Response` object is a typical
jakarta `Response`.
This represents the return of most of our signatures for most of the resources handling REST requests. We will return a response in an async
manner. The `id` parameter accounts for the id we want to find in the database for a given entity.

the method's body calls on our persistence service and performs a `findById()` op, maps the result in the `Response` we want to return and
cares for possible failures.

### bonus: cascading inheritance

what if we want to add a `findAll()` method only to a few resources but not all of them, while keeping all the basic operations we just saw?

we can subclass the `BasicOpsResource` and extend from the new abstract class where required, like this:

```java
public abstract class BasicCatalogResource<D, E> extends BasicOpsResource<D, E> {

    private final BasicPersistenceService<D, E> service;

    protected BasicCatalogResource(final BasicPersistenceService<D, E> service) {
        super(service);
        this.service = service;
    }

    @GET
    @Path("all")
    public Uni<Response> findAll(@QueryParam(value = "sortBy") @NotNull final String sortBy,
                                 @QueryParam(value = "sortDir") final String sortDir,
                                 @QueryParam(value = "pageIx") final Integer pageIx,
                                 @QueryParam(value = "pageSize") @NotNull final Integer pageSize) {
        return service.findAll(new Pageable(sortBy, sortDir, pageIx, pageSize))
                .map(pageResult -> Response.ok(pageResult).build())
                .onFailure(NoResultException.class).recoverWithItem(Response.status(NOT_FOUND)::build)
                .onFailure(PgException.class).recoverWithItem(ResponseUtils::processPgException)
                .onFailure().recoverWithItem(ResponseUtils::failToServerError);
    }

}
```

this illustrates how you can "cascade down" inheritance.

> **teaching note:** <br>
> a word of advice, probably 2 or exceptionally 3 "cascading extensions" are enough, or the code can get hard to follow.

### how do we reuse this code then?

let's check a few resource classes that extend our abstract class...

`SeniorityResource.java`

```java

@Path("/v1/cardDifficulty")
@Produces("application/json")
public class CardDifficultyResource extends BasicCatalogResource<CardDifficultyDto, CardDifficultyEntity> {

    public CardDifficultyResource(final CardDifficultyService cardDifficultyService) {
        super(cardDifficultyService);
    }

}
```

this is the card difficulty resource that receives requests to CRUD a card's difficulty (easy, hard, trivial, etc.)

it indicates the `@Path` it listens to.<br>
it extends our `BasicOpsResource` class that had the minimal CRUD access methods, and it initializes it by passing the proper persistence
service, the `CardDifficultyService`

```java

@Path("/v1/department")
@Produces("application/json")
public class DepartmentResource extends BasicCatalogResource<DepartmentDto, DepartmentEntity> {

    public DepartmentResource(final DepartmentService departmentService) {
        super(departmentService);
    }

}
```

same with department here...

```java

@Path("/v1/cardStatus")
@Produces("application/json")
public class CardStatusResource extends BasicCatalogResource<StatusDto, StatusEntity> {

    public CardStatusResource(final CardStatusService cardStatusService) {
        super(cardStatusService);
    }

}
```

and again with card status...

**wait a moment... so where are my card difficulty and department and card status endpoints?!**

well, that's the beauty of it!

as all resources extend from the abstract class, each one initializing it with its own persistence service, each one of the concrete
services has access to all those minimal CRUD methods!

let's make some numbers...

the code relevant to the minimal CRUD ops on the `BasicOpsResource.java` is 40 lines.<br>
the code relevant to the additional `findAll()` op on the `BasicCatalogResource.java` is 11 lines.<br>

> **we are reusing 40 lines of code on each concrete class extending from the BasicOpsResource.**<br>
> **we are reusing 51 lines of code on each concrete class extending from the BasicCatalogResource.**<br>
> **it only takes a 2 lines constructor to add basic CRUD endpoints to a new resource**

given that at the time of this writing **{{ site.showcase.name }}** has:

* 7 resources inheriting from BasicCatalogResource
    * 7 * 51 = 357 lines of code reused
    * 7 * 5 = 35 methods reused
* 14 resources inheriting from BasicOpsResource
    * 14 * 40 = 560 lines of code reused
    * 14 * 4 = 56 methods reused
* total reused:
    * lines of code: **917**
    * methods: **91**

not only we reused a lot of code looking back, but looking forward, if a new rest resource is required it 2 lines of code to add it!

### bonus: that `@JBossLog` annotation...

oh... it seemed like it was there for no reason as there were no logs anywhere, right?

let's say we need to add logging to our endpoints... to our 91 endpoints... and we did not use inheritance via our tiny abstract
class...<br>
feel the chills crawling down your spine already?

if we added something innocent say...

```java
@GET
@Path("{id}")
public Uni<Response> findById(final UUID id){

        log.infof("calling findById on id: %s",id);

        return basicPersistenceService.findById(id)
        .map(resultDto->Response.ok(resultDto).build())
        .onFailure(NoResultException.class).recoverWithItem(Response.status(NOT_FOUND)::build)
        .onFailure().recoverWithItem(ResponseUtils::failToServerError);
        }

```

this will now be inherited by all our concrete classes!<br>
the same goes if now you want to catch another failure, or transform further the resulting `Response`. So now, instead of cold sweat we can
grab a cold soda, and watch a few (always work related tech) videos with time to spare!

> **teaching note:** <br>
> I hope you realise how a bad addition can affect all the extending classes creating chaos...<br>
> great power, great responsibility...

---

### the data access commonality

you may have noticed that the `BasicOpsResource.java` uses a generic `BasicPersistenceService`.

if CRUD operations have some commonality across REST resources, can it be they also share a commonality on the data access layer? After all,
finding an XXX entity by id and returning a YYY projection for said entity... has a very common feeling...

I will show and explain here only the part of the `BasicPersistenceService` related to inheritance and tackle the rest on different notes
given its difficulty and some advanced features.

> **teaching note:** <br>
> a few features on `BasicPersistenceService` are complex and if you came here just for inheritance you can ignore them.<br>
> Try and work your head around them if you can, as many advanced features present in this class make it a good coding practice.<br>
> Don't worry if you can't, as in other notes we're going to come back and look at each feature and technique used here.

let's dive into 2 simple methods of this persistence class to illustrate inheritance once more.

```java
private PanacheRepositoryBase<E, UUID> repository;
private Class<D>                       dtoClass;

/*
 * This no arg ctor required by quarkus CDI
 */
protected BasicPersistenceService(){
        }

protected BasicPersistenceService(final PanacheRepositoryBase<E, UUID> repository,final Class<D> dtoClass){
        this.repository=repository;
        this.dtoClass=dtoClass;
        }

public Uni<D> findById(final UUID id){
        return repository.find("id",id)
        .project(dtoClass)
        .singleResult();
        }

@WithTransaction
public Uni<Boolean> deleteById(final UUID idToDelete){
        return repository.deleteById(idToDelete);
        }

```

what we want here is it to reuse the code to **find** or **delete** a given entity. This is achieved by initializing the service with a
repository
which is the one that knows where to look for our data. We also initialize the service with a dto class which is the one that is going to
serve as projection on our **findById** operation.

same as with the REST `BasicOpsResource` class, our persistence services will extend from `BasicPersistenceService` like this:

```java
public class LabelService extends BasicPersistenceService<LabelDto, LabelEntity> {

    private final LabelRepository labelRepo;

    public LabelService(final LabelRepository labelRepo) {
        super(labelRepo, LabelDto.class);
        this.labelRepo = labelRepo;
    }

    //[ more methods omitted]
}
```

the `LabelService`, just by extending the `BasicPersistenceService` class, has now access to `findById()` and `deleteById()`. It only has to
pass to its constructor the `labelRepository` and the `labelDto` and the `BasicPersistenceService` code takes care of the rest.

omitted here do to their complexity for an oop basic note, all services that extend from `BasicPersistenceService` inherit the code for:

* findAll()
* findAllPaged()
* countAll()
* update()
* updateInPlace()
* countAll()
* countAllByUserId()

all inherited code amounts to around: 41 lines of code

there are another 21 services, so again we are reusing 861

if we add these to the previously discussed 917 ones we are up to 1778 lines of reused code.

## on increasing productivity

for the moment, all just lingo on OOP, but what can we extract of it into the real world?

say your code is 6606 lines of code (which is current size of **{{ site.showcase.name }}** not counting javadoc), and you saved from coding
1778 via reusability...

if the full codebase of 8384 lines of code (the 6606 + the 1778 we just saved) was to be delivered in 1 month, just by using OOP's
inheritance we could deliver 21% faster, that's almost a full week!<br>
care to translate time in money? or maybe extra time for additional features, training, etc.?

the more you reuse code, the more you reap its benefits.

## on improving quality (defensively)

you're not adding quality by a means of testing or anything of the like, but by reusing code you defensively protect yourself against
possible code drifting. You may slightly change code in one implementation that will break on a general update.<br>
If you detect a problem, fixing a reusable method fixes the bug everywhere, if you want to add functionality or make an improvement, adding
it on the abstract method spreads the new functionality everywhere.
