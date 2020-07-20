package ru.otus.hl.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User extends UserAuth {

    private String firstName;
    private String lastName;
    private String secondName;
    private Integer age;
    private Gender gender = Gender.NOT_SET;
    private City city;
    

    public User(long id) {
        setId(id);
    }

    public boolean hasCity() {
        return city != null && city.getName() != null && !city.getName().trim().isEmpty();
    }

    public User(long id, String login, String password, String firstName, String lastName, String secondName, Integer age, Gender gender,
            City city, String interests) {
        super(id, login, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.secondName = secondName;
        this.age = age;
        this.gender = gender;
        this.city = city;
        this.interests = interests;
    }


    private String interests;
}