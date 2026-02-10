package com.example.chatactivity.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatactivity.R;
import com.example.chatactivity.model.Usuario;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private List<Usuario> usuarios;
    private Set<Usuario> seleccionados = new HashSet<>();
    private OnUsuarioSeleccionadoListener listener;

    public interface OnUsuarioSeleccionadoListener {
        void onUsuarioSeleccionado(Usuario usuario, boolean isChecked);
    }

    public UsuarioAdapter(List<Usuario> usuarios, OnUsuarioSeleccionadoListener listener) {
        this.usuarios = usuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.textView.setText(usuario.getNombre());
        holder.checkBox.setChecked(seleccionados.contains(usuario));

        // Manejar clics en el CheckBox directamente
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                seleccionados.add(usuario);
            } else {
                seleccionados.remove(usuario);
            }
            listener.onUsuarioSeleccionado(usuario, isChecked);
        });

        // También manejar clics en el item completo (opcional)
        holder.itemView.setOnClickListener(v -> {
            boolean isChecked = !holder.checkBox.isChecked();
            holder.checkBox.setChecked(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvusuario); // ID en item_usuario.xml
            checkBox = itemView.findViewById(R.id.checkbox_usuario); // ID en item_usuario.xml
        }
    }
}
