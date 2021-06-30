---
title: installing openJDK
excerpt: This note is about showing how to install **openJDK** on a linux box
sidebar:
  title: "/The_Java_Lang"
  nav: sidebar-java
toc: false
---

Ubuntu flavored distributions have the openjdk packages already on its own repos.

open a terminal like:
* KDE's Konsole
* GNOME's Terminal

```shell
# for the java openjdk 14 you need to run
you@host:~$ sudo apt install openjdk-14-jdk
```

wait for the installation to complete...

```shell
# confirm that you have the Openjdk java runtime installed correctly by running the java command 
# and checking you get a similar result as below
user@hostname:~$ java -version

openjdk version "14.0.2" 2020-07-14
OpenJDK Runtime Environment (build 14.0.2+12-Ubuntu-120.04)
OpenJDK 64-Bit Server VM (build 14.0.2+12-Ubuntu-120.04, mixed mode, sharing)

# confirm that you have the Openjdk java compiler installed correctly by running the javac command
# and checking you get a similar result as below
user@hostname:~$ javac -version

javac 14.0.2
```

if your system does not recognize the *javac* command most likely you installed the jre instead of the jdk, review the steps above and make sure you executed them properly.