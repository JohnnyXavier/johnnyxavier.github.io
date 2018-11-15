---
layout: page
title: /java
categories: java
---
This is a collection of notes about a few things Java

{% include latest_notes.html %}


##### /java_language_related_notes
<ul>
    {% for post in site.categories[page.categories] %}
        {% for tag in post.tags  %}
            {% if tag contains "language"  %}
                <li>
                  <a href="{{ post.url }}">{{ post.date | date_to_string }} - {{ post.title }} [{{post.tags |join: ", "}}]</a>
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

***
## /java_Frameworks_related_notes
##### /java_SpringFramework_related_notes
<ul>
    {% for post in site.categories[page.categories] %}
        {% for tag in post.tags  %}
            {% if tag contains "Spring"  %}
                <li>
                  <a href="{{ post.url }}">{{ post.date | date_to_string }} - {{ post.title }}</a>
                </li>
            {% endif %}
        {% endfor %}
    {% endfor %}
</ul>

* Spring
    * Spring boot
    * Spring data
        * multiple datasources
            * via anotations
            * via .properties
    * Spring social
    * Spring security
    * Spring MQ

***
##### /java_Akka_related_notes

* Akka
    * Akka FSM

##### /java_JBOSS_javaFrameworks_related_notes

*JBOSS
    * Hibernate