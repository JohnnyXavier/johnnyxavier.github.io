---
title: Setting user icon / avatar in manjaro KDE
excerpt: This note is about how to set user icon / avatar in manjaro kde when default gui procedure fails
sidebar:
title: "/Gnu_Linux"
nav: sidebar-gnu_linux
---

a problem I just had when installing [Manjaro linux](https://manjaro.org/) ([KDE](https://kde.org/) edition) was that I could not change my user's avatar image. No matter what I did it kept defaulting to blank

after a few web searches I stumbled on a good pointer.

turn out manjaro read the user image file from this location `/var/lib/AccountsService/icons` and that location if you check who owns it is... `ROOT`

so it happens that you go to kde settings or manjaro settings and change the image, input your admin password, but the image is not displayed.

it happens because when you authenticate as admin you can save your chosen image, but after that, your user itself cannot read from it...

the fix, in my case, is this one liner

```shell
[user@hostname: ~]$ sudo chown <user>:root /var/lib/AccountsService/icons/<user>
```
`<user>` is your username, and inside that `icons` folder there is a file with you username that will be the avatar image. It is the same image set by kde that you can fine as `.face` in your home folder.

the above command will make that file readable to you user, so you will be able to set as usual from the system settings and change it at will.