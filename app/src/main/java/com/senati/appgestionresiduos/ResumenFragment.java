package com.senati.appgestionresiduos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Locale;

public class ResumenFragment extends Fragment {

    private TextView tvResumenCantidad, tvResumenPendiente;
    private DatabaseHelper databaseHelper;

    public ResumenFragment() {
        // Constructor vacío obligatorio para el Fragment.
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(
                R.layout.fragment_resumen,
                container,
                false
        );

        tvResumenCantidad = vista.findViewById(R.id.tvResumenCantidad);
        tvResumenPendiente = vista.findViewById(R.id.tvResumenPendiente);
        databaseHelper = new DatabaseHelper(requireContext());

        actualizarResumen();

        return vista;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            actualizarResumen();
        }
    }

    private void actualizarResumen() {
        ArrayList<Residuo> listaResiduos = databaseHelper.obtenerResiduos();

        double totalKg = 0;
        int pendientes = 0;

        for (Residuo residuo : listaResiduos) {
            totalKg += residuo.getCantidad();

            if (residuo.getSincronizado() == 0) {
                pendientes++;
            }
        }

        tvResumenCantidad.setText(
                "Registros locales: " + listaResiduos.size()
                        + " | Total: "
                        + String.format(Locale.getDefault(), "%.2f", totalKg)
                        + " kg"
        );

        tvResumenPendiente.setText(
                "Pendientes de sincronización: " + pendientes
        );
    }
}