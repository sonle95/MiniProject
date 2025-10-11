package likeUniquloWeb.configuration;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DropboxConfig {
    @Value("${dropbox.access.token}")
    private String accessToken;

    @Bean
    public DbxClientV2 dropboxClient() {
        log.info("🔧 Initializing Dropbox Client...");
        log.info("🔑 Token loaded: {}...", accessToken != null && !accessToken.isEmpty()
            ? accessToken.substring(0, Math.min(20, accessToken.length())) + "..."
            : "NULL or EMPTY");
        log.info("📏 Token length: {} characters", accessToken != null ? accessToken.length() : 0);

        DbxRequestConfig config = DbxRequestConfig.newBuilder("minimalistMenWear-web/1.0").build();
        DbxClientV2 client = new DbxClientV2(config, accessToken);

        log.info("✅ Dropbox Client initialized successfully");
        return client;
    }
}
