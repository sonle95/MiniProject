package likeUniquloWeb.configuration;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DropboxConfig {
    @Value("${dropbox.app.key}")
    private String appKey;

    @Value("${dropbox.app.secret}")
    private String appSecret;

    @Value("${dropbox.refresh.token}")
    private String refreshToken;

    @Bean
    public DbxClientV2 dropboxClient() {
        log.info("🔧 Initializing Dropbox Client...");

        try {
            log.info("🔑 App Key: {}...", appKey != null && appKey.length() > 10
                ? appKey.substring(0, 10) + "..."
                : "NULL");
            log.info("🔑 Refresh Token: {}...", refreshToken != null && refreshToken.length() > 20
                ? refreshToken.substring(0, 20) + "..."
                : "NULL");

            DbxRequestConfig config = DbxRequestConfig.newBuilder("minimalistMenWear-web/1.0").build();

            // Try to create client with refresh token (OAuth 2.0)
            try {
                log.info("📝 Attempting to create client with refresh token...");

                DbxCredential credential = new DbxCredential(
                    null,                   // accessToken (will be auto-generated)
                    -1L,                    // expiresAt
                    refreshToken,           // refreshToken
                    appKey,                 // appKey
                    appSecret               // appSecret
                );

                DbxClientV2 client = new DbxClientV2(config, credential);

                // Test in background
                new Thread(() -> {
                    try {
                        var account = client.users().getCurrentAccount();
                        log.info("✅ Dropbox Client verified with refresh token");
                        log.info("👤 Connected as: {} ({})",
                            account.getName().getDisplayName(),
                            account.getEmail());
                    } catch (Exception e) {
                        log.warn("⚠️ Could not verify Dropbox connection: {}", e.getMessage());
                    }
                }).start();

                log.info("✅ Dropbox Client initialized successfully");
                return client;

            } catch (Exception refreshTokenError) {
                log.warn("⚠️ Failed to create client with refresh token: {}", refreshTokenError.getMessage());
                log.warn("⚠️ Dropbox upload feature may not work properly");

                // Return a basic client without credentials (will fail on upload but won't block startup)
                log.info("✅ Created fallback Dropbox client");
                return new DbxClientV2(config, ""); // Empty token - won't work but won't crash
            }

        } catch (Exception e) {
            log.error("❌ Critical error initializing Dropbox: {}", e.getMessage());
            log.warn("⚠️ Creating fallback client - Dropbox features will be disabled");

            // Last resort: return a basic client that won't crash the app
            DbxRequestConfig config = DbxRequestConfig.newBuilder("minimalistMenWear-web/1.0").build();
            return new DbxClientV2(config, ""); // Empty token
        }
    }
}
