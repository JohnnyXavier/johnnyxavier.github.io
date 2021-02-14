---
title: installing GraalVM
excerpt: This note is about showing how to install **GraalVM** on a linux box
sidebar:
  title: "/The_Java_language"
  nav: sidebar-java
---
### introduction
[graalVM](https://www.graalvm.org) is deemed to be a high performance runtime for java (and a few other languages)

although there is a claim of faster speeds on *hotspot mode*, GraalVM caught my attention mainly because of the ***native-image*** feature.<br>
with ***native-image*** we could achieve extremely fast cold starts, and very low memory footprint.<br>
both features combined, make java an interesting player in the **serverless microservices** arena (i.e. [aws lambda](https://aws.amazon.com/lambda/))

---

### download
we first need to download GraalVM. Their downloads page is here [graalvm downloads](https://www.graalvm.org/downloads/) <br>
the page lists the latest versions and has some download links from github. Check the downloads page to get the latest updates

to download the *(currently)* latest version **(v.21)**, go to this github url: [graal-github-vm-21.0.0.2-tag](https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-21.0.0.2)

let's choose the J11 amd64 link which will download the **graalvm-ce-java11-linux-amd64-21.xxx.xxx.xxx.tar.gz**. If you have another system or architecture, choose yours instead of amd64.


### installation
open a terminal like:
* KDE's Konsole
* GNOME's Terminal

#### extracting the downloaded file
navigate to where you downloaded the file, in my case, the `Downloads` folder and extract the files from the compressed archive

```shell
# extract the file, in my case, from the Downloads folder
user@hostname:~$ cd ~/Downloads
user@hostname:~/Downloads$ tar -xvzf graalvm-ce-java11-linux-amd64-21.xxx.xxx.xxx.tar.gz

```

#### local configuration

in ubuntu distros the java sdks are usually installed under `/usr/lib/jvm/`. It's tempting to install graalVM also into that folder, but I usually prefer to leave the system paths to be handled by proper installers that will maintain links and permissions automatically.

this local configuration makes my life easy, so here it is.

in you home directory, create a `bin` directory, and a `jvm` vm directory inside `bin`.
```shell
# creating a bin and a jvm directory inside it with a single command
user@hostname:~$ mkdir -p ~/bin/jvm
```

this will allow you to have all your manual jvm installations organized, similar as what the system does.

now move the extracted files from `Downloads` to you newly created `~/bin/java` directory. 

```shell
# moving the extracted files to ~/bin/jvm
user@hostname:~$ mv ~/Downloads/graalvm-ce-java11-21.xxx.xxx.xxx/ ~/bin/jvm/
```

now we are going to create a symlink to that graalvm version that will allow us to easily refer to it around the system

```shell
# creating a symlink of this version of GraalVM
user@hostname:~$ ln -s ~/bin/jvm/graalvm-ce-java11-21.0.0/ ~/bin/jvm/graalvm

# check that your directory and symlink are created and are correct, your ls -lh output should be similar
# that the one below
user@hostname:~$ ls -lh ~/bin/jvm
total 4.0K
lrwxrwxrwx  1 user user   25 Jan 30 22:52 graalvm -> graalvm-ce-java11-21.0.0/
drwxr-xr-x 10 user user 4.0K Jan 30 19:32 graalvm-ce-java11-21.0.0
```

#### updating .bashrc file
we are going to update the file `.bashrc` to simplify our lives when calling graal commands from the console, particularly, the graal update command `gu`, which will allow us to install graal software such as **native-image**, and the `native-image` command itself.

using a text editor add the lines below at the end of the `.bashrc` file, I will use vim

```shell
user@hostname:~$ vim ~/.bashrc

# add the lines below at the end of the file and save
# remember to replace YOUR_USER_HERE with your own username

# Add graal-vm installer to path
export PATH=$PATH:/home/YOUR_USER_HERE/bin/jvm/graalvm/lib/installer/bin

# Add graal native-image to path
export PATH=$PATH:/home/YOUR_USER_HERE/bin/jvm/graalvm/lib/svm/bin

# save thw changes by pressing the escape key and then :q and enter
# now that you are back ot the prompt execute the command below so the change take effect without needing
# to close and reopen the terminal
user@hostname:~$ source .bashrc
```

#### adding graalVM as a java alternative
in order to use graal as a proper java alternative we are going to add it to the system like this.

let's see how many java installations we already have
```shell
user@hostname:~$ update-alternatives --config java
There are 3 choices for the alternative java (providing /usr/bin/java).

  Selection    Path                                            Priority   Status
------------------------------------------------------------
* 0            /usr/lib/jvm/java-14-openjdk-amd64/bin/java      1411      auto mode
  2            /usr/lib/jvm/java-11-openjdk-amd64/bin/java      1111      manual mode
  3            /usr/lib/jvm/java-14-openjdk-amd64/bin/java      1411      manual mode
  4            /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java   1081      manual mode
```

we are going to add now graalVM java to the list, in my case I will use priority **1000**, as long as u don't use you're ok.<br>
the path will correspond to the ones you used at the beginning of this post

```shell
# install the graalVM alternative
# remember to replace YOUR_USER_HERE with your own username

user@hostname:~$ sudo update-alternatives --install /usr/bin/java java /home/YOUR_USER_HERE/bin/jvm/graalvm/bin/java 1000

# check now all java alternatives as before, you should see your newly added GraalVM alternative like below
There are 4 choices for the alternative java (providing /usr/bin/java).

  Selection    Path                                            Priority   Status
------------------------------------------------------------
* 0            /usr/lib/jvm/java-14-openjdk-amd64/bin/java      1411      auto mode
  1            /home/YOUR_USER_HERE/bin/jvm/graalvm/bin/java    1000      manual mode
  2            /usr/lib/jvm/java-11-openjdk-amd64/bin/java      1111      manual mode
  3            /usr/lib/jvm/java-14-openjdk-amd64/bin/java      1411      manual mode
  4            /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java   1081      manual mode

Press <enter> to keep the current choice[*], or type selection number:
```

#### switching to GraalVM java alternative
as you can see above now we have GraalVM as a java alternative, but the system is still using the previously setup version (J14)
to switch between versions just do this

```shell
# configure GraalVM as the active java alternative with the previous config option but as sudo
user@hostname:~$ sudo update-alternatives --config java

There are 4 choices for the alternative java (providing /usr/bin/java).

  Selection    Path                                            Priority   Status
------------------------------------------------------------
* 0            /usr/lib/jvm/java-14-openjdk-amd64/bin/java      1411      auto mode
  1            /home/YOUR_USER_HERE/bin/jvm/graalvm/bin/java    1000      manual mode
  2            /usr/lib/jvm/java-11-openjdk-amd64/bin/java      1111      manual mode
  3            /usr/lib/jvm/java-14-openjdk-amd64/bin/java      1411      manual mode
  4            /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java   1081      manual mode

# select the number of your GraalVM installation on the below prompt
Press <enter> to keep the current choice[*], or type selection number: 1

# run the command again to confirm GraalVM alternative is now selected, the asterix should be next to
# the GraalVM alternative
user@hostname:~$ update-alternatives --config java
There are 4 choices for the alternative java (providing /usr/bin/java).

  Selection    Path                                            Priority   Status
------------------------------------------------------------
  0            /usr/lib/jvm/java-14-openjdk-amd64/bin/java      1411      auto mode
* 1            /home/YOUR_USER_HERE/bin/jvm/graalvm/bin/java    1000      manual mode
  2            /usr/lib/jvm/java-11-openjdk-amd64/bin/java      1111      manual mode
  3            /usr/lib/jvm/java-14-openjdk-amd64/bin/java      1411      manual mode
  4            /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java   1081      manual mode

# you can double check by querying the java version at the prompt like this
user@hostname:~$ java -version
openjdk version "11.0.10" 2021-01-19
OpenJDK Runtime Environment GraalVM CE 21.0.0 (build 11.0.10+8-jvmci-21.0-b06)
OpenJDK 64-Bit Server VM GraalVM CE 21.0.0 (build 11.0.10+8-jvmci-21.0-b06, mixed mode, sharing)
```

#### installing native-image
all set with GraalVM installation and configuration!

let's now install `native-image` which is a powerful tool to convert jvm based apps into blazing fast start low memory footprint native images

if you followed all the above steps, particularly the one updating the `.bashrc`, you should be able to call the command `gu`

```shell
# let's install the native-image executable
user@hostname:~$ gu install native-image

# to make sure it is installed run the command below, you should get a similar output
user@hostname:~$ native-image --version
GraalVM Version 21.0.0 (Java Version 11.0.10+8-jvmci-21.0-b06)
```
---

**you are now all set to start playing with GraalVM!!!**