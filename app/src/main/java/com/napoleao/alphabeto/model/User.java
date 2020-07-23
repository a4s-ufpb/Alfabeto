package com.napoleao.alphabeto.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private List<Challenge> challenges = new ArrayList<>();
    private List<Tema> contexts = new ArrayList<>();

    public User() {
    }

    public User(Long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    public List<Tema> getContexts() {
        return contexts;
    }

    public void setContexts(List<Tema> contexts) {
        this.contexts = contexts;
    }
}
