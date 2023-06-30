package kumtor.kg.PCMaintanence.service.auth;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public String getUsername(Principal principal) {
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) principal;
        return token.getAccount().getKeycloakSecurityContext()
                .getToken().getEmail();
    }
}
