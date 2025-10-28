package pe.upc.edu.bibflipbackend.branching.apllication.internal.outboundedservice.acl;

import pe.upc.edu.bibflipbackend.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

@Service
public class ExternalIamService {
    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    /**
     * Verifica si un usuario existe.
     * @param userId El ID del usuario.
     * @return true si el usuario existe, false en caso contrario.
     */
    public boolean existsUserById(Long userId) {
        return !iamContextFacade.existsUser(userId);
    }

    /**
     * Obtiene el nombre de usuario por su ID.
     * @param userId El ID del usuario.
     * @return El nombre de usuario o cadena vacía si no existe.
     */
    public String getUsernameById(Long userId) {
        return iamContextFacade.fetchUsernameByUserId(userId);
    }

    /**
     * Verifica si un usuario tiene un rol específico.
     * @param userId El ID del usuario.
     * @param roleName El nombre del rol a verificar (sin el prefijo ROLE_).
     * @return true si el usuario tiene el rol, false en caso contrario.
     */
    public boolean hasRole(Long userId, String roleName) {
        String roles = iamContextFacade.getUserRolesByUserId(userId);
        return roles.contains("ROLE_" + roleName);
    }

    /**
     * Verifica si un usuario tiene el rol SUPERVISOR.
     * @param userId El ID del usuario.
     * @return true si el usuario tiene el rol SUPERVISOR, false en caso contrario.
     */
    public boolean isSupervisor(Long userId) {
        return hasRole(userId, "SUPERVISOR");
    }
}