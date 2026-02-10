package com.example.chatactivity.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatactivity.R;
import com.example.chatactivity.fragments.ChatFragment;
import com.example.chatactivity.model.Grupo;
import com.example.chatactivity.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class AdapterConversaciones extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USUARIO = 0;
    private static final int TYPE_GRUPO = 1;

    private List<Object> listaElementos; // Mezcla de Usuarios y Grupos
    private FragmentManager fragmentManager;
    private String usuarioNombre;

    public AdapterConversaciones(List<Usuario> usuarioList, List<Grupo> grupoList, FragmentManager fragmentManager, String usuarioNombre) {
        this.listaElementos = new ArrayList<>();
        this.fragmentManager = fragmentManager;
        this.usuarioNombre = usuarioNombre;

        // Agregar primero los usuarios y luego los grupos a la lista combinada
        this.listaElementos.addAll(usuarioList);
        this.listaElementos.addAll(grupoList);
    }

    @Override
    public int getItemViewType(int position) {
        if (listaElementos.get(position) instanceof Usuario) {
            return TYPE_USUARIO;
        } else {
            return TYPE_GRUPO;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_USUARIO) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_usuario, parent, false);
            return new UsuarioViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_grupo, parent, false);
            return new GrupoViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UsuarioViewHolder) {
            Usuario usuario = (Usuario) listaElementos.get(position);
            ((UsuarioViewHolder) holder).tvUsuario.setText(usuario.getNombre());

            ((UsuarioViewHolder) holder).btnConversar.setOnClickListener(v -> abrirChat(usuario.getNombre()));
        } else {
            Grupo grupo = (Grupo) listaElementos.get(position);
            ((GrupoViewHolder) holder).tvGrupo.setText(grupo.getNombreGrupo());

            ((GrupoViewHolder) holder).btnConversarGrupo.setOnClickListener(v -> abrirChatGrupo(grupo.getNombreGrupo()));
        }
    }

    @Override
    public int getItemCount() {
        return listaElementos.size();
    }

    // ViewHolder para usuarios
    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsuario;
        Button btnConversar;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsuario = itemView.findViewById(R.id.tvusuario);
            btnConversar = itemView.findViewById(R.id.btnconversar);
        }
    }

    // ViewHolder para grupos
    public static class GrupoViewHolder extends RecyclerView.ViewHolder {
        TextView tvGrupo;
        Button btnConversarGrupo;

        public GrupoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGrupo = itemView.findViewById(R.id.tvgrupo);
            btnConversarGrupo = itemView.findViewById(R.id.btnconversarG);
        }
    }

    private void abrirChat(String destinatario) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("usuario_nombre", usuarioNombre);
        bundle.putString("destinatario", destinatario);
        chatFragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, chatFragment)
                .addToBackStack(null)
                .commit();
    }

    private void abrirChatGrupo(String nombreGrupo) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("usuario_nombre", usuarioNombre);
        bundle.putString("grupo_nombre", nombreGrupo);
        chatFragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, chatFragment)
                .addToBackStack(null)
                .commit();
    }

    public void actualizarLista(List<Usuario> usuarios, List<Grupo> grupos) {
        listaElementos.clear(); // Limpiar lista
        listaElementos.addAll(usuarios); // Agregar usuarios
        listaElementos.addAll(grupos);   // Agregar grupos
        notifyDataSetChanged(); // Notificar cambios
    }
}

