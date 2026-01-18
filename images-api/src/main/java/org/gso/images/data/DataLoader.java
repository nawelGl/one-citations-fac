package org.gso.images.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gso.images.model.ImageModel;
import org.gso.images.repository.ImageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataLoader {

    private final ImageRepository imageRepository;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            if (imageRepository.count() == 0) {
                log.info("Chargement automatique des images...");

                List<String> urls = List.of(
                        "https://images.unsplash.com/photo-1543852786-1cf6624b9987",
                        "https://images.unsplash.com/photo-1533738363-b7f9aef128ce",
                        "https://images.unsplash.com/photo-1592194996308-7b43878e84a6"
                );

                for (int i = 0; i < urls.size(); i++) {
                    ImageModel img = ImageModel.builder()
                            .url(urls.get(i))
                            .build();
                    imageRepository.save(img);
                }

                log.info("{} images ajoutées avec succès !", urls.size());
            }
        };
    }
}