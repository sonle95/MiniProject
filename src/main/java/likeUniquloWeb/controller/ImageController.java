package likeUniquloWeb.controller;

import likeUniquloWeb.dto.response.ApiResponse;
import likeUniquloWeb.dto.response.ImageResponse;
import likeUniquloWeb.service.ImageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "*")
public class ImageController {

    ImageService imageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<ImageResponse> uploadImagesForProduct
            (@RequestParam Long productId, @RequestParam("files")List<MultipartFile> files) throws IOException {
        return imageService.upLoadImagesForProduct(productId, files);
    }

//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public List<ImageResponse> uploadProductImages
//            (@RequestParam Long productId, @RequestParam("files")List<MultipartFile> files) throws IOException {
//        return imageService.upLoadProductImages(productId, files);
//    }

    @GetMapping
    public List<ImageResponse> getAll(){
        return imageService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        imageService.delete(id);
    }

    @GetMapping("/page")
    public Page<ImageResponse> getImagesByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String keySearch
    ) {
        return imageService.getImagesByPage(page, size, sortDir, keySearch);
    }

}
