package ru.iu3.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "artists")
@Access(AccessType.FIELD)
public class Artist {

    public Artist() { }
    public Artist(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    public long id;

    @Column(name = "name", nullable = false, unique = true)
    public String name;

    @Column(name = "age", nullable = false)
    public String age;

    @ManyToOne()
    @JoinColumn(name = "countryid")
    public Country country;

    @OneToMany(mappedBy = "artist")
    public Set<Painting>
            paintings = new HashSet<>();
}