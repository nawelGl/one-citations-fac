package org.gso.images.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "images")
public class ImageModel {

    @Id
    private String id;

    private String url;
}