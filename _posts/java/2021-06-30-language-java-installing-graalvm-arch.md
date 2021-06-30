---
title: installing GraalVM on ArchLinux-family distros
excerpt: This note is about showing how to install **GraalVM** on am ArchLinux-family linux box
sidebar:
  title: "/The_Java_Lang"
  nav: sidebar-java
---
### introduction
this post covers installing [GraalVM](https://www.graalvm.org) on [ArchLinux](https://archlinux.org/) based distros. [Manjaro (KDE edition)](https://manjaro.org/) in my case.<br>
this is the exact same post as for no-arch distros but tailored for ArchLinux based ones.

for distributions that use `update-alternatives` command (i.e., [Debian](https://www.debian.org/) family, [Red-Hat](https://www.redhat.com/) family, [SuSE](https://www.suse.com/) family) read [general GraalVm installation post](/language-java-installing-graalvm-no-arch)

---

[GraalVM](https://www.graalvm.org) is deemed to be a high performance runtime for java (and a few other languages)

although there is a claim of faster speeds on *hotspot mode*, GraalVM caught my attention mainly because of the ***native-image*** feature.<br>
with ***native-image*** we could achieve extremely fast cold starts, and very low memory footprint.<br>
both features combined, make java an interesting player in the **serverless microservices** arena (i.e. [aws lambda](https://aws.amazon.com/lambda/))

---

### download
we first need to download GraalVM. Their downloads page is here [graalvm downloads](https://www.graalvm.org/downloads/) <br>
the page lists the latest versions and has some download links from github. Check the downloads page to get the latest updates

to download the latest *(at the time of this post)* version **(v.21.1)**, go to this github url: [graal-github-vm-21.1.0](https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-21.1.0)

let's choose the J16 amd64 link which will download the **graalvm-ce-java11-linux-amd64-21.xxx.xxx.xxx.tar.gz**. If you have another system or architecture, choose yours instead of amd64.


### installation
open a terminal like:
* KDE's Konsole
* GNOME's Terminal

#### extracting the downloaded file
navigate to where you downloaded the file, in my case, the `Downloads` folder and extract the files from the compressed archive

```shell
# extract the file, in my case, from the Downloads folder
user@hostname:~$ cd ~/Downloads
user@hostname:~/Downloads$ tar -xvzf graalvm-ce-java16-linux-amd64-21.xxx.xxx.xxx.tar.gz
```

If you run into a problem, you may need to make the file readable first, like this `chmod +r graalvm-ce-java16-linux-amd64-21.xxx.xxx.xxx.tar.gz`

#### local configuration
given than arch has its own way of selecting between multiple java installations, we're going to match the arch way.<br>
there is a script called `archlinux-java`, that looks for java installations in a particular directory, and allows us to set the one we want.

if I run the script now I will get the following:
```shell
# checking the installed java versions
user@hostname:~$ archlinux-java status
Available Java environments:
  java-11-openjdk
  java-16-openjdk (default)
  java-8-openjdk
```

in arch, the `jvms` are stored under `/lib/jvm`. If I list what is inside my own directory I will get this:

```shell
# checking the contents of /lib/jvm
user@hostname:~$ ls -lah /lib/jvm/
total 228K
drwxr-xr-x   5 root root 4,0K Jun 30 12:57 .
drwxr-xr-x 220 root root 208K Jun 30 09:11 ..
lrwxrwxrwx   1 root root   15 Jun 30 12:32 default -> java-16-openjdk
lrwxrwxrwx   1 root root   15 Jun 30 12:32 default-runtime -> java-16-openjdk
drwxr-xr-x   7 root root 4,0K Jun 13 13:30 java-11-openjdk
drwxr-xr-x   7 root root 4,0K Jun 26 09:54 java-16-openjdk
drwxr-xr-x   6 root root 4,0K Jun 26 11:02 java-8-openjdk
```

the mentioned script has a particular way of recognizing jvms, and it is the [following one](https://wiki.archlinux.org/title/java#Package_pre-requisites_to_support_archlinux-java):
> *Place all files under **/usr/lib/jvm/java-${JAVA_MAJOR_VERSION}-${VENDOR_NAME}***

we need to copy the downloaded files into a directory that matches the above, so I chose **java-16-graalvm**; as long as u respect the convention required by the script, choose the name that makes you happy.<br>
this will allow you to have all your manual jvm installations organized, in the way the system expects it.

```shell
# copying the extracted files to /lib/jvm
user@hostname:~$ sudo cp -r ~/Downloads/graalvm-ce-java16-21.1.0/ /lib/jvm/java-16-graalvm
```

if I now run the script I will get this
```shell
# checking the installed java versions
user@hostname:~$ archlinux-java status
Available Java environments:
  java-11-openjdk
  java-16-graalvm
  java-16-openjdk (default)
  java-8-openjdk
```
we have GraalVM among the options!

if I check my current java version I will get:

```shell
user@hostname:~$ java -version
openjdk version "16.0.1" 2021-04-20
OpenJDK Runtime Environment (build 16.0.1+9)
OpenJDK 64-Bit Server VM (build 16.0.1+9, mixed mode)
```

switching to graal:
```shell
#switching jdks
sudo archlinux-java set java-16-graalvm

#checking new jdk is running
user@hostname:~$ java -version
openjdk version "16.0.1" 2021-04-20
OpenJDK Runtime Environment GraalVM CE 21.1.0 (build 16.0.1+9-jvmci-21.1-b05)
OpenJDK 64-Bit Server VM GraalVM CE 21.1.0 (build 16.0.1+9-jvmci-21.1-b05, mixed mode, sharing)

#checking graal utilities are also present after we switched
user@hostname:~$ gu available
Downloading: Component catalog from www.graalvm.org
ComponentId              Version             Component name                Stability                     Origin 
---------------------------------------------------------------------------------------------------------------------------------
llvm-toolchain           21.1.0              LLVM.org toolchain            Experimental                  github.com
native-image             21.1.0              Native Image                  Experimental                  github.com
nodejs                   21.1.0              Graal.nodejs                  Experimental                  github.com
R                        21.1.0              FastR                         Experimental                  github.com
wasm                     21.1.0              GraalWasm                     Experimental                  github.com
```

as you can see from above, now we are running on GraalVM java, and when we switched jdks the graal utilities became available as well.

#### installing native-image
all set with GraalVM installation and configuration!

let's now install `native-image` which is a powerful tool to convert jvm based apps into blazing fast start low memory footprint native images

```shell
# let's install the native-image executable
user@hostname:~$ gu install native-image
Downloading: Component catalog from www.graalvm.org
Processing Component: Native Image
Downloading: Component native-image: Native Image  from github.com
Installing new component: Native Image (org.graalvm.native-image, version 21.1.0)

# to make sure it is installed run the command below, you should get a similar output
user@hostname:~$ native-image --version
GraalVM 21.1.0 Java 16 CE (Java Version 16.0.1+9-jvmci-21.1-b05)
```
---

**NOTE:** if you switch to another jvm then you will also lose graalvm's utilities like `native-image` and `gu`

**you are now all set to start playing with GraalVM!!!**