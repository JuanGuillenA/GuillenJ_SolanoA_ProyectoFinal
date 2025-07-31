// src/componentes/Layout.jsx
import React from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../autenticacion/ContextoAutenticacion";

export default function Layout({ tipo }) {
  const { usuario, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate("/login", { replace: true });
  };

  const menuAdmin = [
    { to: "/admin",             label: "Inicio"           },
    { to: "/admin/medicos",     label: "Médicos"         },
    { to: "/admin/usuarios",    label: "Usuarios"        },
    { to: "/admin/citas",       label: "Citas"           },
    { to: "/admin/historial",   label: "Historial Citas" },
    { to: "/admin/reportes",    label: "Reportes"        },
  ];

  const menuPaciente = [
    { to: "/paciente",               label: "Inicio"           },
    { to: "/paciente/solicitar-cita", label: "Solicitar Cita" },
    { to: "/paciente/mis-citas",     label: "Mis Citas"        },
    { to: "/paciente/historial",     label: "Historial"        },
    { to: "/paciente/perfil",        label: "Mi Perfil"        },
  ];

  const menu = tipo === "admin" ? menuAdmin : menuPaciente;

  return (
    <div className="app-container">
      <aside className="sidebar">
        <div className="sidebar-header">Consultorio</div>
        <nav>
          {menu.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end
              className={({ isActive }) =>
                isActive ? "sidebar-link active" : "sidebar-link"
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>

      <div className="main-wrapper">
        <header className="header">
          <h1>
            {usuario
              ? `Bienvenido, ${usuario.displayName}`
              : "Consultorio"}
          </h1>
          <button
            className="btn btn-danger btn-logout"
            onClick={handleLogout}
          >
            Salir
          </button>
        </header>

        <main className="main-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
