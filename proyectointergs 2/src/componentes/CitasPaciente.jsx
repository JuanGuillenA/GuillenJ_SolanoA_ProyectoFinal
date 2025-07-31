// src/componentes/CitasPaciente.jsx

import React from "react";
import { useAuth } from "../autenticacion/ContextoAutenticacion";
import { useCitasPaciente } from "../data/useCitas";
import useMedicos from "../data/useMedicos";

/**
 * CitasPaciente:
 *  - Lista las citas del paciente autenticado, consumiendo tu API REST.
 */
export default function CitasPaciente() {
  // Obtenemos el usuario actual (necesitamos su UID para el query)
  const { usuario } = useAuth();

  // Hook que hace GET /citas?pacienteId={usuario.uid}
  // Debe devolver [{ id, pacienteUid, medicoId, fechaISO, hora, especialidad, estado }, …]
  const { citas } = useCitasPaciente(usuario.uid);

  // Para resolver nombre de médico
  const { medicos } = useMedicos();

  const medicoPorId = (id) =>
    medicos.find((m) => m.id === id)?.nombre || "—";

  return (
    <div>
      <h3>Mis Citas</h3>

      {citas.length === 0 ? (
        <p>No tienes citas registradas.</p>
      ) : (
        <ul>
          {citas.map((c) => (
            <li key={c.id} style={{ marginBottom: "8px" }}>
              <strong>Fecha:</strong> {c.fechaISO}
              <br />
              <strong>Hora:</strong> {c.hora}
              <br />
              <strong>Médico:</strong> {medicoPorId(c.medicoId)}
              <br />
              <strong>Especialidad:</strong> {c.especialidad}
              <br />
              <strong>Estado:</strong>{" "}
              <span
                style={{
                  color:
                    c.estado === "confirmada" ? "green" : "orange",
                }}
              >
                {c.estado}
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
