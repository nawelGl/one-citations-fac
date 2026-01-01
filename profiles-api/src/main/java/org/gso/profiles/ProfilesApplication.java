package org.gso.profiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@SecurityScheme(
    name = "Keycloak",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(
        authorizationCode = @OAuthFlow(
            authorizationUrl = "http://localhost:8080/realms/one-citations/protocol/openid-connect/auth",
            tokenUrl = "http://localhost:8080/realms/one-citations/protocol/openid-connect/token",
            scopes = {
                @OAuthScope(name = "profile", description = "access profile"),
                @OAuthScope(name = "email", description = "access email")
            }
        )
    )
)
public class ProfilesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfilesApplication.class, args);
	}

}
