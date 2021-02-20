---
title: using `readelf` to get info about an executable file
excerpt: This note is about using `readelf` tool to obtain fine-grained information about an ELF file
sidebar:
  title: "/Gnu_Linux"
  nav: sidebar-gnu_linux
---
## readelf description
from the `man` entry for `readelf`:

> DESCRIPTION 
> readelf displays information about one or more ELF format object files.  The options control what
> particular information to display.
>
> elffile... are the object files to be examined.  32-bit and 64-bit ELF files are supported,
> as are archives containing ELF files.
>
> This program performs a similar function to objdump but it goes into more detail and it exists
> independently of the BFD library, so if there is a bug in BFD then readelf will not be affected.

## elf definition
and regarding the meaning of ELF... it stands for **E**xecutable and **L**inkable **F**ormat<br>
check this excerpt from wikipedia about the [Executable and Linkable Format](https://en.wikipedia.org/wiki/Executable_and_Linkable_Format) files

> In computing, the Executable and Linkable Format (ELF, formerly named Extensible Linking Format), is a common standard file format for executable files, object code, shared libraries, and core dumps.
> 
> First published in the specification for the application binary interface (ABI) of the Unix operating system version named System V Release 4 (SVR4),[2] and later in the Tool Interface Standard,[1] it was quickly accepted among different vendors of Unix systems.
> 
> In 1999, it was chosen as the standard binary file format for Unix and Unix-like systems on x86 processors by the 86open project.

for a very nice article on ELF you can check this post [The 101 of ELF files on Linux: Understanding and Analysis](https://linux-audit.com/elf-binaries-on-linux-understanding-and-analysis)

## back to `readelf`

to check the header of a file to obtain general info we can do the following
```shell
# let's check the elf header of -jump-, a small program in asm I did to learn asm_x64
you@hostname:~$ readelf --file-header ./jump
ELF Header:
  Magic:   7f 45 4c 46 02 01 01 00 00 00 00 00 00 00 00 00 
  Class:                             ELF64
  Data:                              2s complement, little endian
  Version:                           1 (current)
  OS/ABI:                            UNIX - System V
  ABI Version:                       0
  Type:                              EXEC (Executable file)
  Machine:                           Advanced Micro Devices X86-64
  Version:                           0x1
  Entry point address:               0x401040
  Start of program headers:          64 (bytes into file)
  Start of section headers:          15336 (bytes into file)
  Flags:                             0x0
  Size of this header:               64 (bytes)
  Size of program headers:           56 (bytes)
  Number of program headers:         11
  Size of section headers:           64 (bytes)
  Number of section headers:         36
  Section header string table index: 35
```
among a few entries we can see that the entry point is located at address `0x401040`

if we want to see where `main` starts, we can look for it among the **elf** symbols like this

```shell
you@hostname:~$ readelf --symbols ./jump | grep main
     2: 0000000000000000     0 FUNC    GLOBAL DEFAULT  UND __libc_start_main@GLIBC_2.2.5 (2)
    61: 0000000000000000     0 FUNC    GLOBAL DEFAULT  UND __libc_start_main@@GLIBC_
    71: 0000000000401130     0 NOTYPE  GLOBAL DEFAULT   13 main
```

in entry line 71, we can see that `main` starts at address `0x401130` a little away from the entry point we saw above,`0x401040`
