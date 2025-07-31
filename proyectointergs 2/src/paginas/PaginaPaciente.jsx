// src/paginas/PaginaPaciente.jsx

import React from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../autenticacion/ContextoAutenticacion";

export default function PaginaPaciente() {
  const { usuario } = useAuth();
  const navigate    = useNavigate();

  return (
    <div className="page-container">
      {/* Bienvenida */}
      <div className="card">
        <h2 className="section-title">
          Bienvenido, {usuario.displayName || "Paciente"}
        </h2>
        <p>
          En tu portal de paciente puedes gestionar tus citas, consultar tu historial y actualizar tu perfil.
        </p>
      </div>

      {/* Acciones rápidas (en lugar de estadísticas) */}
      <div className="card" style={{ textAlign: "center", padding: "2rem" }}>
        <h3 className="section-title">Acciones Rápidas</h3>
        <div className="actions-container" style={{ display: "flex", gap: "1rem", justifyContent: "center", marginTop: "1rem" }}>
          <button
            className="btn btn-primary"
            onClick={() => navigate("/paciente/solicitar-cita")}
          >
            Solicitar nueva cita
          </button>
          <button
            className="btn btn-success"
            onClick={() => navigate("/paciente/mis-citas")}
          >
            Ver mis citas
          </button>
          <button
            className="btn btn-info"
            onClick={() => navigate("/paciente/historial")}
          >
            Ver historial
          </button>
          <button
            className="btn btn-secondary"
            onClick={() => navigate("/paciente/perfil")}
          >
            Ver/Editar mi perfil
          </button>
        </div>
      </div>
    </div>
  );
}
