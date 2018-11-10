---
layout: page
title: /Gnu_linux
---
This is a collection of notes about a few things GNU\linux


I will be collecting here some guides on installation troubleshooting and problem resolution
Most of this was found scattered over the net and put together here for reference.

Hope it helps you too

##### /installation_related_notes
<ul>
  {% for post in site.posts %}
        {% for tag in post.tags  %}
        {% if tag == "linux"  %}
    <li>
      <a href="{{ post.url }}">{{ post.date | date_to_string }} - {{ post.title }}</a>
    </li>
        {% endif %}
        {% endfor %}
  {% endfor %}
</ul>
