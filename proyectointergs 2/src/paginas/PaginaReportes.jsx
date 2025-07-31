// src/paginas/PaginaReportes.jsx
import React, { useState } from 'react'
import useMedicos from '../data/useMedicos'

// Base URL tomada de la variable de entorno (ngrok) o fallback a localhost
const BASE = 'https://6d69c2462486.ngrok-free.app/citas/rest/reportes'

export default function PaginaReportes() {
  const { medicos = [] } = useMedicos()
  const [medicoId, setMedicoId] = useState('')
  const [desde, setDesde]       = useState('')
  const [hasta, setHasta]       = useState('')

  const openReport = (path, needsMedico = true) => {
    if (needsMedico && !medicoId) {
      alert('Selecciona un médico primero.')
      return
    }
    if (!desde || !hasta) {
      alert('Selecciona rango de fechas.')
      return
    }
    const qs = new URLSearchParams({
      ...(needsMedico ? { medicoId } : {}),
      desde,
      hasta
    }).toString()

    window.open(`${BASE}/${path}?${qs}`, '_blank')
  }

  return (
    <div className="page-container" style={{ maxWidth: 800, margin: '0 auto', padding: '1rem' }}>
      <h2 className="section-title">Reportes de Administración</h2>

      {/* Filtros */}
      <div className="card">
        <div className="filter-container" style={{ alignItems: 'center' }}>
          <div className="filter-group">
            <label>Médico</label>
            <select value={medicoId} onChange={e => setMedicoId(e.target.value)} className="filter-select">
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
            <input type="date" value={desde} onChange={e => setDesde(e.target.value)} className="filter-input" />
          </div>
          <div className="filter-group">
            <label>Hasta</label>
            <input type="date" value={hasta} onChange={e => setHasta(e.target.value)} className="filter-input" />
          </div>
        </div>
      </div>

      {/* Botones de reportes */}
      <div className="card" style={{ marginTop: '1rem' }}>
        <h3 className="section-title" style={{ fontSize: '1.125rem' }}>Descarga tus reportes</h3>
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
            gap: '0.75rem',
            marginTop: '1rem'
          }}
        >
          <button className="btn btn-primary" onClick={() => openReport('ocupacion/medicos/pdf', false)}>
            Ocupación (PDF)
          </button>
          <button className="btn btn-secondary" onClick={() => openReport('ocupacion/medicos/excel', false)}>
            Ocupación (Excel)
          </button>
          <button className="btn btn-primary" onClick={() => openReport(`medico/${medicoId}/pdf`, true)}>
            Citas por Médico (PDF)
          </button>
          <button className="btn btn-secondary" onClick={() => openReport(`medico/${medicoId}/excel`, true)}>
            Citas por Médico (Excel)
          </button>
          <button className="btn btn-primary" onClick={() => openReport('especialidades/pdf', false)}>
            Citas por Especialidad (PDF)
          </button>
          <button className="btn btn-secondary" onClick={() => openReport('especialidades/excel', false)}>
            Citas por Especialidad (Excel)
          </button>
        </div>
      </div>
    </div>
  )
}
