---
title: Linux single screen share over the web with multiple monitors
excerpt: This note is about sharing a single screen with a web app on a multiple monitor setup
sidebar:
  title: "/Gnu_Linux"
  nav: sidebar-gnu_linux
---

a problem I've been having was being on a call and needing to share my screen.

given that I have 2 monitors, when you try to share screens with many web apps such as google's hangout / meet you can either share an application window or share screen.<br>
in a windows or OSX setup, you can choose which screen to share, in linux you will be sharing all your monitors at once. The more monitors you have the worse it gets.

choosing to share an app window is not bad, but all modals, menus, popups, etc, will not be shared. Still the key to this trick is to share an app.

## /Sharing_a_single_screen
I could not find back the StackOverflow post but I am sure I read over there  this trick.

what we can do to circumvent the linux screen sharing problem is to share a screen capturing app. I chose VLC for this but maybe this will work the same with others

### /VLC_setup
you need to have VLC installed.
on Debian based OSs something like this will install it if you don't have it yet

#### /debian_based_os
```bash
sudo apt install vlc
```

with vlc installed go to `media` menu and select `open capture device` like this:

<figure>
    <a href="/assets/images/vlc_media_menu.png"><img src="/assets/images/vlc_media_menu.png"></a>
  	<figcaption>vlc media menu</figcaption>
</figure>

choose `desktop` as `capture mode` and select `show more options`
replace the edit options for something like the following:

#### vlc desktop capture options
```bash
:screen-fps=20.000000 :live-caching=300 :screen-top=0 :screen-left=1920 :screen-width=1920 :screen-height=1080
```

**you control the area to share with:**
* :screen-top=0
* :screen-left=1920
* :screen-width=1920
* :screen-height=1080

that means that the `top` `left` corner starts **0** pixels **from** the **top** and **1920** pixels **from** the **left**, in my case, that's the start of my second monitor.<br>
The `width` and `height` will control how much you want to capture, in my case you see the full resolution of my right monitor.

changing those parameters you can choose any area in any multiple monitor setup you might have.

you should get something like this
<figure>
    <a href="/assets/images/vlc_capture_device_setup.png"><img src="/assets/images/vlc_capture_device_setup.png"></a>
	<figcaption>vlc capture device options screen</figcaption>
</figure>

hit play, and you are done with the VLC part

### /Web_App
taking google's hangout / meet as an example, when you want to share a single screen, choose the option to share a single app, and then choose the VLC app, who should be playing your desktop.

do not minimize VLC thou as it might stop the feed.

enjoy!