package com.senati.appgestionresiduos;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrarResiduoActivity extends AppCompatActivity {

    private Spinner spinnerTipoResiduo;
    private EditText etCantidad, etObservacion;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_residuo);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerTipoResiduo = findViewById(R.id.spinnerTipoResiduo);
        etCantidad = findViewById(R.id.etCantidad);
        etObservacion = findViewById(R.id.etObservacion);
        databaseHelper = new DatabaseHelper(this);

        cargarTiposResiduo();

        findViewById(R.id.btnGuardar).setOnClickListener(v -> {
            guardarRegistro();
        });
    }

    private void cargarTiposResiduo() {
        String[] tipos = {
                "Seleccione un tipo",
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

        spinnerTipoResiduo.setAdapter(adapter);
    }

    private void guardarRegistro() {
        String tipo = spinnerTipoResiduo.getSelectedItem().toString();
        String cantidadTexto = etCantidad.getText().toString().trim();
        String observacion = etObservacion.getText().toString().trim();

        if (spinnerTipoResiduo.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un tipo de residuo.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cantidadTexto.isEmpty()) {
            etCantidad.setError("Ingrese la cantidad en kg.");
            etCantidad.requestFocus();
            return;
        }

        double cantidad;

        try {
            cantidad = Double.parseDouble(cantidadTexto.replace(",", "."));

            if (cantidad <= 0) {
                etCantidad.setError("La cantidad debe ser mayor a cero.");
                etCantidad.requestFocus();
                return;
            }

        } catch (NumberFormatException e) {
            etCantidad.setError("Ingrese una cantidad válida.");
            etCantidad.requestFocus();
            return;
        }

        String fechaActual = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        Residuo residuo = new Residuo(
                tipo,
                cantidad,
                observacion,
                fechaActual
        );

        long resultado = databaseHelper.guardarResiduo(residuo);

        if (resultado != -1) {
            Toast.makeText(this, "Registro guardado correctamente.", Toast.LENGTH_SHORT).show();
            limpiarFormulario();
        } else {
            Toast.makeText(this, "No se pudo guardar el registro.", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarFormulario() {
        spinnerTipoResiduo.setSelection(0);
        etCantidad.setText("");
        etObservacion.setText("");
        etCantidad.requestFocus();
    }
}