package org.gso.citations.configuration;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Non authentifié (Token JWT manquant ou invalide)"),
        @ApiResponse(responseCode = "403", description = "Accès refusé (Droits insuffisants)")
})
public @interface SecureOperation {
}