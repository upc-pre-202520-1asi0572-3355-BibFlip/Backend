package pe.upc.edu.bibflipbackend.booking.application.internal.outboundedservices.acl;

import pe.upc.edu.bibflipbackend.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

@Service
public class ExternalUserService {
    private final IamContextFacade iamContextFacade;

    public ExternalUserService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public boolean existUserById(Long userId) {
        return iamContextFacade.existsUser(userId);
    }
}
