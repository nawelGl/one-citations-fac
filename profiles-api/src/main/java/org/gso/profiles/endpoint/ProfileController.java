package org.gso.profiles.endpoint;

import java.net.URI;
import java.util.List;
import com.github.rutledgepaulv.qbuilders.builders.GeneralQueryBuilder;
import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import com.github.rutledgepaulv.qbuilders.visitors.MongoVisitor;
import com.github.rutledgepaulv.rqe.pipes.QueryConversionPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gso.profiles.dto.PageDto;
import org.gso.profiles.dto.ProfileDto;
import org.gso.profiles.model.ProfileModel;
import org.gso.profiles.service.ProfileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping(
        value = ProfileController.PATH,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Profile API", description = "Endpoints pour la gestion des profils utilisateurs")
public class ProfileController {

    public static final String PATH = "/api/v1/profiles";
    //private static final int MAX_PAGE_SIZE = 200;

    private final ProfileService profileService;
    //private final QueryConversionPipeline pipeline = QueryConversionPipeline.defaultPipeline();


    @Operation(summary = "Créer un profil manuellement", description = "Création d'un profil (souvent géré auto via /current).")
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileDto> createProfile(@RequestBody ProfileDto profileDto) {
        ProfileDto createdProdile = profileService.createProfile(profileDto.toModel()).toDto();
        return ResponseEntity
                .created(
                        ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path(createdProdile.id())
                                .build()
                                .toUri()
                ).body(createdProdile);
    }

    @Operation(summary = "Obtenir un profil par ID", description = "Récupère un profil via son ID technique MongoDB.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil trouvé"),
            @ApiResponse(responseCode = "404", description = "Profil introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable("id") @NonNull String profileId) {
        return ResponseEntity.ok(profileService.getProfile(profileId).toDto());
    }

    @Operation(summary = "Mettre à jour mon profil", description = "Met à jour les informations (Nom, Prénom, Age) de l'utilisateur connecté.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil mis à jour"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Profil introuvable")
    })
    @PutMapping("/current")
    public ResponseEntity<ProfileDto> updateProfile(@AuthenticationPrincipal Jwt jwt, 
            @RequestBody @NonNull ProfileDto profileDto) {
            String email = jwt.getClaimAsString("email");
            ProfileModel profile = profileService.findByMail(email);

            if (profile == null) {
                return ResponseEntity.notFound().build();
            }

            if (profileDto.firstName() != null) {
                profile.setFirstName(profileDto.firstName());
            }
            if (profileDto.lastName() != null) {
                profile.setLastName(profileDto.lastName());
            }
            if (profileDto.age() >= 13) {
                profile.setAge(profileDto.age());
            }

            ProfileModel savedProfile = profileService.save(profile);

        return ResponseEntity.ok(savedProfile.toDto());
    }

    // @GetMapping
    // public ResponseEntity<PageDto<ProfileDto>> searchProfile(@RequestParam(required = false) String query,
    //                                                          @PageableDefault(size = 20) Pageable pageable) {
    //     Pageable checkedPageable  = checkPageSize(pageable);
    //     Criteria criteria = convertQuery(query);
    //     Page<ProfileModel> results = profileService.searchProfiles(criteria, checkedPageable);
    //     PageDto<ProfileDto> pageResults = toPageDto(results);
    //     return ResponseEntity
    //             .status(HttpStatus.OK)
    //             .body(pageResults);
    // }

    // @GetMapping(params = "mail")
    // public ResponseEntity<PageDto<ProfileDto>> searchByMail(@RequestParam String mail,
    //                                                          @PageableDefault(size = 20) Pageable pageable) {
    //     Page<ProfileModel> results = profileService.searchByMail(mail, pageable);
    //     PageDto<ProfileDto> pageResults = toPageDto(results);
    //     return ResponseEntity
    //             .status(HttpStatus.OK)
    //             .body(pageResults);
    // }

    @Operation(summary = "Rechercher des profils", description = "Recherche par email ou liste tous les profils (paginé).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping
    public ResponseEntity<PageDto<ProfileDto>> searchProfiles(
            @RequestParam(required = false) String mail,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ProfileModel> results;

        if (mail != null && !mail.isBlank()) {
            results = profileService.searchByMail(mail, pageable);
        } else {
            results = profileService.findAll(pageable);
        }
        PageDto<ProfileDto> pageResults = toPageDto(results);
        return ResponseEntity.ok(pageResults);
    }


    @Operation(summary = "Supprimer mon profil", description = "Supprime le profil de l'utilisateur connecté.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Profil supprimé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Profil introuvable")
    })
    @DeleteMapping("/current")
    public ResponseEntity<Void> deleteCurrentUserProfile(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        ProfileModel profile = profileService.findByMail(email);

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        profileService.delete(profile.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtenir mon profil", description = "Récupère le profil de l'utilisateur connecté. Si le profil n'existe pas, il est créé automatiquement à partir du Token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil récupéré (ou créé)"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/current")
    public ResponseEntity<ProfileDto> getCurrentUserProfile(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        String userId = jwt.getSubject();

        ProfileModel profile = profileService.findByMail(email);

        if (profile == null) {
            log.info("Utilisateur inconnu en base. Création automatique...");

            profile = ProfileModel.builder()
                    .mail(email)
                    .userId(userId)
                    .firstName(jwt.getClaimAsString("given_name"))
                    .lastName(jwt.getClaimAsString("family_name"))
                    .age(18)
                    .build();

            profile = profileService.save(profile);
        }

        return ResponseEntity.ok(profile.toDto());
    }

    // /**
    //  * Convertit une requête RSQL en un objet Criteria compréhensible par la base
    //  *
    //  * @param stringQuery
    //  * @return
    //  */
    // private Criteria convertQuery(String stringQuery) {
    //     Criteria criteria;
    //     if (StringUtils.hasText(stringQuery)) {
    //         Condition<GeneralQueryBuilder> condition = pipeline.apply(stringQuery, ProfileModel.class);
    //         criteria = condition.query(new MongoVisitor());
    //     } else {
    //         criteria = new Criteria();
    //     }
    //     return criteria;
    // }

    // private Pageable checkPageSize(Pageable pageable) {
    //     if (pageable.getPageSize() > MAX_PAGE_SIZE) {
    //         return PageRequest.of(pageable.getPageNumber(), MAX_PAGE_SIZE);
    //     }
    //     return pageable;
    // }

    private PageDto<ProfileDto> toPageDto(Page<ProfileModel> results) {
        List<ProfileDto> profiles = results.map(ProfileModel::toDto).toList();
        URI nextUri = null;
        if (results.hasNext()) {
            nextUri =
                    ServletUriComponentsBuilder.fromCurrentContextPath()
                            .queryParam("page", results.nextOrLastPageable().getPageNumber())
                            .queryParam("size", results.nextOrLastPageable().getPageSize())
                            .build().toUri();
        }

        return new PageDto<>(
                results.getSize(),
                results.getTotalElements(),
                nextUri,
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .queryParam("page", results.previousOrFirstPageable().getPageNumber())
                        .queryParam("size", results.previousOrFirstPageable().getPageSize())
                        .build().toUri(),
        ServletUriComponentsBuilder.fromCurrentContextPath()
                .queryParam("page", results.nextOrLastPageable().getPageNumber())
                .queryParam("size", results.nextOrLastPageable().getPageSize())
                .build().toUri(),
                profiles);
    }
}
