<?php

$servidor = "localhost";
$usuario = "root";
$contrasena = "";
$baseDatos = "ecolim_db";

$conexion = new mysqli($servidor, $usuario, $contrasena, $baseDatos);

if ($conexion->connect_error) {
    header("Content-Type: application/json; charset=UTF-8");
    echo json_encode([
        "success" => false,
        "mensaje" => "Error de conexión con la base de datos."
    ]);
    exit;
}

$conexion->set_charset("utf8mb4");