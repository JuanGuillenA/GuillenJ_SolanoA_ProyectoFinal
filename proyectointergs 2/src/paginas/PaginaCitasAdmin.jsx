// src/paginas/PaginaCitasAdmin.jsx
import React, { useState, useMemo, useEffect } from "react";
import {
  useCitasAdmin,
  actualizarCita,
} from "../data/useCitas";
import useMedicos   from "../data/useMedicos";
import useUsuarios  from "../data/useUsuarios";
import useHorarios  from "../data/useHorarios";
import * as svc     from "../api/citasService";
import { eliminar } from "../api/medicoService";

// — helper para formatear horas en “HH:MM”
function formatHora(raw) {
  if (Array.isArray(raw)) {
    const [h, m] = raw;
    return `${String(h).padStart(2, "0")}:${String(m).padStart(2, "0")}`;
  }
  const s = String(raw);
  if (s.includes(":")) {
    const [h, m] = s.split(":");
    return `${h.padStart(2, "0")}:${m.padStart(2, "0")}`;
  }
  const nums = s.replace(/\D/g, "").padStart(4, "0");
  const h = nums.slice(0, nums.length - 2);
  const m = nums.slice(-2);
  return `${h}:${m}`;
}

// — helper para formatear fechas [YYYY, M, D] → "YYYY-MM-DD"
function formatFecha(raw) {
  if (Array.isArray(raw) && raw.length === 3) {
    const [y, m, d] = raw;
    return `${y}-${String(m).padStart(2, "0")}-${String(d).padStart(2, "0")}`;
  }
  return typeof raw === "string" && raw ? raw : "—";
}

