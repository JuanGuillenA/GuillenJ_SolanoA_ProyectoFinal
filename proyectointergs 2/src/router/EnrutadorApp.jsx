// src/router/EnrutadorApp.jsx

import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "../autenticacion/ContextoAutenticacion";

import Layout from "../componentes/Layout";
import RutaProtegida from "../autenticacion/RutaProtegida";

import PaginaLogin               from "../paginas/PaginaLogin";
import PaginaAdminInicio         from "../paginas/PaginaAdminInicio";
import PaginaHistorialAdmin      from "../paginas/PaginaHistorialAdmin";
import PaginaReportes            from "../paginas/PaginaReportes";
import PaginaMedicos             from "../paginas/PaginaMedicos";
import PaginaUsuarios            from "../paginas/PaginaUsuarios";
import PaginaCitasAdmin          from "../paginas/PaginaCitasAdmin";

import PaginaPaciente            from "../paginas/PaginaPaciente";
import PaginaHistorialPaciente   from "../paginas/PaginaHistorialPaciente";
import PaginaSolicitarCita       from "../paginas/PaginaSolicitarCita";
import PaginaCitasPaciente       from "../paginas/PaginaCitasPaciente";
import PaginaPerfilPaciente      from "../paginas/PaginaPerfilPaciente";

/**
 * RedirSegunRol:
 *  - Mientras cargamos auth, no renderiza nada.
 *  - Luego redirige a /admin o /paciente según el rol, o /login si no hay sesión.
 */
function RedirSegunRol() {
  const { rol, cargando } = useAuth();
  if (cargando) return null;
  if (rol === "admin")    return <Navigate to="/admin" />;
  if (rol === "paciente") return <Navigate to="/paciente" />;
  return <Navigate to="/login" />;
}

export default function EnrutadorApp() {
  return (
    <BrowserRouter>
      <Routes>
        
        {/* ── Login ─────────────────────────────────────────── */}
        <Route path="/login" element={<PaginaLogin />} />

        {/* ── Rutas Admin ───────────────────────────────────── */}
        <Route
          path="/admin/*"
          element={
            <RutaProtegida rolRequerido="admin">
              <Layout tipo="admin" />
            </RutaProtegida>
          }
        >
          {/* Dashboard */}
          <Route index element={<PaginaAdminInicio />} />
          {/* Historial de citas */}
          <Route path="historial" element={<PaginaHistorialAdmin />} />
          {/* Reportes PDF/Excel */}
          <Route path="reportes"  element={<PaginaReportes />} />
          {/* Gestión de médicos, usuarios y citas */}
          <Route path="medicos"   element={<PaginaMedicos />} />
          <Route path="usuarios"  element={<PaginaUsuarios />} />
          <Route path="citas"     element={<PaginaCitasAdmin />} />
          {/* Catch-all: redirige a /admin */}
          <Route path="*" element={<Navigate to="/admin" replace />} />
        </Route>

        {/* ── Rutas Paciente ────────────────────────────────── */}
        <Route
          path="/paciente/*"
          element={
            <RutaProtegida rolRequerido="paciente">
              <Layout tipo="paciente" />
            </RutaProtegida>
          }
        >
          {/* Dashboard paciente */}
          <Route index element={<PaginaPaciente />} />
          {/* Historial propio */}
          <Route path="historial" element={<PaginaHistorialPaciente />} />
          {/* Solicitar nueva cita */}
          <Route path="solicitar-cita" element={<PaginaSolicitarCita />} />
          {/* Ver mis citas pendientes/confirmadas */}
          <Route path="mis-citas"      element={<PaginaCitasPaciente />} />
          {/* Perfil */}
          <Route path="perfil"         element={<PaginaPerfilPaciente />} />
          {/* Catch-all: redirige a /paciente */}
          <Route path="*" element={<Navigate to="/paciente" replace />} />
        </Route>

        {/* ── Root: redirige según sesión/rol ──────────────── */}
        <Route path="/" element={<RedirSegunRol />} />
        {/* Cualquier ruta desconocida */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
