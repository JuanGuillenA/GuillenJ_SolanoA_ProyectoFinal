// src/data/useCitas.js

import { useState, useEffect } from 'react';
import * as svc from '../api/citasService';

// Hook para el Admin (no lo tocamos)
export function useCitasAdmin() {
  const [citas, setCitas]     = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    svc.listarTodas().then(list => {
      setCitas(list);
      setLoading(false);
    });
  }, []);

  const confirmar = async (id) => {
    const updated = await svc.modificar(id, { estado: 'CONFIRMADA' });
    setCitas(cs => cs.map(c => c.id === updated.id ? updated : c));
  };

  const eliminar = async (id) => {
    if (!window.confirm('¿Seguro que quieres eliminar esta cita?')) return;
    await svc.eliminar(id);
    setCitas(cs => cs.filter(c => c.id !== id));
  };

  return { citas, confirmar, eliminar, loading };
}



// Hook para el paciente
export function useMisCitas(pacienteId) {
  const [citas,   setCitas]   = useState([]);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState(null);

  useEffect(() => {
    setLoading(true);
    setError(null);

    if (!pacienteId) {
      setCitas([]);
      setLoading(false);
      return;
    }

    // Ahora le pasamos un objeto con pacienteId
    svc.historial({ usuario:pacienteId })
      .then(list => {
        setCitas(list);
      })
      .catch(err => {
        console.error('Error al cargar historial:', err);
        setError(err);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [pacienteId]);

  return { citas, loading, error };
}

// Exponemos correctamente los helpers
export const crearCita      = svc.crearCita;    // ← antes era svc.crear
export const eliminarCita   = svc.eliminar;
export const actualizarCita = svc.modificar;
export const verCita        = svc.ver;
export const listarHistorial= svc.historial;
