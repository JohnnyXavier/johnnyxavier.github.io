---
title: Reactive Programming
excerpt: This note is about reactive programming in using Quarkus and Mutiny
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

This note is about reactive programming with Quarkus and Mutiny on the app.

---

## intro and resources

{{ site.showcase.name }} is a fully reactive application. From the Quarkus core, to the web container and even the database client and orm.

if a component was required, I would choose the reactive option.

### why choosing a fully reactive stack from Quarkus.io

there are too main reason for that decision.

1. _to learn a new tech and a whole new framework_: I used Spring for years and project reactor was ok, but it was time to get out of
   the comfort zone and learn something new.
2. _build a full application in a reactive manner_: I wanted to test for myself if the claims about the non-blocking io / data stream /
   message passing model were true under load.

### resources on the web

**Quarkus**:

* [continuum](https://quarkus.io/continuum/)
* [quarkus reactive architecture](https://quarkus.io/version/main/guides/quarkus-reactive-architecture)
* [mutiny primer](https://quarkus.io/version/main/guides/mutiny-primer)

**Smallrye Mutiny**:

* [what is reactive programming](https://smallrye.io/smallrye-mutiny/2.2.0/reference/what-is-reactive-programming/)
* [Uni](https://smallrye.io/smallrye-mutiny/2.2.0/tutorials/creating-uni-pipelines/)
* [Multi](https://smallrye.io/smallrye-mutiny/2.2.0/tutorials/creating-multi-pipelines/)

**Eclipse Vert.x**:

* [Vert.x core](https://vertx.io/docs/vertx-core/java/)

## some examples of reactive style

## simple

```java

@Path("/v1/project")
@Produces("application/json")
public class ProjectResource extends BasicOpsResource<ProjectDto, ProjectEntity> {
    private final ProjectService projectService;

    public ProjectResource(final ProjectService projectService) {
        super(projectService);
        this.projectService = projectService;
    }

    @GET
    @Path("createdBy/{userId}")
    public Uni<Response> findAllCreatedByUserId(final UUID userId,
                                                @QueryParam(value = "sortBy") @NotNull final String sortBy,
                                                @QueryParam(value = "sortDir") final String sortDir,
                                                @QueryParam(value = "pageIx") final Integer pageIx,
                                                @QueryParam(value = "pageSize") @NotNull final Integer pageSize) {
        return projectService.findAllByUserIdPaged(userId, new Pageable(sortBy, sortDir, pageIx, pageSize))
                .map(projectDtos -> Response.ok(projectDtos).build());
    }

    @GET
    @Path("account/{accountId}")
    public Uni<Response> findAllByAccountId(final UUID accountId,
                                            @QueryParam(value = "sortBy") @NotNull final String sortBy,
                                            @QueryParam(value = "sortDir") final String sortDir,
                                            @QueryParam(value = "pageIx") final Integer pageIx,
                                            @QueryParam(value = "pageSize") @NotNull final Integer pageSize) {
        return projectService.findAllByAccountIdPaged(accountId, new Pageable(sortBy, sortDir, pageIx, pageSize))
                .flatMap(projectDtos -> Uni.createFrom().item(Response.ok(projectDtos).build()));

    }
}
```

let's focus here on the 2 methods at play in this REST resource class.

both return a `Uni<Response>`.<br>

we can think of a `Uni` as our main "reactive unit" from the Mutiny library, it represents a `lazy stream` emitting an item or failure.<br>
If you want to develop an async op, `Uni`s provide the tools to do so, from something very simple as our 2 methods above to a full reactive
pipeline.

the two key concepts to understand how a Quarkus reactive app works are `LAZY` and `STREAM`.

by `lazy` we're going to define that if we do not have a **final subscriber** to the **Uni** pipeline, nothing will execute.<br>
by `stream` we're going to define that `Uni`s handle series of events to produce a final single result. (`Multi`'s behave similarly but with
streams of data as result, instead of a single item)

observing our first example to get all projects created by a given **userId** we call the `projectService.findAllByUserIdPaged()`. That
service also returns a Uni and as we want to return a `Response` and not the raw DTOs, we concatenate to the Uni.map() method to transform
our service's result into the desired `Response`. Our `Uni.map()` method returns itself a `Uni` of the mapping op. Even if we did not call
explicitly on `subscribe()`, returning `Uni<Response>` by our REST resource acts as an implicit subscription.

> teaching note:<br>
> it is completely valid to use `subscribe()` as unlike `await()` it resolves in an async manner. The gotcha with `subscribe()` is that flow
> of a method chain like above becomes "interrupted" given that you need to do something with the now resolved result of your
> computation.<br>
> if you can avoid subscribing when there is a round-trip op, do it so. If it is an event / broker related code, subscribing might be the
> way to go.

the second method has a small difference only shown here for demonstration's sake.<br>
we are concatenating `.flatMap()`. In mutiny's API, `flatMap()` means that the transformation is **also** reactive / asynchronous,
whereas `.map()` transformations are synchronous.

in our example generating a `Response` from the service's result is a sync op, there is no delayed op of any kind, but if you can picture
that the result is a projectId, and we call another remote REST endpoint to fetch data from that projectId, then wrapping that new network
call in a `Uni` makes total sense.

## complex

complexity will depend a lot on the ops chained into the reactive pipeline. We will examine a few difficult ones when touching reactive
hibernate sections.

let's see here a more elaborated example of a REST resource chaining more than one op.

```java

@Produces("application/json")
@JBossLog
@WithSession
public abstract class BasicOpsResource<D, E> {
    @PUT
    @Path("{idToUpdate}")
    @Consumes("application/x-www-form-urlencoded")
    public Uni<Response> update(@NotNull final UUID idToUpdate, @FormParam("field") final String field, @FormParam("value") final String value) {
        return basicPersistenceService.update(idToUpdate, field, value)
                .replaceWith(Response.accepted()::build)
                .onFailure(PersistenceException.class).recoverWithItem(ResponseUtils::persistenceExResponse)
                .onFailure(NoSuchElementException.class).recoverWithItem(Response.status(NOT_FOUND)::build)
                .onFailure(PgException.class).recoverWithItem(ResponseUtils::processPgException)
                .onFailure().recoverWithItem(ResponseUtils::failToServerError);
    }
}
```

this `update()` method from the `BasicOpsResource` class not only calls on a service but instead of transforming the result as before it
replaces it fully by calling `.replaceWith()`. As many things can go wrong when trying to update records... from trying to update a
non-existing id, to trying to persist incorrect data or plain db errors, we can concatenate failure events emitted by the `Uni` pipeline and
gracefully respond to each case.