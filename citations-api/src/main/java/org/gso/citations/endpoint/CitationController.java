package org.gso.citations.endpoint;

import org.gso.citations.dto.CitationDto;
import org.gso.citations.service.CitationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping(
        value = CitationController.PATH,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Citations API", description = "Endpoints pour la gestion des citations")
public class CitationController {

    public static final String PATH = "/api/v1/citations";
    private final CitationService citationService;

    // POST /citation si rôle == writter
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<CitationDto> postMethodName(@RequestBody CitationDto citationDto) {
        CitationDto createdCitation = citationService.createCitation(citationDto.toModel()).toDto();
        return ResponseEntity
                .created(
                        ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path(createdCitation.id())
                                .build()
                                .toUri()
                ).body(createdCitation);
    }
    

    // GET /citations?status=PENDING si rôle == moderator

    // PATCH citation/{id}/validate : permet au modérateur de valider les citations à publier

    // DELETE /citations/{id} si current user == citation owner
    // Permet à un utilisateur de supprimer une de ces citations

    // GET /citations/me : permet à un user de lister ses citations (notamment utile pour supprimer)
    // On part du principe qu'il y aurait un bouton supprimer dans le front pour chaque citation
    // car un user n'est pas censé connaître les ID.

    // GET /citations : avec statut validé par défuat - pour l'affichage de citations avec image de fond
}
