package com.myapp.backend.model;

import java.util.UUID;

public abstract class User {
    protected String name;
    protected String id;
    protected String email;
    protected String password;
    protected int age;

    public User() {
        // Generate ID even in no-arg constructor
        this.id = UUID.randomUUID().toString();
    }

    public User(String name, String email, String password) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() { return name; }
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public int getAge() { return age; }

    public void setName(String name) { this.name = name; }
    public void setId(String id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setAge(int age) { this.age = age; }
}
