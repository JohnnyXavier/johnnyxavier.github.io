---
title: BMC Anvil's Caching Tech
excerpt: This note is about Caching strategies for the application
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_anvil
---

## Caching Technologies present and upcoming

as caching is a major feature, I focused on getting the application out without adding an external cache layer.<br>
to showcase tech and skills it is ok, but if the app evolves into something bigger a proper cache layer will be in order.

### present

currently I implemented [caffeine cache](https://github.com/ben-manes/caffeine) over some expensive ops to demonstrate a few uses and tricks
for startup-like applications.

### future

in the future, if the application grows above the showcase status, the cache implementation will be most likely an IMDG such as
[Hazelcast](https://hazelcast.org/imdg/) or red-hat's [Infinispan](https://infinispan.org/).

given that I am already on red-hat / Jboss ecosystem I might look into infinispan and learn something new, although I have used extensively
Hazelcast in past projects with great results.
