package ru.practicum.ewm.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.util.ActionStatus;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import java.time.LocalDateTime;

import static ru.practicum.ewm.util.DateTimePattern.DATE_TIME_PATTERN;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class UpdateEventRequest {
    @Size(min = 20, max = 2000, message = "Annotation length must be more then 20 less then 2000")
    private String annotation;
    @Positive(message = "Category id must be positive")
    private Long category;
    @Size(min = 20, max = 7000, message = "must be more then 20 less then 7000")
    private String description;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private ActionStatus actionStatus;
    @Size(min = 3, max = 120, message = "must be more then 3 less then 120")
    private String title;

    @Override
    public String toString() {
        return "NewEventDto{" +
                "annotation='" + annotation + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
