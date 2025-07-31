// src/paginas/PaginaHistorialAdmin.jsx
import React, { useState } from 'react'
import { useHistorialCitas } from '../data/useHistorialCitas'
import useMedicos            from '../data/useMedicos'
import useUsuarios           from '../data/useUsuarios'
import useEspecialidades     from '../data/useEspecialidades'
import { obtenerNombrePaciente } from '../utils/pacienteHelper'



// — helper para formatear horas “HH:MM” —
function formatHora(raw) {
  if (Array.isArray(raw)) {
    const [h, m] = raw
    return `${String(h).padStart(2,'0')}:${String(m).padStart(2,'0')}`
  }
  const s = String(raw)
  if (s.includes(':')) {
    const [h, m] = s.split(':')
    return `${h.padStart(2,'0')}:${m.padStart(2,'0')}`
  }
  const nums = s.replace(/\D/g,'').padStart(4,'0')
  const h    = nums.slice(0, nums.length-2)
  const m    = nums.slice(-2)
  return `${h}:${m}`
}

// — helper para formatear fechas [YYYY, M, D] a "YYYY-MM-DD" —
function formatFecha(raw) {
  if (Array.isArray(raw) && raw.length === 3) {
    const [y,m,d] = raw
    return `${y}-${String(m).padStart(2,'0')}-${String(d).padStart(2,'0')}`
  }
  if (typeof raw==='string' && /^\d{4}-\d{2}-\d{2}$/.test(raw)) {
    return raw
  }
  return '—'
}

export default function PaginaHistorialAdmin() {
  const { medicos = [] }     = useMedicos()
  const { usuarios = [] }    = useUsuarios()
  const {
    especialidades,
    loading: loadingEsp,
    error:   errorEsp
  } = useEspecialidades()

  // filtros que se envían al back
  const [filtros, setFiltros] = useState({
    medicoId:       '',
    especialidadId: '',
    estado:         '',
    desde:          '',
    hasta:          '',
  })

  // término de búsqueda libre para paciente
  const [searchPaciente, setSearchPaciente] = useState('')

  const {
    data:    citas     = [],
    loading: loadingCitas,
    error:   errorCitas
  } = useHistorialCitas(filtros)

  const handleFiltroChange = e => {
    const { name, value } = e.target
    setFiltros(prev => ({ ...prev, [name]: value }))
  }

  if (loadingEsp) return <p style={{ textAlign:'center' }}>Cargando especialidades…</p>
  if (errorEsp)   return <p style={{ textAlign:'center', color:'red' }}>Error cargando especialidades.</p>

  // primer filtro: el que envías al back (médico/estado/fechas/especialidad)
  const preFiltradas = citas.filter(c => {
    if (filtros.medicoId && c.medicoId !== Number(filtros.medicoId)) return false
    if (filtros.estado     && c.estado     !== filtros.estado)           return false
    if (filtros.desde && filtros.hasta) {
      const f = formatFecha(c.fecha)
      if (f < filtros.desde || f > filtros.hasta) return false
    }
    if (filtros.especialidadId) {
      const doctor = medicos.find(d => d.id === c.medicoId)
      if (!doctor || doctor.especialidadId !== Number(filtros.especialidadId)) return false
    }
    return true
  })

  // segundo filtro: búsqueda libre por paciente (displayName o email)
  const citasFiltradas = preFiltradas.filter(c => {
    if (!searchPaciente.trim()) return true
    const term = searchPaciente.trim().toLowerCase()
    // buscar usando firebaseUid
    const usr  = usuarios.find(u => u.firebaseUid === c.pacienteId)
    const nombre = usr
      ? (usr.displayName || usr.email).toLowerCase()
      : ''
    return nombre.includes(term)
  })

  return (
    <div className="page-container" style={{ maxWidth: "1400px", margin: "0 auto", padding: "1rem" }}>
      <h2 className="section-title">Historial de Citas (Admin)</h2>

      <div className="filter-container" style={{ margin: "1rem 0", display: 'flex', flexWrap: 'wrap', gap: '1rem' }}>
        <div className="filter-group">
          <label>Buscar Paciente</label>
          <input
            type="text"
            placeholder="Escribe nombre o email..."
            value={searchPaciente}
            onChange={e => setSearchPaciente(e.target.value)}
            className="filter-input"
          />
        </div>
        <div className="filter-group">
          <label>Médico</label>
          <select name="medicoId" value={filtros.medicoId} onChange={handleFiltroChange} className="filter-select">
            <option value="">-- Médico --</option>
            {medicos.map(d => (
              <option key={d.id} value={d.id}>
                {d.nombre} {d.apellido}
              </option>
            ))}
          </select>
        </div>
        <div className="filter-group">
          <label>Especialidad</label>
          <select name="especialidadId" value={filtros.especialidadId} onChange={handleFiltroChange} className="filter-select">
            <option value="">-- Especialidad --</option>
            {especialidades.map(e => (
              <option key={e.id} value={e.id}>
                {e.nombre}
              </option>
            ))}
          </select>
        </div>
        <div className="filter-group">
          <label>Estado</label>
          <select name="estado" value={filtros.estado} onChange={handleFiltroChange} className="filter-select">
            <option value="">-- Estado --</option>
            <option value="PENDIENTE">Pendiente</option>
            <option value="CONFIRMADA">Confirmada</option>
          </select>
        </div>
        <div className="filter-group">
          <label>Desde</label>
          <input type="date" name="desde" value={filtros.desde} onChange={handleFiltroChange} className="filter-input" />
        </div>
        <div className="filter-group">
          <label>Hasta</label>
          <input type="date" name="hasta" value={filtros.hasta} onChange={handleFiltroChange} className="filter-input" />
        </div>
      </div>

      {loadingCitas ? (
        <p style={{ textAlign:'center' }}>Cargando historial…</p>
      ) : errorCitas ? (
        <p style={{ textAlign:'center', color:'red' }}>Error al cargar historial.</p>
      ) : (
        <div className="table-container">
          <table className="table" style={{ minWidth: "1000px", tableLayout: "auto" }}>
            <thead>
              <tr>
                <th>Paciente</th>
                <th>Médico</th>
                <th>Especialidad</th>
                <th>Fecha</th>
                <th>Hora</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {citasFiltradas.length === 0 ? (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '1rem' }}>
                    No hay resultados
                  </td>
                </tr>
              ) : citasFiltradas.map(c => {
                  const usuario = usuarios.find(u => u.firebaseUid === c.pacienteId)
                  const doctor  = medicos.find(d => d.id === c.medicoId)
                  const esp     = especialidades.find(e => e.id === doctor?.especialidadId)
                  return (
                    <tr key={c.id}>
                      <td>{ obtenerNombrePaciente(c.pacienteId, usuarios) }</td>
                      <td>{doctor ? `${doctor.nombre} ${doctor.apellido}` : c.medicoId}</td>
                      <td>{esp ? esp.nombre : '—'}</td>
                      <td>{formatFecha(c.fecha)}</td>
                      <td>{formatHora(c.hora)}</td>
                      <td style={{ textTransform: 'capitalize' }}>{c.estado.toLowerCase()}</td>
                    </tr>
                  )
                })
              }
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
