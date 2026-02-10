package com.example.chatactivity.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatactivity.ApiService;
import com.example.chatactivity.R;
import com.example.chatactivity.RetrofitClient;
import com.example.chatactivity.adapters.AdapterChat;
import com.example.chatactivity.model.Mensaje;
import com.example.chatactivity.service.ChatWebSocketClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdapterChat adapterChat;
    private List<Mensaje> listaMensajes;
    private EditText etMensaje;
    private String usuarioNombre;
    private String destinatario;
    private Button btnEnviar;


    private ChatWebSocketClient webSocketClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recuperar usuarioNombre del Bundle
        if (getArguments() != null) {
            usuarioNombre = getArguments().getString("usuario_nombre");

            // Verificar si es un chat privado o grupal
            if (getArguments().containsKey("grupo_nombre")) {
                destinatario = getArguments().getString("grupo_nombre");  // Aquí asignamos el grupo como destinatario
            } else {
                destinatario = getArguments().getString("destinatario");
            }

            Log.d("ChatFragment", "Usuario: " + usuarioNombre + ", Destinatario: " + destinatario);

            if (destinatario == null || destinatario.isEmpty()) {
                Log.e("ChatFragment", "Destinatario no recibido correctamente.");
            }
            // Actualizar el TextView con el nombre del destinatario
            TextView tvDestinatario = view.findViewById(R.id.tvDestinatario);
            if (tvDestinatario != null) {
                tvDestinatario.setText(destinatario);
            }

        }

        recyclerView = view.findViewById(R.id.recyclerChat);
        etMensaje = view.findViewById(R.id.etmensaje);
        btnEnviar = view.findViewById(R.id.btnenviarmensaje);

        listaMensajes = new ArrayList<>();
        adapterChat = new AdapterChat(listaMensajes, usuarioNombre);

        recyclerView.setAdapter(adapterChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Cargar los mensajes guardados en SharedPreferences
        List<Mensaje> mensajesGuardados = cargarMensajes(destinatario);
        List<Mensaje> mensajesConvertidos = new ArrayList<>();

        for (Mensaje mensaje : mensajesGuardados) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String tipo = (destinatario.equals(usuarioNombre)) ? "TIPO_ENVIADO" : "TIPO_RECIBIDO";
                mensajesConvertidos.add(new Mensaje(System.currentTimeMillis(), usuarioNombre, destinatario, mensaje.getMensaje(), LocalDateTime.now(), tipo));
            }
        }

        if (adapterChat == null) {
            adapterChat = new AdapterChat(mensajesConvertidos, usuarioNombre);
            recyclerView.setAdapter(adapterChat);
        } else {
            adapterChat.actualizarMensajes(mensajesConvertidos);
        }

        recyclerView.scrollToPosition(mensajesConvertidos.size() - 1);



        /////////////////////////////////////  ConectarWebSockets  /////////////////////////////////////
        if (usuarioNombre == null) {
            Log.e("ChatFragment", "El nombre del usuario es null");
        } else {
            try {
                URI uri = new URI("ws://192.168.1.38:8081/chat?username=" + URLEncoder.encode(usuarioNombre, "UTF-8"));
                webSocketClient = new ChatWebSocketClient(uri, this::mensajeRecibido);
                webSocketClient.connect();
            } catch (URISyntaxException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        btnEnviar.setOnClickListener(v -> {
            Log.d("ChatFragment", "Botón de enviar presionado");
            enviarMensaje();
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        // Cargar los mensajes anteriores cada vez que el fragmento se vuelve a mostrar
      //  cargarMensajesAnteriores();
    }
    private void mensajeRecibido(String mensajeJson) {

        // Imprimir el mensaje recibido en la consola para depuración
        Log.d("Chat", "Mensaje recibido: " + mensajeJson);

        getActivity().runOnUiThread(() -> {
            try {
                Gson gson = new Gson();
                JsonObject jsonObject = JsonParser.parseString(mensajeJson).getAsJsonObject();

                // Verificar si es una notificación
                if (jsonObject.has("tipo") && jsonObject.get("tipo").getAsString().equals("notificacion")) {
                    String mensaje = jsonObject.has("mensaje") ? jsonObject.get("mensaje").getAsString() : "Mensaje sin contenido";

                    // Aquí, podemos verificar si el mensaje es sobre la desconexión del destinatario
                    if (mensaje.contains("desconectado")) {
                        // Mostrar un Toast si el destinatario se ha desconectado
                        Toast.makeText(getContext(), "El destinatario se ha desconectado", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    Log.d("Chat", "Notificación recibida: " + mensaje);
                    return; // No continuar con el procesamiento del mensaje normal
                }

                // Verificar si el mensaje es grupal
                boolean esGrupo = jsonObject.has("esGrupo") && jsonObject.get("esGrupo").getAsBoolean();
                String destinatarioMensaje = null;

                if (jsonObject.has("destinatario") && !jsonObject.get("destinatario").isJsonNull()) {
                    destinatarioMensaje = jsonObject.get("destinatario").getAsString();
                }

                // Filtrar los mensajes que no deben ser recibidos por el remitente
                String remitenteMensaje = jsonObject.has("remitente") ? jsonObject.get("remitente").getAsString() : null;

                // Si el remitente del mensaje es el mismo que el usuarioNombre, no lo procesamos
                if (remitenteMensaje != null && remitenteMensaje.equals(usuarioNombre)) {
                    Log.d("ChatFragment", "Mensaje recibido es del remitente, no se procesa.");
                    return; // No mostrar el mensaje al remitente
                }

                // Si es un mensaje grupal, mostrarlo solo si el destinatario coincide con el grupo actual
                if (esGrupo) {

                    if (destinatarioMensaje != null && destinatarioMensaje.equals(usuarioNombre)) {
                        // Mostrar el mensaje
                        Mensaje mensajeRecibido = gson.fromJson(mensajeJson, Mensaje.class);
                        String mensajeMostrar = mensajeRecibido.getMensaje(); // Extraer solo el contenido del mensaje

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            listaMensajes.add(new Mensaje(System.currentTimeMillis(), mensajeRecibido.getRemitente(), usuarioNombre, mensajeMostrar, LocalDateTime.now()));
                        }
                        adapterChat.notifyItemInserted(listaMensajes.size() - 1);
                        recyclerView.scrollToPosition(listaMensajes.size() - 1);
                    }
                } else {
                    // Si es mensaje privado, procesar normalmente
                    Mensaje mensajeRecibido = gson.fromJson(mensajeJson, Mensaje.class);
                    String mensajeMostrar = mensajeRecibido.getMensaje(); // Extraer solo el contenido del mensaje

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        listaMensajes.add(new Mensaje(System.currentTimeMillis(), mensajeRecibido.getRemitente(), usuarioNombre, mensajeMostrar, LocalDateTime.now()));
                    }
                    adapterChat.notifyItemInserted(listaMensajes.size() - 1);
                    recyclerView.scrollToPosition(listaMensajes.size() - 1);

                    // Guardar mensaje en SharedPreferences
                    List<Mensaje> mensajes = cargarMensajes(destinatario);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Mensaje nuevoMensaje = new Mensaje(System.currentTimeMillis(), usuarioNombre, destinatario, mensajeMostrar, LocalDateTime.now());
                        mensajes.add(nuevoMensaje); // Agregar el mensaje a la lista
                    }
                    Log.d("ChatFragment", "Guardando mensaje recibido: " + mensajeMostrar);
                    guardarMensajes(destinatario, mensajes);

                }

            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                Log.e("Chat", "Error al procesar el JSON del mensaje");
            }
        });
    }

    private void enviarMensaje() {
        Log.d("ChatFragment", "Ejecutando enviarMensaje()");
        String mensajeTexto = etMensaje.getText().toString().trim();
        // Verificar si el mensaje no está vacío y si el destinatario es válido
        if (!mensajeTexto.isEmpty() && destinatario != null && !destinatario.isEmpty()) {



            // Verificar si el WebSocket está conectado antes de enviar el mensaje
            if (webSocketClient != null && webSocketClient.isOpen()) {

                // Crear un JSON para enviar
                JSONObject jsonMensaje = new JSONObject();
                try {
                    jsonMensaje.put("remitente", usuarioNombre);
                    jsonMensaje.put("destinatario", destinatario);  // Asegúrate de que el destinatario esté correctamente seteado
                    jsonMensaje.put("mensaje", mensajeTexto);

                    Log.d("ChatFragment", "Enviando mensaje: " + jsonMensaje);

                    // Agregar un flag para saber si es mensaje grupal
                    if (getArguments().containsKey("grupo_nombre")) {
                        jsonMensaje.put("esGrupo", true);

                    } else {
                        jsonMensaje.put("esGrupo", false);
                    }
                    // Enviar el mensaje a través del WebSocket
                    webSocketClient.send(jsonMensaje.toString());

                    // Agregar el mensaje al RecyclerView (para la UI)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // Crear un objeto Mensaje con los datos correspondientes
                        Mensaje nuevoMensaje = new Mensaje(System.currentTimeMillis(), usuarioNombre, destinatario, mensajeTexto, LocalDateTime.now());

                        // Agregar el nuevo mensaje a la lista de mensajes en memoria
                        listaMensajes.add(nuevoMensaje);

                        // Guardar mensaje en SharedPreferences
                        List<Mensaje> mensajes = cargarMensajes(destinatario); // Obtener los mensajes guardados
                        mensajes.add(nuevoMensaje); // Agregar el nuevo mensaje a la lista de mensajes guardados
                        guardarMensajes(destinatario, mensajes); // Guardarlos nuevamente
                    }
                    adapterChat.notifyItemInserted(listaMensajes.size() - 1);
                    recyclerView.scrollToPosition(listaMensajes.size() - 1);
                    etMensaje.setText(""); // Limpiar el EditText
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Si no se puede conectar, mostrar un error o mensaje de reconexión
                Log.e("WebSocket", "No está conectado. No se puede enviar el mensaje.");
            }
        } else {
            // Si el destinatario es nulo o vacío, mostrar un error
            Log.e("Mensaje", "No se puede enviar el mensaje. El destinatario no está definido.");
            Toast.makeText(getContext(), "Destinatario no definido.", Toast.LENGTH_SHORT).show();
        }
    }


    private void guardarMensajes(String destinatario, List<Mensaje> mensajes) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ChatMessages", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convertimos la lista de mensajes a un solo String (JSON)
        Gson gson = new Gson();
        String mensajesJson = gson.toJson(mensajes);

        Log.d("ChatFragment", "Mensajes a guardar: " + mensajesJson);
        // Guardamos los mensajes bajo la clave del destinatario (usuario o grupo)
        editor.putString(destinatario, mensajesJson);
        editor.apply();

        Log.d("ChatFragment", "Mensajes guardados para " + destinatario + ": " + mensajesJson);
    }


    private List<Mensaje> cargarMensajes(String destinatario) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ChatMessages", Context.MODE_PRIVATE);
        String mensajesJson = sharedPreferences.getString(destinatario, null);

        if (mensajesJson != null) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Mensaje>>() {}.getType();  // Aquí cambia a List<Mensaje>
            List<Mensaje> mensajes = gson.fromJson(mensajesJson, tipoLista);

            Log.d("ChatFragment", "Mensajes cargados para " + destinatario + ": " + mensajesJson);
            return mensajes;
        } else {
            Log.d("ChatFragment", "No hay mensajes guardados para " + destinatario);
            return new ArrayList<>(); // Retornar lista vacía si no hay mensajes guardados
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        webSocketClient.close();
    }



}
