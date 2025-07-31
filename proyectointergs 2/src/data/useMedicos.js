// src/data/useMedicos.js
import { useState, useEffect } from 'react';
import * as svc from '../api/medicoService';

export default function useMedicos({ especialidadId=null, buscar=null }={}) {
  const [medicos, setMedicos] = useState([]);
  useEffect(() => {
    svc.listar({ especialidadId, buscar }).then(setMedicos);
  }, [especialidadId, buscar]);

  return {
    medicos,
    crearMedico: svc.crear,
    verMedico:   svc.ver,
    editarMedico:svc.modificar,
    eliminarMedico:svc.eliminar,
    listarMedicos: svc.listar    // <-- añadimos esto para recargar manualmente
  };
}
