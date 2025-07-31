// src/paginas/PaginaPerfilPaciente.jsx
import React, { useState, useEffect } from "react";
import { useAuth } from "../autenticacion/ContextoAutenticacion";
import api from "../api/http";

export default function PaginaPerfilPaciente() {
  const { usuario } = useAuth();
  const [perfil, setPerfil] = useState({
    id: null,
    nombre: "",
    email: "",
    telefono: "",
  });
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function cargar() {
      try {
        const resp = await api.get(`/usuarios/firebase/${usuario.uid}`);
        const dto = resp.data;
        setPerfil({
          id: dto.id,
          nombre: dto.nombre || usuario.displayName || "",
          email: dto.email || usuario.email,
          telefono: dto.telefono || "",
        });
      } catch (e) {
        console.error(e);
        setError("Error cargando perfil.");
      }
    }
    if (usuario?.uid) cargar();
  }, [usuario]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setPerfil((p) => ({ ...p, [name]: value }));
  };

  const handleSave = async () => {
    if (!perfil.id) {
      setError("ID de usuario desconocido.");
      return;
    }
    setGuardando(true);
    setError(null);
    try {
      await api.put(`/usuarios/${perfil.id}`, {
        nombre: perfil.nombre,
        telefono: perfil.telefono,
      });
      alert("Perfil guardado con éxito.");
    } catch (e) {
      console.error(e);
      setError("Error al guardar.");
    } finally {
      setGuardando(false);
    }
  };

  return (
    <div className="page-container">
      <h2 className="section-title">Mi perfil</h2>
      {error && <p className="text-error">{error}</p>}

      <div className="card" style={{ maxWidth: "400px", margin: "0 auto" }}>
        <div className="form-group">
          <label>Nombre:</label>
          <input
            type="text"
            name="nombre"
            className="form-input"
            value={perfil.nombre}
            onChange={handleChange}
          />
        </div>

        <div className="form-group">
          <label>Email:</label>
          <input
            type="email"
            name="email"
            className="form-input"
            readOnly
            value={perfil.email}
          />
        </div>

        <div className="form-group">
          <label>Teléfono:</label>
          <input
            type="tel"
            name="telefono"
            className="form-input"
            placeholder="Tu número de teléfono"
            value={perfil.telefono}
            onChange={handleChange}
          />
        </div>

        <button
          className="btn btn-primary"
          onClick={handleSave}
          disabled={guardando}
        >
          {guardando ? "Guardando…" : "Guardar"}
        </button>
      </div>
    </div>
  );
}
