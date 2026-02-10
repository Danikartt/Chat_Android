package com.example.chatactivity.service;

public class NombreGrupoRequest {
    private String nombreActual;
    private String nuevoNombre;

    // Constructor, getters y setters
    public NombreGrupoRequest(String nombreActual, String nuevoNombre) {
        this.nombreActual = nombreActual;
        this.nuevoNombre = nuevoNombre;
    }

    public String getNombreActual() {
        return nombreActual;
    }

    public void setNombreActual(String nombreActual) {
        this.nombreActual = nombreActual;
    }

    public String getNuevoNombre() {
        return nuevoNombre;
    }

    public void setNuevoNombre(String nuevoNombre) {
        this.nuevoNombre = nuevoNombre;
    }
}
