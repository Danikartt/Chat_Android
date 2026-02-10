package com.example.chatactivity.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatactivity.ApiService;
import com.example.chatactivity.R;
import com.example.chatactivity.RetrofitClient;
import com.example.chatactivity.adapters.AdapterConversaciones;
import com.example.chatactivity.model.Grupo;
import com.example.chatactivity.model.Usuario;
import com.example.chatactivity.service.GrupoViewModel;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConversacionesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterConversaciones adapter;
    private List<Usuario> usuarioList;
    private List<Grupo>listaGrupos;
    private String usuarioNombre;
    private GrupoViewModel grupoViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            usuarioNombre = getArguments().getString("usuario_nombre");
            Log.d("conversacionesFragment", "Usuario recibido: " + usuarioNombre);
        } else {
            Log.e("conversacionesFragment", "getArguments() es null en onCreate()");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflamos el layout del fragment
        return inflater.inflate(R.layout.fragment_conversaciones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar_conversaciones);
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        setHasOptionsMenu(true);

        // Inicializar RecyclerView
        recyclerView = view.findViewById(R.id.recyclerConvers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Log.d("DEBUG", "usuarioActual antes de pasarlo al adapter: " + usuarioNombre);

        // Inicializar lista vacía y adaptador
        usuarioList = new ArrayList<>();
        listaGrupos = new ArrayList<>();
        adapter = new AdapterConversaciones(usuarioList, listaGrupos, requireActivity().getSupportFragmentManager(), usuarioNombre);
        recyclerView.setAdapter(adapter);

        grupoViewModel = new ViewModelProvider(requireActivity()).get(GrupoViewModel.class);
        grupoViewModel.getGrupos().observe(getViewLifecycleOwner(), nuevosGrupos -> {
            listaGrupos.clear();
            listaGrupos.addAll(nuevosGrupos);
            adapter.actualizarLista(usuarioList, listaGrupos);
        });

        cargarConversaciones();
    }

    private void cargarConversaciones() {
        ApiService apiService = RetrofitClient.getApiService();

        // Obtener usuarios conectados
        apiService.obtenerUsuariosConectados().enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuarioList.clear();
                    usuarioList.addAll(response.body().stream()
                            .filter(usuario -> !usuario.getNombre().equals(usuarioNombre)) // Excluir usuario actual
                            .collect(Collectors.toList()));

                    Log.d("conversacionesFragment", "Usuarios obtenidos: " + usuarioList.size());

                    obtenerGrupos();
                } else {
                    Log.e("conversacionesFragment", "Error en respuesta usuarios: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e("conversacionesFragment", "Error al obtener usuarios", t);
                Toast.makeText(getContext(), "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Obtener grupos creados
    public void obtenerGrupos() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.obtenerGrupos().enqueue(new Callback<List<Grupo>>() {
            @Override
            public void onResponse(Call<List<Grupo>> call, Response<List<Grupo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaGrupos.clear();
                    listaGrupos.addAll(response.body());

                    Log.d("conversacionesFragment", "Grupos obtenidos: " + listaGrupos.size());

                    adapter.actualizarLista(usuarioList, listaGrupos);
                }else {
                    Log.e("conversacionesFragment", "Error en respuesta grupos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Grupo>> call, Throwable t) {
                Log.e("conversacionesFragment", "Error al obtener grupos", t);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_crear_grupo) {
            // Abrir Fragment para crear grupo
            CrearGrupoFragment crearGrupoFragment = new CrearGrupoFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, crearGrupoFragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        } else if (item.getItemId() == R.id.action_administrar_grupos) {

            // Abrir Fragment para administrar grupos
            AdministrarGruposFragment administrarGruposFragment = new AdministrarGruposFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, administrarGruposFragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume() {
        super.onResume();
        obtenerGrupos(); // Recargar la lista de grupos desde la API
    }


}