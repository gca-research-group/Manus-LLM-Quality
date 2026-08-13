package com.store.model;

public class Supplier {
    private String id;
    private String name;

    public Supplier(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
