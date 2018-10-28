---
layout: page
title: /java
---
<p class="message">
Testing some darcula highlight
</p>

* Language
    * evolution of For
    * vanilla vs spring / hibernate sites and examples
        * JDBC / postgreSQL queries
        * MVC no spring
        * MVC no spring
* frameworks
    * Spring
        * Spring boot
        * Spring data
        * Spring social
        * Spring security
        * Spring MQ
    * Akka
        * Akka FSM
    * Hibernate


<p class="code-title">Java Syntax highlight demo</p>

``` java
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final List<String> strings = Arrays.asList("hello", "World");

    public static void main(String[] args) {

        for (String string : strings) {
            System.out.println(string);
        }

        strings.forEach(System.out::println);
    }
    
    private static anotherfunction(List<String> messages){

        for (int i = 0; i < messages.size; i++) {

            System.out.println(messages[i]);
        }
    }
}
```

