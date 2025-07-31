// src/paginas/PaginaAdminInicio.jsx
import React, { useMemo } from "react";
import useMedicos from "../data/useMedicos";
import useUsuarios from "../data/useUsuarios";
import { useCitasAdmin } from "../data/useCitas";

export default function PaginaAdminInicio() {
  const { medicos } = useMedicos();
  const { usuarios } = useUsuarios();
  const { citas } = useCitasAdmin();


  const { totalPendientes, totalConfirmadas } = useMemo(() => {
    let pendientes = 0, confirmadas = 0;
    citas.forEach(c => {
      if ((c.estado||"").toLowerCase() === "pendiente") pendientes++;
      if ((c.estado||"").toLowerCase() === "confirmada") confirmadas++;
    });
    return { totalPendientes: pendientes, totalConfirmadas: confirmadas };
  }, [citas]);

  return (
    <div className="page-container">
      <div className="card" style={{ marginBottom: "1.5rem" }}>
        <h2 className="section-title">Panel de Administrador</h2>
        <p>
          ¡Bienvenido al sistema de gestión de citas! Aquí podrás ver de un
          vistazo las métricas más importantes de tu plataforma y acceder a
          las secciones de Médicos, Usuarios y Citas desde el menú lateral.
        </p>
        <p style={{ marginTop: "0.5rem", color: "#555" }}>
          Usa el menú para:
          <ul style={{ marginTop: "0.25rem", paddingLeft: "1.25rem" }}>
            <li>Registrar, editar o eliminar médicos.</li>
            <li>Gestionar horarios disponibles de cada médico.</li>
            <li>Promover usuarios a administradores.</li>
            <li>Ver, confirmar, editar o eliminar citas.</li>
          </ul>
        </p>
      </div>

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fill,minmax(220px,1fr))",
          gap: "1rem",
          marginBottom: "1.5rem",
        }}
      >
        {[{
            label: "Total Médicos",
            count: medicos.length,
            color: "--color-azul-principal"
          },{
            label: "Total Usuarios",
            count: usuarios.length,
            color: "--color-gris-700"
          },{
            label: "Citas Pendientes",
            count: totalPendientes,
            color: "--color-amarillo"
          },{
            label: "Citas Confirmadas",
            count: totalConfirmadas,
            color: "--color-verde"
          }]
          .map(({ label, count, color }) => (
            <div key={label} className="card" style={{ textAlign:"center", padding:"1rem" }}>
              <div style={{ fontSize:"2.5rem", fontWeight:"bold", color:`var(${color})` }}>
                {count}
              </div>
              <div style={{ marginTop:"0.5rem", fontWeight:"500", color:`var(${color})` }}>
                {label}
              </div>
            </div>
          ))
        }
      </div>

      <div className="card">
        <h3 className="section-title">Tips Rápidos</h3>
        <ul style={{ paddingLeft:"1.25rem", color:"#444", marginTop:"0.5rem" }}>
          <li>Para crear un médico nuevo, ve a <strong>Médicos</strong>.</li>
          <li>Asigna horarios en <strong>Médicos</strong>, eligiendo un doctor.</li>
          <li>Revisa las citas en <strong>Citas</strong> para confirmar/editar.</li>
          <li>Promociona pacientes en <strong>Usuarios</strong>.</li>
        </ul>
      </div>
    </div>
  );
}
