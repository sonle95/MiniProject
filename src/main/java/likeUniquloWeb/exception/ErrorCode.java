package likeUniquloWeb.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(000,"unidentified", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001,"User existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(1005,"Product not found", HttpStatus.NOT_FOUND),
    USER_INVALID(1003,"at least 8 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004,"at least 8 characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006,"unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007,"forbidden",HttpStatus.FORBIDDEN),
    ACCESS_DENIED( 1008, "Access denied", HttpStatus.FORBIDDEN),
    INTERNAL_ERROR( 1009, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    UPLOAD_FAIL(1010,"upload failed", HttpStatus.BAD_REQUEST),
    NO_FILE_UPLOADED(1011,"no file uploaded", HttpStatus.BAD_REQUEST),
    NOT_FOUND(1012,"not found", HttpStatus.NOT_FOUND),
    QUANTITY_NOT_ENOUGH(1023,"Insufficient", HttpStatus.BAD_REQUEST),
    OUT_OF_STOCK(1024,"out of stock", HttpStatus.BAD_REQUEST)
    ;



    ErrorCode(int code, String message, HttpStatus httpStatus) {

        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }


    int code;
    String message;
    HttpStatus httpStatus;



}
