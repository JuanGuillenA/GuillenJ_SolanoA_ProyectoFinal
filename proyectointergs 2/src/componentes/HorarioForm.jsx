// src/componentes/HorarioForm.jsx
import React, { useState } from "react";

/**
 * DIAS:
 *  - Array con índices de días de la semana (0=domingo,…6=sábado).
 */
const DIAS = [
  { idx: 1, label: "Lunes" },
  { idx: 2, label: "Martes" },
  { idx: 3, label: "Miércoles" },
  { idx: 4, label: "Jueves" },
  { idx: 5, label: "Viernes" },
  { idx: 6, label: "Sábado" },
  { idx: 0, label: "Domingo" },
];

export default function HorarioForm({ onSave }) {
  const [diasSemana, setDiasSemana]   = useState([]);
  const [fechaInicio, setFechaInicio] = useState("");
  const [fechaFin, setFechaFin]       = useState("");
  const [horaInicio, setHoraInicio]   = useState("");
  const [horaFin, setHoraFin]         = useState("");

  const toggleDia = (d) =>
    setDiasSemana(prev =>
      prev.includes(d) ? prev.filter(x => x !== d) : [...prev, d]
    );

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!diasSemana.length || !fechaInicio || !fechaFin || !horaInicio || !horaFin) {
      alert("Completa todos los campos.");
      return;
    }
    if (fechaInicio > fechaFin) {
      alert("La fecha de inicio debe ser anterior o igual a la de fin.");
      return;
    }

    onSave({ diasSemana, fechaInicio, fechaFin, horaInicio, horaFin });
    setDiasSemana([]);
    setFechaInicio("");
    setFechaFin("");
    setHoraInicio("");
    setHoraFin("");
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginBottom: "1rem" }}>
      <div>
        <label style={{ display: "block" }}>Días de la semana:</label>
        {DIAS.map(d => (
          <label key={d.idx} style={{ marginRight: 12 }}>
            <input
              type="checkbox"
              checked={diasSemana.includes(d.idx)}
              onChange={() => toggleDia(d.idx)}
            /> {d.label}
          </label>
        ))}
      </div>

      <div style={{ marginTop: 12, display: "flex", gap: 16 }}>
        <div>
          <label>Fecha inicio:</label><br />
          <input type="date" value={fechaInicio} onChange={e => setFechaInicio(e.target.value)} required />
        </div>
        <div>
          <label>Fecha fin:</label><br />
          <input type="date" value={fechaFin} onChange={e => setFechaFin(e.target.value)} required />
        </div>
        <div>
          <label>Hora inicio:</label><br />
          <input type="time" value={horaInicio} onChange={e => setHoraInicio(e.target.value)} required />
        </div>
        <div>
          <label>Hora fin:</label><br />
          <input type="time" value={horaFin} onChange={e => setHoraFin(e.target.value)} required />
        </div>
      </div>

      <button type="submit" style={{ marginTop: 16 }}>Guardar horario</button>
    </form>
  );
}