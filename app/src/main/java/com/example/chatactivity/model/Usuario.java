package com.example.chatactivity.model;


public class Usuario {
    private Long id;
    private String nombre;
    private String clave;
    private boolean online;

    public Usuario(Long id, String nombre, String clave) {
        this.id = id;
        this.nombre = nombre;
        this.clave = clave;
        this.online = false;
    }

    public Usuario(String nombre, String clave) {
        this.nombre = nombre;
        this.clave = clave;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }
}


