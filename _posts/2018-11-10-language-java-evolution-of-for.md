---
layout: post
title: Evolution of the for loop in java
author: Johnny Xavier
image: java-logo.png
updated:
categories: java
extract: This note is about the different possibilities to write a for loop in java across time and different versions 
tags: java language
---

Lets show the different alternatives of the for  loop in java applied to a List<Strings>

Defining the `List<String>` we will use across the code:

```java
List<String> names = Arrays.asList("foo", "bar", "baz", "foobar", "raboof");
```


### *Standard  for  loop*
this is the `for` loop found in different programming languages

```java
for (int i = 0; i < names.size(); i++) {
            doSomething(names.get(i));
        }
```

### *Iterator for  loop*
slightly variation of the standard `for` loop, note how we keep all 3 parts of the standard `for` loop but the break condition uses the `i.hasNext()` and the increment is in the loop’s body as `i.next()`

```java
for (Iterator<String> i = names.iterator(); i.hasNext(); ) {
            doSomething(i.next());
            }
```


### *added in J5, forEach  loop*
note how the above `Iterator` ceremony has been replaced by this new approach
although not a silver bullet it is a very convenient way of forward traversing this `Collection`<br>
[https://docs.oracle.com/javase/1.5.0/docs/guide/language/foreach.html](https://docs.oracle.com/javase/1.5.0/docs/guide/language/foreach.html)
```java
for (String name : names) {
            doSomething(name);
        }
```

### *added in J8, forEach  Loop associated to the Collection  with lambda expression*
J8 added the forEach to `Collections` removing a bit more of the ceremony and clutter from before.
here with a lambda expression that takes 1 `List` item as an argument and passes it to a function to process.
```java
names.forEach(name -> doSomething(name));
```


### *added in J8, forEach Loop associated to the Collection with method reference*
same as above but we replaced the lambda expression with a method reference
this removes all redundant mentions to arguments to be processed by a given function.
```java
names.forEach(this::doSomething);
```

### *as a bonus, an oldie not very common to see... the `ListIterator<T>` added in J2 is close to the `iterator` with the added possibility of backwards traversing a list*
it is basically used as an `iterator` with the difference that when you want to traverse backwards a `List` you specify the element on the `List` you want use as the point from where you want to move backwards.
We are going to start from the very end of the `List` in this example to show reverse traversing.

forward
```java
// traversing forward
// same form as the Iterator  but we use the ListIterator interface instead
for (ListIterator<String> listIterator = names.listIterator(); listIterator.hasNext(); ) {
    doSomething(listIterator.next());
}
```

backwards
```java
// traversing backwards
// we specify here the size of the list to position the iterator cursor at the very end of the list
for (ListIterator<String> listIterator = names.listIterator(names.size()); listIterator.hasPrevious(); ) {
    doSomething(listIterator.previous());
}
```