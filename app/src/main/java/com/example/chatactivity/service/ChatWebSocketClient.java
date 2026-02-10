package com.example.chatactivity.service;

import static java.security.AccessController.getContext;

import android.util.Log;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ChatWebSocketClient extends WebSocketClient {

    public interface WebSocketListener {
        void onMessageReceived(String message);
    }

    private WebSocketListener listener;

    public ChatWebSocketClient(URI serverUri, WebSocketListener listener) {
        super(serverUri);
        this.listener = listener;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Conexión abierta");
    }

    @Override
    public void onMessage(String message) {
        Log.d("WebSocket", "Mensaje recibido: " + message);
        if (listener != null) {
            listener.onMessageReceived(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "Conexión cerrada: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.e("WebSocket", "Error: ", ex);
    }
}
