---
title: BMC Anvil's FAQs
excerpt: This note group the FAQ for the software
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

BMC Anvil's FAQ
---

### For how long data is stored on the system?

once deployed the services will be restarted **at least once a week** dropping all tables and wiping all data.<br>
there are no backups of the database so all data is deleted.<br>
as this is an evolving project I might push updates wiping the data more often.

---

### Will there be a commercial version of BMC Anvil?

the back-end is **Open Source Software** and Apache Licensed.<br>
the front-end / UI is a licensed version
of [Falcon React](https://themes.getbootstrap.com/product/falcon-admin-dashboard-webapp-template-react/)
template by [ThemeWagon](https://themewagon.com/)<br>

if there is enough traction and interest, a cloud based version will be developed.<br>
if the above becomes true, there will be a free self-hosted version available too

---

### What is the tech Stack Used on the Back-End?

I used [Java (OpenJDK)](https://openjdk.org/) as the implementation language and the following Technologies:

* OpenJDK **{{ site.showcase.java-version }}** (and updating to latest as it gets out and proves compatible, **21ea**
  is [currently not](/bmc-showcase-note-utils-lombok/#java-21-and-lombok-as-of-this-writing))
* PostgreSQL **{{ site.showcase.postgres-version }}**
* Quarkus **{{ site.showcase.quarkus-version }}** ([and updating to latest as it gets out](https://quarkus.io/blog/tag/release/))
    * quarkus-resteasy-reactive
    * quarkus-resteasy-reactive-jackson
    * quarkus-hibernate-reactive-panache
    * quarkus-hibernate-validator
    * quarkus-reactive-pg-client
    * quarkus-config-yaml
    * quarkus-arc
* Hibernate reactive **{{ site.showcase.hibernate-version }}** (via quarkus dep)
* utils:
    * lombok
    * maven
* [Robot avatars by Robohash](https://robohash.org/)

---

### What is the tech Stack Used on the Front-End / UI?

you can check the [Falcon Template](https://themes.getbootstrap.com/product/falcon-admin-dashboard-webapp-template-react/) web to learn
more!

---

### Where can I see / get the Back-End Code?

the code is public and hosted on **GitHub**.<br>
Go to [{{ site.showcase.name }}]({{ site.showcase.repo-url }})'s repo, or just hit your terminal with:<br>

 ```shell
 git clone https://github.com/JohnnyXavier/bare-metal-flow
 ```