---
title: using `update-alternatives` to manage multiple versions of an executable
excerpt: This note is about using `update-alternatives` tool to obtain fine-grained information about an ELF file
sidebar:
  title: "/Gnu_Linux"
  nav: sidebar-gnu_linux
---
## update-alternatives description
from the `man` entry for `update-alternatives`:

> DESCRIPTION
>
> update-alternatives - maintain symbolic links determining default commands

----

## intro
we gave an example on how to use `update-alternatives` to configure multiple java installations in the java section of the site regarding either multiple automated installations of java, or some from the package manager system and others manually installed.

this is to show the same procedure with python which is a little less complicated as the system (OpenSuSE in this case), installs the binaries in `/usr/bin`

this could also be similarly done to configure different text editors, if you have more than one installed, for example

## procedure
in this case, ***OpenSuSE tumbleweed***, python is installed in versions **2** and **3**. Version 2, being the default, this means that when you (or maybe another program) call the `python` command it is symlinked to python2, and python2 is in turn linked to whatever version of python 2 installed on you system.

on my system **python 2** and **python 3** are both installed. This is my bash output

```shell
you@hostname:~$ python --version
Python 2.7.18
```

### python installation location under OpenSuSE
in this OS all python version are under `/usr/bin`

```shell
you@hostname:~$ ls /usr/bin | grep python
lrwxrwxrwx  1 root root       24 Apr 11 17:56 python -> python2
lrwxrwxrwx  1 root root        9 Mar  9 00:01 python2 -> python2.7
-rwxr-xr-x  1 root root      15K Mar  9 00:01 python2.7
lrwxrwxrwx  1 root root        9 Mar 18 23:24 python3 -> python3.8
-rwxr-xr-x  2 root root      15K Mar  8 23:41 python3.6
-rwxr-xr-x  2 root root      15K Mar  8 23:41 python3.6m
-rwxr-xr-x  1 root root      15K Mar 18 23:25 python3.8
```

what we want to achieve is to be able to default the python command to python 2 or 3.


### configuring the alternatives

* first thing is to delete the python symlink as we are going to create a new one with `update-alternatives`
* next we are going to add python as an entry in the `update-alternatives` system pointing to python2
* next we are going to add python as an entry in the `update-alternatives` system but pointing to python3

```shell
# deleting python symlink
you@hostname:~$ sudo rm /usr/bin/python

# adding python2 to update-alternatives
you@hostname:~$ sudo update-alternatives --install /usr/bin/python python /usr/bin/python2 1000

# adding python3 to update-alternatives
you@hostname:~$ sudo update-alternatives --install /usr/bin/python python /usr/bin/python3 2000
```

that's it!

the default selected version will be the one with the highest priority number.<br>
in our case it will be **python3** as it hast priority number **2000**. If you want to choose the other version you just configure it by calling the `update-alternatives` like so

```shell
# check currently active version
you@hostname:~$ python --version
Python 3.8.8

#configure a different python version
you@hostname:~$ sudo update-alternatives --config python
There are 2 choices for the alternative python (providing /usr/bin/python).

  Selection    Path              Priority   Status
------------------------------------------------------------
* 0            /usr/bin/python3   2000      auto mode
  1            /usr/bin/python2   1000      manual mode
  2            /usr/bin/python3   2000      manual mode

Press <enter> to keep the current choice[*], or type selection number: 1

# as u can see python3, having the highest priority number is automatically chosen by default
# let's choose option 1 to change to python2: select 1 on the prompt and <enter>

#making sure the change was applied
you@hostname:~$ python --version
Python 2.7.18

# let's change it back to python3
you@hostname:~$ sudo update-alternatives --config python
There are 2 choices for the alternative python (providing /usr/bin/python).

  Selection    Path              Priority   Status
------------------------------------------------------------
  0            /usr/bin/python3   2000      auto mode
* 1            /usr/bin/python2   1000      manual mode
  2            /usr/bin/python3   2000      manual mode

Press <enter> to keep the current choice[*], or type selection number: 0

# as u can see python2 is selected, check the star (*) in front of it.
# let's choose option 0 to change to python3: select 0 on the prompt and <enter>
# note: selected option 0 is going for the auto mode, which will use the highest priority number.
# if you add a version in the future with priority number 3000... that version will be auto selected!
  
#making sure the change was applied
you@hostname:~$ python --version
Python 3.8.8
```

all done!