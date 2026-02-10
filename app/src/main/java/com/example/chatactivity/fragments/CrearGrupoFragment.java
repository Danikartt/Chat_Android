package com.example.chatactivity.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatactivity.ApiService;
import com.example.chatactivity.R;
import com.example.chatactivity.RetrofitClient;
import com.example.chatactivity.adapters.UsuarioAdapter;
import com.example.chatactivity.model.Grupo;
import com.example.chatactivity.model.Usuario;
import com.example.chatactivity.service.GrupoViewModel;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CrearGrupoFragment extends Fragment {

    private EditText etNombreGrupo;
    private RecyclerView recyclerUsuarios;
    private Button btnCrearGrupo;
    private UsuarioAdapter adapter;
    private List<Usuario> usuarioList;
    private List<Usuario> usuariosSeleccionados;
    private String usuarioActual;
    private GrupoViewModel grupoViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            usuarioActual = getArguments().getString("usuario_nombre");
            Log.d("CrearGrupoFragment", "Usuario actual recibido: " + usuarioActual);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear_grupo, container, false);

        etNombreGrupo = view.findViewById(R.id.etNombreGrupo);
        recyclerUsuarios = view.findViewById(R.id.recyclerUsuarios);
        btnCrearGrupo = view.findViewById(R.id.btnCrearGrupo);

        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));

        usuarioList = new ArrayList<>();
        usuariosSeleccionados = new ArrayList<>();
        adapter = new UsuarioAdapter(usuarioList, (usuario, isChecked) -> {
            if (isChecked) {
                usuariosSeleccionados.add(usuario);
            } else {
                usuariosSeleccionados.remove(usuario);
            }
        });
        recyclerUsuarios.setAdapter(adapter);

        grupoViewModel = new ViewModelProvider(requireActivity()).get(GrupoViewModel.class);

        obtenerUsuariosConectados();

        btnCrearGrupo.setOnClickListener(v -> crearGrupo());

        return view;
    }

    private void obtenerUsuariosConectados() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.obtenerUsuariosConectados().enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CrearGrupoFragment", "Usuarios conectados recibidos: " + response.body().size());

                    usuarioList.clear();
                    usuarioList.addAll(response.body().stream()
                            .filter(usuario -> !usuario.getNombre().equals(usuarioActual)) // Excluir usuario actual
                            .collect(Collectors.toList()));

                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("CrearGrupoFragment", "Error en la respuesta del servidor, código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e("CrearGrupoFragment", "Error al obtener usuarios conectados", t);
                Toast.makeText(getContext(), "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void crearGrupo() {
        String nombreGrupo = etNombreGrupo.getText().toString().trim();
        if (nombreGrupo.isEmpty() || usuariosSeleccionados.isEmpty()) {
            Toast.makeText(getContext(), "Ingresa un nombre y selecciona miembros", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> nombresUsuarios = usuariosSeleccionados.stream()
                .map(Usuario::getNombre)
                .collect(Collectors.toList());

        // Crear el objeto Grupo con id=0 (el servidor lo generará)
        Grupo nuevoGrupo = new Grupo(0L, nombreGrupo, nombresUsuarios);

        // Llamar a la API con el objeto Grupo
        ApiService apiService = RetrofitClient.getApiService();
        Call<Grupo> call = apiService.crearGrupo(nuevoGrupo);

        call.enqueue(new Callback<Grupo>() {
            @Override
            public void onResponse(Call<Grupo> call, Response<Grupo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Grupo grupoCreado = response.body();

                    // Agregar el grupo al ViewModel para actualizar la UI en AdministrarGruposFragment
                    grupoViewModel.agregarGrupo(grupoCreado);

                    Log.d("CrearGrupoFragment", "✅ Grupo creado: " + grupoCreado.getNombreGrupo());
                    Toast.makeText(getContext(), "Grupo creado", Toast.LENGTH_SHORT).show();

                    Log.d("CrearGrupoFragment","Lista de grupos creados: ");
                    // Cerrar el fragmento y regresar
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Log.e("CrearGroupFragment", "⚠️ Error al crear el grupo: " + response.message());
                    Toast.makeText(getContext(), "Error al crear el grupo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Grupo> call, Throwable t) {
                Log.e("CrearGroupFragment", "❌ Fallo en la API: " + t.getMessage());
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
