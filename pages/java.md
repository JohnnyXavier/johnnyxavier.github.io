---
layout: page
title: /java
---
This is a collection of notes about a few things Java

<ul>
  {% for post in site.posts %}
        {% for tag in post.tags  %}
        {% if tag == "java" and tag == "language"  %}
    <li>
      <a href="{{ post.url }}">{{ post.date | date_to_string }} - {{ post.title }}</a>
    </li>
        {% endif %}
        {% endfor %}
  {% endfor %}
</ul>

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
            * multiple datasources
                * via anotations
                * via .properties
        * Spring social
        * Spring security
        * Spring MQ
    * Akka
        * Akka FSM
    * Hibernate


###### Java Syntax highlight demo
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

