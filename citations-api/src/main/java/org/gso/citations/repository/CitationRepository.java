package org.gso.citations.repository;

import java.util.List;

import org.gso.citations.model.CitationModel;
import org.gso.citations.model.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitationRepository extends MongoRepository<CitationModel, String> {

    List<CitationModel> findAllByStatus(Status status);

    List<CitationModel> findAllByWriterId(String writerId);
}
