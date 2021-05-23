---
title: C++20 modules with `gnu g++`
excerpt: This note is about using C++20 modules feature on a simple example with `gnu g++`
sidebar:
  title: "/The_C++_Language"
  nav: sidebar-c++
---
## C++20 - modules with `gnu g++`
this page will describe the gnu\g++ way of developing with modules with c++20.

## Intro
while reading books on latest C++, I found I could not compile any of the examples straight away, making a bit difficult to follow and practice as I read along.

many of the resources I tried on the net or paper to compile and expand on the examples where not complete or very simple, rendering the examples on the books hard to follow.

here I leave an example of my own which is simple enough but can scale to more elaborated ones as I break implementation of module components into many files.<br>
For the time being, all module definitions still remain on a single file. 

## Current C++20 supported features by gnu g++
check [this website](https://gcc.gnu.org/projects/cxx-status.html) for the support status of c++20 features by gnu\g++.

## Installing gnu g++
I am using OpenSuSE Tumbleweed, if you have a different one, the goal is to install gcc11 with c++ support.
Check your ow distro as usually non rolling distribution require some additional steps before adding some latest versions of software.

run the below to get latest gcc11 in your machine.
it will install the latest (for now) version of gcc with c++ support along with the many corresponding libraries and headers.
```shell
# on a machine running OpenSuSE tumbleweed
user@hostname:~$ sudo zypper in gcc11 gcc11-c++ gcc11-info 

```

## simple_program
to demonstrate simple, yet multi_file **_"hello modules"_** intro program featuring **_c++20 modules_** I will do the following:

* create a main file that makes use of a module
* create a module definition file
    * with a class
    * with an isolated function
* create a module implementation file for the class defined on the previous module file 
* create a module implementation file for the method defined on the previous module file

the base headers we are going to keep importing them as `#include` given the extra compilation steps required, which will be covered when using `make` tools

The example will be a simple Person class with a function that operates on a Person.

### file_extensions_matter
given that support for c++20 modules is a still a little green, g++ can get confused by a few file extensions used for other compilers or on some books.

use the same extensions as in this example, and you'll be ok. I you change `.cpp` for a `.cppm` for example, you will get an error message similar to the one below.

```shell
/usr/lib64/gcc/x86_64-suse-linux/11/../../../../x86_64-suse-linux/bin/ld:person_module.cppm: file format not recognized;
treating as linker script
/usr/lib64/gcc/x86_64-suse-linux/11/../../../../x86_64-suse-linux/bin/ld:person_module.cppm:1: syntax error
collect2: error: ld returned 1 exit status
```

### the_person_module
```c++
module;
#include <iostream>
export module data_type;

// by exporting the namespace we export everything under it, or else we need to export
// everything explicitely
export namespace data_type
{
    // a class in a module under a namespace
    class Person
    {
    private:
        int m_age;
        std::string_view const m_name;

    public:
        Person(int age, std::string_view const &name);
        Person(int age);
        void printPerson();

        // getter as example...
        // can also add the setters if desired
        int getAge();
        std::string_view const &getName();
    };

    // a standalone function in a module under a namespace
    void print_person_external(Person &person);
};
```
the module defines under namespace `data_type`, a `Person` class that has 2 private members, 2 public constructors, and 2 public getters plus a standalone function that will operate on a `Person`.

we have to declare at the top of the file the **`module`** keyword and after the includes we state that we want to export this module with the name `data_type`

we also need to tell the compiler what do we want to export, by prepending a declaration with `export`. In this case we are declaring that we want to export a namespace, and by doing that everything in that namespace is exported (the class `Person` and also the function `print_person_external`)

I we would have placed the function outside the namespace it would not have been exported. If it is the case you want something outside the namespace and still exported as part of the module, just prepend the declaration with `export` and it will also be exported.

#### **the order is important...**<br>
if you place the `#include <iostream>` above the `module;` and try to compile the example, you will get the following error:

```shell
person_module.cpp:2:1: error: module-declaration only permitted as first declaration,
or ending a global module fragment
    2 | module;
      | ^~~~~~
person_module.cpp:4:8: error: module-declaration only permitted as first declaration,
or ending a global module fragment
    4 | export module data_type;
      |        ^~~~~~
person_module.cpp:7:1: error: ‘export’ may only occur after a module interface declaration
    7 | export namespace data_type
      | ^~~~~~
```
### the_person_class_implementation
```c++
module;

#include <iostream>
module data_type;

namespace data_type
{
  // implementations of the person Class here
  Person::Person(int age, std::string_view const &name) : m_age{age}, m_name{name} {};
  Person::Person(int age) : m_age{age}, m_name{"default"} {};

  void Person::printPerson()
  {
    std::cout << "person name is: " << m_name << std::endl;
    std::cout << "person age is: " << m_age << std::endl;
  }

  int Person::getAge()
  {
    return m_age;
  }

  std::string_view const &Person::getName()
  {
    return m_name;
  }
}
```

this file contains the implementation of the class constructors and methods.

note how we still use `module;` at the top of the file, followed by `#include` and the module name that we are implementing **WITHOUT** the `export` keyword, as we are not exporting anything.

we start with the namespace and inside the implementation of both constructors, the internal `printPerson()` function and both getters.  This is just like any other implementation.

note how we are referring to the private members `m_age` and `m_name` without problems as they are visible by `Person` itself.

#### **the order is still important...**<br>
as we've seen before order matters... If you would have placed the `#include <iostream>` after `module data_type;` you will get the following error when trying to compile:
```shell
In file included from /usr/include/c++/11/bits/exception_ptr.h:38,
                 from /usr/include/c++/11/exception:147,
                 from /usr/include/c++/11/ios:39,
                 from /usr/include/c++/11/ostream:38,
                 from /usr/include/c++/11/iostream:39,
                 from person.cpp:3:
/usr/include/c++/11/bits/cxxabi_init_exception.h:52:9: error: cannot declare ‘struct std::type_info’ in a different module
   52 |   class type_info;
      |         ^~~~~~~~~
<built-in>: note: declared here
In file included from /usr/include/c++/11/bits/exception_ptr.h:39,
                 from /usr/include/c++/11/exception:147,
                 from /usr/include/c++/11/ios:39,
                 from /usr/include/c++/11/ostream:38,
                 from /usr/include/c++/11/iostream:39,
                 from person.cpp:3:
/usr/include/c++/11/typeinfo:88:9: error: cannot declare ‘struct std::type_info’ in a different module
   88 |   class type_info
      |         ^~~~~~~~~
<built-in>: note: declared here
In file included from /usr/include/c++/11/bits/exception_ptr.h:40,
                 from /usr/include/c++/11/exception:147,
                 from /usr/include/c++/11/ios:39,
                 from /usr/include/c++/11/ostream:38,
                 from /usr/include/c++/11/iostream:39,
                 from person.cpp:3:
/usr/include/c++/11/new:89:27: error: cannot define ‘enum class std::align_val_t’ in different module
   89 |   enum class align_val_t: size_t {};
      |                           ^~~~~~
<built-in>: note: declared here
/usr/include/c++/11/new:89: confused by earlier errors, bailing out
```

### the_person_function_implementation
```c++
module;

#include <iostream>
module data_type;

namespace data_type
{
  //implementation of the functions here
  void print_person_external(Person &person)
  {
    std::cout << "person name is: " << person.getName() << std::endl;
    std::cout << "person age is: " << person.getAge() << std::endl;
  };
}

```
same as with the `Person` class implementation, we state this will be part of module `data_type`.

I just did this on a different file to get my head around bigger projects and how to keep possible big codebases tidy and separated. Even if it doesn't make much sense on this small program, it illustrates how breaking implementation of a module export can be achieved.

### the_main
```c++
import data_type;

#include <iostream>
using namespace data_type;
using namespace std;

int main(void)
{
  cout << "------\n";
  cout << "------ printing person details with INTERNAL to class function\n\n";

  cout << "------ person created with single arg ctor\n";
  Person person{1};
  cout << "person age: " << person.getAge() << endl;
  person.printPerson();

  cout << "\n------\n";
  cout << "------ person created with 2 args ctor\n";
  Person person2{2, "test"};
  cout << "person2 age: " << person2.getAge() << endl;
  cout << "person2 name: " << person2.getName() << endl;
  person2.printPerson();

  cout << "\n------\n";
  cout << "------ printing person details with EXTERNAL to class function\n\n";

  print_person_external(person);

  return 0;
}

```

this is a ceremonial `main.cpp` to illustrate the c++20 modules feature, note how we do not `#include` a module, but we `import` it.

after some `cout` to get our bearings into the console print, we use all the functionality declared and implemented on every file:
* multiple constructors
* multiple getters
* internal (to the class) print person function
* external (to the class) print person function

### g++_compilation_command
we have 4 files:
* main.cpp (main)
* person_module.cpp (module)
* person.cpp (implementation of Person::functions)
* person_func.cpp (implementation of isolated function)

to compile this you need to call g++ v11 indicating that you want to use c++20, and the modules features like this.
```shell
user@hostname:~$ g++-11 -std=c++20 -fmodules-ts -o person person_module.cpp person.cpp person_func.cpp main.cpp
```
the shell will not respond with anything, indicating everything went ok. To execute this little demo just hit `./person` on your console to run it.

```shell
# To execute this little demo just type `./person` and press enter
user@hostname:~$ ./person 

# you should see the following output

------
------ printing person details with INTERNAL to class function

------ person created with single arg ctor
person age: 1
person name is: default
person age is: 1

------
------ person created with 2 args ctor
person2 age: 2
person2 name: test
person name is: test
person age is: 2

------
------ printing person details with EXTERNAL to class function

person name is: default
person age is: 1
```
enjoy!