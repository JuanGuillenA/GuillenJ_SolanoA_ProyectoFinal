// src/api/authService.js
import http from './http';

export async function verify(idToken) {
  const resp = await http.post('/auth/verify', { idToken });
  return resp.data; // { id, email, role }
}
