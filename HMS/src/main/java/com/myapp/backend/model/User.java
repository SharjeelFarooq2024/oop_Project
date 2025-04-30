package com.myapp.backend.model;

public abstract class User {
    protected String name;
    protected String id;
    protected String email;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }
    public String getId() { return id; }
    public String getEmail() { return email; }
}
