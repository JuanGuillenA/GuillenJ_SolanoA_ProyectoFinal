// src/componentes/CitasAdmin.jsx

import React from "react";
import { useCitasAdmin } from "../data/useCitas";
import useMedicos from "../data/useMedicos";
import useUsuarios from "../data/useUsuarios";

/**
 * CitasAdmin:
 *  - Muestra todas las citas (admin).
 *  - Permite confirmar las que estén "pendiente".
 */
export default function CitasAdmin() {
  // Hook que trae { citas, confirmar }
  const { citas, confirmar } = useCitasAdmin();

  // Listas de médicos y usuarios desde tu API REST
  const { medicos }   = useMedicos();
  const { usuarios }  = useUsuarios();

  // Busca nombre de médico por su id
  const medicoPorId = (id) =>
    medicos.find((m) => m.id === id)?.nombre || "—";

  // Devuelve "Nombre · Email" o el id si no encuentra
  const pacienteInfo = (uid) => {
    const u = usuarios.find((x) => x.id === uid);
    return u
      ? `${u.displayName || "(sin nombre)"} · ${u.email}`
      : uid;
  };

  return (
    <table className="w-full text-sm">
      <thead>
        <tr className="text-left">
          <th>Paciente</th>
          <th>Médico</th>
          <th>Fecha</th>
          <th>Hora</th>
          <th>Estado</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {citas.map((c) => (
          <tr key={c.id} className="border-t">
            <td>{pacienteInfo(c.pacienteUid)}</td>
            <td>{medicoPorId(c.medicoId)}</td>
            <td>{c.fechaISO}</td>
            <td>{c.hora}</td>
            <td>
              <span
                className={
                  c.estado === "confirmada"
                    ? "text-green-600"
                    : "text-yellow-600"
                }
              >
                {c.estado}
              </span>
            </td>
            <td>
              {c.estado === "pendiente" && (
                <button
                  onClick={() => confirmar(c.id)}
                  className="btn-xs bg-green-600 text-white"
                >
                  Confirmar
                </button>
              )}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
