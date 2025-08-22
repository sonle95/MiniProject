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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    ProductRepository productRepository;
    ImageRepository imageRepository;
    ImageMapper imageMapper;

    @Override
    public List<ImageResponse> upLoadProductImages(Long productId, List<MultipartFile> files) throws IOException {
        Product product = productRepository.findById(productId).orElseThrow(()->
                new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if(files == null || files.isEmpty()){
            throw new AppException(ErrorCode.NO_FILE_UPLOADED);
        }
        List<Image> images;

        String uploadDir = "src/main/resources/static/uploads/";
        List<String> filePaths = FileUploadUtil.uploadFiles(files, uploadDir);
            images = filePaths.stream().map(path -> {
            Image image = new Image();
            image.setUrl(path);
            image.setProduct(product);
            return image;
        }).toList();

        return imageMapper.imgToDto(imageRepository.saveAll(images));
    }
}
