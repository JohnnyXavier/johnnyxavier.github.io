---
layout: page
title: /Java_Spring_Data
categories: java.spring.data
---
This is a collection of notes about a few things Java After Winter

Thou [**`Hibernate`**](http://hibernate.org/) is a beast on it's own, until there is a critical mass of notes about it, I will keep them here under a Spring Data umbrella.

{% include latest_notes.html %}

##### /spring_data_hibernate notes
<ul>
    {% for post in site.categories[page.categories] %}
        {% for tag in post.tags  %}
            {% if tag contains "hibernate"  %}
                <li>
                  <a href="{{ post.url }}">{{ post.date | date_to_string }} - {{ post.title }} [{{post.tags |join: ", "}}]</a>
                </li>
            {% endif %}
        {% endfor %}
    {% endfor %}
</ul>