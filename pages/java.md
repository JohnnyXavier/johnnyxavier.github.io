---
layout: page
title: /java
categories: java
---
This is a collection of notes about a few things Java

<div class="latest">
    <h2>Latest notes on <strong>/{{ page.categories }}</strong></h2>
    {% for post in site.categories[page.categories] limit:4%}
    <a href="{{post.url}}">
        <div class="card">
            <div class="card-title">{{ post.title }}</div>
            <div class="card-body">
                <div class="card-img">
                    <div class="centerer"></div>
                    <img src="{{ site.baseurl }}/public/images/{{ post.image }}">
                </div>
                <div class="card-container">
                    <div class="card-extract">{{ post.extract}}</div>
                    <div class="card-author">by {{ post.author}}</div>
                    <div class="card-date">{{ post.date | date_to_string }}</div>
                </div>
            </div>
        </div>
    </a>
    {% endfor %}
</div>

***

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