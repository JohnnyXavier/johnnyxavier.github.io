---
layout: post
title: filtering with streams in java
author: Johnny Xavier
image: java-logo.png
updated:
categories: java
extract: This note is about showing how to filter the elements of a <strong>List</strong> with J8 <strong>streams</strong> and J8 <strong>filters</strong> next to the classic conditional <strong>if</strong> 
tags: java language
---

A very common everyday need is to look for a value among many.
To make this post easy, we are going to look for a string in a `List<String>`  in few different ways and build more realistic approaches in future posts with different Collections of various Objects

We are going to use the same filtering criteria and see what we can get from J8 streams vs a classic approach.

Defining the `List<String>` we will use across the code:

```java
List<String> strings = Arrays.asList("foo", "bar", "baz", "foobar", "raboof");
```

### *Classic `if` filtering*
The `if` statement works by testing a condition.

##### Filtering for single return value from many valid results AND a single criterion
```java
//we need a single value, therefore we will return
//as soon as something matches our single criterion

for (String string : strings) {
    if (string.contains("foo")) {
        return string;
    }
}
```

##### Filtering for a List of values AND a single criterion
```java
List<String> result = new ArrayList<>();

for (String string : strings) {
    if (string.contains("foo")) {
        result.add(string);
    }
}

return result;
```

##### Filtering for a List of values AND a multiple criteria
```java
// the if statement starts getting a little crowded with conditions

List<String> result = new ArrayList<>();

for (String string : strings) {
    if (string.contains("f") && !string.contains("bar")
        && string.contains("r")) {
        result.add(string);
    }
}

return result;
```