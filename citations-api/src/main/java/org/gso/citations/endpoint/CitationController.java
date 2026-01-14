package org.gso.citations.endpoint;

import java.util.List;
import java.util.stream.Collectors;

import org.gso.citations.dto.CitationDto;
import org.gso.citations.dto.PageDto;
import org.gso.citations.model.CitationModel;
import org.gso.citations.model.Status;
import org.gso.citations.repository.CitationRepository;
import org.gso.citations.service.CitationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    @GetMapping("/pending")
    @PreAuthorize("hasRole('moderator')")
    public ResponseEntity<List<CitationDto>> getPendingCitations() {
            List<CitationModel> citations = citationRepository.findAllByStatus(Status.PENDING);

            return ResponseEntity.ok(citations.stream()
                            .map(CitationModel::toDto)
                            .collect(Collectors.toList()));
    }


    // PATCH citation/{id}/validate : permet au modérateur de valider les citations à publier
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
    @GetMapping
    public ResponseEntity<List<CitationDto>> getAllValidatedCitations() {
            List<CitationModel> citations = citationRepository.findAllByStatus(Status.VALIDATED);

            return ResponseEntity.ok(citations.stream()
                            .map(CitationModel::toDto)
                            .collect(Collectors.toList()));
    }
}
