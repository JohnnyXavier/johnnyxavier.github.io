---
title: BMC Anvil's Quarkus Stack
excerpt: This note is about Quarkus frameworks and ecosystem used on the application
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_anvil
---

I went through an intro with some numbers and charts, then some reasons to pick a java ecosystem. Good performance was a reason...
stability, maturity, libraries, etc. were others.

## And one day java was not bloated...

I've been a spring and springboot dev at work and although a joy to work with, and being able to create a simple REST-CRUD webservice in no
time, it gets very heavy when starting-up and deploying and even during development it can be a challenge.

during an experiment with new tech I tried [Quarkus on small apps](/arch-design-aws-serverless-java-simple); the kind of applications one
would do with python or js to run on aws lambdas, the one place where spring is quasi de-facto banned due to startup times, and memory
footprint.

I found Quarkus to have a fantastic set of tools no shy to spring and a few advantages too. When compiling an app in native mode, startup
times and memo footprint are almost on par with js and golang. Quarkus made a java app running like no other java app.

### So Quarkus

so [Quarkus](https://quarkus.io/) then...

these are the features that made me implement the app using Quarkus instead of other frameworks or mixing matching libraries for the sake of
the highest benchmark performance:

* small footprint even on jvm mode
* native ready
* development tools
* true live reload on change while developing
* seamless jboss / red-hat integration
* reactive stack
    * resteasy
    * hibernate react2
    * mutiny(flow API)
* huge libraries ecosystem support

### easy to overlook features: dev tools

Quarkus dev tools are at another level.

* the live reload on {{ site.showcase.name }} takes, with all libraries and a full `drop-create` of the database, just 2 seconds on the
  clock... and we are talking around 10K lines of code and 50 tables with seeded data. Changing the smallest thing on an entity or method is
  completely painless.
* the dev UI allows to change cache settings, reset DBs, change pools, check endpoints, browse scores all on they fly is near mind-blowing.

I have not seen yet something like this except on hazelcast's settings and control panel.

the importance of these features is again... `time to market`, the easier the system works for you, the easier to get things done faster. It
makes also onboarding and training new devs easier and faster.

the Quarkus version used by {{ site.showcase.name }} is {{ site.showcase.quarkus-version }}, but I usually update it as fast as they update.

### easy to overlook features: support

maybe because it is under red-hat's + JBOSS umbrella, integration with other tools from the group is seamless and time to fix bug reports is
very fast, and you can actively see creators of core java tools as hibernate getting involved.
