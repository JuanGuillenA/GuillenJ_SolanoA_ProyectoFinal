import http from './http';

export const listar = () =>
  http.get('/especialidades').then(r => r.data);

export const crear = (dto) =>
  http.post('/especialidades', dto).then(r => r.data);

export const ver = (id) =>
  http.get(`/especialidades/${id}`).then(r => r.data);

export const modificar = (id, dto) =>
  http.put(`/especialidades/${id}`, dto).then(r => r.data);

export const eliminar = (id) =>
  http.delete(`/especialidades/${id}`);
