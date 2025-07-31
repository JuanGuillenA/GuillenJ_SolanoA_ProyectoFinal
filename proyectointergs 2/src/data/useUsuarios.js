import { useState, useEffect } from 'react';
import * as svc from '../api/usuarioService';

export default function useUsuarios(rol=null) {
  const [usuarios, setUsuarios] = useState([]);
  useEffect(() => {
    svc.listar(rol).then(setUsuarios);
  }, [rol]);

  return {
    usuarios,
    crearUsuario: svc.crear,
    verUsuario:   svc.ver,
    verPorUid:    svc.verPorFirebaseUid,
    editarUsuario:svc.modificar,
    eliminarUsuario:svc.eliminar
  };
}
