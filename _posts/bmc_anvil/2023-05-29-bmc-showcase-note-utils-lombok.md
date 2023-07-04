---
title: Utilities - lombok
excerpt: This note is lombok annotations
sidebar:
  title: "/BMC_Anvil"
  nav: sidebar-bmc_showcase
---

This note is about lombok annotations and how they reduce boilerplate code and help generate and keep in sync core methods

references:

* [project lombok](https://projectlombok.org/)

---

## intro

[project lombok](https://projectlombok.org/) or just `lombok` is a library that lifts the weight off the most common boilerplate code found
in java apps.

it is commonly used to avoid writing standard getters / setters, equals + hashcode, adding in loggers or taking care of various
constructors and builders among many other goodies.

`lombok` is praised and detracted here and there, if you are interested about those types of disputes here is
a [reddit thread](https://www.reddit.com/r/java/comments/z1fgj7/should_you_still_be_using_lombok/).

it remains to see what will happen in the future with the JDK APIs lombok use.

if you are sincerely interested in how `lombok` does what it does, and criticise or praise them for their work check them out over
their [GitHub repo](https://github.com/projectlombok/lombok)!

for my part the benefits of using it greatly outweigh the possible penalties that may or may not occur in the future. If for any reason
lombok stops working with a future Java release, I'll [delombok](https://projectlombok.org/features/delombok) the app and that's it.

### java 21 and lombok as of this writing

there was a small change in the JDK and lombok
will [not work on Java 21 for the moment](https://github.com/projectlombok/lombok/issues/3393).

> note:<br>
> at the time of this writing JDK 21 is in ea and is expected to be ga by 2023/09/19<br>
> [jdk 21 info](https://openjdk.org/projects/jdk/21/)

specifically:<br>
**JDK 21** is changing `JCTree` to `JCFieldAccess` and lombok refers to the former type

**JDK 20** `com.sun.tools.javac.tree.JCTree`

```java
    public static class JCImport extends JCTree implements ImportTree {

    public JCTree                         qualid;
    public com.sun.tools.javac.code.Scope importScope;

    protected JCImport(JCTree qualid, boolean importStatic) {
        this.qualid = qualid;
        this.staticImport = importStatic;
    }
}
```

**JDK 21** `com.sun.tools.javac.tree.JCTree`

```java
    public static class JCImport extends JCTree implements ImportTree {

    public JCFieldAccess                  qualid;
    public com.sun.tools.javac.code.Scope importScope;

    protected JCImport(JCFieldAccess qualid, boolean importStatic) {
        this.qualid = qualid;
        this.staticImport = importStatic;
    }
}
```

there is a fix in the making [already here](https://github.com/Rawi01/lombok/commit/d9e6e4d231b67acd0f467e503d2c9f1ffb97ae03) if you are
interested

## lombok usage in {{ site.showcase.name }}

there are 2 main usages across the app:

* getters and setters
* equals and hashcode

the `BaseEntity.java`:

```java

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = LAZY)
    private UserEntity createdBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
```

the above is the base `entity` for the application's data access and will show a few usages of `lombok` here and on inheritors.<br>
as this is a `lombok` note we are going to pass on JPA annotations and concentrate only on `lombok` ones.

### get / set

the 2 annotations `@Getter` and `@Setter` create for each field a `public TYPE getFieldName()`
and `public void setFieldName(final TYPE fieldName)`.

this is very practical, saving us a lot of boilerplate and when prototyping not having to care for adding or deleting get/set is a bliss.

### equals and hashcode

`.equals()` will define what makes 2 objects to be "equal" to one another by implementing what `equivalent` means for a given data type.<br>
we use `.equals()` to know if an object """IS the same as""" another object regarding certain parameters.

with our example `entity` above when can we say we retrieve the same employee record from the DB?<br>
we could say we have the same employee record based on a person's first and/or last name... but...<br>
we could say we have the same employee record based on a person's passport... but...<br>
we could say we have the same employee record based on a person's SSN... AHA!<br>

what makes a difference among the 3 possible equality choices is the uniqueness and **immutability** of the data chosen?<br>
a person can change names or have the same as another, and the passport number although unique, will change once renewed.<br>
The SSN on the other hand, will remain unique and immutable for life. So if we retrieve a person's record and want to know if it is equal to
one we have in memory, we apply the `.equals()` which will compare the SSN of both objects.

`.hashcode()` will transform our object into an int value. There are some rules to follow thou as
specified [in the docs](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/lang/Object.html#hashCode()) that tie it tightly
to `.equals()`.

we need these 2 methods overridden in our entities with our custom definition of equality for each as they are going to be used in hash
based collections such as `HashSet`.

let's see what `lombok` can do for us to avoid the `equals` and `hashcode` boilerplate.

the annotation that generates both methods is `@EqualsAndHashCode(onlyExplicitlyIncluded = true)`. The addition
of `onlyExplicitlyIncluded = true` means to only consider the fields we explicitly include for generating both methods. That is achieved by
annotating the desired fields with `@EqualsAndHashCode.Include`.

in our entity, the only method marked as included is the `private UUID id`. Given that UUIDs are unique, and once we save a record they will
never change, we can safely say that if 2 records have the same `id` they are the same record...

> showcase / teaching note:<br>
> can you see down the road why UUIDs were chosen over auto-incrementing values?<br>
> among other things, it makes comparing 2 records trivial as we can target a single field.

what will those annotations generate?

regarding equals():

```java
public abstract class BaseEntity {

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof final BaseEntity other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$id  = this.getId();
                Object other$id = other.getId();
                if (this$id == null) {
                    return other$id == null;
                } else return this$id.equals(other$id);
            }
        }
    }
}
```

so after doing some de rigueur comparisons, `lombok` generates what we expected, we compare 2 objects based on their ids:

```java
if(!this$id.equals(other$id)){
        return false;
        }
```

regarding hashCode():

```java
public abstract class BaseEntity {

    public int hashCode() {
        int    PRIME  = true;
        int    result = 1;
        Object $id    = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        return result;
    }
}
```

disregarding the technique used, we see `lombok` is also using the `id` to generate a hash of the object.

the advantage of using `lombok` here, is that if we change what is included in the equality, both methods will be updated without requiring
manual intervention.

## inheritance and lombok

that base entity is used as the common set of properties for all other entities, how do we propagate equals / hashcode to inheritors?

let check an example:

```java

@Entity
@Table(name = "comment")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class CommentEntity extends BaseEntity {

    @Column(columnDefinition = "text")
    private String comment;

    @ManyToOne
    private CardEntity card;

}
```

in this class we see again the getter and setter plus a slightly modified call
to `@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)`, it has the extra parameter `callSuper = true`. That parameter
means to go to the super class to look for what defines equals/hashcode there.

when we check the resulting class this is what we will see:

```java
public abstract class BaseEntity {

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof final CommentEntity other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else {
                return super.equals(o);
            }
        }
    }

    public int hashCode() {
        int result = super.hashCode();
        return result;
    }
}
```

as you can see the generated code calls on `BaseEntity` methods, again keeping everything in sync without manual intervention.

but what if we want to keep the `BaseEntity` methods that use the `id` as equality parameter AND add another one, say the `comment` field?
we just annotate the field with `@EqualsAndHashCode.Include` and `lombok` will use the parent's method plus the new field to generate
equals / hashCode.

it does not make sense in our case and this tip is for demonstration purposes only, so let's check what would lombok have generated if
we had included the `comment` field.

```java
public abstract class BaseEntity {

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof final CommentEntity other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else {
                Object this$comment  = this.getComment();
                Object other$comment = other.getComment();
                if (this$comment == null) {
                    return other$comment == null;
                } else return this$comment.equals(other$comment);
            }
        }
    }

    public int hashCode() {
        int    PRIME    = true;
        int    result   = super.hashCode();
        Object $comment = this.getComment();
        result = result * 59 + ($comment == null ? 43 : $comment.hashCode());
        return result;
    }

}
```

as you can see, both methods include the comment field in both methods and the parent's ones too.

