---
title: Introduction
excerpt: This note is about what to expect of the Application and the accompanying articles
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_anvil
---

## why the articles and the application

stated in many places already, I will just recap:

> as a software dev / architect, applying for jobs always has a technical interview leg. Among the many styles of interviews there is a
> mandatory technical challenge of some sort.
>
> {{ site.showcase.name }} aims to save the time recruiters and tech teams spend trying to find the correct candidate for their needs.
>
> this app and the accompanying articles are extensive enough that any tech team can evaluate if the skills showcased here are what they
> were
> looking for.

## how was {{ site.showcase.name }} done:

I set myself a few constrains for the first drop:

* it has to be around 10K lines of code:
    * the idea is to showcase a working segment of a real life-like application
* it has to be achieved in a certain amount of time - 2 to 3 working weeks for everything BE (on a 9 to 17 schedule):
    * this includes all planing, investigation, reading, and coding
    * applications rarely are the perfect execution of the perfect plan presented for the perfect uni thesis
    * by giving myself time constrains it forced me to often make lesser of two evil choices
* it has to focus on implementing application code and not application's integration
    * this means that, on purpose, no auth nor external cache layer is to be implemented as part of the showcase
    * architectural choices are considered separately outside the code base
        * having worked as an architect for a few years, it was very tempting to start wiring the application with many services and
          breaking it down to smaller microservices, and creating a pulumi deployment framework, and... and...
        * I refrained myself to go down the arch path and instead focused on a startup-company-like application.

## how this showcase will evolve

I will be adding notes little by little until most or all the typical questions on interviews are covered.

if this showcase has enough traction, I'll add features as planned and requested and publish the whole as FOSS if possible (there will be
details on the FE licence to solve)

## how this showcase can be used

### as a real app (in the future)

this is a real application that can be continued by yourself and used it to you convenience

## as a guide / teaching app

this whole application, the notes and any fragment of the back en code, **- and the back end code only -**, can be used in your own
applications and as snippets anywhere you like, it's [currently released under apache](2023-05-23-bmc-anvil-intro-licenses) and even if I
choose another license in the future it will most likely be another OSS kind of license.

## a note on perfection and coherence on the app

### perfection

creating a real application under time constraint, instead of small snippets can play against myself regarding technical evaluations, as
each one of us has a style and preferences to tackle certain problems. You may find yourself thinking...

* I wouldn't have a user with so many properties...
* I would have used liquibase...
* I would try to avoid reflection...
* I would normalize the database even more...
* I would break this down even further...
* log4j2 is faster than...
* I don't like lombok...
* ...

and it is fine, we all have a way of doing things and on a real app showcase you will find room for improvement and change.

### code coherence

you will find a same problem solved in more than a single way... this is on purpose.

for example:

* I chose a few services to go full orm, others to add named queries, others to use native queries...
* I chose to use an annotation here, but call on `Mutiny SessionFactory` there...
* I chose to add/remove "many to many records" fully manually here and via orm entities there...

it is the only way to showcase those different approaches by using a single application for it.

I tried to use the different approaches where they better fit, or to group them on a given service, but as this is a showcase as much as a
teaching app, there may be features that seem unlikely to be used on a same class here and there.<br>
when it is the case it will be accordingly flagged.

There will also be, thou very few to avoid impacting the app, approaches that play against performance with an accompanying note and a fix
on a subsequent method to showcase how to better approach some tasks or how to get out of a few troubles.

