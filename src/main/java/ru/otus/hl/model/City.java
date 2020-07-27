package ru.otus.hl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {

    private int id;
    private String name;
    
    public City(String name) {
        this.name = name;
    }
}