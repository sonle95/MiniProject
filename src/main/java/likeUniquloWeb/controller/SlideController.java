package likeUniquloWeb.controller;

import likeUniquloWeb.dto.response.SlideResponse;
import likeUniquloWeb.service.SlideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/slides")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SlideController {

    private final SlideService slideService;


    @PostMapping("/upload")
    public List<SlideResponse> uploadSlidesForWeb(@RequestParam("files") List<MultipartFile> files) throws IOException {
        return slideService.uploadSlidesForWeb(files);
    }

//    @PostMapping("/upload")
//    public List<SlideResponse> uploadSlides(@RequestParam("files") List<MultipartFile> files) throws IOException {
//        return slideService.uploadSlides(files);
//    }

    @GetMapping
    public List<SlideResponse> getAllSlides() {
        return slideService.getAllSlides();
    }

    @DeleteMapping("/show/{id}")
    public void delete(@PathVariable Long id) {
        slideService.deleteById(id);
    }
}
