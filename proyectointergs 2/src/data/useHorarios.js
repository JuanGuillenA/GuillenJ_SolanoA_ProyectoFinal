// src/data/useHorarios.js
import { useState, useEffect } from 'react';
import * as svc from '../api/horarioService';

export default function useHorarios(medicoId) {
  const [horarios, setHorarios] = useState([]);
  const [loading,  setLoading]  = useState(true);
  const [error,    setError]    = useState(null);

  useEffect(() => {
    setLoading(true);
    setError(null);

    if (!medicoId) {
      setHorarios([]);
      setLoading(false);
      return;
    }

    // ← Aquí usamos listar(medicoId) para traer *todos* sus horarios
    svc.listar(medicoId)
      .then(data => {
        // asumimos que listar siempre devuelve un array
        setHorarios(data);
      })
      .catch(err => {
        console.error("Error cargando horarios:", err);
        setError(err);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [medicoId]);

  return {
    horarios,
    loading,
    error,
    crearRecurrencia: svc.crearRecurrencia,
    eliminarHorario:  svc.eliminar,
    modificarHorario: svc.modificar,
    verHorario:       svc.ver,
  };
}
