package org.hazelcast.evergreencache;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Form {

    private final List<Person> persons;

    public Form(long count) {
        persons = LongStream.range(0, count)
                .mapToObj(index -> new Person())
                .collect(Collectors.toList());
    }

    public Form(List<Person> persons) {
        this.persons = Collections.unmodifiableList(persons);
    }

    public List<Person> getPersons() {
        return persons;
    }
}