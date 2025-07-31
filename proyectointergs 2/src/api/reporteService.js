// src/api/reportesService.js
import http from './http'

// ocupación por médico
export const ocupacionPdf = (medicoId, desde, hasta) =>
  http.get('/reportes/ocupacion/pdf', {
    params: { medicoId, desde, hasta },
    responseType: 'blob'
  })

export const ocupacionExcel = (medicoId, desde, hasta) =>
  http.get('/reportes/ocupacion/excel', {
    params: { medicoId, desde, hasta },
    responseType: 'blob'
  })

// citas por especialidad
export const especialidadPdf = (desde, hasta) =>
  http.get('/reportes/especialidades/pdf', {
    params: { desde, hasta },
    responseType: 'blob'
  })

export const especialidadExcel = (desde, hasta) =>
  http.get('/reportes/especialidades/excel', {
    params: { desde, hasta },
    responseType: 'blob'
  })

// citas agrupadas por médico
export const citasPorMedicoPdf = (medicoId, desde, hasta) =>
  http.get(`/reportes/medico/${medicoId}/pdf`, {
    params: { desde, hasta },
    responseType: 'blob'
  })

export const citasPorMedicoExcel = (medicoId, desde, hasta) =>
  http.get(`/reportes/medico/${medicoId}/excel`, {
    params: { desde, hasta },
    responseType: 'blob'
  })
