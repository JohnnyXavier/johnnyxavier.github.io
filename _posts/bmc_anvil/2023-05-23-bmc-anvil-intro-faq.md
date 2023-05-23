---
title: BMC Anvil's FAQs
excerpt: This note group the FAQ for the software
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_anvil
---

BMC Anvil's FAQ
---

### For how long data is stored on the system?

The services are restarted **at least once a week** dropping all tables and wiping all data.<br>
There are no backups of the database so all data is deleted.<br>
As this is an evolving project I might push updates wiping the data more often.

---

### Will there be a commercial version of BMC Anvil?

The back-end is **Open Source Software** and Apache Licensed.<br>
The front-end / UI is a licensed version
of [Falcon React](https://themes.getbootstrap.com/product/falcon-admin-dashboard-webapp-template-react/)
template by [ThemeWagon](https://themewagon.com/)<br>

If there is enough traction and interest, a cloud based version will be developed.<br>
If the above becomes true, there will be a free self-hosted version available too

---

### What is the tech Stack Used on the Back-End?

I used [Java (OpenJDK)](https://openjdk.org/) as the implementation language and the following Technologies:

* OpenJDK **20** (and updating to latest as it gets out and proves compatible)
* PostgreSQL **15.x**
* Quarkus **3.x.x** (and updating to latest as it gets out)
    * quarkus-resteasy-reactive
    * quarkus-resteasy-reactive-jackson
    * quarkus-hibernate-reactive-panache
    * quarkus-reactive-pg-client
    * quarkus-hibernate-validator
    * quarkus-config-yaml
    * quarkus-arc
* utils:
    * lombok
    * maven
* [Robot avatars by Robohash](https://robohash.org/)

---

### What is the tech Stack Used on the Front-End / UI?

You can check the [Falcon Template](https://themes.getbootstrap.com/product/falcon-admin-dashboard-webapp-template-react/) web to learn
more!

---

### Where can I see / get the Back-End Code?

The code is public and hosted on **GitHub**. Go to [BMC flow](https://github.com/JohnnyXavier)'s repo, or just hit your terminal with:<br>

 ```shell
 git clone https://github.com/JohnnyXavier
 ```