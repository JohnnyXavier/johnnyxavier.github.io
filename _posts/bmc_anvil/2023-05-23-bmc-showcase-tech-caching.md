---
title: BMC Anvil's Caching Tech
excerpt: This note is about Caching strategies for the application
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

## Caching Technologies present and upcoming

caching being a major feature, I focused on getting the application out without adding an external cache layer.<br>
to showcase tech and skills it is ok not to have a dedicated cache, but if the app evolves into something bigger a proper cache layer will
be in order.

### present

currently I implemented [caffeine cache](https://github.com/ben-manes/caffeine) over some expensive ops to demonstrate a few uses and tricks
for startup-company-like applications.

if you are curious on how to tackle bigger company apps that require proper caching, you can check
this [functional reactive pipeline note](/language-java-reactive-functional-pipelines/) that shows the core engine of an application.

the above note can be a preview of the evolution of {{ site.showcase.name }}.

### future

in the future, if the application grows above the showcase status, the cache implementation will be most likely an IMDG such as
[Hazelcast](https://hazelcast.org/imdg/) or red-hat's [Infinispan](https://infinispan.org/).

given that I am already on red-hat / Jboss ecosystem I might look into infinispan and learn something new, although I have used extensively
Hazelcast in past projects with great results.
