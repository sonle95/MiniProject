package likeUniquloWeb.service;

import likeUniquloWeb.dto.response.SlideResponse;
import likeUniquloWeb.entity.Slide;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.FileUploadUtil;
import likeUniquloWeb.mapper.SlideMapper;
import likeUniquloWeb.repository.SlideRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlideServiceImpl implements SlideService{
    SlideRepository slideRepository;
    SlideMapper slideMapper;
    DropboxService dropboxService;

    @Value("${app.upload.dir}")
    @NonFinal
    String uploadDir;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<SlideResponse> uploadSlidesForWeb(List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new AppException(ErrorCode.NO_FILE_UPLOADED);
        }

        List<String> dropboxUrls = dropboxService.uploadFiles(files, "slides");

        List<Slide> slides = dropboxUrls.stream().map(url -> {
            Slide slide = new Slide();
            slide.setImgUrl(url);
            slide.setActive(true);
            return slide;
        }).toList();

        return slideMapper.toDtoList(slideRepository.saveAll(slides));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<SlideResponse> uploadSlides(List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new AppException(ErrorCode.NO_FILE_UPLOADED);
        }

        String uploadPath = uploadDir +  "/slides";
        List<String> filePaths = FileUploadUtil.uploadFiles(files, uploadPath);

        List<Slide> slides = filePaths.stream().map(path -> {
            Slide slide = new Slide();
            slide.setImgUrl("/slides/" + path);
            slide.setActive(true);
            return slide;
        }).toList();

        return slideMapper.toDtoList(slideRepository.saveAll(slides));
    }

    @Override
    public List<SlideResponse> getAllSlides() {
        return slideMapper.toDtoList(slideRepository.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteById(Long id) {
        slideRepository.deleteById(id);
    }
}
