package org.gso.citations.model;

import java.time.LocalDateTime;

import org.gso.citations.dto.CitationDto;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class CitationModel {

    @Id
    private String id;
    private String content;
    private String writerId;
    private String moderatorId;
    private Status status;
    @CreatedDate
    private LocalDateTime created;
    @LastModifiedDate
    private LocalDateTime modified;

    public CitationDto toDto(){
        return new CitationDto(
            this.id,
            this.content,
            this.writerId,
            this.moderatorId,
            this.status,
            this.created,
            this.modified
        );
    }

}
