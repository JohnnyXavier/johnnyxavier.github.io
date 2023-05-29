---
title: BMC Anvil's testing
excerpt: This note is about testing strategies for the application
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_anvil
---

## Testing Strategies

### Unit Testing

after some thinking, I will write a few of these to prevent an interviewer to have a heart attack if they don't see any...

### End-To-End testing

I will try and prioritize these tests and integrate them via postman.

### On testing in general:

when writing the app it was second nature to start testing "everything".

I realized that on a brand new moving target as this app, unit-testing everything meant to redo or update a lot of testing code as the
entities and the app grew more and more complex...

that got me thinking and I started reading and watching videos about testing strategies...

I then created a suite in postman that would rarely change and still built some degree of a guardrail for the code. Basically, regardless
the implementation the result was still being tested and passed or failed.

I am not yet sold on a given testing strategy, so I will meditate about this a little bit more before committing to test the code in one
given way or a mix of both.

because of the nature of the app a mix of both might be the way to go eventually. 