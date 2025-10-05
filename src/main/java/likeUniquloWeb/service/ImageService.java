package likeUniquloWeb.service;

import likeUniquloWeb.dto.response.ImageResponse;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface ImageService {


    List<ImageResponse> upLoadProductImages(Long productId, List<MultipartFile> files)
        throws IOException;

    List<ImageResponse> getAll();

    void delete(Long id);
}
