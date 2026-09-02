<?php

header("Content-Type: application/json; charset=UTF-8");
require_once "conexion.php";

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    http_response_code(405);

    echo json_encode([
        "success" => false,
        "mensaje" => "Método no permitido."
    ]);
    exit;
}

$datos = json_decode(file_get_contents("php://input"), true);

$tipo = trim($datos["tipo"] ?? "");
$cantidad = $datos["cantidad"] ?? 0;
$observacion = trim($datos["observacion"] ?? "");
$fecha = trim($datos["fecha"] ?? "");

if ($tipo === "" || $cantidad <= 0 || $fecha === "") {
    http_response_code(400);

    echo json_encode([
        "success" => false,
        "mensaje" => "Datos incompletos o inválidos."
    ]);
    exit;
}

$sql = "INSERT INTO residuos (tipo, cantidad, observacion, fecha)
        VALUES (?, ?, ?, ?)";

$sentencia = $conexion->prepare($sql);
$sentencia->bind_param("sdss", $tipo, $cantidad, $observacion, $fecha);

if ($sentencia->execute()) {
    echo json_encode([
        "success" => true,
        "mensaje" => "Residuo registrado en el servidor.",
        "id" => $conexion->insert_id
    ]);
} else {
    http_response_code(500);

    echo json_encode([
        "success" => false,
        "mensaje" => "No se pudo guardar el residuo."
    ]);
}

$sentencia->close();
$conexion->close();