---
title: BMC Anvil's Implementation Language
excerpt: This note is about the application's implementation language
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_anvil
---

## JAVA: the app's Implementation Language

as stated in the intro I decided to build a showcase application and the language of choice is Java, basically because after researching
other languages nothing was mind-blowing above and beyond, and it makes sense if I am going to be recruited in a jvm team to develop in the
main jvm language.

### about implementation languages in general

a thing to consider, is that besides the particular language, we also choose an ecosystem of frameworks and tools around said language.

on top of that not only we choose a language's ecosystem but also a "platform ecosystem" which comprises a metal or virtual provider,
caching, security, distribution networks, orchestrators, etc.

when considering this, the amount of times the implementation language itself is a bottleneck might not even be as relevant.

### some java pointers to consider

when extracting the language from the void into the real world, I considered:

* speed within a given framework
* raw speed
* frameworks
    * maintenance
    * maturity
    * stability
    * even maintainers themselves
* footprint
* time to market

### raw / frameworks performance

I touched on frameworks benchmarks at the [introduction article](bmc-anvil-tech-intro).<br>
at the top 10 we have js, java with small footprint frameworks, rust and c++ mainly.

given the good performance on benchmarks, the extended ecosystem, stability and maturity of both the language and frameworks and adding the
fast time to market and dev availability java became the implementation language of choice.

### frameworks

for web business apps java and its frameworks shine in this department. Not only extended frameworks like Hibernate, in mem caches, web
containers, DI, etc. are solid and well established, but internal utilities like streams, collections, concurrent collections, etc. are also
very solid and avoid the developers to hand code structures that have been around for a long time now.

### footprint

regarding footprint when compiling java + Quarkus + graalvm + native, startup times and memory / disk footprint are on par(ish) with more
recent languages like Go.

**note**: golang will compile almost instantly and compiling java natively to achieve small footprints will take quarkus minutes... many...

### time to market / productivity

I owe a small explanation on `"time to market"` """metric""".

this metric is as tricky as it can be as there is no way to properly measure it, unless an exact same dev knowing all possible tricks on
many languages, would implement an exact same feature in the best possible way for each language/framework. So I went for this imperfect
measure:

> given 2 languages that are on par on performance, stability, etc., which one takes the fewer lines of code to achieve a given new feature,
> and / or an update to an existing one.

for example in Quarkus (and similarly in spring boot), if you want to add cache to a service you just
add `@CacheResult(cacheName = "users-by-id")` and you can control the cache with external properties. This is a trivial example about how
easy is for even a junior developer to add a caching feature. If you need a db service to work inside a transaction you just annotate it
with `@WithTransaction`, which again is very easy and straightforward. If you want to code with DI in mind, you just annotate a
class with `@ApplicationScoped` and `@Inject` it where ever you need it (or init it in the constructor and make the "injected"
field `private final`).

devil's advocate on myself... saying "very" and "easy" and "trivial" is not tied to any metric, that is why I went with lines of code... or
in the above case a single annotation.

I understand it may not be fair, but it is at least a "neutral" way of measuring how much will it take to "add a cache" or "have your
query run in a transaction".

it is not only little code to achieve a lot of functionality but also requires little knowledge, which means less experienced developers
may achieve complex features, directly translating into the project's times and costs.
