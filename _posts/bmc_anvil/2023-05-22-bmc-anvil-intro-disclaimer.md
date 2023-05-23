---
title: Purpose of BMC Anvil
excerpt: This note is about why create an application as a showcase of technology and skills
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_anvil
---

You will find in these introductory notes, the 'why' of this Software and what to look for into the future.

---

### Purpose of this Software

As a **back-end *Software Developer / Architect***, I am required to demonstrate my skills on hiring interviews.<br>
The above usually entails one or many forms of evaluation depending the on hiring process.

Popular forms of technical evaluation include:

* <code>General Language and Frameworks knowledge</code>
* <code>Specific CS questions and challenges</code>
* <code>Live coding</code>
* <code>Challenges on coding websites</code>
* <code>In person coding challenges</code>
* <code>More elaborated @Home coding challenges</code>

With that in mind I started building some small architectures and software samples to kickstart the
conversations and be able to show my trade. <br />A code and architecture portfolio of sorts...

You can find more of my code and arch by navigating the different parts of this site

The purpose of this software is `(or was...)` to showcase my abilities as a<br>
**back-end Software Developer / Architect**

---

### Evolution of this Software

Evolution from the original purpose

**As stated above on the purpose section, this project begun as a means to demonstrate my technical skills.**

So... The original idea was to populate my site with articles about several day-to-day problems and challenges we face when developing
business applications.
I had a few topics in mind that would kickstart a comprehensive portfolio of skills.

I started to think about what could be a good example for mapping complex entities, or how to return efficient projections of those
entities, or how to use reactive hibernate on cascading dependent ops...
More and more the usual simple "video club store" examples started to fall short.

The web is filled with such examples where an entity has 1 or 2 relations and 2 fields...
or the database has 2 tables...
or a service that returns a reactive "hello world"...
or a projection example that explodes the minute you want data from an inner entity...
or...

That's when I set sail to create a "simple" app, but one that would be real enough to be able to create a portfolio of articles that would
tackle and explain real life problems.

Thou I planned for a simple kanban board at the very beginning, I thought of adding a few features to showcase different techs...
Those few features became more than a few, and more complex and rounded, the simple board started to have statistics, and users and
scheduling...
The scheduling started to grow with shrinkages and multiple accounts and metrics...

By the time I had above 100 endpoints, several services and a completely modeled DB, the idea of adding a front end started forming...

Given that I am mainly **not** a front-end developer, I decided to buy an admin template and connect the back-end to it myself to see what I
had
so far.

**This is the result of that original idea of creating a few articles for a personal skills portfolio.**

**An application that is fully usable, and still a fun showcase of thousands of lines of technologies, techniques and unavoidable
decisions that must be made when starting from zero.**

---

### Software State

Current state of the software

So how far along am I with the system...?<br>
I'll review here features in place and missing ones

Currently, the application core can:

* CRUD users, accounts, projects, boards, cards
* Supports kanban, sprints and service-desk boards
* Displays statistics at various levels
* Schedules users on projects
* Has operative warnings and checks on excess of work
* Cards support comments, multiple assignees, watchers, dates, tasks, labels, status, types...
* An amazing front-end / ui thanks to the bootstrap-react template by ThemeWagon

Currently, the application is missing:

* **Proper Auth**: For the moment the focus is on the app itself and auth will eventually come down to:
    * a pluggable OIDC provider such as Keycloak (most likely)
    * internal simple jwt security (probably as a side preject, but not likely)
    * Internal elaborated jwt security (very unlikely as friends don't let friends code their own caches nor security, and after several k's
      of loc extending keycloak for a previous employer, I can attest the truth in that saying)
* **Distributed Cache**: currently a few techniques are in place to avoid expensive ops, such as select count(*), but it is all app-local
  using **Caffeine in-memory cache**.
  A proper distributed cache is to be implemented:
    * **Hazelcast**: this is the one I have the most experience with and, it is not only blazing fast, it has so many good features on its
      own
      grid
    * **Infinispan**: as I am using probably a full(ish) red-hat stack (or sponsored) I might as well... Stats are very good too but will
      need
      to investigate the rest of the grid's features

---

### Looking Into The Future

So... What's next?!<br>
**First and foremost**: This is an OSS system (bar the ui...) so you can check out the back end and add or modify anything you like if you
want to put on the hours.

For my part: I will keep adding features and enhancing current ones, making this system more and more complete.
With no particular order, my roadmap of features includes:

* More statistics! The more data, the better!
* Stateful interface! Being able to reorder some ui elements or pinning favorites makes us all happy!
* Docker installation! Want to install this on your own machines? well let's make it easy!
* ML for scheduling / estimation! This AI won't take over the world but based on statistical data we could calculate how long will this type
  of task will take if handed by a junior for example.
* Internal chat
* Notifications
* Integrations with popular repos such as: gitLab ot gitHub

If this has enough traction to be something else than a side project to showcase my skills, then I'll open a wishlist section, so you can
tell me which features you would like to see implemented!

For the time being, enjoy the app, and consider it my presentation card as a back-end developer / architect.
See you soon!

**[Johnny X](https://www.linkedin.com/in/johnnyvera)**