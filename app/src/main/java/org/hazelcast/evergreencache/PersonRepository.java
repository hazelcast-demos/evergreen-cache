package org.hazelcast.evergreencache;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.util.List;

public class PersonRepository {

    private final JdbcTemplate template;

    public PersonRepository(JdbcTemplate template) {
        this.template = template;
    }

    public long count() {
        return template.queryForObject("SELECT COUNT(*) FROM Person", Long.class);
    }

    public List<Person> findAll() {
        return template.query(
                "SELECT id, firstName, lastName, birthdate FROM Person",
                (rs, rownum) -> {
                    Person person = new Person();
                    person.setId(rs.getLong("id"));
                    person.setFirstName(rs.getString("firstName"));
                    person.setLastName(rs.getString("lastName"));
                    Date birthdate = rs.getDate("birthdate");
                    if (birthdate != null) {
                        person.setBirthdate(birthdate.toLocalDate());
                    }
                    return person;
                });
    }

    public void save(Person person) {
        template.update(
                "UPDATE Person SET firstName = ?, lastName = ?, birthdate = ? WHERE id = ?",
                person.getFirstName(),
                person.getLastName(),
                person.getBirthdate(),
                person.getId()
        );
    }
}
