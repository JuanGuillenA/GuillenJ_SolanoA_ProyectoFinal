// src/utils/pacienteHelpers.js
export function obtenerNombrePaciente(pacienteId, usuarios) {
    const usuario = usuarios.find(u => u.firebaseUid === pacienteId)
    return usuario ? usuario.displayName : ''
  }