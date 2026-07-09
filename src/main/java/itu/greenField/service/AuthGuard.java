package itu.greenField.service;

import itu.greenField.model.Employes;
import itu.greenField.model.FRole;
import jakarta.servlet.http.HttpSession;

/**
 * Contrôle d'accès par rôle, appliqué explicitement dans chaque contrôleur
 * (pas d'interceptor / pas de Spring Security). Reprend le pattern existant
 * basé sur l'attribut de session « employe ».
 */
public final class AuthGuard {

    public static final String SESSION_KEY = "employe";

    private AuthGuard() {
    }

    /** Employé connecté (ou null si aucune session employé). */
    public static Employes current(HttpSession session) {
        Object o = session == null ? null : session.getAttribute(SESSION_KEY);
        return (o instanceof Employes) ? (Employes) o : null;
    }

    public static boolean isAdmin(HttpSession session) {
        Employes e = current(session);
        return e != null && FRole.Administrateur.equals(e.getRole());
    }

    public static boolean isCaissier(HttpSession session) {
        Employes e = current(session);
        return e != null && FRole.Caissier.equals(e.getRole());
    }

    /** Code du point de vente du caissier connecté, ou null. */
    public static String pointDeVenteCode(HttpSession session) {
        Employes e = current(session);
        if (e != null && e.getPointDeVente() != null) {
            return e.getPointDeVente().getCode();
        }
        return null;
    }

    /**
     * Levée par le garde d'un contrôleur quand l'accès est refusé.
     * Le contrôleur la capture via @ExceptionHandler pour rediriger vers le login.
     */
    public static class AccesRefuseException extends RuntimeException {
        public AccesRefuseException() {
            super("Accès refusé : rôle insuffisant.");
        }
    }
}
