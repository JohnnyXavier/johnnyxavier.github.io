---
title: Quarkus configuration utilities
excerpt: This note is about configuring a Quarkus app
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

This note is about configuring an application within Quarkus ecosystem and using YAML instead of properties

references:

* [config-yaml](https://quarkus.io/version/main/guides/config-yaml)
* [config-reference](https://quarkus.io/version/main/guides/config-reference)
* [config-mappings](https://quarkus.io/version/main/guides/config-mappings)

---

## YAML

to use yaml config to a quarkus app you only need to add the corresponding dependency to the project.

maven:

```xml

<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-config-yaml</artifactId>
</dependency>
```

and that is basically it.

## profiles with quarkus

with `yaml` as configuration source if you want to add profiles you just prepend the properties to configure with the profile %name between
double quotes.

for example:

```yaml
"%dev":
  quarkus:
    http:
      cors:
        ~: true
        origins: "/.*/"
        access-control-allow-credentials: true
```

this tells quarkus how to configure `cors` when running on `dev`.

I chose that property as it has something significant... the `~` character.<br>
as `cors` is a property in itself and the prefix of the other properties, we use the `~` as a `null` key, to overcome the `YAML` format
namespace limitation.

NOT SUPPORTED!

```yaml
"%dev":
  quarkus:
    http:
      cors: true #invalid
        origins: "/.*/"
        access-control-allow-credentials: true
```

the above code, which is not supported by `YAML`, is te reason for the `~` when configuring properties that are both properties and
prefixes.

## from YAML to code

in order to be able to use the configuration files to configure our own code, `Quarkus` makes it just as easy as `SpringBoot` to do it.

let's examine a few lines in the `application.yaml` file.

```yaml
obfuscation:
  cities:
    - new_york
    - angeles
    - chicago
    - houston
    - phoenix
    - philly
    - antonio
  # [full list truncated]
  alphabet:
    - alpha
    - bravo
    - charlie
    - delta
    - echo
    - foxtrot
    - golf
  # [full list truncated]
```

when deleting a user we want to do a soft delete (inactivation) and not a hard delete (full record deletion) so we can keep track of who
created a given ticket for example, even if after they leave the project. The list above will be used to replace user's data with "fantasy"
data.

what I did then was to generate a list of future fake name / lastname and put it on the properties file under obfuscation. To add
a little more complexity I broke the list into 2. Just for showcase.

### mapping the properties into code.

to get those 2 list of names we just need to do the as follows.

```java

@ConfigMapping(prefix = "obfuscation")
public interface ObfuscationConfig {

    List<String> cities();

    List<String> alphabet();

}
```

`Quarkus` (`SmallRye` Config lib in this case), requires the annotation `@ConfigMapping` and pass the prefix of our properties.

given that cities and alphabet are a list of names (String), we create 2 methods that return a `List<String>`, take no parameters and are named
just like our source properties.

### accessing the mapped properties

the `@ConfigMapping` annotation allows for CDI, so to use the properties mapped as methods on our interface, we just inject it where
needed.

```java

@ApplicationScoped
public class SecurityUtils {

    private final Random random = new Random();

    private final ObfuscationConfig config;

    // constructor DI
    public SecurityUtils(final ObfuscationConfig config) {
        this.config = config;
    }


    public void obfuscateUser(final UserEntity user) {

        List<String> cities  = config.cities();
        List<String> letters = config.alphabet();

        String firstName = letters.get(random.nextInt(letters.size()));
        String lastName  = cities.get(random.nextInt(cities.size()));
        String email     = firstName + "@" + lastName + ".com";

        user.setEmail(email);
    }
}
```

that's a little fun utility to showcase how properties are mapped and accessed within `Quarkus`.