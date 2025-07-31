// src/componentes/CitaForm.jsx
import React, { useState, useEffect, useMemo } from "react";
import { useAuth } from "../autenticacion/ContextoAutenticacion";
import { crearCita, historial } from "../api/citasService";
import useEspecialidades from "../data/useEspecialidades";
import useMedicos from "../data/useMedicos";
import useHorarios from "../data/useHorarios";

export default function CitaForm({ onCitaGuardada }) {
  const { usuario } = useAuth();
  const { especialidades, loading: loadingEsp, error: errorEsp } = useEspecialidades();
  const { medicos = [] } = useMedicos();
  const [espId, setEspId] = useState("");
  const [medId, setMedId] = useState("");
  const [fecha, setFecha] = useState("");
  const [horaTxt, setHoraTxt] = useState("");
  const [error, setError] = useState("");
  const { horarios, loading: loadingHor, error: errorHor } = useHorarios(medId);

  const candidatos = useMemo(
    () => medicos.filter(m => String(m.especialidadId) === String(espId)),
    [medicos, espId]
  );

  const [ocupadas, setOcupadas] = useState([]);
  useEffect(() => {
    if (!medId || !fecha) {
      setOcupadas([]);
      return;
    }
    historial({ medicoId: medId, desde: fecha, hasta: fecha })
      .then(list =>
        setOcupadas(
          list.map(c => {
            const hh = String(c.hora[0]).padStart(2, "0");
            const mm = String(c.hora[1]).padStart(2, "0");
            return `${hh}:${mm}`;
          })
        )
      )
      .catch(() => setOcupadas([]));
  }, [medId, fecha]);

  const slotsDisponibles = useMemo(() => {
    if (!fecha) return [];
    const [yy, mm, dd] = fecha.split("-").map(Number);
    const dayIdx = new Date(fecha).getDay();
    const out = [];

    horarios.forEach(h => {
      const aplicaFecha = Array.isArray(h.fecha)
        ? h.fecha[0] === yy && h.fecha[1] === mm && h.fecha[2] === dd
        : Array.isArray(h.dias) && h.dias.includes(dayIdx);
      if (!aplicaFecha) return;

      let cur = new Date(yy, mm - 1, dd, h.horaInicio[0], h.horaInicio[1]);
      const end = new Date(yy, mm - 1, dd, h.horaFin[0], h.horaFin[1]);
      while (cur < end) {
        const hh = String(cur.getHours()).padStart(2, "0");
        const mn = String(cur.getMinutes()).padStart(2, "0");
        out.push(`${hh}:${mn}`);
        cur = new Date(cur.getTime() + 30 * 60000);
      }
    });

    return out;
  }, [horarios, fecha]);

  if (loadingEsp || loadingHor) return <p>Cargando datos…</p>;
  if (errorEsp) return <p className="text-error">Error cargando especialidades.</p>;
  if (errorHor) return <p className="text-error">Error cargando horarios.</p>;

  const calcularHorarioId = () => {
    if (!horaTxt || !fecha) return null;
    const [yy, mm, dd] = fecha.split("-").map(Number);
    const dayIdx = new Date(fecha).getDay();

    return (
      horarios.find(h => {
        const aplicaFecha = Array.isArray(h.fecha)
          ? h.fecha[0] === yy && h.fecha[1] === mm && h.fecha[2] === dd
          : Array.isArray(h.dias) && h.dias.includes(dayIdx);
        if (!aplicaFecha) return false;

        const [H, M] = horaTxt.split(":").map(Number);
        const t = H * 60 + M;
        const desde = h.horaInicio[0] * 60 + h.horaInicio[1];
        const hasta = h.horaFin[0] * 60 + h.horaFin[1];
        return t >= desde && t < hasta;
      })?.id || null
    );
  };

  const handleSubmit = e => {
    e.preventDefault();
    setError("");

    if (!espId) return setError("Selecciona especialidad.");
    if (!medId) return setError("Selecciona médico.");
    if (!fecha) return setError("Selecciona fecha.");
    if (!horaTxt) return setError("Escribe hora en formato HH:MM.");
    if (!/^\d\d:\d\d$/.test(horaTxt)) return setError("Formato de hora inválido.");
    if (!slotsDisponibles.includes(horaTxt)) return setError("El médico NO atiende a esa hora.");
    if (ocupadas.includes(horaTxt)) return setError("Ya hay una cita a esa hora.");

    const horarioId = calcularHorarioId();
    if (!horarioId) return setError("No se encontró horario válido.");
    if (!window.confirm(`¿Confirmas cita el ${fecha} a las ${horaTxt}?`)) return;

    crearCita({ pacienteId: usuario.id, medicoId: medId, horarioId })
      .then(() => {
        alert("Cita creada correctamente.");
        onCitaGuardada?.();
        setEspId(""); setMedId(""); setFecha(""); setHoraTxt("");
      })
      .catch(err => {
        // Si el back responde 409, mostramos su mensaje
        if (err.response?.status === 409) {
          const msg = err.response.data?.error 
                    || "Ya existe una cita en ese horario.";
          setError(msg);
        } else {
          // Otros errores
          console.error(err);
          setError("Error al crear cita. Intenta de nuevo.");
        }
      });
    };

  return (
    <form onSubmit={handleSubmit} className="cita-form">
      <h3 className="section-title">Solicitar cita</h3>
      {error && <p className="text-error">{error}</p>}

      <div className="form-group">
        <label>Especialidad</label>
        <select
          value={espId}
          onChange={e => {
            setEspId(e.target.value);
            setMedId("");
            setFecha("");
            setHoraTxt("");
          }}
        >
          <option value="">-- Seleccione --</option>
          {especialidades.map(esp => (
            <option key={esp.id} value={esp.id}>{esp.nombre}</option>
          ))}
        </select>
      </div>

      <div className="form-group">
        <label>Médico</label>
        <select
          value={medId}
          disabled={!espId}
          onChange={e => {
            setMedId(e.target.value);
            setFecha("");
            setHoraTxt("");
          }}
        >
          <option value="">-- Seleccione --</option>
          {candidatos.map(m => (
            <option key={m.id} value={m.id}>
              {m.nombre} {m.apellido}
            </option>
          ))}
        </select>
      </div>

      <div className="form-group">
        <label>Fecha</label>
        <input
          type="date"
          value={fecha}
          disabled={!medId}
          onChange={e => {
            setFecha(e.target.value);
            setHoraTxt("");
          }}
        />
      </div>

      <div className="form-group">
        <label>Hora (HH:MM)</label>
        <input
          type="text"
          value={horaTxt}
          disabled={!fecha}
          placeholder="14:30"
          onChange={e => setHoraTxt(e.target.value)}
        />
      </div>

      <div className="form-actions">
        <button type="submit" className="btn btn-primary">Guardar Cita</button>
        <button
          type="button"
          className="btn btn-secondary"
          onClick={() => {
            setEspId("");
            setMedId("");
            setFecha("");
            setHoraTxt("");
            setError("");
          }}
        >
          Cancelar
        </button>
      </div>
    </form>
  );
}

