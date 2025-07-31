import http from './http';

// crea slots según recurrencia
export const crearRecurrencia = (dto) =>
  http.post('/horarios/recurrencias', dto);

// lista slots de un médico
export const listar = (medicoId) =>
  http.get('/horarios', { params: { medicoId } }).then(r => r.data);

export const ver = (id) =>
  http.get(`/horarios/${id}`).then(r => r.data);

export const modificar = (id, dto) =>
  http.put(`/horarios/${id}`, dto).then(r => r.data);

export const eliminar = (id) =>
  http.delete(`/horarios/${id}`);
