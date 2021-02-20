---
title: configuring multiple JDKs
excerpt: This note is about showing how to configure multiple JDKs on a linux box
sidebar:
  title: "/The_Java_language"
  nav: sidebar-java
---
### introduction
there are many reasons to install more than 1 jdk on a system.

it can be that a part of your codebase is running on **JDK_X** while the newer services run on **JDK_Y**<br>
it can be that you are trying some features of a newer version, but still develop under an **L**ong**T**erm**S**upport version (***LTS***)

whatever the reason may be, having more that 1 JDK on the system is not a problem if properly configured
for this note, we're going to install via [apt](https://wiki.debian.org/Apt), 2 different jdk versions and manually a third one

---
### installing a jdk via apt
you can check this note -[installing_openjdk](/language-java-installing-openjdk/)- or continue reading here...

to install a jdk if you don't have any or to install a second one if you already have one, just do the following

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

to install a second one, repeat the above but selecting a different jdk, java 8 for example like so:
```shell
# for the java openjdk 8 you need to run
you@host:~$ sudo apt install openjdk-8-jdk
```

wait for the installation to complete... and confirm like before that java vas installed...

if you ran the `java -version` again, you will see now the java 8 info being displayed, as it was the last one you installed...<br>
if it is not the case, and you still see java 14 info, continue reading to see how to select the other version

---
#### selecting a java version
now that you have more than one jdk installed on your system you can easily see them and switch between them with the [update-alternatives](https://manpages.debian.org/buster/dpkg/update-alternatives.1.en.html) command

the open-jdk installs several java commands, for example:
* java
* javac
* javadoc
* jar
* etc...

to switch between the `java` command installed versions do the following:
```shell
# let's first check which java version we are currently running
you@host:~$ java -version
# in my case it's a graalvm v21 running on java 11
openjdk version "11.0.10" 2021-01-19
OpenJDK Runtime Environment GraalVM CE 21.0.0 (build 11.0.10+8-jvmci-21.0-b06)
OpenJDK 64-Bit Server VM GraalVM CE 21.0.0 (build 11.0.10+8-jvmci-21.0-b06, mixed mode, sharing)

# let's now change it to something else, like java 8
you@host:~$ sudo update-alternatives --config java
# the above command will display something similar to the following
There are 4 choices for the alternative java (providing /usr/bin/java).

  Selection    Path                                            Priority   Status
------------------------------------------------------------
  0            /usr/lib/jvm/java-14-openjdk-amd64/bin/java      1411      auto mode
* 1            /home/username/bin/jvm/graalvm/bin/java          1000      manual mode
  2            /usr/lib/jvm/java-11-openjdk-amd64/bin/java      1111      manual mode
  3            /usr/lib/jvm/java-14-openjdk-amd64/bin/java      1411      manual mode
  4            /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java   1081      manual mode

Press <enter> to keep the current choice[*], or type selection number:  4
# as you can see the graalvm version is selected which corresponds with what we found before.
# I will select option 4 which is java 8
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java to provide
 /usr/bin/java (java) in manual mode

# if you run again update-alternatives you will see that java 8 is now the selected version
you@host:~$ sudo update-alternatives --config java
# the above command will display something similar to the following
There are 4 choices for the alternative java (providing /usr/bin/java).

  Selection    Path                                            Priority   Status
------------------------------------------------------------
  0            /usr/lib/jvm/java-14-openjdk-amd64/bin/java      1411      auto mode
  1            /home/username/bin/jvm/graalvm/bin/java          1000      manual mode
  2            /usr/lib/jvm/java-11-openjdk-amd64/bin/java      1111      manual mode
  3            /usr/lib/jvm/java-14-openjdk-amd64/bin/java      1411      manual mode
* 4            /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java   1081      manual mode

Press <enter> to keep the current choice[*], or type selection number:  

# and to confirm...
you@host:~$ java -version
openjdk version "1.8.0_282"
OpenJDK Runtime Environment (build 1.8.0_282-8u282-b08-0ubuntu1~20.04-b08)
OpenJDK 64-Bit Server VM (build 25.282-b08, mixed mode)
```

the `update-alternatives` is a very solid and tidy way of keeping many JDKs versions on your system and switch among them without much effort.

---
### manually installing a jdk
if you want to know how to manually install a jdk using `update-alternatives`, you can check this note -[installing_graal_vm](/language-java-installing-graalvm/)- for a detailed step by step explanation on how to manually install **GraalVM**
