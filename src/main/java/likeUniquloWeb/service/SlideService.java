package likeUniquloWeb.service;

import likeUniquloWeb.dto.response.SlideResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SlideService {
    List<SlideResponse> uploadSlidesForWeb(List<MultipartFile> files) throws IOException;
    List<SlideResponse> uploadSlides(List<MultipartFile> files) throws IOException;
    List<SlideResponse> getAllSlides();
    void deleteById(Long id);
}
