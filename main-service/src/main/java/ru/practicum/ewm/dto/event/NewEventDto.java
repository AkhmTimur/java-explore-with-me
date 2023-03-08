package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

import static ru.practicum.ewm.util.DateTimePattern.DATE_TIME_PATTERN;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank(message = "Annotation must not be blank")
    private String annotation;
    @NotNull(message = "Category_id must not be null")
    @Positive(message = "Category_id must be positive")
    private Long category;
    @NotBlank(message = "Description must not be blank")
    private String description;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @Future(message = "Date must be in the future")
    private LocalDateTime eventDate;
    private boolean paid;
    private int participantLimit;
    @Value("true")
    private boolean requestModeration;
    @NotBlank(message = "must not be blank")
    private String title;
}
