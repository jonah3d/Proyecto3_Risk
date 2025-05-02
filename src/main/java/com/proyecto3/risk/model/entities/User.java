package com.proyecto3.risk.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
@Entity
@Table(name = "user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic(optional = false)
    @Column(nullable = false, name = "first_name")
    @NotEmpty(message = "First name cannot be null or empty")
    @JsonProperty("firstName")
    private String firstName;

    @Basic(optional = false)
    @Column(nullable = false, name = "last_name")
    @NotNull(message = "Last name cannot be null or empty")
    @JsonProperty("lastName")
    private String lastName;

    @Basic(optional = false)
    @Column(nullable = false, unique = true, name = "email")
    @NotNull(message = "Email cannot be null or empty")
    @Email(message = "Email should be valid")
    @JsonProperty("email")
    private String email;

    @Basic(optional = false)
    @Column(nullable = false, unique = true, name = "username")
    @NotNull(message = "Username cannot be null or empty")
    @JsonProperty("username")
    private String username;

    @Basic(optional = false)
    @Column(nullable = false, name = "password")
    @NotNull(message = "Password cannot be null or empty")
    @JsonProperty("password")
    private String password;

    @NotNull(message = "Avatar cannot be null")
    @ManyToOne
    @JoinColumn(nullable = false, name = "avatar_id")
    @JsonProperty("avatar")
    private Avatars avatar;

    @Column(name = "wins")
    @JsonProperty("wins")
    private int wins;

    @Column(name = "games")
    @JsonProperty("games")
    private int games;

    public User() {}

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Avatars getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatars avatar) {
        this.avatar = avatar;
        if (avatar != null) {
            avatar.getUsers().add(this);
        }
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", avatar='" + (avatar != null ? avatar.getUrl() : "null") + '\'' +
                ", wins=" + wins +
                ", games=" + games +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}