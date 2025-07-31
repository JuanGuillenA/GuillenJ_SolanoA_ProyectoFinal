// src/paginas/PaginaUsuarios.jsx
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import * as userSvc from "../api/usuarioService";
import * as citaSvc from "../api/citasService";

export default function PaginaUsuarios() {
  const navigate = useNavigate();

  const [usuarios, setUsuarios]     = useState([]);
  const [roleFilter, setRoleFilter] = useState("all");
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    userSvc.listar().then(setUsuarios).catch(console.error);
  }, []);

  const toggleRole = async (u) => {
    const rawRole   = u.role.toLowerCase() === "admin" ? "paciente" : "admin";
    const nuevoRole = rawRole.toUpperCase();
    try {
      const updated = await userSvc.modificar(u.id, { role: nuevoRole });
      setUsuarios((prev) =>
        prev.map((x) => (x.id === u.id ? updated : x))
      );
    } catch {
      alert("No se pudo cambiar el rol. Intenta de nuevo.");
    }
  };

  const eliminarUsuario = async (u) => {
    if (
      !window.confirm(
        `¿Eliminar usuario '${u.displayName || u.email}' y sus citas?`
      )
    )
      return;

    try {
      const citas = await citaSvc.historial({ pacienteId: u.id });
      await Promise.all(citas.map((c) => citaSvc.eliminar(c.id)));
      await userSvc.eliminar(u.id);
      setUsuarios((prev) => prev.filter((x) => x.id !== u.id));
      alert("Usuario y sus citas eliminados correctamente.");
    } catch {
      alert("Error al eliminar usuario. Revisa la consola.");
    }
  };

  const filtered = usuarios.filter((u) => {
    if (roleFilter !== "all" && u.role.toLowerCase() !== roleFilter)
      return false;
    if (searchTerm.trim()) {
      const term = searchTerm.toLowerCase();
      return (
        (u.displayName || "").toLowerCase().includes(term) ||
        (u.email || "").toLowerCase().includes(term)
      );
    }
    return true;
  });

  return (
    <div className="page-container" style={{ maxWidth: "1400px", margin: "0 auto", padding: "1rem" }}>
      <button className="btn btn-secondary" onClick={() => navigate("/admin")}>
        &larr; Volver
      </button>

      <h2 className="section-title">Usuarios</h2>

      <div className="filter-container" style={{ margin: "1rem 0" }}>
        <div className="filter-group">
          <label>Filtrar por rol</label>
          <select
            className="filter-select"
            value={roleFilter}
            onChange={(e) => setRoleFilter(e.target.value)}
          >
            <option value="all">Todos</option>
            <option value="paciente">Paciente</option>
            <option value="admin">Admin</option>
          </select>
        </div>
        <div className="filter-group">
          <label>Buscar</label>
          <input
            className="filter-input"
            type="text"
            placeholder="Nombre o correo..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      <div className="table-container">
        <table
          className="table"
          style={{
            minWidth: "1000px",
            tableLayout: "auto"
          }}
        >
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Email</th>
              <th>Rol</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((u) => (
              <tr key={u.id}>
                <td>{u.displayName || "—"}</td>
                <td>{u.email}</td>
                <td style={{ textTransform: "capitalize" }}>
                  {u.role.toLowerCase()}
                </td>
                <td style={{ display: "flex", gap: "0.5rem" }}>
                  <button
                    className="btn btn-primary"
                    onClick={() => toggleRole(u)}
                  >
                    Cambiar a{" "}
                    {u.role.toLowerCase() === "admin" ? "paciente" : "admin"}
                  </button>
                  <button
                    className="btn btn-danger"
                    onClick={() => eliminarUsuario(u)}
                  >
                    Eliminar
                  </button>
                </td>
              </tr>
            ))}
            {filtered.length === 0 && (
              <tr>
                <td colSpan={4} style={{ textAlign: "center", padding: "1rem" }}>
                  No se encontraron usuarios.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
