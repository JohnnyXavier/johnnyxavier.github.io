---
layout: page
title: /Java
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