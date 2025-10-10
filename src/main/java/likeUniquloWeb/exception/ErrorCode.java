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
    OUT_OF_STOCK(1024,"out of stock", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1015, "user not found", HttpStatus.NOT_FOUND),
    VARIANT_NOT_FOUND(1016,"variant not found", HttpStatus.NOT_FOUND),
    STOCK_NOT_FOUND(1017,"stock not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(1018,"role not found", HttpStatus.NOT_FOUND),
    REVIEW_NOT_FOUND(1019,"review not found", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1020, "category not existed", HttpStatus.NOT_FOUND),
    CATEGORY_MUST_NOT_BE_NULL(1021,"category must not be null", HttpStatus.BAD_REQUEST),
    PRODUCT_MUST_NOT_BE_NULL(1022,"product must not be null", HttpStatus.BAD_REQUEST),
    PRODUCT_NAME_MUST_NOT_BE_NULL(1023, "product name must not be null", HttpStatus.BAD_REQUEST),
    PRODUCT_PRICE_MUST_NOT_BE_NULL_AND_ABOVE_ZERO(1024, "product price must not be null", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_FOUND(1025, "permission not found", HttpStatus.NOT_FOUND),
    VARIANT_MUST_NOT_BE_NULL(1026,"variant must not be null", HttpStatus.BAD_REQUEST),
    STOCK_MUST_NOT_BE_NULL(1027, "stock must not be null", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK(1028, "insufficient stock", HttpStatus.INSUFFICIENT_STORAGE),
    ORDER_NOT_FOUND(1029,"order not found", HttpStatus.BAD_REQUEST),
    CAN_NOT_DELETE_ORDER(1030,"order can not be deleted", HttpStatus.BAD_REQUEST),
    CAN_NOT_UPDATE_ORDER(1031,"order can not be updated", HttpStatus.BAD_REQUEST),
    ITEM_NOT_FOUND(1032,"item not found", HttpStatus.NOT_FOUND),
    ORDER_MUST_NOT_BE_NULL(1033,"order must not be null",HttpStatus.BAD_REQUEST),
    ITEM_MUST_NOT_BE_NULL(1034,"item must not be null", HttpStatus.BAD_REQUEST),
    QUANTITY_MUST_NOT_LESS_THAN_ZERO(1035, "quantity must be greater than 0", HttpStatus.BAD_REQUEST),
    CATEGORY_EXISTED(1036, "category existed", HttpStatus.BAD_REQUEST),
    CART_EMPTY(1037,"cart is empty", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(1038,"cart not found", HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(1039, "email existed", HttpStatus.BAD_REQUEST),
    PASSWORD_WRONG(1040,"password is wrong", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_NOT_FOUND(1041,"refresh token not found", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_EXPIRED(1042, "refresh token expired", HttpStatus.UNAUTHORIZED),
    REVIEW_ALREADY_EXISTS(1043, "review existed", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_REVIEW_CREATION(1044, "review is not authorized", HttpStatus.UNAUTHORIZED),
    REVIEW_INVALID(1045, "review invalid", HttpStatus.BAD_REQUEST),
    REVIEW_TOO_LONG(1046, "review too long", HttpStatus.BAD_REQUEST),
    REVIEW_WITHOUT_PURCHASE(1047, "review without purchase", HttpStatus.UNAUTHORIZED),
    ADDRESS_EXISTED(1048, "address existed", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1049,"Password and confirm password do not match", HttpStatus.BAD_REQUEST ),
    INVALID_TOKEN(1050,"invalid token", HttpStatus.BAD_REQUEST),
    FORBIDDEN(1051, "method is not allowed", HttpStatus.FORBIDDEN),
    QUANTITY_EXISTED(1052, "quantity is set already", HttpStatus.FORBIDDEN),
    ADDRESS_LIMIT_EXCEEDED(1053,"each user must not have more than three addresses", HttpStatus.FORBIDDEN)
    ,CATEGORY_HAS_PRODUCTS(1054,"category has products", HttpStatus.FORBIDDEN),
    VARIANT_EXISTED(1055,"variant existed", HttpStatus.BAD_REQUEST)
    ;

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
