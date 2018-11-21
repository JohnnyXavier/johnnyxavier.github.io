---
layout:         page
title:          /Programming
categories:     programming
---
<p class="message">
  Hi there!<br>
  My name is John Xavier, a tech enthousiast writting some notes on the web
</p>

{% include latest_notes.html %}

##### /design_patterns_related_notes
<ul>
    {% for post in site.categories[page.categories] %}
        {% for tag in post.tags  %}
            {% if tag contains "design_patterns" %}
                <li>
                  <a href="{{ post.url }}">{{ post.date | date_to_string }} - {{ post.title }} [{{post.tags |join: ", "}}]</a>
                </li>
            {% endif %}
        {% endfor %}
    {% endfor %}
</ul>

##### /data_structures_related_notes
<ul>
    {% for post in site.categories[page.categories] %}
        {% for tag in post.tags  %}
            {% if tag contains "data_structures" %}
                <li>
                  <a href="{{ post.url }}">{{ post.date | date_to_string }} - {{ post.title }} [{{post.tags |join: ", "}}]</a>
                </li>
            {% endif %}
        {% endfor %}
    {% endfor %}
</ul>


##### /algorithms_related_notes
<ul>
    {% for post in site.categories[page.categories] %}
        {% for tag in post.tags  %}
            {% if tag contains "algorithms" %}
                <li>
                  <a href="{{ post.url }}">{{ post.date | date_to_string }} - {{ post.title }} [{{post.tags |join: ", "}}]</a>
                </li>
            {% endif %}
        {% endfor %}
    {% endfor %}
</ul>

* Motorola metrowerks
* Mac G3 os 8.x / 9.x
* Commodore 128 (1985)
* Algorithms
* Design patterns
* PseudoCode
* Assembler
* intro to von neumann architecture
* virtualBox VM descriptor
* * maybe docker
