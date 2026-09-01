package com.senati.appgestionresiduos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NOMBRE_BD = "ecolim.db";
    private static final int VERSION_BD = 1;

    public static final String TABLA_RESIDUOS = "residuos";
    public static final String COLUMNA_ID = "id";
    public static final String COLUMNA_TIPO = "tipo";
    public static final String COLUMNA_CANTIDAD = "cantidad";
    public static final String COLUMNA_OBSERVACION = "observacion";
    public static final String COLUMNA_FECHA = "fecha";
    public static final String COLUMNA_SINCRONIZADO = "sincronizado";

    public DatabaseHelper(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String crearTabla = "CREATE TABLE " + TABLA_RESIDUOS + " ("
                + COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMNA_TIPO + " TEXT NOT NULL, "
                + COLUMNA_CANTIDAD + " REAL NOT NULL, "
                + COLUMNA_OBSERVACION + " TEXT, "
                + COLUMNA_FECHA + " TEXT NOT NULL, "
                + COLUMNA_SINCRONIZADO + " INTEGER DEFAULT 0)";

        db.execSQL(crearTabla);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_RESIDUOS);
        onCreate(db);
    }

    public long guardarResiduo(Residuo residuo) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(COLUMNA_TIPO, residuo.getTipo());
        valores.put(COLUMNA_CANTIDAD, residuo.getCantidad());
        valores.put(COLUMNA_OBSERVACION, residuo.getObservacion());
        valores.put(COLUMNA_FECHA, residuo.getFecha());
        valores.put(COLUMNA_SINCRONIZADO, residuo.getSincronizado());

        long resultado = db.insert(TABLA_RESIDUOS, null, valores);
        db.close();

        return resultado;
    }

    public ArrayList<Residuo> obtenerResiduos() {
        ArrayList<Residuo> listaResiduos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLA_RESIDUOS + " ORDER BY " + COLUMNA_ID + " DESC",
                null
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNA_ID));
            String tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_TIPO));
            double cantidad = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMNA_CANTIDAD));
            String observacion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_OBSERVACION));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_FECHA));
            int sincronizado = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNA_SINCRONIZADO));

            listaResiduos.add(new Residuo(
                    id, tipo, cantidad, observacion, fecha, sincronizado
            ));
        }

        cursor.close();
        db.close();

        return listaResiduos;
    }

    public ArrayList<Residuo> obtenerResiduosFiltrados(String tipoFiltro, String fechaFiltro) {
        ArrayList<Residuo> listaResiduos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        StringBuilder consulta = new StringBuilder(
                "SELECT * FROM " + TABLA_RESIDUOS + " WHERE 1=1"
        );

        ArrayList<String> argumentos = new ArrayList<>();

        if (!tipoFiltro.equals("Todos")) {
            consulta.append(" AND ").append(COLUMNA_TIPO).append(" = ?");
            argumentos.add(tipoFiltro);
        }

        if (!fechaFiltro.isEmpty()) {
            consulta.append(" AND ").append(COLUMNA_FECHA).append(" LIKE ?");
            argumentos.add(fechaFiltro + "%");
        }

        consulta.append(" ORDER BY ").append(COLUMNA_ID).append(" DESC");

        Cursor cursor = db.rawQuery(
                consulta.toString(),
                argumentos.toArray(new String[0])
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNA_ID));
            String tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_TIPO));
            double cantidad = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMNA_CANTIDAD));
            String observacion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_OBSERVACION));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_FECHA));
            int sincronizado = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNA_SINCRONIZADO));

            listaResiduos.add(new Residuo(
                    id, tipo, cantidad, observacion, fecha, sincronizado
            ));
        }

        cursor.close();
        db.close();

        return listaResiduos;
    }

    public ArrayList<Residuo> obtenerNoSincronizados() {
        ArrayList<Residuo> listaResiduos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLA_RESIDUOS
                        + " WHERE " + COLUMNA_SINCRONIZADO + " = 0",
                null
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNA_ID));
            String tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_TIPO));
            double cantidad = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMNA_CANTIDAD));
            String observacion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_OBSERVACION));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_FECHA));

            listaResiduos.add(new Residuo(id, tipo, cantidad, observacion, fecha, 0));
        }

        cursor.close();
        db.close();

        return listaResiduos;
    }

    public void marcarComoSincronizado(int id) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(COLUMNA_SINCRONIZADO, 1);

        db.update(
                TABLA_RESIDUOS,
                valores,
                COLUMNA_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
    }
}