export default function PaginaCitasAdmin() {
  const { citas, confirmar, eliminar } = useCitasAdmin();
  const { medicos }          = useMedicos();
  const { usuarios }         = useUsuarios();

  const [estadoFilter, setEstadoFilter] = useState("todas");
  const [searchTerm,   setSearchTerm]   = useState("");

  const [editingId,  setEditingId]  = useState(null);
  const [editMedico, setEditMedico] = useState("");
  const [editFecha,  setEditFecha]  = useState("");
  const [editHora,   setEditHora]   = useState("");
  const [editError,  setEditError]  = useState("");

  const { horarios: horariosEdit }       = useHorarios(editMedico);
  const [takenSlotsEdit, setTakenSlotsEdit] = useState([]);

  useEffect(() => {
    if (!editMedico || !editFecha) {
      setTakenSlotsEdit([]);
      return;
    }
    svc
      .historial({
        medicoId: editMedico,
        desde:    editFecha,
        hasta:    editFecha,
        estado:   "CONFIRMADA",
      })
      .then(list => {
        const self = citas.find(c => c.id === editingId);
        const ocupadas = list
          .map(c => c.hora)
          .filter(h => !(self && self.hora === h));
        setTakenSlotsEdit(ocupadas);
      })
      .catch(() => setTakenSlotsEdit([]));
  }, [editMedico, editFecha, editingId, citas]);

  const filteredCitas = useMemo(() => {
    return citas.filter(c => {
      const est = (c.estado || "").toLowerCase();
      if (estadoFilter !== "todas" && est !== estadoFilter) return false;

      const med = medicos.find(m => m.id === c.medicoId);
      const nombreMed = med
        ? `${med.nombre} ${med.apellido}`.toLowerCase()
        : "";

      const pac = usuarios.find(u => u.firebaseUid === c.pacienteId);
      const infoPac = pac
        ? `${(pac.displayName || `${pac.nombre} ${pac.apellido}`).trim()} · ${pac.email}`.toLowerCase()
        : "";

      if (searchTerm.trim()) {
        const term = searchTerm.trim().toLowerCase();
        return nombreMed.includes(term) || infoPac.includes(term);
      }
      return true;
    });
  }, [citas, medicos, usuarios, estadoFilter, searchTerm]);

  const validarEdicion = () => {
    if (!editMedico || !editFecha || !editHora) {
      setEditError("Debe completar Médico, Fecha y Hora.");
      return false;
    }
    if (takenSlotsEdit.includes(editHora)) {
      setEditError("Esa hora ya está confirmada para este médico y fecha.");
      return false;
    }
    setEditError("");
    return true;
  };

  const handleGuardarEdicion = async () => {
    if (!validarEdicion()) return;
    try {
      await actualizarCita(editingId, {
        medicoId: editMedico,
        fechaISO: editFecha,
        hora:     editHora,
      });
      setEditingId(null);
      setEditMedico("");
      setEditFecha("");
      setEditHora("");
      setEditError("");
    } catch (err) {
      console.error("Error actualizando cita:", err);
      setEditError(err.message || "Error al actualizar la cita.");
    }
  };

  return (
    <div className="page-container">
      <h2 className="section-title">Citas</h2>

      {/* filtros */}
      <div className="filter-container" style={{ marginBottom: "1rem" }}>
        <div className="filter-group">
          <label>Estado</label>
          <select
            className="filter-select"
            value={estadoFilter}
            onChange={e => setEstadoFilter(e.target.value)}
          >
            <option value="todas">Todas</option>
            <option value="pendiente">Pendiente</option>
            <option value="confirmada">Confirmada</option>
          </select>
        </div>
        <div className="filter-group">
          <label>Buscar</label>
          <input
            className="filter-input"
            type="text"
            placeholder="Paciente o médico..."
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {/* listado */}
      {filteredCitas.length === 0 ? (
        <p>No hay citas que coincidan con los filtros.</p>
      ) : (
        filteredCitas.map(c => {
          const medObj      = medicos.find(m=>m.id===c.medicoId);
          const pacienteObj = usuarios.find(u=>u.firebaseUid===c.pacienteId);
          const pacienteStr = pacienteObj
            ? `${(pacienteObj.displayName||`${pacienteObj.nombre} ${pacienteObj.apellido}`).trim()} · ${pacienteObj.email}`
            : "—";

          return (
            <div key={c.id} className="card" style={{ marginBottom: "1rem" }}>
              {editingId === c.id ? (
                <>
                  <h3 className="section-title" style={{ fontSize: "1.25rem" }}>
                    Editar cita de {pacienteStr}
                  </h3>
                  <div className="filter-container">
                    <div className="filter-group">
                      <label>Médico</label>
                      <select
                        className="filter-select"
                        value={editMedico}
                        onChange={e => {
                          setEditMedico(e.target.value);
                          setEditFecha("");
                          setEditHora("");
                        }}
                      >
                        <option value="">-- Seleccione médico --</option>
                        {medicos.map(m=>(
                          <option key={m.id} value={m.id}>
                            {m.nombre} {m.apellido} · {m.especialidad}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div className="filter-group">
                      <label>Fecha</label>
                      <input
                        className="filter-input"
                        type="date"
                        value={editFecha}
                        onChange={e => {
                          setEditFecha(e.target.value);
                          setEditHora("");
                        }}
                      />
                    </div>
                    <div className="filter-group">
                      <label>Hora</label>
                      <select
                        className="filter-select"
                        value={editHora}
                        onChange={e => setEditHora(e.target.value)}
                      >
                        <option value="">-- Seleccione hora --</option>
                        {(() => {
                          const diaNum = new Date(editFecha).getDay();
                          const row = horariosEdit.find(h => h.dias?.includes(diaNum));
                          return (row?.slots||[])
                            .filter(s => !takenSlotsEdit.includes(s))
                            .map(s => (
                              <option key={s} value={s}>
                                {formatHora(s)}
                              </option>
                            ));
                        })()}
                      </select>
                    </div>
                  </div>

                  {editError && (
                    <p className="text-error">{editError}</p>
                  )}

                  <div className="form-actions">
                    <button className="btn btn-primary" onClick={handleGuardarEdicion}>
                      Guardar
                    </button>
                    <button className="btn btn-secondary" onClick={()=>setEditingId(null)}>
                      Cancelar
                    </button>
                  </div>
                </>
              ) : (
                <>
                  <p><strong>Paciente:</strong> {pacienteStr}</p>
                  <p><strong>Médico:</strong> {medObj ? `${medObj.nombre} ${medObj.apellido}` : "—"}</p>
                  <p><strong>Especialidad:</strong> {c.especialidad}</p>
                  <p><strong>Fecha:</strong> {formatFecha(c.fecha)}</p>
                  <p><strong>Hora:</strong>  {formatHora(c.hora)}</p>
                  <p>
                    <strong>Estado:</strong>{" "}
                    <span style={{ color: c.estado.toLowerCase() === "confirmada" ? "green" : "orange" }}>
                      {c.estado.toLowerCase()}
                    </span>
                  </p>

                  <div className="item-actions" style={{ marginTop: "0.5rem" }}>
                    <button className="btn btn-secondary" onClick={()=>{
                      setEditingId(c.id);
                      setEditMedico(c.medicoId);
                      setEditFecha(formatFecha(c.fecha));
                      setEditHora(c.hora);
                      setEditError("");
                    }}>
                      Editar
                    </button>

                    {c.estado.toLowerCase() === "pendiente" && (
                      <button className="btn btn-success" onClick={()=>confirmar(c.id)}>
                        Confirmar
                      </button>
                    )}

                    <button className="btn btn-danger" onClick={()=>eliminar(c.id)}>
                      Eliminar
                    </button>
                  </div>
                </>
              )}
            </div>
          );
        })
      )}
    </div>
  );
}
