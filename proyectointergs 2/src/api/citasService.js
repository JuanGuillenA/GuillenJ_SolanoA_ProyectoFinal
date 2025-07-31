import http from "./http";

// Creamos la cita enviando pacienteId (numérico), horarioId y medicoId
export async function crearCita({ pacienteId, horarioId, medicoId }) {
  const payload = {
    pacienteId,   // ahora coincide con tu DTO Java
    horarioId,    // id del horario
    medicoId      // id del médico
  };
  const resp = await http.post("/citas", payload);
  return resp.data;
}

export const listarTodas = () =>
  http.get("/citas").then(r => r.data);

export const historial = params =>
  http.get("/citas/historial", { params }).then(r => r.data);

export const modificar = (id, dto) =>
  http.put(`/citas/${id}`, dto).then(r => r.data);

export const eliminar = id =>
  http.delete(`/citas/${id}`);

export const ver = id =>
  http.get(`/citas/${id}`).then(r => r.data);
