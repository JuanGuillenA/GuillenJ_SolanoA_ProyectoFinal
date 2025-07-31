// src/App.js
import React from 'react'
import { ProveedorAutenticacion } from './autenticacion/ContextoAutenticacion'
import EnrutadorApp from './router/EnrutadorApp'

export default function App() {
  return (
    // Envuelves toda la app en tu proveedor de auth
    <ProveedorAutenticacion>
    <EnrutadorApp />
    </ProveedorAutenticacion>
  )
}
