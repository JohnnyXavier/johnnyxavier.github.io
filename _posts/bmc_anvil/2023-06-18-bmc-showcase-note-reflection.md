---
title: Java Reflection
excerpt: This note is about using java reflection techniques
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

this note is about using java reflection

---

reflection is a feature in java that allows to introspect / modify properties of classes fields and methods. It also allows to instantiate
classes or call methods among other things

it can come in handy to manipulate the internals of a software when they are unknown at compile time. This is an advanced feature to use
carefully as it can make the code difficult to understand and alienate less experienced contributors on large codebases.

## motivation

in {{ site.showcase.name }} we have many endpoints and data types that can be updated by an end user like cards, comments, labels, user
data, etc.

every time we want to update a single simple field we would need to do something like this

```java
class DemoUpdate {
    public void updateUser(User user, String fieldToUpdate, String newValue) {

        switch (fieldToUpdate) {
            case "firstName" -> user.setFirstName(newValue);
            case "lastName" -> user.setLastName(newValue);
            case "age" -> user.setAge(Integer.valueOf(newValue));
            case "etc" -> user.setEtc(newValue);

            default -> throw new IllegalStateException("Unexpected value for field: " + fieldToUpdate);
        }
    }
}
```

this is fine, it's straightforward and each single field is updated calling its corresponding method.

there is a caveat I have not mentioned to start simple that is... we are in a full reactive context, which means the code always starts
and ends with a Uni and the flow should not be interrupted, or it will never subscribe therefor it won't execute...

what we would really need to update a single simple field is more like to be as below:

```java
class DemoUpdate2 {
    public Uni<Void> updateUser(final User user, final String fieldToUpdate, final String newValue) {

        return switch (fieldToUpdate) {
            case "firstName" -> {
                user.setFirstName(newValue);
                yield Uni.createFrom().voidItem();
            }
            case "lastName" -> {
                user.setLastName(newValue);
                yield Uni.createFrom().voidItem();
            }
            case "age" -> {
                user.setAge(Integer.valueOf(newValue));
                yield Uni.createFrom().voidItem();
            }
            case "etc" -> {
                user.setEtc(newValue);
                yield Uni.createFrom().voidItem();
            }

            default -> throw new IllegalStateException("Unexpected value for field: " + fieldToUpdate);
        };
    }
}
```

that's not very pretty, but it is necessary in our context.<br>

we could also try this:

```java
class DemoUpdate2 {
    public Uni<Void> updateUser(final User user, final String fieldToUpdate, final String newValue) {

        switch (fieldToUpdate) {
            case "firstName" -> user.setFirstName(newValue);
            case "lastName" -> user.setLastName(newValue);
            case "age" -> user.setAge(Integer.valueOf(newValue));
            case "etc" -> user.setEtc(newValue);

            default -> throw new IllegalStateException("Unexpected value for field: " + fieldToUpdate);
        }

        return Uni.createFrom().voidItem();
    }
}
```

which is a perfect solution, as it will set what we want, and at the end it keeps the `Uni` chain going.

> **showcase note:**
> this being a showcase, a few solutions such as this one, are explicitly coded as examples of tech and as skills demonstration.

for the sake of technology, let's explore a way to directly return the switch and have a method that will take care of updating anything we
want while also keeping the `Uni` flow.

We would need to we reuse the simple statement `yield Uni.createFrom().voidItem();`. There has to be a way to wrap the `setter` we want to
invoke in a method that will return always our `Uni`, but going down that path it would create so many methods for each setter.

## reflection implementation

it would be nice to have a way to pass any object and then tell java to invoke a given method with a given value... something like this...

```java
class FooReflection {
    public Uni<Void> invokeOnDemand(final User user, final String method, final String value) {
        //implementation omitted 
    }
}
```

the above is impossible to use directly as there is no direct way to call a method programmatically within the boundaries of a `User`
object. Each method has to be explicitly called.

there is a second obstacle... the method above expects a user and a string value. What if we want to update a card, a label, a board with
something different from a `String`. The idea of method above is of no use.

here is when this gets more complex and interesting... reflection and generics to the rescue.

> **teaching note:** <br>
> generics is explained on [its own article](/bmc-showcase-note-generics), we are going to focus on `Reflection` only here.

let's examine how can we achieve calling any setter with any simple value on any one of our dataTypes:

```java
public abstract class BasicPersistenceService<D, E> {

    @WithTransaction
    @SneakyThrows
    protected Uni<Void> updateInPlace(final E toUpdate, final MethodNames methods, final Object value) {
        Method m = toUpdate.getClass().getMethod(methods.getMethodName(), value.getClass());
        m.invoke(toUpdate, value);

        return Uni.createFrom().voidItem();
    }
}
```

the generic`E` is determined by each implementing class at construction time, it tells which entity type is in use for the particular
implementation, it's the entity type we are going to update.

looking at the method signature we have:

* the `E`ntity to update, it can be anything.
* the method we want to call.
* the new value we want to set that can also be any type.

when we examine the method's body we get into reflection territory.

```java
Method m=toUpdate.getClass().getMethod(methods.getMethodName(),value.getClass());
```

the above line of code does the following:

* it takes whatever entity to update we pass to it and acquires its class
* once we have what class we are dealing with, we want now to get a specific method from that class

the `getMethod()` method takes the method's name we want to call as 1st argument and an array (`...`) of data types as second. Between the 2
parameters we can define a single specific method to call from the given class.

* the `MethodNames` is only an enum that holds the possible method names we want to call.
* the value in itself can give us its class type by calling on the `getClass()` method.

that first line is in charging of getting the method we want to call to update our given entity. Let's see how we actually call it:

```java
m.invoke(toUpdate,value);
```

what we got is a `Method` itself, to invoke it on a particular object and not another we call it by passing to the invoking method, the
actual instantiated object and the value.<br>
The above code is the same as `user.setCallSign()` or `label.setName()` or `card.setDescription()` or `board.setIsFavorite()`.

the full method returns a void `Uni`.

## usage

to use this the update method from the very beginning now will look like this:

```java
public class UserService extends BasicPersistenceService<UserDto, UserEntity> {

    public Uni<Void> update(final UserEntity toUpdate, final String key, final String value) {
        return switch (key) {
            case "email" -> updateInPlace(toUpdate, SET_EMAIL, value.toLowerCase());
            case "callSign" -> updateInPlace(toUpdate, SET_CALL_SIGN, value);
            case "avatar" -> updateInPlace(toUpdate, SET_AVATAR, value);

            default -> throw new IllegalStateException("Unexpected value: " + key);
        };
    }
}
```

now instead of explicitly calling the method we pass it as a variable and let reflection do the rest.

## corollary

as you can see this is extremely powerful. We didn't need to code at all what method to call on which object for each update possibility.

the technique allowed us to have a simple update method to update every entity and the common code every update need.

### debugging

debugging is straightforward as a breakpoint reveals everything we are working with

check how it looks like on an IDE:

<figure>
    <a href="/assets/images/reflection_debugging.jpeg"><img src="/assets/images/reflection_debugging.jpeg" alt="reflection debugging"></a>
  	<figcaption>reflection debugging</figcaption>
</figure>

you can see how the generic `E`, resolves to the proper `CardEntity` type and the method is also correctly resolved as `SetDescription` with a
single parameter of type `String`