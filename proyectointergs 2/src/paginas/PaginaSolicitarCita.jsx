// src/paginas/PaginaSolicitarCita.jsx

import React from "react";
import CitaForm from "../componentes/CitaForm";

/**
 * PáginaSolicitarCita:
 *  - Muestra el formulario para que el paciente solicite una nueva cita.
 *  - Utiliza el componente CitaForm, que maneja toda la lógica de:
 *      • cargar especialidades
 *      • filtrar médicos por especialidad
 *      • calcular fechas válidas según el horario del médico
 *      • excluir horas ya ocupadas
 *      • enviar la nueva cita a tu API REST
 */
 export default function PaginaSolicitarCita() {
  return (
    <div className="page-container">
      <h2 className="section-title">Solicitar cita</h2>
      <div className="card cita-form">
        <CitaForm />
      </div>
    </div>
  );
}