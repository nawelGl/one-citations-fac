package org.gso.images.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gso.images.model.ImageModel;
import org.gso.images.repository.ImageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/images")
@Tag(name = "Images API", description = "Service de redimensionnement d'images aléatoires")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final String imgProxyUrl = "http://localhost:8081";

    private final List<String> imageSources = List.of(
            "https://images.unsplash.com/photo-1543852786-1cf6624b9987",
            "https://images.unsplash.com/photo-1533738363-b7f9aef128ce",
            "https://images.unsplash.com/photo-1592194996308-7b43878e84a6",
            "https://images.unsplash.com/photo-1464802686167-b939a6910659",
            "https://images.unsplash.com/photo-1608178398319-48f814d0750c",
            "https://images.unsplash.com/photo-1502318217862-aa4e294ba657",
            "https://images.unsplash.com/photo-1621428047980-d773eb8b5f59",
            "https://images.unsplash.com/photo-1477959858617-67f85cf4f1df",
            "https://images.unsplash.com/photo-1496568816309-51d7c20e3b21",
            "https://images.unsplash.com/photo-1483653364400-eedcfb9f1f88",
            "https://images.unsplash.com/photo-1517511620798-cec17d428bc0",
            "https://images.unsplash.com/photo-1768050197707-5576b319e2d3"
        );

    private final Random random = new Random();

    private final ImageRepository imageRepository;

    @Operation(summary = "Obtenir une image aléatoire")
    @GetMapping("/random")
    public RedirectView getRandomImage(
            @RequestParam(defaultValue = "1200") int width,
            @RequestParam(defaultValue = "1800") int height) {

        String randomSourceUrl = imageSources.get(random.nextInt(imageSources.size()));

        String finalUrl = String.format("%s/insecure/rs:fill:%d:%d/plain/%s@jpg",
                imgProxyUrl, width, height, randomSourceUrl);

        log.info("Redirection vers : " + finalUrl);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(finalUrl);
        return redirectView;
    }

    @Operation(summary = "Ajouter une nouvelle image manuellement")
    @PostMapping
    public ResponseEntity<ImageModel> addImage(@RequestBody ImageModel image) {
        ImageModel savedImage = imageRepository.save(image);
        
        log.info("Nouvelle image ajoutée via API : {}", savedImage.getUrl());

        return ResponseEntity.status(HttpStatus.CREATED).body(savedImage);
    }
}