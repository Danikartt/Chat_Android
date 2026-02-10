package com.example.chatactivity.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatactivity.R;
import com.example.chatactivity.model.Mensaje;

import java.util.List;
public class AdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Mensaje> listaMensajes;
    private String usuarioActual;  // Usuario actual para distinguir enviados y recibidos

    private static final int TIPO_ENVIADO = 1;
    private static final int TIPO_RECIBIDO = 2;

    public AdapterChat(List<Mensaje> listaMensajes, String usuarioActual) {
        this.listaMensajes = listaMensajes;
        this.usuarioActual = usuarioActual;
        Log.d("AdapterChat", "Usuario Actual: " + usuarioActual);  // Verifica que el usuarioActual se esté asignando correctamente
    }

    @Override
    public int getItemViewType(int position) {
        Mensaje mensaje = listaMensajes.get(position);
        Log.d("AdapterChat", "Comparando Remitente: " + mensaje.getRemitente() + " con UsuarioActual: " + usuarioActual);
        return mensaje.getRemitente().equals(usuarioActual) ? TIPO_ENVIADO : TIPO_RECIBIDO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TIPO_ENVIADO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemenviado, parent, false);
            return new MensajeEnviadoViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrecibido, parent, false);
            return new MensajeRecibidoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mensaje mensaje = listaMensajes.get(position);
        Log.d("AdapterChat", "Mensaje en posición " + position + ": " + mensaje.getMensaje());
        Log.d("AdapterChat", "Grupo ID: " + mensaje.getGrupoId());  // Log para verificar el grupoId

        if (holder.getItemViewType() == TIPO_ENVIADO) {
            ((MensajeEnviadoViewHolder) holder).tvMensaje.setText(mensaje.getMensaje());

        } else {
            // Para los mensajes recibidos
            MensajeRecibidoViewHolder recibidoHolder = (MensajeRecibidoViewHolder) holder;
            recibidoHolder.tvMensaje.setText(mensaje.getMensaje());

            Log.d("AdapterChat", "Remitente del mensaje en posición " + position + ": " + mensaje.getRemitente());
            recibidoHolder.tvRemitente.setVisibility(View.VISIBLE);
            recibidoHolder.tvRemitente.setText(mensaje.getRemitente());
        }
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    static class MensajeEnviadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvMensaje;

        public MensajeEnviadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvenviado);
        }
    }

    static class MensajeRecibidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvMensaje;
        TextView tvRemitente;
        public MensajeRecibidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvrecibido);
            tvRemitente = itemView.findViewById(R.id.tvRemitente);
        }
    }
    public void actualizarMensajes(List<Mensaje> nuevosMensajes) {
        this.listaMensajes.clear();  // Eliminar los mensajes anteriores
        this.listaMensajes.addAll(nuevosMensajes); // Agregar los nuevos mensajes
        notifyDataSetChanged(); // Notificar cambios al RecyclerView
    }

}