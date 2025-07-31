// src/paginas/PaginaLogin.jsx

import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../autenticacion/ContextoAutenticacion';

export default function PaginaLogin() {
  const { usuario, rol, loginGoogle, loginCorreo, registrar, cargando } =
    useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    console.log(
      '[Login] useEffect:',
      { cargando, usuario, rol }
    );
    if (cargando) return;
    if (usuario) {
      const path = rol === 'admin' ? '/admin' : '/paciente';
      console.log('[Login] navegando a', path);
      navigate(path, { replace: true });
    }
  }, [usuario, rol, cargando, navigate]);

  const [esRegistro, setEsRegistro] = useState(false);
  const [form, setForm] = useState({ nombre: '', email: '', pass: '' });
  const [err, setErr] = useState('');

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (cargando) return;
    setErr('');
    try {
      if (esRegistro) {
        await registrar(form.email, form.pass, form.nombre);
      } else {
        await loginCorreo(form.email, form.pass);
      }
    } catch (e) {
      const code = e.code || '';
      if (code.includes('email-already-in-use')) {
        setErr('Ese correo ya está registrado. Inicia sesión o usa otro.');
        setEsRegistro(false);
      } else if (code.includes('weak-password')) {
        setErr('La contraseña debe tener al menos 6 caracteres.');
      } else if (
        code.includes('wrong-password') ||
        code.includes('user-not-found')
      ) {
        setErr('Correo o contraseña incorrectos.');
      } else {
        setErr(e.message);
      }
    }
  };

  const handleGoogle = async () => {
    if (cargando) return;
    setErr('');
    try {
      await loginGoogle(esRegistro);
    } catch (e) {
      setErr(e.message);
    }
  };

  if (cargando) {
    return (
      <p style={{ textAlign: 'center', marginTop: '2rem' }}>
        Cargando sesión...
      </p>
    );
  }

  return (
    <div
      className="app-container"
      style={{ justifyContent: 'center', alignItems: 'center' }}
    >
      <div
        className="card"
        style={{ maxWidth: '400px', margin: '2rem auto', padding: '2rem' }}
      >
        <h1
          style={{
            textAlign: 'center',
            marginBottom: '1rem',
            fontSize: '1.5rem',
          }}
        >
          Agenda Médica
        </h1>

        {err && (
          <p
            style={{
              backgroundColor: '#dc2626',
              color: '#fff',
              padding: '0.75rem',
              borderRadius: '4px',
              marginBottom: '1rem',
              fontSize: '0.9rem',
              textAlign: 'center',
            }}
          >
            {err}
          </p>
        )}

        <form onSubmit={handleSubmit}>
          {esRegistro && (
            <div className="form-group">
              <label>Nombre completo</label>
              <input
                type="text"
                name="nombre"
                value={form.nombre}
                onChange={handleChange}
                required
                disabled={cargando}
              />
            </div>
          )}

          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              required
              disabled={cargando}
            />
          </div>

          <div className="form-group">
            <label>Contraseña</label>
            <input
              type="password"
              name="pass"
              value={form.pass}
              onChange={handleChange}
              required
              disabled={cargando}
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%' }}
            disabled={cargando}
          >
            {esRegistro ? 'Registrarme' : 'Iniciar sesión'}
          </button>
        </form>

        <button
          onClick={handleGoogle}
          className="btn"
          style={{
            width: '100%',
            backgroundColor: '#fff',
            color: '#374151',
            border: '1px solid var(--color-gris-300)',
            marginTop: '1rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
          disabled={cargando}
        >
          <img
            src="https://www.svgrepo.com/show/355037/google.svg"
            alt="Google"
            style={{ width: '20px', height: '20px', marginRight: '0.5rem' }}
          />
          <span>Continuar con Google</span>
        </button>

        <p
          style={{
            textAlign: 'center',
            marginTop: '1rem',
            fontSize: '0.9rem',
          }}
        >
          {esRegistro ? '¿Ya tienes cuenta?' : '¿Eres nuevo aquí?'}{' '}
          <button
            onClick={() => setEsRegistro(!esRegistro)}
            style={{
              color: 'var(--color-azul-principal)',
              background: 'none',
              border: 'none',
              cursor: 'pointer',
            }}
            disabled={cargando}
          >
            {esRegistro ? 'Inicia sesión' : 'Crea una cuenta'}
          </button>
        </p>
      </div>
    </div>
  );
}
