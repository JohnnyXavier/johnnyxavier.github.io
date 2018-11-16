package io.ioforge;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NoteAboutFor {

    private static List<String> names = Arrays.asList("foo", "bar", "baz", "foobar", "raboof");


    public static void main(String[] args) {
        standardForLoop();
        iteratorForLoop();
        iteratorForEachRemaining();
        forEachLoop();
        collectionForEachWithLambda();
        collectionForEachWithMethodReference();
        listIteratorForLoopForward();
        listIteratorForLoopBackwards();

    }

    private static void iteratorForEachRemaining() {
        Iterator<String> iterator = names.iterator();
        iterator.forEachRemaining(name -> doSomething(name));
    }

    private static void listIteratorForLoopBackwards() {
        for (ListIterator<String> listIterator = names.listIterator(names.size()); listIterator.hasPrevious(); ) {
            doSomething(listIterator.previous());
        }
    }

    private static void listIteratorForLoopForward() {
        for (ListIterator<String> listIterator = names.listIterator(); listIterator.hasNext(); ) {
            doSomething(listIterator.next());
        }
    }

    private static void collectionForEachWithMethodReference() {
        names.forEach(NoteAboutFor::doSomething);
    }

    private static void collectionForEachWithLambda() {
        names.forEach(name -> doSomething(name));
    }

    private static void forEachLoop() {
        for (String name : names) {
            doSomething(name);
        }
    }

    private static void iteratorForLoop() {
        for (Iterator<String> i = names.iterator(); i.hasNext(); )
            doSomething(i.next());
    }

    public static void standardForLoop() {
        for (int i = 0; i < names.size(); i++) {
            doSomething(names.get(i));
        }
    }

    public static void doSomething(String name) {
        System.out.println(name);
    }
}
