// src/api/firebase.js
import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider } from "firebase/auth";

const firebaseConfig = {
  apiKey: "AIzaSyBRyS2emCxYlgLGagDUevU2UCqsYNbdH7o",
  authDomain: "proyectoguillensolano.firebaseapp.com",
  projectId: "proyectoguillensolano",
  storageBucket: "proyectoguillensolano.firebasestorage.app",
  messagingSenderId: "668237316137",
  appId: "1:668237316137:web:57865f80a2bffe3b84dc38"
};

const app = initializeApp(firebaseConfig);

// Sólo exportamos lo esencial para auth
export const auth     = getAuth(app);
export const provider = new GoogleAuthProvider();
