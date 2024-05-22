package core.project.library.domain.events;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

public record Events(@NotNull LocalDateTime creation_date,
                     @NotNull LocalDateTime last_update_date) {

    public Events() {
        this(LocalDateTime.now(), LocalDateTime.now());
    }

    public Events {
        Objects.requireNonNull(creation_date);
        Objects.requireNonNull(last_update_date);
    }
}
