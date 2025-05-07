package com.proyecto3.risk.model.dtos;

public class UserResponseDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private int wins;
    private int games;
    private AvatarResponseDto avatar; // A nested DTO for the avatar



    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public AvatarResponseDto getAvatar() { return avatar; }
    public void setAvatar(AvatarResponseDto avatar) { this.avatar = avatar; }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }
}
