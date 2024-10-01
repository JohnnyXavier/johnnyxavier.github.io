---
title: Java Generics
excerpt: This note is about java generics
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

this note is about java generics

---

## intro to generics

generics are a way to use types as parameters in classes, interfaces and methods. If we take a method for example, it will receive a type
input.

[formal definition and tutorial from Oracle](https://docs.oracle.com/javase/tutorial/java/generics/index.html)

you can read from **Oracle** above, or follow though these smaller notes.<br>
I will not cover everything on generics, but will cover enough to illustrate how generics power {{ site.showcase.name }}.

### very basics: a generic method

Let's illustrate:

```java
class GenericExample {
    public <E> String testX(E myGenericType) {
        return myGenericType.getClass().getSimpleName();
    }
}
```

the simple method above takes some type and returns which class it is. Note how we also declare the `E` between angle brackets before the
return type. This is how we tell the compiler that the `E` type is "_owned_" by the method.

If we call that method with a `String` it will return "String".

```java
class GenericExample {
    void callTestX() {
        String demo = "";
        System.out.println(testX(demo));
    }
}
```

```shell
$ String
```

if we call it with an `Integer`...

```java
class GenericExample {
    void callTestX() {
        Integer demo = 1;
        System.out.println(testX(demo));
    }
}
```

```shell
$ Integer
```

### basics: a generic class

the example above shows how the method can receive "anything". But generics are not anything, in fact they are proper types that have strong
compile time checks. Because they have type we can avoid casts and when dealing with methods we can rest assured they are type safe.

generics can be used as inputs, as well as return types and class parameters. Let's see what a class parameter means.

```java
class GenericCoord<T> {

    private T x;
    private T y;

    // getters and setters
    public T getX() {
        return x;
    }

    public T getY() {
        return y;
    }

    public void setX(final T x) {
        this.x = x;
    }

    public void setY(final T y) {
        this.y = y;
    }

}
```

the above class can hold a pair of coordinates. Note how a single parameter `T`, is used as the type for our coordinates in X and Y.
Remember that a generic is type safe, so if T is a `Double` then both x and y are `Double`

I mentioned on the previous section that a method with generic parameters needed to declare the generic `T` between angled brackets before
the return type, yet here on the setter we are not declaring it. That's because the setter method does not "own" the generic type, and it is
referring to the one declared by the class itself! You can of course add a Type owned only by the method in addition to the ones from the
class like this for example:<br>
`public <U> void setX(final T x, final U other){}`

Let's see how we instantiate a class like the above.

```java
class DemoGeneric {
    GenericCoord<Integer> intCoord    = new GenericCoord<>();
    GenericCoord<Long>    longCoord   = new GenericCoord<>();
    GenericCoord<Double>  doubleCoord = new GenericCoord<>();    
    
    intCoord.setX(20); //OK
    longCoord.setX(20L); //OK
    doubleCoord.setX("fail"); //ERROR

    Integer intX = intCoord.getX(); //OK

}
```

you just need to specify which type your generic class will work with, and you have type safety on your code. When we try to set a `String`
for a class instantiated with a `Double`, the compiler will complain with the following error:
> `java: incompatible types: java.lang.String cannot be converted to java.lang.Double`

let's make it easier to understand.<br>
when we write `GenericCoord<Double> intCoord = new GenericCoord<>();` it's as if the `GenericCoord` class would have been written like so:

```java
class GenericCoord {

    private Double x;
    private Double y;

    // getters and setters


    public void setY(final Double y) {
        this.y = y;
    }

    public void setX(final Double x) {
        this.x = x;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }
}
```

it is as if we replaced the generic `T` with the proper `Double` type we made explicit when we declared the variable, therefore when we then
asked to set x with a `String` the compiler came to the rescue!

### mid-difficulty: bounds

the `GenericCoord` is a fine example of the need for bounds. We want to be able to describe coordinates with a pair of numerical values, not
with `String` or anything else. But a `T` can be anything... we need to put some sort of bounds to what our generic construct can accept.
Let's see an example:

```java
class GenericCoord<T extends Number> {

    private T x;
    private T y;

    // getters and setters
    public T getX() {
        return x;
    }

    public T getY() {
        return y;
    }

    public void setX(final T x) {
        this.x = x;
    }

    public void setY(final T y) {
        this.y = y;
    }

}
```

what we are now telling the compiler is that the generic coordinates can be only types that extend `Number`.
ie: `Integer`, `Double`, `Long`, etc. The below code will generate a compile time error:

```java
GenericCoord<String>  stringCoord=new GenericCoord<>();
```

given that `String` does not extend `Number`, it is not a valid type for our `GenericCoord` class and the compiler will emit the following
compile time error:
> java: type argument java.lang.String is not within bounds of type-variable T
> <br><br>
> java: incompatible types: cannot infer type arguments for com.bmc.GenericCoord<><br>
> reason: inference variable T has incompatible bounds<br>
> equality constraints: java.lang.String<br>
> upper bounds: java.lang.Number<br>

as you can see we are given a descriptive error telling us that a `String` is not a `Number` as upper bound. An upper bound violation reads
like the following:

> `String` does not extend `Number`

in a general case, there is an upper bound violation when the type passed does not extend the type constrain we declared.

a lower bound is the opposite but applies to type arguments which we are not going to use. It will work like so:

```java
class DemoGeneric {
    public void lowerBound(List<? super Integer> input) {
        //code here
    }
}
```

the above sets `Integer` and all it's super classes as the accepted type, ergo, `Integer`, `Number`, `Object`.

## generics in the application

### intro

**{{ site.showcase.name }}** relies heavily on generics.

combined with abstract classes, generics are a powerhouse of reusability while enforcing type safety.

we've seen already in the [OOP - inheritance note](/bmc-showcase-note-oop-inheritance) how extending classes allows for code reusability,
with generics we can take that technique to another level, it is not only reusability in a *lines of code sense*, is reusing concepts too.

a concept can be **"persisting data"**, or **"queuing a message"**, or **"reacting to a given event"**, or...

let's use the **"persisting data"** concept for this article...

in **Quarkus Panache** or **SpringBoot Data**, both using `repositories`, each **Entity** has its own repository that inherits the basic
operations fromm the framework, such as finding data by id, deleting, persisting, updating, etc... All those common operations are reused
via implementing a given framework interface.

say, as it is the in **{{ site.showcase.name }}**, that we have many Entities, and we want to implement a delete via id operation on all
their corresponding services. In that scenario we will need a service for each Entity that will call the corresponding `delete` method on
the corresponding repository. Same procedure for finding an Entity by its **id**, or for finding all entities, or all entities paged...

well... it feels like we are doing the exact same thing on different **X** entities, and expecting the corresponding **Y** result...

### a 1st example from `Panache`

it would be really convenient to think of the persistence of data as a concept, and reuse it somehow. We know that each `panache` repository
knows how to persist data to the database just by getting 2 things the entity type, and the id type. For example, it would know how to
persist a `Card` that has a `UUID` as pk.

a `Panache` repository that will know how to handle the above data will look like this:

```java
public class CardRepository implements PanacheRepositoryBase<CardRepository, UUID> {
}
```

### usage in **{{ site.showcase.name }}**

how to use the types as parameters discussed before to make something of that technique then...

I insisted of thinking the persistence commonality as a "concept", if we check
the [2nd definition of concept](https://www.merriam-webster.com/dictionary/concept), it reads as follows:

> 2 : an abstract or **generic** idea generalized from particular instances

is it not the perfect fit for all this?! Feels written by an OOP developer!

