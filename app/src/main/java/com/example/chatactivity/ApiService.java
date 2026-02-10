package com.example.chatactivity;

import com.example.chatactivity.model.Grupo;
import com.example.chatactivity.model.Mensaje;
import com.example.chatactivity.model.Usuario;
import com.example.chatactivity.service.NombreGrupoRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    //Enviar mensaje
    @POST("/api/mensajes/enviar")
    Call<Mensaje> enviarMensaje(@Query("remitente") String remitente, @Query("destinatario") String destinatario, @Query("mensaje") String mensaje);

    //Recibir Mensaje
    @GET("/api/mensajes/recibir/{destinatario}")
    Call<List<Mensaje>> recibirMensajes(@Path("destinatario") String destinatario);

    //Historial de mensajes
    @GET("/api/mensajes/history")
    Call<List<Mensaje>> obtenerHistorialMensajes();

    //Registro de usuario
    @POST("/api/users/register")
    Call<Usuario> registrarUsuario(@Body Usuario usuario);
    // Call<Usuario> registrarUsuario(@Query("nombre") String nombre, @Query("clave") String clave);

    //inicio de sesión
    @POST("/api/users/login")
    Call<Usuario> iniciarSesion(@Body Usuario usuario); // Usamos @Body y Credenciales
    // Call<Usuario> iniciarSesion(@Query("nombre") String nombre, @Query("clave") String clave);

    //usuarios conectados
    @GET("/api/users/online")
    Call<List<Usuario>> obtenerUsuariosConectados();

    //Mensajes privados
    @GET("/api/mensajes/private/{usuario}")
    Call<List<Mensaje>> obtenerMensajesPrivados(@Path("usuario") String usuario);

    // Historial de mensajes entre dos usuarios
    @GET("/api/mensajes/conversacion")
    Call<List<Mensaje>> obtenerMensajes(@Query("remitente") String remitente, @Query("destinatario") String destinatario);

    //Crear un grupo
    @POST("/api/groups/create")
    Call<Grupo> crearGrupo(@Body Grupo grupo);
   // Call<Grupo> crearGrupo(@Query("nombreGrupo") String nombreGrupo, @Query("miembros") List<String> miembros);

    //Obtener Grupos
    @GET("/api/groups/creados")
    Call<List<Grupo>>obtenerGrupos();

    // Renombrar Grupo
  //  @PUT("/api/groups/rename")
   // Call<String> renombrarGrupo(@Query("nombreAntiguo") String nombreAntiguo, @Query("nuevoNombre") String nuevoNombre);
    @PUT("/api/groups/rename")
    Call<Grupo> modificarNombreGrupo(@Body NombreGrupoRequest request);

    //Eliminar Grupos
    @DELETE("/api/groups/{nombreGrupo}")
    Call<Void> eliminarGrupo(@Path("nombreGrupo") String nombreGrupo);


}
