---
layout: post
title: Linux screen share over web with multiple monitors
author: Johnny Xavier
image: Ubuntu-18-04-LTS-Bionic-Beaver.png
updated: 
categories: gnu_linux
extract: This note is about sharing a single screen with a web app on a multiple monitor setup
tags: linux screen share multiple monitors

---

a problem I've been having was being on a call and needing to share my screen.

given that I have 2 monitors, when you try to share screens with many web apps such as google's hangout / meet you can either share an application window or share screen.<br>
in a windows or OSX setup, you can choose which screen to share, in linux you will be sharing all your monitors at once. The more monitors you have the worse it gets.

choosing to share an app window is not bad, but all modals, menus, popups, etc, will not be shared. Still the key to this trick is to share an app.

## Sharing a single screen
I could not find back the StackOverflow post but I am sure I read over there  this trick.

what we can do to circumvent the linux screen sharing problem is to share a screen capturing app. I chose VLC for this but maybe this will work the same with others

### VLC setup
you need to have VLC installed.
on debian based OSs something like this will install it if you don't have it yet

###### debian based os
```bash
sudo apt install vlc
```

with vlc installed go to `media` menu and select `open capture device` like this:
<img style="width: 60%" src="{{ site.baseurl }}/public/images/vlc_media_menu.png">

choose `desktop` as `capture mode` and select `show more options`
replace the edit options for something like the following:

###### vlc desktop capture options
```bash
:screen-fps=20.000000 :live-caching=300 :screen-top=0 :screen-left=1920 :screen-width=1920 :screen-height=1080
```

**you control the area to share with:**
* :screen-top=0
* :screen-left=1920
* :screen-width=1920
* :screen-height=1080

that means that the `top` `left` corner start all to the top and 1920 pixels to the left, in my case, that's the start of my second monitor.<br>
The `width` and `height` will tell how much you want to capture, in my case you see the full resolution of my right monitor.

changing those parameters you can choose either monitor in any monitor setup you have.

you should get something like this
<img style="width: 50%" src="{{ site.baseurl }}/public/images/vlc_capture_device_setup.png">

hit play and you are done