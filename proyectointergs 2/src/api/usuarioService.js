import http from './http';

export const listar = (rol) =>
  http.get('/usuarios', { params: { rol } }).then(r => r.data);

export const crear = (dto) =>
  http.post('/usuarios', dto).then(r => r.data);

export const ver = (id) =>
  http.get(`/usuarios/${id}`).then(r => r.data);

export const verPorFirebaseUid = (uid) =>
  http.get(`/usuarios/firebase/${uid}`).then(r => r.data);

export const modificar = (id, dto) =>
  http.put(`/usuarios/${id}`, dto).then(r => r.data);

export const eliminar = (id) =>
  http.delete(`/usuarios/${id}`);
