package com.senati.appgestionresiduos;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class ReportesActivity extends AppCompatActivity {

    private Spinner spinnerFiltroTipo;
    private EditText etFechaFiltro;
    private TextView tvTotalKg, tvCantidadRegistros;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reportes);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerFiltroTipo = findViewById(R.id.spinnerFiltroTipo);
        etFechaFiltro = findViewById(R.id.etFechaFiltro);
        tvTotalKg = findViewById(R.id.tvTotalKg);
        tvCantidadRegistros = findViewById(R.id.tvCantidadRegistros);
        databaseHelper = new DatabaseHelper(this);

        cargarTiposFiltro();

        findViewById(R.id.btnGenerarReporte).setOnClickListener(v -> {
            generarReporte();
        });
    }

    private void cargarTiposFiltro() {
        String[] tipos = {
                "Todos",
                "Orgánico",
                "Plástico",
                "Papel y cartón",
                "Vidrio",
                "Peligroso"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipos
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerFiltroTipo.setAdapter(adapter);
    }

    private void generarReporte() {
        String tipoFiltro = spinnerFiltroTipo.getSelectedItem().toString();
        String fechaFiltro = etFechaFiltro.getText().toString().trim();

        ArrayList<Residuo> listaResiduos = databaseHelper.obtenerResiduosFiltrados(
                tipoFiltro,
                fechaFiltro
        );

        double totalKg = 0;

        for (Residuo residuo : listaResiduos) {
            totalKg += residuo.getCantidad();
        }

        tvTotalKg.setText(
                "Total recolectado: "
                        + String.format(Locale.getDefault(), "%.2f", totalKg)
                        + " kg"
        );

        tvCantidadRegistros.setText(
                "Registros encontrados: " + listaResiduos.size()
        );
    }
}