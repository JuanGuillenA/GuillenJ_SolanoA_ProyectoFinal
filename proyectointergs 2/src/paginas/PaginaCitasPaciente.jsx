// src/paginas/PaginaCitasPaciente.jsx
import React, { useState, useMemo } from "react";
import { useAuth } from "../autenticacion/ContextoAutenticacion";
import { useHistorialCitas } from "../data/useHistorialCitas";
import useMedicos from "../data/useMedicos";
import useEspecialidades from "../data/useEspecialidades";

export default function PaginaCitasPaciente() {
  const { usuario } = useAuth();
  const { medicos } = useMedicos();
  const {
    especialidades,
    loading: loadingEsp,
    error: errorEsp,
  } = useEspecialidades();
  const {
    data: citas = [],
    loading: loadingCitas,
    error: errorCitas,
  } = useHistorialCitas({ pacienteId: usuario.id });

  const [estadoFilter, setEstadoFilter] = useState("todas");
  const [searchTerm, setSearchTerm] = useState("");

  // 1) Filtrar SOLO las citas de este paciente
  const misCitas = useMemo(
    () => citas.filter(c => c.pacienteId === usuario.id),
    [citas, usuario.id]
  );

  // 2) Enriquecer solo esas citas
  const citasEnriquecidas = useMemo(
    () =>
      misCitas.map((c) => {
        const medico = medicos.find((m) => m.id === c.medicoId) || {};
        const nombreMedico = `${medico.nombre || ""} ${
          medico.apellido || ""
        }`.trim() || "—";
        const esp = especialidades.find(
          (e) => e.id === medico.especialidadId
        );
        const nombreEspecialidad = esp ? esp.nombre : "—";
        const [Y, M, D] = c.fecha || [];
        const [h, m] = c.hora || [];
        const dt = new Date(Y, (M || 1) - 1, D, h, m);
        return {
          id: c.id,
          nombreMedico,
          especialidad: nombreEspecialidad,
          dia: dt.toLocaleDateString("es-ES", { weekday: "short" }),
          fecha: dt.toLocaleDateString("es-ES"),
          hora: dt.toLocaleTimeString("es-ES", {
            hour: "2-digit",
            minute: "2-digit",
          }),
          estado: c.estado,
          estadoLower: c.estado.toLowerCase(),
        };
      }),
    [misCitas, medicos, especialidades]
  );

  // 3) Aplicar filtros de estado y búsqueda sobre las enriquecidas
  const filtered = useMemo(
    () =>
      citasEnriquecidas.filter((c) => {
        if (estadoFilter !== "todas" && c.estadoLower !== estadoFilter)
          return false;
        if (
          searchTerm.trim() &&
          !c.nombreMedico.toLowerCase().includes(searchTerm.toLowerCase())
        )
          return false;
        return true;
      }),
    [citasEnriquecidas, estadoFilter, searchTerm]
  );

  if (loadingEsp || loadingCitas) {
    return <p className="text-center">Cargando datos…</p>;
  }
  if (errorEsp || errorCitas) {
    return <p className="text-error">Error cargando datos.</p>;
  }

  return (
    <div className="page-container">
      <h2 className="section-title">Mis citas</h2>

      <div className="filter-container">
        <div className="filter-group">
          <label>Estado</label>
          <select
            className="filter-select"
            value={estadoFilter}
            onChange={(e) => setEstadoFilter(e.target.value)}
          >
            <option value="todas">Todas</option>
            <option value="pendiente">Pendiente</option>
            <option value="confirmada">Confirmada</option>
          </select>
        </div>
        <div className="filter-group" style={{ flex: 1 }}>
          <label>Buscar</label>
          <input
            className="filter-input"
            type="text"
            placeholder="Buscar por médico…"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {filtered.length === 0 ? (
        <p>No tienes citas que coincidan con los filtros.</p>
      ) : (
        filtered.map((c) => (
          <div key={c.id} className="item-card">
            <div className="item-details">
              <p>
                <strong>Médico:</strong> {c.nombreMedico}
              </p>
              <p>
                <strong>Especialidad:</strong> {c.especialidad}
              </p>
              <p>
                <strong>Día:</strong> {c.dia} ({c.fecha})
              </p>
              <p>
                <strong>Hora:</strong> {c.hora}
              </p>
              <p>
                <strong>Estado:</strong>{" "}
                <span
                  className={
                    c.estadoLower === "confirmada"
                      ? "status-confirmada"
                      : "status-pendiente"
                  }
                >
                  {c.estado}
                </span>
              </p>
            </div>
          </div>
        ))
      )}
    </div>
  );
}
