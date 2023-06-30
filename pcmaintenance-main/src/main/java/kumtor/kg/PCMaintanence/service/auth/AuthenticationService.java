package kumtor.kg.PCMaintanence.service.auth;

import java.security.Principal;

public interface AuthenticationService {

    String getUsername(Principal principal);
}
