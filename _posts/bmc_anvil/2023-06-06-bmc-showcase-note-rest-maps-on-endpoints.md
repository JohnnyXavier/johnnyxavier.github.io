---
title: Handling REST endpoints with a map
excerpt: This note is about using some tricks on REST resources
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

This note is about using some tricks on REST resources.

---

## intro

when dealing with certain entities in **{{ site.showcase.name }}** we find ourselves wanting to get all the entities present in a given
collection.<br>
for example, all users in an account, in a project, in a board, assigned to a card, etc.

taking the **user REST resource** as an example, we could go 2 ways about that requirement:

1. we could add a query parameter that will allow to specify from which collection / collectionId we want the users from:
    * ie: `localhost:8080/v1/users?collection=[COLLECTION_NAME]&collectionid=[COLLECTION_ID]&pagingParams=...`
2. we could build a path with the collection name and id:
    * ie: `localhost:8080/v1/users/[COLLECTION_NAME]/[COLLECTION_ID]?pagingParams=...`

although I chose option number 2, let's look closer at both options.

## query parameters option

this is a tried and true way for passing filters from an endpoint. Using query parameter the urls will become:

* `localhost:8080/v1/users?collection=project&collectionid=fb90e4cf-c725-426a-9066-07ae2577a3aa&pagingParams=...`
* `localhost:8080/v1/users?collection=board&collectionid=fb90e4cf-c725-426a-9066-07ae2577a3aa&pagingParams=...`
* ...

this approach has to make explicit the collection and collection id we want the users from. There is a nice advantage which is that we
only use a single endpoint path and vary the query parameter to filter users.

## distinct paths option

this is another tried and true approach. Using path parameters the urls will become:

* `localhost:8080/v1/users/project/fb90e4cf-c725-426a-9066-07ae2577a3aa?pagingParams=...`
* `localhost:8080/v1/users/board/fb90e4cf-c725-426a-9066-07ae2577a3aa?pagingParams=...`
* ...

this approach makes the collection / collectionId implicit on the url itself. There is a disadvantage which is that each collection will
"generate" a dedicated endpoint path.

there is a personal preference of reading the url and right away knowing what it is about.<br>
I relate this approach is akin the needle gauges in cars, you know what speed you're going from the corner of your eye, while the 1st
approach is like having a digital speedometer, you need to actually read the speed.<br>
with the first approach you would actually need to look for the collection query param and collection id query parameter.

the second approach also means you have distinct paths to which apply statistics, scores and logs of all sorts in a very easy way.

### mapping the parameters to url path params

the distinct path option can be achieved with path parameters, which will still have the benefit of coding a single method to handle all
possible collections. By having 2 path parameters, our `@Path` will look like this:

```java
@GET
@Path("{collection}/{collectionId}")
```

and we are all set! right? not yet...

if we leave things like that, (and the same will happen with the query parameter approach) the collection parameter is free in the open for
anybody to start poking our back end, and many poking bots will have a field day. There may not be any sensitive data to retrieve, but our
database will just work for nothing wasting time and resources.

a way to protect against that with little effort is as follows:

```java

@Path("v1/users")
@Produces("application/json")
@JBossLog
public class UserResource extends BasicOpsResource<UserDto, UserEntity> {

    private final UserService userService;

    private final UserRepository userRepo;

    private final Map<String, String> userSupportedCollections = ofEntries(
            new SimpleImmutableEntry<>("project", "projects"),
            new SimpleImmutableEntry<>("account", "accounts"),
            new SimpleImmutableEntry<>("board", "boards"),
            new SimpleImmutableEntry<>("sprint", "sprints"),
            new SimpleImmutableEntry<>("assignedCard", "assignedCards"),
            new SimpleImmutableEntry<>("watchingCard", "watchingCards"),
            new SimpleImmutableEntry<>("department", "departments"),
            new SimpleImmutableEntry<>("retroBoard", "retroBoards")
    );

    public UserResource(final UserService userService, UserRepository userRepo) {
        super(userService);
        this.userService = userService;
        this.userRepo = userRepo;
    }

    @GET
    @Path("{collection}/{collectionId}")
    public Uni<Response> findAllByCollectionId(final String collection, final UUID collectionId,
                                               @QueryParam(value = "sortBy") @NotNull final String sortBy,
                                               @QueryParam(value = "sortDir") final String sortDir,
                                               @QueryParam(value = "pageIx") final Integer pageIx,
                                               @QueryParam(value = "pageSize") @NotNull final Integer pageSize) {

        String collections = userSupportedCollections.get(collection);
        if (collections == null) {
            return Uni.createFrom().item(Response.ok().status(NOT_FOUND).build());
        } else {
            return userService.findAllInCollectionId(collections, collectionId, new Pageable(sortBy, sortDir, pageIx,
                            pageSize))
                    .map(userDtos -> Response.ok(userDtos).build());
        }
    }
}
```

this REST resource handles the `/v1/users/**` path.

we have a map of all the collections we want to support that has the path parameter received from the web as key and the corresponding name
of the collection in the `UserEntity` as value. This was implemented as a `Map` for front end flexibility, it can perfectly be a `Set`.

the first thing that happens when receiving a request is checking if we support that collection, if we do, we pass down the request to the
service, if we don't, we just return a `404` to the caller.

the nice thing about the `Map`, or a `Set` if you prefer, is that it also allows to see at a glance which collections we are supporting.
Removing or adding another
collection is a single line of code requiring no logic at all.

If you are feeling bold, the supported collections could also be externalized, and we won't even need to recompile the project to manage
those user's paths.

## how far can we go?

I chose the `{collection}/{collectionId}` for efficiency and readability of the code and the resulting endpoints paths.

we could think that the double path parameter can apply to anything and make a `Map` that will filter out the rogue attempts to query
non-existing data.

let's see an example that is **not** part of **{{ site.showcase.name }}**

```java

@Path("v1")
@Produces("application/json")
@JBossLog
public class SingleEntityResource {

    private final SuperService superService;

    Set<String> entitiesSupported = Set.of(
            "user",
            "account",
            "project",
            "card",
            "etc..."
    );

    public SingleEntityResource(final SuperService superService) {
        this.superService = superService;
    }

    @GET
    @Path("{entity}/{entityId}")
    public Uni<Response> findEntityById(final String entity, final UUID entityId) {

        if (!entitiesSupported.contains(entity)) {
            return Uni.createFrom().item(Response.ok().status(NOT_FOUND).build());
        } else {
            return superService.findbyId(entityId)
                    .map(resultDto -> Response.ok(resultDto).build());
        }
    }
}
```

the above resource will handle every **"byId"** request from the application which of course seems nice, but now this resource can return
basically any type of data instead of a **"single concern"** data, which is something of a hot-chilli-ice-cream kind of practice.

not only I would advise against a single resource retuning so many different entities / dtos because of the data disparity, but it will also
be very confusing having some of the user data returned by the **user resource** and some other by this "general purpose multi-data
resource".

single concern practices are our friends (pinch of salt here as always...).

so... if the above is not an option how to make it effective to call on `{entity}/{id}` as it is basically the "same call" for every entity?

there actually is a way to benefit from just having a method to handle all the **"byId"** requests that is tied to each corresponding
resource, and takes care of only one "single concern" data without mixing matching code nor data.

check the [OOP - inheritance note](/bmc-showcase-note-oop-inheritance/#the-basic-ops-resource) and read about the **BasicOpsResource.java**
