package org.gso.images.repository;

import org.gso.images.model.ImageModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends MongoRepository<ImageModel, String> {
}