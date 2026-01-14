package org.gso.citations.endpoint;

import java.util.List;
import java.util.stream.Collectors;
import org.gso.citations.configuration.SecureOperation;
import org.gso.citations.dto.CitationDto;
import org.gso.citations.model.CitationModel;
import org.gso.citations.model.Status;
import org.gso.citations.repository.CitationRepository;
import org.gso.citations.service.CitationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    private final CitationRepository citationRepository;

    // POST /citation si rôle == writter
    @Operation(summary = "Créer une nouvelle citation", description = "Crée une citation avec le statut PENDING. Nécessite le rôle Writer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Citation créée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (Writer uniquement)")
    })
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("hasRole('writer')")
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
    

    // GET /pending si rôle == moderator
    @Operation(summary = "Lister les citations en attente")
    @SecureOperation
    @ApiResponse(responseCode = "200", description = "Liste récupérée")
    @GetMapping("/pending")
    @PreAuthorize("hasRole('moderator')")
    public ResponseEntity<List<CitationDto>> getPendingCitations() {
            List<CitationModel> citations = citationRepository.findAllByStatus(Status.PENDING);

            return ResponseEntity.ok(citations.stream()
                            .map(CitationModel::toDto)
                            .collect(Collectors.toList()));
    }


    // PATCH citation/{id}/validate : permet au modérateur de valider les citations à publier
    @Operation(summary = "Valider une citation", description = "Passe le statut d'une citation à VALIDATED et enregistre l'ID du modérateur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Citation validée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (Moderator uniquement)"),
            @ApiResponse(responseCode = "404", description = "Citation introuvable")
    })
    @PatchMapping("/{id}/validate")
    @PreAuthorize("hasRole('moderator')")
    public ResponseEntity<CitationDto> validateCitation(@PathVariable String id) {
        return citationRepository.findById(id)
                .map(citationModel -> {
                    citationModel.setStatus(Status.VALIDATED);
                    citationModel.setModeratorId(SecurityContextHolder.getContext().getAuthentication().getName());
                    
                    CitationModel updatedCitation = citationRepository.save(citationModel);
                    return ResponseEntity.ok(updatedCitation.toDto());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /citations/{id} si current user == citation owner
    // Permet à un utilisateur de supprimer une de ces citations
    @Operation(summary = "Supprimer une citation")
    @SecureOperation
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Suppression réussie"),
        @ApiResponse(responseCode = "404", description = "Citation non trouvée")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('writer') or hasRole('moderator')")
    public ResponseEntity<Void> deleteCitation(@PathVariable String id) {
        return citationRepository.findById(id).map(citation -> {
            
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = authentication.getName();
            
            boolean isModerator = authentication.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ROLE_moderator"));

            if (citation.getWriterId().equals(currentUserId) || isModerator) {
                citationRepository.deleteById(id);
                return ResponseEntity.noContent().<Void>build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    // GET /my-citations : permet à un user de lister ses citations (notamment utile pour supprimer)
    // On part du principe qu'il y aurait un bouton supprimer dans le front pour chaque citation
    // car un user n'est pas censé connaître les ID.
    @Operation(summary = "Lister mes citations", description = "Retourne les citations créées par l'utilisateur connecté (Writer).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (Writer uniquement)")
    })
    @GetMapping("/my-citations")
    @PreAuthorize("hasRole('writer')")
    public ResponseEntity<List<CitationDto>> getMyCitations() {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CitationModel> citations = citationRepository.findAllByWriterId(currentUserId);
        
        return ResponseEntity.ok(citations.stream()
                .map(CitationModel::toDto)
                .collect(Collectors.toList()));
    }

    // GET /citations : avec statut validé par défuat - pour l'affichage de citations avec image de fond
    @Operation(summary = "Lister les citations validées", description = "Retourne la liste de toutes les citations publiques (Status = VALIDATED).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping
    public ResponseEntity<List<CitationDto>> getAllValidatedCitations() {
            List<CitationModel> citations = citationRepository.findAllByStatus(Status.VALIDATED);

            return ResponseEntity.ok(citations.stream()
                            .map(CitationModel::toDto)
                            .collect(Collectors.toList()));
    }
}
