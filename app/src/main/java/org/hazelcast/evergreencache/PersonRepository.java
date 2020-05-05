package org.hazelcast.evergreencache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PersonRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonRepository.class);

    private final JdbcTemplate template;
    private final Cache entitiesCache;
    private final Cache queryCache;

    public PersonRepository(
            JdbcTemplate template,
            Cache entitiesCache,
            Cache queryCache) {
        this.template = template;
        this.entitiesCache = entitiesCache;
        this.queryCache = queryCache;
    }

    public long count() {
        return findAll().size();
    }

    public List<Person> findAll() {
        var select = "SELECT id, firstName, lastName, birthdate FROM Person";
        var wrapper = queryCache.get(select);
        if (wrapper == null) {
            var keys = new LinkedList<>();
            var persons = template.query(
                    select,
                    (rs, rownum) -> {
                        var person = new Person();
                        var id = rs.getLong("id");
                        keys.add(id);
                        person.setId(id);
                        person.setFirstName(rs.getString("firstName"));
                        person.setLastName(rs.getString("lastName"));
                        var birthdate = rs.getDate("birthdate");
                        if (birthdate != null) {
                            person.setBirthdate(birthdate.toLocalDate());
                        }
                        entitiesCache.put(id, person);
                        return person;
                    });
            queryCache.put(select, keys);
            return persons;
        } else {
            LOGGER.debug("Found query result in cache");
            var keys = (List<Long>) wrapper.get();
            LOGGER.debug("Cached keys are {}", keys);
            return keys.stream()
                    .map(key -> entitiesCache.get(key, Person.class))
                    .collect(Collectors.toList());

        }
    }

    public void save(Person person) {
        entitiesCache.put(person.getId(), person);
        template.update(
                "UPDATE Person SET firstName = ?, lastName = ?, birthdate = ? WHERE id = ?",
                person.getFirstName(),
                person.getLastName(),
                person.getBirthdate(),
                person.getId()
        );
    }
}