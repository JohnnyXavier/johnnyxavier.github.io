---
layout: post
title: filtering with streams in java
author: Johnny Xavier
image: java-logo.png
updated:
categories: java
extract: This note is about showing how to filter the elements of a <strong>List</strong> with J8 <strong>streams</strong> and J8 <strong>filters</strong> next to the classic conditional <strong>if</strong> 
tags: language
---

A very common everyday need is to look for a value among many.
To make this post easy, we are going to look for a string in a `List<String>`  in few different ways and build more realistic approaches in future posts with different Collections of various Objects

We are going to use the same filtering criteria and see what we can get from J8 streams vs a classic approach.

Defining the `List<String>` we will use across the code:

```java
List<String> strings = Arrays.asList("foo", "bar", "baz", "foobar", "raboof");
```
<br>

### *Classic `if` filtering*
The `if` statement works by testing a condition for `true`.

##### Filtering for single return value from many valid results AND a single criterion
we need a single value, therefore we will return as soon as something matches our single criterion
we don't need to loop the entire collection if we found our mark before the end.
```java
for (String value : strings) {
    if (string.contains("foo")) {
        return value;
    }
}
```
<br>

##### Filtering for a List of values AND a single criterion
we need all the values in the `collection` that match our criterion, so we will need to parse the entire `List` and add them
to a new container `List` as we find our matching values.
```java
List<String> resultList = new ArrayList<>();

for (String string : strings) {
    if (string.contains("foo")) {
        result.add(string);
    }
}

return resultList;
```
<br>

##### Filtering for a List of values AND a multiple criteria
the `if` statement starts getting a little crowded with conditions
```java
List<String> resultList = new ArrayList<>();

for (String string : strings) {
    if (string.contains("f") && !string.contains("bar") && string.contains("r")) {
        result.add(string);
    }
}

return resultList;
```

this is pretty much all the filtering that can be done with ***basic*** evaluations inside an `if`.<br>
We could add `OR` conditions, and combine them to make the `if` more complex but in the end it goes down to a truth evaluation.<br>
What if we need to transform our data and keep filtering the data?
<br>
One situation for example, could be selecting a subgroup of elements, processing them somehow and then checking which ones passed a certain threshold.
<br>
Thou difficult to imagine in the classic bookshop examples, let's imagine we produce steel with different alloys and we select from the lot a few bars that match certain criteria then want to keep the ones that better support torsion or tension or something else to decide which alloy to produce (why not, right?)
this means we select according to a few critera, then apply some function/s then reselect with different criteria. 
check this.
##### Filtering for a List of values **AND** a multiple criteria **AND** transforming data **And** refiltering
