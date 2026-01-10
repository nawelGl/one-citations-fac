package org.gso.citations.service;

import org.gso.citations.model.CitationModel;
import org.gso.citations.model.Status;
import org.gso.citations.repository.CitationRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class CitationService {
    private final CitationRepository citationRepository;

    public CitationModel createCitation(CitationModel citation){
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        citation.setWriterId(currentUserId);
        citation.setStatus(Status.PENDING);
        return citationRepository.save(citation);
    }
}
