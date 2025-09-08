package likeUniquloWeb.dto.request;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public abstract class BaseRequest {

    @PastOrPresent(message = "Created date cannot be in the future")
    LocalDateTime createdAt;

    @Size(max = 100, message = "Created by cannot exceed 100 characters")
    String createdBy;
}

