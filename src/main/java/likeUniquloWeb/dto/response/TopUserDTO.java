package likeUniquloWeb.dto.response;

import java.math.BigDecimal;

public record TopUserDTO(String username,
                         BigDecimal totalSpent) {
}
