package likeUniquloWeb.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DropboxService {

    private final DbxClientV2 dropboxClient;

    @Value("${dropbox.base.path}")
    private String basePath;

    /**
     * Upload single file to Dropbox
     * @return Public URL of uploaded file
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.NO_FILE_UPLOADED);
        }

        try {
            // Clean filename
            String originalFileName = file.getOriginalFilename();
            String cleanFileName = originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            String fileName = System.currentTimeMillis() + "_" + cleanFileName;

            // Ensure basePath starts with / and doesn't end with /
            String normalizedBasePath = basePath;
            if (!normalizedBasePath.startsWith("/")) {
                normalizedBasePath = "/" + normalizedBasePath;
            }
            if (normalizedBasePath.endsWith("/")) {
                normalizedBasePath = normalizedBasePath.substring(0, normalizedBasePath.length() - 1);
            }

            // Full path in Dropbox (must start with /)
            String dropboxPath = normalizedBasePath + "/" + folder + "/" + fileName;

            log.info("📤 Uploading to Dropbox: {}", dropboxPath);
            log.info("📊 File size: {} bytes", file.getSize());
            log.info("🔑 Using token: {}...", dropboxClient != null ? "OK" : "NULL");
            log.info("🗂️ Base path: {}", normalizedBasePath);

            // Upload file
            InputStream inputStream = file.getInputStream();
            FileMetadata metadata = dropboxClient.files()
                    .uploadBuilder(dropboxPath)
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(inputStream);

            log.info("✅ File uploaded to Dropbox: {}", dropboxPath);

            // Create shared link
            String publicUrl = createSharedLink(dropboxPath);

            // Validate URL before returning
            if (!publicUrl.startsWith("http://") && !publicUrl.startsWith("https://")) {
                log.error("❌ Invalid URL returned from createSharedLink: {}", publicUrl);
                throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
            }

            log.info("🎉 Final public URL: {}", publicUrl);
            return publicUrl;

        } catch (DbxException e) {
            log.error("❌ Dropbox upload error - Message: {}", e.getMessage());
            log.error("❌ Dropbox upload error - Type: {}", e.getClass().getName());
            log.error("❌ Dropbox upload error - Full stack trace: ", e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        } catch (Exception e) {
            log.error("❌ General upload error: ", e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Upload multiple files
     */
    public List<String> uploadFiles(List<MultipartFile> files, String folder) throws IOException {
        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String url = uploadFile(file, folder);
                urls.add(url);
            }
        }

        return urls;
    }

    /**
     * Create shared link for file
     */
    private String createSharedLink(String path) {
        try {
            log.info("🔗 Creating shared link for path: {}", path);

            // Check if shared link already exists
            var existingLinks = dropboxClient.sharing().listSharedLinksBuilder()
                    .withPath(path)
                    .withDirectOnly(true)
                    .start();

            if (!existingLinks.getLinks().isEmpty()) {
                String url = existingLinks.getLinks().get(0).getUrl();
                log.info("📎 Found existing shared link: {}", url);
                // Convert to direct download link
                return convertToDirectLink(url);
            }

            // Create new shared link
            log.info("📝 Creating new shared link...");
            SharedLinkMetadata sharedLink = dropboxClient.sharing()
                    .createSharedLinkWithSettings(path);

            String url = sharedLink.getUrl();
            log.info("📎 Created new shared link: {}", url);
            // Convert to direct download link
            return convertToDirectLink(url);

        } catch (DbxException e) {
            log.error("❌ DbxException creating shared link for path: {}", path);
            log.error("❌ Error type: {}", e.getClass().getSimpleName());
            log.error("❌ Error message: {}", e.getMessage());
            log.error("❌ Full error details: ", e);

            // Don't return invalid URL - throw exception instead
            throw new RuntimeException("Failed to create Dropbox shared link: " + e.getMessage(), e);
        }
    }

    /**
     * Convert Dropbox shared link to direct download link
     * IMPORTANT: Replace dl=0 with raw=1 (don't add both)
     */
    private String convertToDirectLink(String url) {
        log.info("🔄 Converting Dropbox URL: {}", url);

        // Replace any occurrence of dl=0 with raw=1
        if (url.contains("dl=0")) {
            String directUrl = url.replace("dl=0", "raw=1");
            log.info("✅ Replaced dl=0 with raw=1: {}", directUrl);
            return directUrl;
        }

        // If no dl parameter, add raw=1
        if (url.contains("?") && !url.contains("raw=1") && !url.contains("dl=1")) {
            String directUrl = url + "&raw=1";
            log.info("✅ Added raw=1 parameter: {}", directUrl);
            return directUrl;
        }

        if (!url.contains("?")) {
            String directUrl = url + "?raw=1";
            log.info("✅ Added raw=1 as first parameter: {}", directUrl);
            return directUrl;
        }

        log.info("ℹ️ URL already in direct format: {}", url);
        return url;
    }

    /**
     * Delete file from Dropbox
     */
    public void deleteFile(String dropboxPath) {
        try {
            dropboxClient.files().deleteV2(dropboxPath);
            log.info("✅ File deleted from Dropbox: {}", dropboxPath);
        } catch (DbxException e) {
            log.error("❌ Error deleting file from Dropbox: {}", e.getMessage());
        }
    }
}
