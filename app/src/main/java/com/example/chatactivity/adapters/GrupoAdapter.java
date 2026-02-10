package com.example.chatactivity.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatactivity.ApiService;
import com.example.chatactivity.MainActivity;
import com.example.chatactivity.R;
import com.example.chatactivity.RetrofitClient;
import com.example.chatactivity.fragments.AdministrarGruposFragment;
import com.example.chatactivity.model.Grupo;
import com.example.chatactivity.service.GrupoViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.GrupoViewHolder> {

    private List<Grupo> grupos;
    private GrupoViewModel grupoViewModel;

    public GrupoAdapter(List<Grupo> grupos, GrupoViewModel grupoViewModel) {
        this.grupos = grupos;
        this.grupoViewModel = grupoViewModel;
    }

    @NonNull
    @Override
    public GrupoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grupo, parent, false);
        return new GrupoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoViewHolder holder, int position) {
        Grupo grupo = grupos.get(position);
        holder.tvNombreGrupo.setText(grupo.getNombreGrupo());
        holder.tvMiembros.setText(TextUtils.join(", ", grupo.getMiembros()));

        // Editar nombre del grupo
        holder.tvNombreGrupo.setOnClickListener(v -> {
            editarNombreGrupo(holder.itemView.getContext(), grupo, position);
        });

        // Eliminar grupo
        holder.btnEliminarGrupo.setOnClickListener(v -> {
            eliminarGrupo(position);
        });

        // Eliminar miembro con click
        holder.tvMiembros.setOnClickListener(v -> {
            eliminarMiembro(holder.itemView.getContext(), grupo, position);
        });
    }

    @Override
    public int getItemCount() {
        return grupos.size();
    }

    static class GrupoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreGrupo, tvMiembros;
        Button btnEliminarGrupo;

        public GrupoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreGrupo = itemView.findViewById(R.id.tvNombreGrupo);
            tvMiembros = itemView.findViewById(R.id.tvMiembros);
            btnEliminarGrupo = itemView.findViewById(R.id.btnEliminarGrupo);
        }
    }

    private void editarNombreGrupo(Context context, Grupo grupo, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Editar nombre del grupo");

        final EditText input = new EditText(context);
        input.setText(grupo.getNombreGrupo());
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoNombre = input.getText().toString().trim();
            Log.d("GrupoAdapter", "Nuevo nombre ingresado: " + nuevoNombre);
            if (!nuevoNombre.isEmpty() && !nuevoNombre.equals(grupo.getNombreGrupo())) {
                Log.d("GrupoAdapter", "Llamando a cambiarNombreGrupo en AdministrarGruposFragment");
                // Llamar a la API desde AdministrarGruposFragment
                if (context instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    AdministrarGruposFragment fragment = (AdministrarGruposFragment)
                            activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);


                    if (fragment != null) {
                        fragment.cambiarNombreGrupo(grupo.getNombreGrupo(), nuevoNombre);
                    } else {
                        Log.e("GrupoAdapter", "AdministrarGruposFragment sigue siendo NULL");
                    }
                } else {
                    Log.e("GrupoAdapter", "Contexto no es una instancia de AppCompactActivity");
                }
            } else {
                Log.e("GrupoAdapter", "El nuevo nombre es vacío o igual al actual");
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void eliminarGrupo(int position) {
        Grupo grupoAEliminar = grupos.get(position);

        // Llamada a la API para eliminar el grupo en el servidor
        ApiService apiService = RetrofitClient.getApiService();
        apiService.eliminarGrupo(grupoAEliminar.getNombreGrupo()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    grupos.remove(position); // Eliminar solo el grupo seleccionado
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, grupos.size()); // Actualizar posiciones restantes
                    grupoViewModel.actualizarGrupos(new ArrayList<>(grupos)); // Notificar al ViewModel
                } else {
                    Log.e("GrupoAdapter", "Error al eliminar grupo: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("GrupoAdapter", "Error en la eliminación del grupo", t);
            }
        });
    }

    private void eliminarMiembro(Context context, Grupo grupo, int position) {
        String[] miembrosArray = grupo.getMiembros().toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Eliminar miembro")
                .setItems(miembrosArray, (dialog, which) -> {
                    grupo.getMiembros().remove(which);
                    notifyItemChanged(position);
                    grupoViewModel.actualizarGrupos(grupos);  // Notificar cambios
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
    public void actualizarGrupos(List<Grupo> nuevosGrupos) {
        this.grupos.clear();
        this.grupos.addAll(nuevosGrupos);
        notifyDataSetChanged();
    }
}
