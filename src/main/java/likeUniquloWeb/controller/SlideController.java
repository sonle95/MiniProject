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
    public ResponseEntity<List<SlideResponse>> uploadSlides(@RequestParam("files") List<MultipartFile> files) throws IOException {
        return ResponseEntity.ok(slideService.uploadSlides(files));
    }

    @GetMapping
    public ResponseEntity<List<SlideResponse>> getAllSlides() {
        return ResponseEntity.ok(slideService.getAllSlides());
    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        slideService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
}
