package com.proyecto3.risk.model;

import jakarta.persistence.*;

@Entity
@Table(name = "avatars")
public class Avatars {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "url", unique = true, nullable = false, length = 45)
    private String url;

    public Avatars() {
    }

    public Avatars(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }


    public String getUrl() {
        return url;
    }

    public int getId() {
        return id;
    }
}
