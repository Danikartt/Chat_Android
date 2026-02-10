package com.example.chatactivity.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

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
import com.example.chatactivity.model.Mensaje;
import com.example.chatactivity.model.Usuario;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private SharedPreferences encryptedSharedPreferences;
    private static final String USER_PREFS = "user_prefs";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    private EditText etUsername, etPassword;
    private Button btnLogin,btnRegistro;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etUsername = view.findViewById(R.id.etnombre);
        etPassword = view.findViewById(R.id.etclave);
        btnLogin = view.findViewById(R.id.btninicio);
        btnRegistro = view.findViewById(R.id.btnRegistro);

        sharedPreferences = requireContext().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);

        try {
            MasterKey masterKey = new MasterKey.Builder(requireContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                    requireContext(),
                    USER_PREFS,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        btnRegistro.setOnClickListener(v -> handleRegistro());
        btnLogin.setOnClickListener(v -> handleLogin());
        return view;
    }

    // Resgistrar Usuario
    private void handleRegistro() {
        String nombre = etUsername.getText().toString().trim();
        String clave = etPassword.getText().toString().trim();

        if (nombre.isEmpty() || clave.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = new Usuario(nombre, clave);
        ApiService apiService = RetrofitClient.getApiService();

        apiService.registrarUsuario(usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    //Loguear usuario y acceder
    private void handleLogin() {
        String inputUsername = etUsername.getText().toString().trim();
        String inputPassword = etPassword.getText().toString().trim();

        if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = new Usuario(inputUsername, inputPassword); // Creamos el objeto Credenciales

        ApiService apiService = RetrofitClient.getApiService();
        apiService.iniciarSesion(usuario).enqueue(new Callback<Usuario>() { // Enviamos el objeto Credenciales
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body(); // Obtenemos el objeto Usuario
                    // Guardar credenciales (como lo tenías)
                    sharedPreferences.edit().putString(USERNAME_KEY, inputUsername).apply();
                    encryptedSharedPreferences.edit().putString(PASSWORD_KEY, inputPassword).apply();

                    Log.d("LoginFragment", "Pasando usuario: " + inputUsername);
                    Fragment conversacionesFragment = new ConversacionesFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("usuario_nombre", inputUsername);
                    conversacionesFragment.setArguments(bundle);

                    // Verificar antes de navegar
                    if (conversacionesFragment.getArguments() != null) {
                        Log.d("LoginFragment", "Argumentos asignados correctamente");
                    } else {
                        Log.e("LoginFragment", "Los argumentos no se asignaron correctamente");
                    }

                    navigateToNextFragment(conversacionesFragment);

                    Toast.makeText(getContext(), "Login correcto", Toast.LENGTH_SHORT).show();

                } else {
                    // Manejar errores de respuesta del servidor
                    try {
                        String errorBody = response.errorBody().string(); // Obtener el cuerpo del error
                        Gson gson = new Gson();
                        Mensaje mensaje = gson.fromJson(errorBody, Mensaje.class); // Deserializar el mensaje de error
                        String errorMessage = mensaje.getMensaje();
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show(); // Mostrar el mensaje de error
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error en el login", Toast.LENGTH_SHORT).show(); // Error genérico
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

    }

    private void navigateToNextFragment(Fragment fragment) {
        if (getActivity() == null) {
            Toast.makeText(getContext(), "Error: Actividad no disponible", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Verificar si el fragment ya existe en la pila
            Fragment existingFragment = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());

            if (existingFragment == null) {
                Log.d("Navigation", "Navegando al fragment con TAG: " + fragment.getClass().getSimpleName());
                transaction.replace(R.id.fragmentContainerView, fragment, fragment.getClass().getSimpleName());
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                Log.d("Navigation", "El fragmento ya existe, no se reemplaza.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al cambiar de pantalla", Toast.LENGTH_LONG).show();
        }
    }
}
