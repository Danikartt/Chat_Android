package com.example.chatactivity.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatactivity.ApiService;
import com.example.chatactivity.MainActivity;
import com.example.chatactivity.R;
import com.example.chatactivity.RetrofitClient;
import com.example.chatactivity.adapters.GrupoAdapter;
import com.example.chatactivity.model.ApiClient;
import com.example.chatactivity.model.Grupo;
import com.example.chatactivity.service.GrupoViewModel;
import com.example.chatactivity.service.NombreGrupoRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdministrarGruposFragment extends Fragment {

    private RecyclerView recyclerGrupos;
    private GrupoAdapter grupoAdapter;
    private List<Grupo> listaGrupos = new ArrayList<>();
    private GrupoViewModel grupoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_administrar_grupos, container, false);

        recyclerGrupos = view.findViewById(R.id.recyclerGrupos);
        recyclerGrupos.setLayoutManager(new LinearLayoutManager(getContext()));

        grupoViewModel = new ViewModelProvider(requireActivity()).get(GrupoViewModel.class);
        grupoViewModel.getGrupos().observe(getViewLifecycleOwner(), grupos -> {
            if (grupoAdapter == null) {
                grupoAdapter = new GrupoAdapter(grupos, grupoViewModel);
                recyclerGrupos.setAdapter(grupoAdapter);
            } else {
                grupoAdapter.actualizarGrupos(grupos);
            }
        });

        cargarGrupos();
        return view;
    }
    private void cargarGrupos() {
        Log.d("AdministrarGrupos", "Cargando grupos...");

        ApiService apiService = RetrofitClient.getApiService();
        apiService.obtenerGrupos().enqueue(new Callback<List<Grupo>>() {
            @Override
            public void onResponse(Call<List<Grupo>> call, Response<List<Grupo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaGrupos.clear();
                    listaGrupos.addAll(response.body());
                    grupoAdapter.actualizarGrupos(listaGrupos); // Aquí se actualiza correctamente

                    Log.d("AdministrarGrupos", "Lista de grupos actualizada: " + listaGrupos.size());

                    // Si la lista queda vacía, asegúrate de que el RecyclerView no desaparezca
                    if (listaGrupos.isEmpty()) {
                        recyclerGrupos.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "No hay grupos disponibles", Toast.LENGTH_SHORT).show();
                    } else {
                        recyclerGrupos.setVisibility(View.VISIBLE);
                    }
                    if (grupoAdapter != null) {
                        grupoAdapter.actualizarGrupos(listaGrupos);
                    } else {
                        Log.e("AdministrarGrupos", "grupoAdapter es NULL");
                    }
                } else {
                    Log.e("AdministrarGrupos", "Error al obtener grupos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Grupo>> call, Throwable t) {
                Log.e("AdministrarGrupos", "Error al obtener grupos", t);
            }
        });
    }

    public void cambiarNombreGrupo(String nombreActual, String nuevoNombre) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Log.d("AdministrarGrupos", "Intentando cambiar nombre de: " + nombreActual + " a " + nuevoNombre);

        NombreGrupoRequest request = new NombreGrupoRequest(nombreActual, nuevoNombre);

        Call<Grupo> call = apiService.modificarNombreGrupo(request);

        call.enqueue(new Callback<Grupo>() {
            @Override
            public void onResponse(Call<Grupo> call, Response<Grupo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("AdministrarGrupos", "Nombre cambiado en el servidor. Recargando grupos...");
                    cargarGrupos(); // Recargar la lista de grupos desde el servidor
                    grupoViewModel.actualizarGrupos(listaGrupos); // Notificar al ViewModel
                    ((MainActivity) getActivity()).actualizarConversaciones(); // Refrescar conversacionesFragment
                } else {
                    Log.e("AdministrarGrupos", "Error al modificar nombre: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Grupo> call, Throwable t) {
                Log.e("AdministrarGrupos", "Error al modificar nombre en el servidor", t);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        cargarGrupos();  // Recargar la lista al volver al fragment
    }

}
