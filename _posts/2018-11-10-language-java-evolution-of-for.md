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
List names = Arrays.asList("foo","bar","baz");
```


### *Standard  for  loop*
this is the for loop found in different programming languages

```java
for (int i = 0; i < names.size(); i++) {
            doSomething(names.get(i));
        }
```

### *Iterator for  loop*
slightly variation of the standard for loop, note how we keep all 3 parts of the standard for  loop but the break condition uses the i.hasNext()  and the increment is in the loop’s body as i.next()

```java
for (Iterator<String> i = names.iterator(); i.hasNext(); )
            doSomething(i.next());
```


### *added in J5, forEach  loop*
note how the above iterator ceremony has been replaced by this new approach
although not a silver bullet it is a very convenient way of forward traversing this Collection<br>
[https://docs.oracle.com/javase/1.5.0/docs/guide/language/foreach.html](https://docs.oracle.com/javase/1.5.0/docs/guide/language/foreach.html)
```java
for (String name : names) {
            doSomething(name);
        }
```


### *added in J8, forEach  Loop associated to the Collection  with lambda expression*
```java
names.forEach(name -> doSomething(name));
```


### *added in J8, forEach  Loop associated to the Collection  with method reference*
```java
names.forEach(this::doSomething);
```
