package likeUniquloWeb.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("Access denied for user: {} to resource: {} from IP: {}",
                request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous",
                request.getRequestURI(),
                getClientIP(request));

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDate.now().toString());
        errorResponse.put("status", 403);
        errorResponse.put("error", "Forbidden");
        errorResponse.put("message", "Access denied - insufficient privileges");
        errorResponse.put("path", request.getRequestURI());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

}
