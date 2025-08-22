package likeUniquloWeb.controller;

import likeUniquloWeb.dto.response.ApiResponse;
import likeUniquloWeb.dto.response.ImageResponse;
import likeUniquloWeb.service.ImageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageController {

    ImageService imageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<ImageResponse>> uploadProductImages
            (@RequestParam Long productId, @RequestParam("files")List<MultipartFile> files) throws IOException {
        return ApiResponse.<List<ImageResponse>>builder()
                .result(imageService.upLoadProductImages(productId, files))
                .build();
    }
}
