package com.example.chatactivity.model;

import java.util.List;

public class Grupo {
    private Long id;
    private String nombreGrupo;
    private List<String> miembros;

    public Grupo(Long id, String nombreGrupo, List<String> miembros) {
        this.id = id;
        this.nombreGrupo = nombreGrupo;
        this.miembros = miembros;
    }

    public Grupo(String nombreGrupo, List<String> miembros) {
        this.nombreGrupo = nombreGrupo;
        this.miembros = miembros;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreGrupo() { return nombreGrupo; }
    public void setNombreGrupo(String nombreGrupo) { this.nombreGrupo = nombreGrupo; }
    public List<String> getMiembros() { return miembros; }
    public void setMiembros(List<String> miembros) { this.miembros = miembros; }
}
