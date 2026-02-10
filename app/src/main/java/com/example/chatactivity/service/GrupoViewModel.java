package com.example.chatactivity.service;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatactivity.model.Grupo;

import java.util.ArrayList;
import java.util.List;

public class GrupoViewModel extends ViewModel {
    private final MutableLiveData<List<Grupo>> gruposLiveData = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Grupo>> getGrupos() {
        return gruposLiveData;
    }

    public void agregarGrupo(Grupo grupo) {
        List<Grupo> gruposActuales = new ArrayList<>(gruposLiveData.getValue());
        gruposActuales.add(grupo);
        gruposLiveData.setValue(gruposActuales);
    }
    public void actualizarGrupos(List<Grupo> nuevosGrupos) {
        gruposLiveData.setValue(nuevosGrupos);
    }

    public void eliminarGrupo(int position) {
        List<Grupo> listaActual = new ArrayList<>(gruposLiveData.getValue());
        listaActual.remove(position);
        gruposLiveData.setValue(listaActual);
    }

    public void modificarGrupo(int position, String nuevoNombre) {
        List<Grupo> listaActual = new ArrayList<>(gruposLiveData.getValue());
        listaActual.get(position).setNombreGrupo(nuevoNombre);
        gruposLiveData.setValue(listaActual);
    }
}
