package com.proyecto3.risk.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "avatars")
public class Avatars {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "url", unique = true, nullable = false)
    private String url;

    @OneToMany(mappedBy = "avatar", cascade = CascadeType.MERGE)
    @JsonIgnore
    private List<User> Users = new ArrayList<User>();

    public Avatars() {
    }

    public Avatars(String name, String url) {
        this.name = name;
        this.url = url;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
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


    public List<User> getUsers() {
        return Users;
    }

    public void setUsers(List<User> users) {
        Users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Avatars avatars = (Avatars) o;
        return id == avatars.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
