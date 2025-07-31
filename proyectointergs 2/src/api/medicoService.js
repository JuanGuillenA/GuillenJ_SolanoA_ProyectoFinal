import http from './http';

export const listar = ({ especialidadId, buscar } = {}) =>
  http.get('/medicos', { params: { especialidadId, buscar } })
      .then(r => r.data);

export const crear = (dto) =>
  http.post('/medicos', dto).then(r => r.data);

export const ver = (id) =>
  http.get(`/medicos/${id}`).then(r => r.data);

export const modificar = (id, dto) =>
  http.put(`/medicos/${id}`, dto).then(r => r.data);

export const eliminar = (id) =>
  http.delete(`/medicos/${id}`);
