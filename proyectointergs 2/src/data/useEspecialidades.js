// src/data/useEspecialidades.js
import { useState, useEffect } from 'react';
import * as svc from '../api/especialidadService';

export default function useEspecialidades() {
  const [especialidades, setEspecialidades] = useState([]);
  const [loading, setLoading]               = useState(true);
  const [error, setError]                   = useState(null);

  useEffect(() => {
    setLoading(true);
    svc.listar()
      .then(data => {
        setEspecialidades(data);
        setError(null);
      })
      .catch(err => {
        console.error("Error cargando especialidades:", err);
        setError(err);
      })
      .finally(() => setLoading(false));
  }, []);

  return { especialidades, loading, error };
}
