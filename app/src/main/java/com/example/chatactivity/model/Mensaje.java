package com.example.chatactivity.model;

import android.os.Build;

import java.time.LocalDateTime;
import java.util.Date;

public class Mensaje {
    private Long id;
    private String remitente;
    private String destinatario; // Para mensajes privados
    private String grupoId; // Para mensajes en grupo
    private String mensaje;
    private LocalDateTime timestamp;
    private boolean leido;
    private String tipo;  // "TIPO_ENVIADO" o "TIPO_RECIBIDO"

    // Constructor1
    public Mensaje(Long id, String remitente, String destinatario, String grupoId, String mensaje, LocalDateTime timestamp) {
        this.id = id;
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.grupoId = grupoId;
        this.mensaje = mensaje;
        this.timestamp = timestamp;
        this.leido = false;
    }
    // Constructor2
    public Mensaje(Long id, String usuarioNombre, String otroUsuario, String mensajeTexto, LocalDateTime now) {
        this.id = id;
        this.remitente = usuarioNombre;
        this.destinatario = otroUsuario;
        this.mensaje = mensajeTexto;
        this.timestamp = now;
    }

    // Constructor3
    public Mensaje(Long id, String remitente, String destinatario, String mensaje, LocalDateTime timestamp, String tipo) {
        this.id = id;
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.timestamp = timestamp;
        this.tipo = tipo;  // asigna el tipo
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRemitente() { return remitente; }
    public void setRemitente(String remitente) { this.remitente = remitente; }
    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }
    public String getGrupoId() { return grupoId; }
    public void setGrupoId(String grupoId) { this.grupoId = grupoId; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
    public String getTipo() {return tipo;}
    public void setTipo(String tipo) {this.tipo = tipo;}
}