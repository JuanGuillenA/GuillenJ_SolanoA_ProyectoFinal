// src/paginas/PaginaMedicos.jsx

import React, { useState, useEffect } from "react";
import useMedicos from "../data/useMedicos";
import useHorarios from "../data/useHorarios";
import useEspecialidades from "../data/useEspecialidades";
import * as espSvc from "../api/especialidadService";
import DoctorForm from "../componentes/DoctorForm";
import HorarioForm from "../componentes/HorarioForm";

export default function PaginaMedicos() {
  // 1) Especialidades
  const { especialidades = [] } = useEspecialidades();

  // 2) Médicos (filtrado por especialidad)
  const [espFiltrar, setEspFiltrar] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const {
    medicos,
    crearMedico,
    editarMedico,
    eliminarMedico,
    listarMedicos,
  } = useMedicos({ especialidadId: espFiltrar || null });
  const [listaMedicos, setListaMedicos] = useState([]);
  useEffect(() => setListaMedicos(medicos), [medicos]);

  // 3) Edición inline
  const [editingId, setEditingId] = useState(null);
  const [editarNombre, setEditarNombre] = useState("");
  const [editarTelefono, setEditarTelefono] = useState("");
  const [editError, setEditError] = useState("");

  const nombreEspecialidad = (id) => {
    const e = especialidades.find((x) => String(x.id) === String(id));
    return e ? e.nombre : "—";
  };

  // Guardar nuevo médico
  const salvarMedico = async (data) => {
    let espObj;
    try {
      const lista = await espSvc.listar();
      espObj =
        lista.find((e) => e.nombre === data.especialidad) ||
        (await espSvc.crear({ nombre: data.especialidad }));
    } catch {
      alert("Error validando/creando especialidad.");
      return;
    }
    try {
      await crearMedico({
        nombre: data.nombre,
        apellido: data.apellido,
        email: data.email,
        especialidadId: espObj.id,
        telefono: data.telefono || null,
      });
      const fresh = await listarMedicos({
        especialidadId: espFiltrar || null,
      });
      setListaMedicos(fresh);
      alert("Médico guardado.");
    } catch {
      alert("No se pudo guardar el médico.");
    }
  };

  // Guardar edición inline
  const handleGuardarEdicion = async (id) => {
    if (!editarNombre.trim()) {
      setEditError("Nombre vacío");
      return;
    }
    try {
      await editarMedico(id, {
        nombre: editarNombre.trim(),
        telefono: editarTelefono.trim() || null,
      });
      const fresh = await listarMedicos({
        especialidadId: espFiltrar || null,
      });
      setListaMedicos(fresh);
      setEditingId(null);
      setEditError("");
    } catch {
      setEditError("No se pudo guardar cambios.");
    }
  };

  // Eliminar médico
  const handleEliminarMedico = async (id) => {
    if (!window.confirm("¿Eliminar este médico?")) return;
    try {
      await eliminarMedico(id);
      const fresh = await listarMedicos({
        especialidadId: espFiltrar || null,
      });
      setListaMedicos(fresh);
    } catch {
      alert("No se pudo eliminar el médico.");
    }
  };

  // Filtrado por nombre
  const medicosFiltrados = listaMedicos.filter((m) =>
    m.nombre.toLowerCase().includes(searchTerm.trim().toLowerCase())
  );

  // 4) Asignar Horarios
  const [espHorario, setEspHorario] = useState("");
  const [medicoSel, setMedicoSel] = useState("");
  // obtenemos array de recurrencias existentes:
  const { horarios = [], crearRecurrencia } = useHorarios(
    medicoSel ? Number(medicoSel) : null
  );

  // para convertir índices a nombres si los usas dentro de HorarioForm:
  const DIAS_NOMBRE = [
    "Domingo",
    "Lunes",
    "Martes",
    "Miércoles",
    "Jueves",
    "Viernes",
    "Sábado",
  ];

  return (
    <div className="page-container">
      {/* Alta Médico */}
      <div className="card" style={{ marginBottom: "2rem" }}>
        <h2 className="section-title">Agregar nuevo médico</h2>
        <DoctorForm onSave={salvarMedico} />
      </div>

      {/* Listado Médicos */}
      <div className="card" style={{ marginBottom: "2rem" }}>
        <h2 className="section-title">Listado de Médicos</h2>
        <div className="filter-container">
          <div className="filter-group">
            <label>Especialidad</label>
            <select
              className="filter-select"
              value={espFiltrar}
              onChange={(e) => {
                setEspFiltrar(e.target.value);
                setSearchTerm("");
              }}
            >
              <option value="">-- Todas las especialidades --</option>
              {especialidades.map((esp) => (
                <option key={esp.id} value={esp.id}>
                  {esp.nombre}
                </option>
              ))}
            </select>
          </div>
          <div className="filter-group">
            <label>Buscar</label>
            <input
              className="filter-input"
              placeholder="Buscar nombre..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>

        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Especialidad</th>
                <th>Teléfono</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {medicosFiltrados.map((m) => (
                <tr key={m.id}>
                  {editingId === m.id ? (
                    <>
                      <td>
                        <input
                          className="form-input"
                          value={editarNombre}
                          onChange={(e) => setEditarNombre(e.target.value)}
                        />
                      </td>
                      <td>{nombreEspecialidad(m.especialidadId)}</td>
                      <td>
                        <input
                          className="form-input"
                          value={editarTelefono}
                          onChange={(e) => setEditarTelefono(e.target.value)}
                          placeholder="Teléfono"
                        />
                      </td>
                      <td className="item-actions">
                        <button
                          className="btn btn-primary"
                          onClick={() => handleGuardarEdicion(m.id)}
                        >
                          Guardar
                        </button>
                        <button
                          className="btn btn-secondary"
                          onClick={() => setEditingId(null)}
                        >
                          Cancelar
                        </button>
                        {editError && <p className="text-error">{editError}</p>}
                      </td>
                    </>
                  ) : (
                    <>
                      <td>
                        {m.nombre} {m.apellido}
                      </td>
                      <td>{nombreEspecialidad(m.especialidadId)}</td>
                      <td>{m.telefono || "—"}</td>
                      <td className="item-actions">
                        <button
                          className="btn btn-secondary"
                          onClick={() => {
                            setEditingId(m.id);
                            setEditarNombre(m.nombre);
                            setEditarTelefono(m.telefono || "");
                            setEditError("");
                          }}
                        >
                          Editar
                        </button>
                        <button
                          className="btn btn-danger"
                          onClick={() => handleEliminarMedico(m.id)}
                        >
                          Eliminar
                        </button>
                      </td>
                    </>
                  )}
                </tr>
              ))}
              {medicosFiltrados.length === 0 && (
                <tr>
                  <td colSpan={4} className="text-center">
                    No hay médicos.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Asignar Horario por Médico */}
      <div className="card">
        <h2 className="section-title">Asignar Horario por Médico</h2>

        {/* Paso 1: selecciona especialidad y médico */}
        <div className="filter-container" style={{ marginBottom: "1rem" }}>
          <div className="filter-group">
            <label>Especialidad</label>
            <select
              className="filter-select"
              value={espHorario}
              onChange={(e) => {
                setEspHorario(e.target.value);
                setMedicoSel("");
              }}
            >
              <option value="">-- Selecciona especialidad --</option>
              {especialidades.map((esp) => (
                <option key={esp.id} value={esp.id}>
                  {esp.nombre}
                </option>
              ))}
            </select>
          </div>
          <div className="filter-group">
            <label>Médico</label>
            <select
              className="filter-select"
              value={medicoSel}
              onChange={(e) => setMedicoSel(e.target.value)}
              disabled={!espHorario}
            >
              <option value="">-- Selecciona médico --</option>
              {listaMedicos
                .filter((m) => String(m.especialidadId) === espHorario)
                .map((m) => (
                  <option key={m.id} value={m.id}>
                    {m.nombre} {m.apellido}
                  </option>
                ))}
            </select>
          </div>
        </div>

        {/* Paso 2: formulario para crear la recurrencia */}
        {medicoSel && (
          <HorarioForm
            onSave={async (payload) => {
              // validación de solapamientos: para cada día elegido
              const conflict = payload.diasSemana.some((diaIdx) =>
                horarios.some((r) =>
                  // si el día coincide y los horarios se solapan
                  r.diasSemana.includes(diaIdx) &&
                  !(payload.horaFin <= r.horaInicio ||
                    payload.horaInicio >= r.horaFin)
                )
              );
              if (conflict) {
                alert(
                  "El rango de horas seleccionado se solapa con un horario existente en ese día."
                );
                return;
              }
              // si no hay conflicto, convertimos los días a texto
              const diasSemanaTexto = payload.diasSemana.map(
                (i) => DIAS_NOMBRE[i]
              );
              await crearRecurrencia({
                medicoId: Number(medicoSel),
                fechaInicio: payload.fechaInicio,
                fechaFin: payload.fechaFin,
                diasSemana: diasSemanaTexto,
                horaInicio: payload.horaInicio,
                horaFin: payload.horaFin,
              });
            }}
          />
        )}
      </div>
    </div>
  );
}
