// src/paginas/PaginaHistorialPaciente.jsx
import React, { useState } from 'react';
import { useAuth }           from '../autenticacion/ContextoAutenticacion';
import { useHistorialCitas } from '../data/useHistorialCitas';
import useMedicos            from '../data/useMedicos';
import useEspecialidades     from '../data/useEspecialidades';

// formatea [hora, minuto] o "HH:MM" a "HH:MM"
function formatHora(raw) {
  if (Array.isArray(raw)) {
    const [h, m] = raw;
    return `${String(h).padStart(2,'0')}:${String(m).padStart(2,'0')}`;
  }
  if (typeof raw === 'string' && raw.includes(':')) {
    const [h, m] = raw.split(':');
    return `${h.padStart(2,'0')}:${m.padStart(2,'0')}`;
  }
  return raw;
}

export default function PaginaHistorialPaciente() {
  const { usuario } = useAuth();
  const { medicos = [] }        = useMedicos();
  const {
    especialidades,
    loading: loadingEsp,
    error:   errorEsp
  } = useEspecialidades();

  const [filtros, setFiltros] = useState({
    pacienteId:   usuario.id,
    medicoId:     '',
    especialidad: '',
    estado:       '',
    desde:        '',
    hasta:        '',
  });

  const {
    data: citas = [],
    loading: loadingCitas,
    error:   errorCitas
  } = useHistorialCitas(filtros);

  const handleChange = e => {
    const { name, value } = e.target;
    setFiltros(f => ({ ...f, [name]: value }));
  };

  if (loadingEsp)   return <p className="text-center">Cargando especialidades…</p>;
  if (errorEsp)     return <p className="text-error text-center">Error cargando especialidades.</p>;
  if (loadingCitas) return <p className="text-center">Cargando historial…</p>;
  if (errorCitas)   return <p className="text-error text-center">Error cargando historial.</p>;

  return (
    <div className="page-container">
      <h2 className="section-title">Mi Historial de Citas</h2>

      <div className="filter-container">
        <div className="filter-group">
          <label>Estado</label>
          <select
            name="estado"
            value={filtros.estado}
            onChange={handleChange}
            className="filter-select"
          >
            <option value="">-- Estado --</option>
            <option value="PENDIENTE">Pendiente</option>
            <option value="CONFIRMADA">Confirmada</option>
          </select>
        </div>

        <div className="filter-group">
          <label>Especialidad</label>
          <select
            name="especialidad"
            value={filtros.especialidad}
            onChange={handleChange}
            className="filter-select"
          >
            <option value="">-- Especialidad --</option>
            {especialidades.map(e => (
              <option key={e.id} value={e.nombre}>{e.nombre}</option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label>Médico</label>
          <select
            name="medicoId"
            value={filtros.medicoId}
            onChange={handleChange}
            className="filter-select"
          >
            <option value="">-- Médico --</option>
            {medicos.map(m => (
              <option key={m.id} value={m.id}>
                {m.nombre} {m.apellido}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label>Desde</label>
          <input
            type="date"
            name="desde"
            value={filtros.desde}
            onChange={handleChange}
            className="filter-input"
          />
        </div>

        <div className="filter-group">
          <label>Hasta</label>
          <input
            type="date"
            name="hasta"
            value={filtros.hasta}
            onChange={handleChange}
            className="filter-input"
          />
        </div>
      </div>

      {citas.length > 0 ? (
        <div className="card">
          {citas.map(c => {
            const doctor = medicos.find(m => String(m.id) === String(c.medicoId));
            const esp    = especialidades.find(e =>
              doctor && String(e.id) === String(doctor.especialidadId)
            );
            return (
              <div key={c.id} className="item-card">
                <div className="item-details">
                  <p><strong>{formatHora(c.hora)}</strong></p>
                  <p>{doctor
                    ? `${doctor.nombre} ${doctor.apellido}`
                    : c.medicoId
                  }</p>
                  <p>{esp ? esp.nombre : '—'}</p>
                </div>
                <div className="item-actions">
                  <span className={
                    c.estado === 'CONFIRMADA'
                      ? 'status-confirmada'
                      : 'status-pendiente'
                  }>
                    {c.estado}
                  </span>
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <p>No tienes citas en ese rango.</p>
      )}
    </div>
  );
}
