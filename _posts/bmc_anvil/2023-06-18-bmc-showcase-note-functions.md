---
title: Java Functions
excerpt: This note is about using java functions
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

This note is about using functions in a java

---

## intro: functional programming

there is a [formal definition for functional programing](https://en.wikipedia.org/wiki/Functional_programming)
> ***In computer science, functional programming is a programming paradigm where programs are constructed by applying and composing
> functions. It is a declarative programming paradigm in which function definitions are trees of expressions that map values to other
> values, rather than a sequence of imperative statements which update the running state of the program.***

but I am taking the liberty of making a personal interpretation that I already mentioned
in [this article about functional reactive pipelines](/language-java-reactive-functional-pipelines/#what-i-understand-as-functional)

> thou the above is ok, I understand functional programming like this: ***it is programing by delegation***

## a simple `Function`

in {{ site.showcase.name }} most of the code is already functional-like given that I am using
the [reactive paradigm](https://smallrye.io/smallrye-mutiny/2.2.0/reference/what-is-reactive-programming/), but I created a small utility on
the `Pageable` class to illustrate how the `Function` interface can be used by ourselves.

this example is a simple example, and I'll build a few more complex ones to illustrate chaining and how `Functions` can be used on `Streams`
or methods

let's examine a Function:

```java
public class Pageable {
    private final ToIntFunction<Integer> checkPageSize = (pageSize) -> max(min(pageSize, MAX_PAGE_SIZE), MIN_PAGE_SIZE);

}
```

a `Function` in java is itself a type that represents a function, and it comprises one argument and one result. In our case,
the `ToIntFunction` is a specialization that is telling us that the return type is an int, so we don't need to specify the return type.

in this simple example the `Function` checkPageSize receives a page size as an Integer and returns a page size between the minimum or
maximum configured. If the page size is between boundaries, it returns the page size or the corresponding max / min when over /
underflowing.

let's examine how it is used:

```java
public class Pageable {
    public Pageable(final String sortBy, final String sortDir, final Integer pageIx, final Integer pageSize) {
        // omitted code here
        this.page = Optional.ofNullable(pageIx)
                .map(index -> Page.of(max(index, 0), checkPageSize.applyAsInt(pageSize)))
                .orElseGet(() -> Page.ofSize(pageSize));
    }
}
```

all `Function` have an `apply()` method and in this case an `applyAsInt()`, just like a method, you pass the corresponding argument and
that's it.

## outro

in the above case using a `Function` instead of a method achieves little. I'm going to add more complex and useful examples of function
shortly.