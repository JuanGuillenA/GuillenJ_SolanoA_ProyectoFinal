import { useState, useEffect, useCallback } from 'react'
import * as svc from '../api/citasService'

/**
 * Carga citas según cualquier combinación de filtros.
 * Si no pones ningún filtro, devuelve **todas** las citas.
 */
export function useHistorialCitas({
  pacienteId   = '',
  medicoId     = '',
  desde        = '',
  hasta        = '',
  estado       = '',
  especialidad = '',
} = {}) {
  const [data,    setData]    = useState([])
  const [loading, setLoading] = useState(true)
  const [error,   setError]   = useState(null)

  const fetch = useCallback(async () => {
    setLoading(true)
    setError(null)

    // Construimos params solo con los que vengan
    const params = {}
    if (pacienteId)   params.usuario   = pacienteId
    if (medicoId)     params.medicoId     = medicoId
    if (desde)        params.desde        = desde
    if (hasta)        params.hasta        = hasta
    if (estado)       params.estado       = estado
    if (especialidad) params.especialidad = especialidad

    try {
      const lista = await svc.historial(params)
      setData(lista)
    } catch (e) {
      console.error('Error al cargar historial:', e)
      setError(e)
    } finally {
      setLoading(false)
    }
  }, [pacienteId, medicoId, desde, hasta, estado, especialidad])

  useEffect(() => {
    fetch()
  }, [fetch])

  return { data, loading, error, refetch: fetch }
}
