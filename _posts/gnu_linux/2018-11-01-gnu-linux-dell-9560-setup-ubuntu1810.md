---
title: Ubuntu 18.10 Cosmic Cuttlefish Setup - on Dell 9560
excerpt: This note is about installing Ubuntu 18 <strong>Cosmic Cuttlefish</strong> on a Dell 9560 intended to be a developer box.
sidebar:
  title: "/Gnu_Linux"
  nav: sidebar-gnu_linux
---
This note is almost the same as **installing ubuntu 18.04** with the difference of a tweak at boot after installation completes.

I reproduce here the previous note with the corresponding updates

The actual OS installation is very straightforward, so I will skip it and focus on the tweaks needed to actually make the OS work.
> sources:
>
> [Dell_XPS_15_9560 archlinux wiki](https://wiki.archlinux.org/index.php/Dell_XPS_15_9560)<br>
> [how-to-dual-boot-windows-10-and-ubuntu-18-04-on...](https://medium.com/@pwaterz/how-to-dual-boot-windows-10-and-ubuntu-18-04-on-the-15-inch-dell-xps-9570-with-nvidia-1050ti-gpu-4b9a2901493d)<br>
> [rcasero issues fixed](https://github.com/rcasero/doc/issues)
>

### /Dell_9560_specs
* CPU: intel i7-7700HQ 3.8GHz
* RAM: 32Gb
* HD:  1Tb SSD
* Display: 15.6 / 4K
* Dual GPU: intel / NVidia GTX 1050

### /bios_setup
access dell's bios by restarting or turning on the computer
* press `F2` or `F12` at the Dell Logo until you land on a boot menu and select the bios settings options
* modify these settings:
  * `General` -> `Advanced Boot Options`: check `Enable Legacy Option ROMs`
  * `General` -> `Boot Sequence` : select `Legacy` under boot list option
  * `System Configuration` -> `SATA Operation`: select `AHCI`
  * `Secure Boot` -> `Secure Boot Enable` : select `Disabled`

#### optional / good to know settings:
* virtualization settings
  * `Virtualization Support` -> `Enable Intel Virtualization Technology` should be enabled
  * `Virtualization Support` -> `VT for Direct I/O` should be enabled
* touchscreen settings
  * `System Configuration` -> `touchscreen` : enable or disable it from here, it works fine should you choose to leave it enabled

### /OS_installation
* insert a flash drive with Ubuntu `Cosmic Cuttlefish`
* reboot and press `F12` at the Dell logo
* select the flash drive from the available drives and hit enter
* install ubuntu with defaults (unless you know what you are doing) as we are going to change the relevant settings after install is complete.
* when installation completes, you will be asked to reboot. The system will hang trying to reboot so you will need to power off and on pressing the power button

> a few **key** features do no work straight away so we are going to tune them in the next sections
>
> the misses include:<br>
> nvidia 1050 (discrete card) / power management

### /OS_tuneup
after installing and rebooting you will be presented with `GRUB`.
if you don't see the boot menu and the computer just sits doing nothing, reboot again and hit the `SHIFT` key
choosing safemode in cuttlefish won't work so we need to adjust some parameters.

hit they `e` key to be able to modify grub params
 
remove `"quiet splash"` from kernel params and replace both with `nomodeset`

press `F10` key to continue with modified params

* graphics:
  * add graphics ppa, install nvidia drivers but select intel graphics chip:

#### Adding graphic drivers
```shell
sudo add-apt-repository ppa:graphics-drivers/ppa
sudo apt update
sudo apt install nvidia-driver-410 #this driver will in time be different as they update it
sudo prime-select intel #this will select the integrated intel card, which is less power hungry
# sudo prime-select nvidia #this will select the high performance nvidia GTX 1050 card.

# using the nvidia chip decreases significantly battery life
```
do not reboot
* `GRUB`:
  * the graphic card caused lots of seemingly unrelated problems, from not booting to not rebooting or not waking up after sleep, etc
passing a few kernel mods solves this.

#### Tweaking GRUB for graphics
```shell
sudo nano /etc/default/grub
# locate the entry below and add:
# nvidia-drm.modeset=1 nouveau.modeset=0 mem_sleep_default=deep nouveau.runpm=0
# it should look similar as below
GRUB_CMDLINE_LINUX_DEFAULT="quiet splash nvidia-drm.modeset=1 nouveau.modeset=0 mem_sleep_default=deep nouveau.runpm=0"
# this solve the problems caused by the graphic cards either in nvidia or in intel mode
``` 
as you are editing `grub` you can also tune a few other options.

#### Tweaking GRUB - other options
```shell
GRUB_TIMEOUT=10 # this will give you more time into the grub screen (time is in secs)

# with the 4K panel the default resolution is 4k. editing text in that res is mission impossible
# select the one that suits you best running vbeinfo in the grub menu after booting up
GRUB_GFXMODE=1920x1440

# once you are done, save the file and then update the boot loader
sudo update-grub
```

reboot for all changes to take effect

* power Management:

I installed a few recommended power management utils, thou I could not benchmark a before and after thoroughly.
The **big noticeable** difference in power consumption and temperature drop was the switching from the nvidia card to the intel one.
I leave the usual suspects for power management here

#### Powertop
```shell
sudo apt install tlp tlp-rdw powertop
sudo tlp start
sudo powertop --auto-tune
# auto tune will behave differently if the laptop is plugged or unplugged
# reboot
```

last but not least:

* Kernel update:

#### Ukuu for kernel updates
```shell
uname -r 
-> 4.15.0-36-generic
# running  uname -r will display your current kernel version

sudo add-apt-repository ppa:teejee2008/ppa
sudo apt install ukuu
sudo ukuu --install-latest #ukuu can also be used graphically

# accept the prompts and then reboot
# after rebooting, run again uname -r to check you new kernel version is up
uname -r 
-> 4.18.15-041815-generic
```

After all this tweaks my laptop ran smoothly.

I connect and disconnect usb perifs without problems (USB mouse and kbd, headseat via jack, externals ssd HD and mecha HD via usb) and connect and disconnect
a 2nd 24inch monitor via hdmi without problems.
Booting has no problems and sleep / wakeup has no issues