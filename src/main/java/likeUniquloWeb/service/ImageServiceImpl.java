package likeUniquloWeb.service;

import likeUniquloWeb.dto.response.ImageResponse;
import likeUniquloWeb.entity.Image;
import likeUniquloWeb.entity.Product;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.FileUploadUtil;
import likeUniquloWeb.mapper.ImageMapper;
import likeUniquloWeb.repository.ImageRepository;
import likeUniquloWeb.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    ProductRepository productRepository;
    ImageRepository imageRepository;
    ImageMapper imageMapper;
    DropboxService dropboxService;

    @Value("${app.upload.dir}")
    @NonFinal
    String uploadDir;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<ImageResponse> upLoadImagesForProduct(Long productId, List<MultipartFile> files) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (files == null || files.isEmpty()) {
            throw new AppException(ErrorCode.NO_FILE_UPLOADED);
        }
        List<String> dropboxUrls = dropboxService.uploadFiles(files, "products");

        List<Image> images = dropboxUrls.stream().map(url -> {
            Image image = new Image();
            image.setUrl(url);
            image.setProduct(product);
            return image;
        }).toList();

        return imageMapper.imgToDto(imageRepository.saveAll(images));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<ImageResponse> upLoadProductImages(Long productId, List<MultipartFile> files) throws IOException {

        Product product = productRepository.findById(productId).orElseThrow(()->
                new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if(files == null || files.isEmpty()){
            throw new AppException(ErrorCode.NO_FILE_UPLOADED);
        }
        List<Image> images;

        String uploadDir = "C:\\Users\\ADMIN\\Desktop\\likeUniWebImages";

        List<String> filePaths = FileUploadUtil.uploadFiles(files, uploadDir);
            images = filePaths.stream().map(path -> {
            Image image = new Image();
                image.setUrl("/uploads/" + path);
            image.setProduct(product);
            return image;
        }).toList();

        return imageMapper.imgToDto(imageRepository.saveAll(images));

    }


    @Override
    public List<ImageResponse> getAll(){
        return imageMapper.imgToDto(imageRepository.findAll());
    }


    @Override
    public Page<ImageResponse> getImagesByPage(int page, int size, String sortDir, String keySearch) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by("id").ascending()
                : Sort.by("id").descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Image> images;
        if (keySearch == null || keySearch.trim().isEmpty()) {
            images = imageRepository.findAll(pageable);
        } else {
            images = imageRepository.searchImagesByProductNameOrId(keySearch.trim(), pageable);
        }

        return images.map(imageMapper::imageToDto);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void delete(Long id) {
        if (id == null) {
            log.warn("Attempted to delete image with null ID");
            return;
        }

        Optional<Image> image = imageRepository.findById(id);
        if (image.isEmpty()) {
            log.warn("Image with id {} not found", id);
            return;
        }


        imageRepository.deleteById(id);
    }
}
