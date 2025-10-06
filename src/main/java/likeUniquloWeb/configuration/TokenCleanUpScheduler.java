package likeUniquloWeb.configuration;

import likeUniquloWeb.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@ConditionalOnProperty(name = "app.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class TokenCleanUpScheduler {
    AuthenticationService authenticationService;

    //    * Runs every 6 hours to clean up expired refresh tokens
//     * Cron expression: "0 0 */6 * * *" means:
//            * - 0 seconds
//     * - 0 minutes
//     * - Every 6 hours
//     * - Any day of month
//     * - Any month
//     * - Any day of week
//     */
    @Scheduled(cron = "0 0 */6 * * *")
    public void cleanupExpiredTokens() {
        log.info("Starting scheduled cleanup of expired refresh tokens");

        try {
            authenticationService.cleanupExpiredTokens();
            log.info("Successfully completed cleanup of expired refresh tokens");
        } catch (Exception e) {
            log.error("Error during token cleanup", e);
        }
    }

    @Scheduled(fixedRate = 14400000, initialDelay = 60000)
    public void cleanupExpiredTokensAlternative() {
        log.debug("Alternative token cleanup - running every 4 hours");

        try {
            authenticationService.cleanupExpiredTokens();
        } catch (Exception e) {
            log.error("Error during alternative token cleanup", e);
        }
    }


    @Scheduled(cron = "0 0 2 * * *")
    public void dailyTokenMaintenance() {
        log.info("Starting daily token maintenance at 2 AM");

        try {

            authenticationService.cleanupExpiredTokens();


            log.info("Daily token maintenance completed successfully");

        } catch (Exception e) {
            log.error("Error during daily token maintenance", e);
        }
    }
}
