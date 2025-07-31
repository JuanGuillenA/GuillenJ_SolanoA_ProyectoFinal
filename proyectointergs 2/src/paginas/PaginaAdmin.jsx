// src/paginas/PaginaAdmin.jsx
import React, { useState } from 'react';
import { useAuth } from '../autenticacion/ContextoAutenticacion';
import useMedicos from '../data/useMedicos';
import useHorarios from '../data/useHorarios';
import CitasAdmin from '../componentes/CitasAdmin';
import useUsuarios from '../data/useUsuarios';
import DoctorForm from '../componentes/DoctorForm';
import DoctorList from '../componentes/DoctorList';
import HorarioForm from '../componentes/HorarioForm';
import http from '../api/http';

export default function PaginaAdmin() {
  const { usuario } = useAuth();
  const { medicos, crear: crearMedico } = useMedicos();
  const [medicoSel, setMedicoSel] = useState('');
  const { horarios, crear: crearHorario } = useHorarios(medicoSel);
  const usuarios = useUsuarios();
  const [nuevoAdmin, setNuevoAdmin] = useState('');

  const promover = async (e) => {
    e.preventDefault();
    const usr = usuarios.find(u => u.email === nuevoAdmin);
    if (!usr) {
      alert('No existe usuario con ese correo');
      return;
    }
    try {
      await http.put(`/usuarios/promover/${usr.id}`, { role: 'admin' });
      alert('Usuario promovido a administrador');
      setNuevoAdmin('');
    } catch (err) {
      console.error('Error promoviendo:', err);
      alert('No se pudo promover al usuario');
    }
  };

  return (
    <div className="page-container">
      <h2 className="section-title">Panel de Administrador</h2>

      <div className="card">
        <h3 className="section-title">1. Registro de médicos</h3>
        <DoctorForm onSave={crearMedico} />
        <DoctorList medicos={medicos} />
      </div>

      <div className="card">
        <h3 className="section-title">2. Horarios por médico</h3>
        <div className="form-group">
          <label>Selecciona médico</label>
          <select
            value={medicoSel}
            onChange={e => setMedicoSel(e.target.value)}
            className="form-input"
          >
            <option value="">-- Selecciona médico --</option>
            {medicos.map(m => (
              <option key={m.id} value={m.id}>
                {m.nombre}
              </option>
            ))}
          </select>
        </div>
        {medicoSel && (
          <>
            <HorarioForm onSave={crearHorario} />
            <ul className="list-disc pl-5 mt-2">
              {horarios.map(h => (
                <li key={h.id}>
                  {h.dias
                    .map(d =>
                      ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'][d]
                    )
                    .join(', ')}
                  : {h.slots.join(', ')}
                </li>
              ))}
            </ul>
          </>
        )}
      </div>

      <div className="card">
        <h3 className="section-title">3. Citas</h3>
        <CitasAdmin />
      </div>

      <div className="card">
        <h3 className="section-title">4. Promover usuario a admin</h3>
        <form onSubmit={promover} className="form-actions">
          <input
            type="email"
            placeholder="correo@ejemplo.com"
            className="form-input flex-1"
            value={nuevoAdmin}
            onChange={e => setNuevoAdmin(e.target.value)}
            required
          />
          <button type="submit" className="btn btn-primary">
            Promover
          </button>
        </form>
      </div>
    </div>
);
}
