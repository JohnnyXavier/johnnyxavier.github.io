---
title: BMC Anvil's Authentication and Authorization
excerpt: This note is about the application Auth strategy
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

## Authentication and Authorization Strategies and Implementation

just as [caching](/bmc-showcase-tech-caching), authorization and authentication are key in any modern business application, and because of
that, the first drop of {{ site.showcase.name }} has none. On purpose.

integrating with an auth framework or provider is not at all the reason I built the app. The main reason this application exists is to
showcase possible answers to the many questions that arise in a tech interview and coding exercises and to learn and demonstrate exciting
technologies.

as stated elsewhere, if the application generates interest and grows, I will integrate a proper auth solution, and my weapon of choice
is [Keycloak](https://www.keycloak.org/).

not only it is also part of red-hat and integrates nicely with Quarkus, but I have written several providers for keycloak in recent jobs,
allowing us to augment the server to many needs.
