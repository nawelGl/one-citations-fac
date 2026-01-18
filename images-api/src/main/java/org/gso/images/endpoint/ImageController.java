package org.gso.images.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/images")
@Tag(name = "Images API", description = "Service de redimensionnement d'images aléatoires")
@Slf4j
public class ImageController {

    private final String imgProxyUrl = "http://localhost:8081";

    private final List<String> imageSources = List.of(
            "https://images.unsplash.com/photo-1543852786-1cf6624b9987",
            "https://images.unsplash.com/photo-1533738363-b7f9aef128ce",
            "https://images.unsplash.com/photo-1592194996308-7b43878e84a6");

    private final Random random = new Random();

    @Operation(summary = "Obtenir une image aléatoire")
    @GetMapping("/random")
    public RedirectView getRandomImage(
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "300") int height) {

        String randomSourceUrl = imageSources.get(random.nextInt(imageSources.size()));

        String finalUrl = String.format("%s/insecure/rs:fill:%d:%d/plain/%s@jpg",
                imgProxyUrl, width, height, randomSourceUrl);

        log.info("Redirection vers : " + finalUrl);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(finalUrl);
        return redirectView;
    }
}