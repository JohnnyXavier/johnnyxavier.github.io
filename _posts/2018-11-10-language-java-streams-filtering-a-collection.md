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

One situation for example, could be selecting a subgroup of elements, processing them somehow and then checking which ones passed a certain threshold.

Thou difficult to imagine in the classic bookshop examples, let's imagine we produce different steel alloys and we select from the lot a few bars that match certain criteria then want to keep the ones that better support torsion or tension or something else to decide which alloy to produce for a specific need (why not, right?).

this means we select according to a few criteria, then apply some function/s then reselect with different criteria. 

check this as a more elaborate example
##### let's create a SteelBar class that will have a few fields to play with.
```java
import lombok.Data;

@Data
public class SteelBar {
    private String alloyBatch;
    private Integer carbonPercent;
    private Integer ironPercent;
    private Integer otherMetalsPercent;
    private Integer otherNonMetalsPercent;
    private Integer Strength;
}
```
##### let's imagine a simple way to initialize many of those SteelBars with some data.
```java
package io.ioforge;

import io.ioforge.elements.SteelBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoteAboutIfAndFilter {
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        List<SteelBar> steelBars = generateNewBatchOfSteelBars(10);
    }

    private static List<SteelBar> generateNewBatchOfSteelBars(int bars) {
        List<SteelBar> steelBars = new ArrayList<>();

        for (int i = 0; i < bars; i++) {
            Integer remainingPercentage = 100;
            SteelBar steelBar = new SteelBar();
            steelBar.setAlloyBatch("batch_" + i);
            steelBar.setOtherMetalsPercent(RANDOM.nextInt(3));
            steelBar.setOtherNonMetalsPercent(RANDOM.nextInt(3));
            steelBar.setCarbonPercent(RANDOM.nextInt(4));
            steelBar.setStrength(RANDOM.nextInt(11) + 90);  //this will set a random strength between 90 and 100
            remainingPercentage -= steelBar.getOtherMetalsPercent();
            remainingPercentage -= steelBar.getOtherNonMetalsPercent();
            remainingPercentage -= steelBar.getCarbonPercent();

            steelBar.setIronPercent(remainingPercentage);

            steelBars.add(steelBar);
        }
        return steelBars;
    }

}
```
so far not much fuzz, just random numbers on our SteelBars
##### Filtering for a List of values **AND** a multiple criteria **AND** transforming data **And** refiltering
now let's see how we can filter this SteelBars
<br>
say we want the bars that have between 1% and 3% CarbonPercent and to those bars we will apply some transformation, like reheating them, flex them etc...<br>
we want in the end the ones that at the end will show a strength higher than 90%.
```java
package io.ioforge;

import io.ioforge.elements.SteelBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

public class NoteAboutIfAndFilter {
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        //let's init
        List<SteelBar> steelBars = generateNewBatchOfSteelBars(10);
        steelBars.forEach(System.out::println);

        // let's do what we want to actually do in a traditional way
        filterWithIf(steelBars);
    }

    private static void filterWithIf(List<SteelBar> steelBars) {
        List<SteelBar> theGoodBarsIF = new ArrayList<>();

        for (SteelBar steelBar : steelBars) {
            if (steelBar.getCarbonPercent() >= 1 && steelBar.getCarbonPercent() <= 3) {
                stressSteelBar(steelBar);
                if (steelBar.getStrength() >= 90) {
                    theGoodBarsIF.add(steelBar);
                }
            }
        }

        System.out.println();
        theGoodBarsIF.forEach(System.out::println);
    }
}
```
now that's starting to get crowded and a bit hard to follow...<br>
what is evident to write today will be a mystery to read in a few months time.

Real world examples will involve maybe sending a subset of data to a validation engine for example and returning just the ones your service modified with a certain tag or even return completely different.

Real world scenarios are far from ideal and you can send tiny pieces of data and get massive jsons in response.

back to our example:

This looks like a chaining of operations, or a flow of operations. A few are related, others not so much.

it comes down to the following.
* filter some data
* transform that data, or map a property of the data to something else
* filter the transformed data
* return the result

### *J8 filtering*
J8 came just with a tool to do this.
`streams`
in this note we will just check out how can we replace the above code with some features offered by `Streams` but the possibilities are way  beyond this simple filtering and processing 

let's check out then how to refactor the above code into J8 `Streams`

```java
package io.ioforge;

import io.ioforge.elements.SteelBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

public class NoteAboutIfAndFilter {
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        //let's init
        List<SteelBar> steelBars = generateNewBatchOfSteelBars(10);
        steelBars.forEach(System.out::println);

        // let's do what we want to actually do in a traditional way
        // filterWithIf(steelBars);

        // let's do what we want to actually do in a J8 way
        filterWithStream(steelBars);
    }

    private static void filterWithStream(List<SteelBar> steelBars) {
        List<SteelBar> theGoodBarsStream = steelBars.stream()
                .filter(steelBar -> steelBar.getCarbonPercent() >= 1)
                .filter(steelBar -> steelBar.getCarbonPercent() <= 3)
                .map(steelBar -> stressSteelBar(steelBar))
                .filter(steelBar -> steelBar.getStrength() >= 90)
                .collect(toList());

        System.out.println();
        theGoodBarsStream.forEach(System.out::println);
    }
}
```
alright!

so we mainly gained in clarity and extensibility. What you write now stays evident in the months to come.

the streams allow us to chain the filtering with the processing and more filtering seamlessly without clutter and keeping everything clear to read.
as speaker Venkat S. says in many of his talks, **we got rid of the ceremony** around filtering and processing.

if you need to introduce another process after you got the SteelBars with strength >= 90 it will just adding another `.map()`<br>
if you need to add another filter, aka another `if` somewhere, just add another `.filter()`
you can select an manipulate data without getting deeper into indentations without loosing the original focus of the task.

##### Debugging bonus
when dealing with the traditional way we saw above, if something is failing to check our conditions or we don't understand why a process is not giving the expected results and it is nested deep in a few `if`s we will have to follow the debugger steps by step to check where the culprit is.

IntelliJ IDEA incorporated a tool to do just that with streams. Given a stream it will evaluate the chain of operations and visually show how each one is transforming the data after each step.

check this screenshot made with data from our SteelBars
you can see how each step is represented with it's resulting state.

![Strean Trace]({{ site.baseurl }}/public/images/Stream Trace_001.png "Strean Trace ")
