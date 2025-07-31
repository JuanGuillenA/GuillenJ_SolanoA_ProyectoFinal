// src/autenticacion/ContextoAutenticacion.jsx
// src/autenticacion/ContextoAutenticacion.jsx
import React, { createContext, useContext, useEffect, useState } from 'react';
import {
  GoogleAuthProvider,
  signInWithPopup,
  signInWithRedirect,
  getAdditionalUserInfo,
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut,
  onAuthStateChanged,
  updateProfile,
} from 'firebase/auth';

import { auth } from '../api/firebase';
import { verify } from '../api/authService';

const Ctx = createContext();
const googleProvider = new GoogleAuthProvider();
googleProvider.setCustomParameters({ prompt: 'select_account' });

export function ProveedorAutenticacion({ children }) {
  const [usuario, setUsuario] = useState(null);
  const [rol, setRol]         = useState(null);
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (cred) => {
      if (!cred) {
        setUsuario(null);
        setRol(null);
        setCargando(false);
        return;
      }
      try {
        const idToken = await cred.getIdToken(true);
        // verify te devuelve el perfil de tu BD:
        const { id, email, role } = await verify(idToken);
        const rolNorm = role.toLowerCase();

        // ← CAMBIO: uid sigue siendo el firebase-uid,
        //             guardamos también id=ID_NUMÉRICO_BD
        setUsuario({
          uid: cred.uid,
          displayName: cred.displayName,
          email,
          id,       // ← aquí el ID de la tabla Paciente
        });
        setRol(rolNorm);
      } catch (err) {
        console.error('[Auth] Error verificando token:', err);
        await signOut(auth);
        setUsuario(null);
        setRol(null);
      }
      setCargando(false);
    });
    return () => unsubscribe();
  }, []);

  const loginGoogle = async (intentoRegistro = false) => {
    try {
      const cred = await signInWithPopup(auth, googleProvider);
      const info = getAdditionalUserInfo(cred);
      if (intentoRegistro && !info.isNewUser) {
        await signOut(auth);
        throw new Error('Ese correo ya existe. Por favor inicia sesión.');
      }
      return cred;
    } catch (err) {
      if (err.code === 'auth/popup-blocked') {
        const cred = await signInWithRedirect(auth, googleProvider);
        const info = getAdditionalUserInfo(cred);
        if (intentoRegistro && !info.isNewUser) {
          await signOut(auth);
          throw new Error('Ese correo ya existe. Por favor inicia sesión.');
        }
        return cred;
      }
      throw err;
    }
  };

  const loginCorreo = (email, pass) =>
    signInWithEmailAndPassword(auth, email, pass);

  const registrar = async (email, pass, nombre) => {
    const { user: newUser } = await createUserWithEmailAndPassword(
      auth,
      email,
      pass
    );
    if (nombre) {
      await updateProfile(newUser, { displayName: nombre });
    }
    return newUser;
  };

  const logout = () => signOut(auth);

  return (
    <Ctx.Provider
      value={{
        usuario,
        rol,
        cargando,
        loginGoogle,
        loginCorreo,
        registrar,
        logout,
      }}
    >
      {children}
    </Ctx.Provider>
  );
}

export const useAuth = () => useContext(Ctx);
