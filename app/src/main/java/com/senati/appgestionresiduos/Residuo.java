package com.senati.appgestionresiduos;

public class Residuo {

    private int id;
    private String tipo;
    private double cantidad;
    private String observacion;
    private String fecha;
    private int sincronizado;

    public Residuo(String tipo, double cantidad, String observacion, String fecha) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.observacion = observacion;
        this.fecha = fecha;
        this.sincronizado = 0;
    }

    public Residuo(int id, String tipo, double cantidad, String observacion,
                   String fecha, int sincronizado) {
        this.id = id;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.observacion = observacion;
        this.fecha = fecha;
        this.sincronizado = sincronizado;
    }

    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public double getCantidad() {
        return cantidad;
    }

    public String getObservacion() {
        return observacion;
    }

    public String getFecha() {
        return fecha;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    @Override
    public String toString() {
        String texto = tipo + " - " + cantidad + " kg"
                + "\nFecha: " + fecha;

        if (!observacion.isEmpty()) {
            texto += "\nObservación: " + observacion;
        }

        return texto;
    }
}