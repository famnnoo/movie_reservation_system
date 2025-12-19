package com.app.movie.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private String address;
    private Double latitude;
    private Double longitude;


    @OneToMany(mappedBy = "location")
    private java.util.Set<Cinema> cinemas = new java.util.HashSet<>();
}
