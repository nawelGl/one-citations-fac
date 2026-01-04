package org.gso.profiles.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.gso.profiles.model.ProfileModel;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ProfileDto (
        String id,
        @NotEmpty String userId,
        @Email String mail,
        @Min(13) int age,
        String firstName,
        String lastName,
        @JsonSerialize(using = LocalDateTimeSerializer.class) @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
        LocalDateTime created,
        @JsonSerialize(using = LocalDateTimeSerializer.class) @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
        LocalDateTime modified) {

    public ProfileModel toModel() {
        return ProfileModel.builder()
                .id(this.id)
                .userId(this.userId)
                .age(this.age)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .build();
    }

    public ProfileDto withId(String id) {
        return new ProfileDto(
                id,
                this.userId,
                this.mail,
                this.age,
                this.firstName,
                this.lastName,
                this.created,
                this.modified
        );
    }
}
