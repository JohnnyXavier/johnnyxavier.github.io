---
title: BMC Anvil's Introduction
excerpt: This note is an introduction to BMC Anvil application
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

## A general intro to {{ site.showcase.name }}'s Tech Stack

### The trigger for the app, a little story

an original thought to building this showcase app was to do so in a few languages and combinations of frameworks. Those languages and
frameworks were:

* java + springboot
* java + quarkus
* golang "raw"
* golang + gorm + gin / fiber + ...
* nodejs + express

the idea was to try and continue a benchmark started some time ago on those frameworks, with a more realistic application than the ones used
in benchmarks I've seen over the web.<br>
that idea was very quickly discarded as one of the points of interest was `time to market per feature`and my level on each of the above
combinations not being equal would not make much justice to the task.

having more experience in the java / js / ts family than in Golang it was quickly evident that a semi big app will play against Golang due
to my own skills and not because of the language or ecosystem per se.

I had to resort to Go experienced friends with many questions to achieve this or that feature in an efficient manner. "Raw" golang will
execute ok if directly hit against, but will be slower to deliver features unless an experienced dev is handling the code, and even then...

I'll never know how this app built around Go / JS would play against the tech stack I chose, but what follows is what I know from the
benchmark world.

when we look at round 21 (latest to date of this writing)
@ [techempower benchmarks r21](https://www.techempower.com/benchmarks/#section=data-r21&test=db) and check the single and multiple queries
results we see this:
<figure>
    <a href="/assets/images/round21-single-query.jpeg"><img src="/assets/images/round21-single-query.jpeg" alt="r21 single query"></a>
  	<figcaption>round 21 single query</figcaption>
</figure>
<figure>
    <a href="/assets/images/round21-multi-query.jpeg"><img src="/assets/images/round21-multi-query.jpeg" alt="r21 multi query"></a>
  	<figcaption>round 21 multi query</figcaption>
</figure>

when checking cached queries:<br>
the difference in performance between `Go` and `Java` best scores, is **0.2%**
<figure>
    <a href="/assets/images/round21-cached-query.jpeg"><img src="/assets/images/round21-cached-query.jpeg" alt="r21 cached query"></a>
  	<figcaption>round 21 cached query</figcaption>
</figure>

when we look at [round 20](https://www.techempower.com/benchmarks/#section=data-r20&test=db),
[round 19](https://www.techempower.com/benchmarks/#section=data-r19&test=db) and even far back, there is always a java framework contending
for top performer.

I cannot tell why GoLang raw at least, is not on the top ten these last 2 years. Based on my personal benchmarks, I have a very strong
feeling it must be up there, but there is no point arguing with the numbers. We could thou argue with the code behind and with the
methodologies, but as stated at the beginning I would not be the one doing Go and its frameworks any justice.

I did some research and found a few articles and videos confirming or refuting techempower's numbers.

What was strangely eye-opening was that the [gofiber](https://gofiber.io/) team uses techempower round 19 data on their own
[benchmark docs](https://docs.gofiber.io/extra/benchmarks/#techempower). They only kind of make go vs go comparisons, but the benchmark
round they point to, when compared to all languages and frameworks paints a poor performance picture on most tests.

among others, I found this [recent benchmark video](https://www.youtube.com/watch?v=8CiErLxdaA8) about go+fiber vs java+springboot which
gives a nice advantage in a few departments to Go+fiber but at the end of test 2 well... seems that when traffic increases go+fiber start to
struggle, I just cannot tell what happened.

<figure>
    <a href="/assets/images/go-java-anton-video-test-2-latency.jpeg">
    <img src="/assets/images/go-java-anton-video-test-2-latency.jpeg" alt="go fiber java Spring Boot anton video test 2 latency"></a>
  	<figcaption>go fiber java Spring Boot anton video test 2 latency</figcaption>
</figure>
<figure>
    <a href="/assets/images/go-java-anton-video-test-2.jpeg">
    <img src="/assets/images/go-java-anton-video-test-2.jpeg" alt="go fiber java Spring Boot anton video test 2 CPU"></a>
  	<figcaption>go fiber java Spring Boot anton video test 2 CPU</figcaption>
</figure>

I could keep investigating but at some point I needed to start a showcase app.

---

### So... one app one language

because of the above, I decided not to go for benchmarking multiple languages, but to focus instead on a single more rounded app built with
a language in which I have more experience.

as stated on the [disclaimer](/bmc-showcase-intro-disclaimer), the purpose of this application and accompanying notes is a way of making easier
for tech recruiters and tech teams wanting to hire personnel, to check on my skills through various notes and a bigger app than a standard
"coding challenge".

once I started doing this application and researching on the techs I was implementing, I also discovered that many guides on the web use
small examples to explain their own tech, that sometimes are hard to interpolate to a real world app. What I am building here can also be
used
to complement a few of these guides with bigger and more complex guides.

If you came for the showcase or for the examples / guides, and have any question, feel free to reach out over
**[LinkedIn: Johnny X](https://www.linkedin.com/in/johnnyvera)** or [GitHub](https://github.com/JohnnyXavier).