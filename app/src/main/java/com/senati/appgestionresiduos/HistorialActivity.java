package com.senati.appgestionresiduos;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistorialActivity extends AppCompatActivity {

    private static final String URL_API =
            "http://10.0.2.2/ecolim_api/registrar_residuo.php";

    private ListView listViewHistorial;
    private TextView tvSinRegistros;
    private DatabaseHelper databaseHelper;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listViewHistorial = findViewById(R.id.listViewHistorial);
        tvSinRegistros = findViewById(R.id.tvSinRegistros);
        databaseHelper = new DatabaseHelper(this);
        requestQueue = Volley.newRequestQueue(this);

        findViewById(R.id.btnSincronizar).setOnClickListener(v -> {
            sincronizarRegistros();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarHistorial();
    }

    private void cargarHistorial() {
        ArrayList<Residuo> listaResiduos = databaseHelper.obtenerResiduos();

        if (listaResiduos.isEmpty()) {
            tvSinRegistros.setVisibility(View.VISIBLE);
            listViewHistorial.setVisibility(View.GONE);
        } else {
            tvSinRegistros.setVisibility(View.GONE);
            listViewHistorial.setVisibility(View.VISIBLE);

            ArrayAdapter<Residuo> adapter = new ArrayAdapter<>(
                    this,
                    R.layout.item_residuo,
                    listaResiduos
            );

            listViewHistorial.setAdapter(adapter);
        }
    }

    private void sincronizarRegistros() {
        ArrayList<Residuo> pendientes = databaseHelper.obtenerNoSincronizados();

        if (pendientes.isEmpty()) {
            Toast.makeText(
                    this,
                    "No hay registros pendientes de sincronización.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Toast.makeText(
                this,
                "Sincronizando " + pendientes.size() + " registro(s)...",
                Toast.LENGTH_SHORT
        ).show();

        enviarResiduoAlServidor(pendientes, 0);
    }

    private void enviarResiduoAlServidor(ArrayList<Residuo> pendientes, int posicion) {
        if (posicion >= pendientes.size()) {
            Toast.makeText(
                    this,
                    "Sincronización completada correctamente.",
                    Toast.LENGTH_LONG
            ).show();

            cargarHistorial();
            return;
        }

        Residuo residuo = pendientes.get(posicion);
        JSONObject datos = new JSONObject();

        try {
            datos.put("tipo", residuo.getTipo());
            datos.put("cantidad", residuo.getCantidad());
            datos.put("observacion", residuo.getObservacion());
            datos.put("fecha", residuo.getFecha());

        } catch (JSONException e) {
            Toast.makeText(
                    this,
                    "Error al preparar los datos.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        JsonObjectRequest solicitud = new JsonObjectRequest(
                Request.Method.POST,
                URL_API,
                datos,
                respuesta -> {
                    if (respuesta.optBoolean("success", false)) {
                        databaseHelper.marcarComoSincronizado(residuo.getId());

                        enviarResiduoAlServidor(pendientes, posicion + 1);
                    } else {
                        Toast.makeText(
                                this,
                                "El servidor rechazó un registro.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },
                error -> Toast.makeText(
                        this,
                        "No se pudo conectar con el servidor. Verifica XAMPP.",
                        Toast.LENGTH_LONG
                ).show()
        );

        requestQueue.add(solicitud);
    }
}