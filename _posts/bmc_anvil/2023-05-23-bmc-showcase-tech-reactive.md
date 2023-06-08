---
title: BMC Anvil's Reactive Tech
excerpt: This note is about Reactive Stack used on the application
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

## Reactive Technology used by the application

Quarkus stack allows to choose from classic and reactive versions of many frameworks and libraries, and I went full reactive.

the reason behind this was a little due to performance gains but in this case mostly to learn a new stack and ways of working.

all **{{ site.showcase.name }}** is done in a reactive way, every incorporation that can run in a reactive fashion was chosen over the "classic"
counterpart.

reactive stack powering the app comprises:

* reactive resteasy
* reactive resteasy jackson
* reactive hibernate panache
* reactive postgres client
* mutiny

this choice makes the code and some techniques a little different than if I had chosen spring with reactor, or even quarkus with classic
hibernate, and they will be showcased in time.

although it is early to say, my preliminary load tests show Quarkus reactive stack performing as expected and above, plus being non-blocking
I get simple fast requests to respond with low latency even with the system under heavy load.
