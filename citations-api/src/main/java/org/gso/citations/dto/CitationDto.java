package org.gso.citations.dto;

import java.time.LocalDateTime;
import org.gso.citations.model.CitationModel;
import org.gso.citations.model.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CitationDto(
        String id,
        @NotEmpty(message = "Le contenu est obligatoire.") String content,
        String writerId,
        String moderatorId,
        @NotNull Status status,
        @JsonSerialize(using = LocalDateTimeSerializer.class) @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
        LocalDateTime created,
        @JsonSerialize(using = LocalDateTimeSerializer.class) @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
        LocalDateTime modified
    ) {

        public CitationModel toModel(){
            return CitationModel.builder()
                .id(this.id)
                .content(this.content)
                .writerId(this.writerId)
                .moderatorId(this.moderatorId)
                .status(this.status)
                .build();
        }

        public CitationDto withId(String id){
            return new CitationDto(
                id,
                this.content,
                this.writerId,
                this.moderatorId,
                this.status,
                this.created,
                this.modified
            );
        }
    
}
