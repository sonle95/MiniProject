package likeUniquloWeb.service;

import likeUniquloWeb.dto.response.ImageResponse;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface ImageService {


    List<ImageResponse> upLoadProductImages(Long productId, List<MultipartFile> files)
        throws IOException;

    List<ImageResponse> getAll();

    Page<ImageResponse> getImagesByPage(int page, int size, String sortDior, String keySearch);

    void delete(Long id);
}
