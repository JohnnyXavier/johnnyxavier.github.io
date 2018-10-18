---
layout: post
title: Dell 9560 Laptop Setup
date: 17 10 2018
tags: linux OS Ubuntu Dell 9560 setup configuration
---

This note is about installing Ubuntu 18 `Bionic Beaver` on a Dell 9560 intended to be a developer box.
The actual OS installation is very straightforward so I will skip it and focus on the tweaks needed to actually make the OS work.


### /Dell_9560_specs
* CPU: intel i7-7700HQ 3.8GHz
* RAM: 32Gb
* HD:  1Tb SSD
* Display: 15.6 / 4K
* Dual GPU: intel / NVidia GTX 1050

### /generate_a_flash_drive_with_ubuntu_from_a_Linux_distro

### /generate_a_flash_drive_with_ubuntu_from_Windows

### /generate_a_flash_drive_with_ubuntu_from_MacOS

### /bios_setup
access dell's bios by restarting or turning on the computer
* press `F2` or `F12` at the Dell Logo until you land on a boot menu and select the bios settings options
* modify these settings:
* * `General` -> `Advanced Boot Options`: check `Enable Legacy Option ROMs`
* * `General` -> `Boot Sequence` : select `Legacy` under boot list option
* * `System Configuration` -> `SATA Operation`: select `AHCI`
* * `Secure Boot` -> `Secure Boot Enable` : select `Disabled`

#### optional / good to know settings:
* virtualization settings
* * `Virtualization Support` -> `Enable Intel Virtualization Technology` should be enabled
* * `Virtualization Support` -> `VT for Direct I/O` should be enabled
* touchscreen settings
* * `System Configuration` -> `touchscreen` : enable or disable it from here

### /OS_installation
* insert a flash drive with Ubuntu
* reboot and press `F12` at the Dell logo
* select the flash drive from the available drives and hit enter
* install ubuntu with defaults (unless you know what you are doing) as we are going to change the relevant settings after install is complete.

> a few **key** features do no work straight away so we are going to tune them in the next sections
>
> the misses include:
>
> nvidia 1050 (discrete card) / power management

### /OS_tuneup
after installing and rebooting you will be presented with `GRUB` choose the `safemode` option and hit enter
* graphics:
* * add graphics ppa , install nvidia drivers but select intel graphics chip:

```bash
sudo add-apt-repository ppa:graphics-drivers/ppa
sudo apt update
sudo apt install nvidia-driver-410 #this driver will in time be different as they update it
sudo prime-select intel #this will select the integrated intel card, which is less power hungry
#sudo prime-select nvidia #this will select the high performance nvidia GTX 1050 card.

#using the nvidia chip decreases significantly battery life
```
do not reboot
* GRUB:
* * 


* power Management:

### /Software_installation
* Java HotSpot (Oracle Java)
* * JDK <= 8
* * JDK 10
* * JDK 11
* Open JDK

