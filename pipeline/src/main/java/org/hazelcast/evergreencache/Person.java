package org.hazelcast.evergreencache;

import java.io.Serializable;
import java.time.LocalDate;

public class Person implements Serializable {

    private static final long serialVersionUID = 42L;

    public Long id;

    public String firstName;
    public String lastName;

    public LocalDate birthdate;

    public void setBirthdate(long daysSinceBirth) { //object mapping uses this setter
        this.birthdate = daysSinceBirth == 0 ? null : LocalDate.ofEpochDay(daysSinceBirth);
    }
}
