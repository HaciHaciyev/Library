package core.project.library.domain.events;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record Events(@NotNull LocalDateTime creation_date,
                     @NotNull LocalDateTime last_update_date) {

    public Events() {
        this(LocalDateTime.now(), LocalDateTime.now());
    }
}
