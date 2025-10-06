//package likeUniquloWeb.exception;
//
//import likeUniquloWeb.dto.response.ApiResponse;
//import org.apache.tomcat.websocket.AuthenticationException;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import java.io.IOException;
//import java.nio.file.AccessDeniedException;
//
//@ControllerAdvice
//public class GlobalHandlingException {
//
//    @ExceptionHandler(value = AppException.class)
//    ResponseEntity<ApiResponse> appException(AppException exception){
//        ErrorCode errorCode = exception.getErrorCode();
//        ApiResponse apiResponse = new ApiResponse<>();
//        apiResponse.setCode(errorCode.getCode());
//        apiResponse.setMessage(errorCode.getMessage());
//        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
//    }
//
//    @ExceptionHandler(value = MethodArgumentNotValidException.class)
//    ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException exception){
//        String enumKey = exception.getFieldError().getDefaultMessage();
//        ErrorCode errorCode = ErrorCode.valueOf(enumKey);
//        ApiResponse apiResponse = new ApiResponse<>();
//        apiResponse.setCode(errorCode.getCode());
//        apiResponse.setMessage(errorCode.getMessage());
//        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
//    }
//
//    @ExceptionHandler(value = AccessDeniedException.class)
//    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception){
//        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
//        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.builder()
//                        .code(errorCode.getCode())
//                        .message(errorCode.getMessage())
//                .build());
//
//    }
//
//    @ExceptionHandler(value = AuthenticationException.class)
//    ResponseEntity<ApiResponse> handlingAuthenticationException(AuthenticationException exception){
//        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
//        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.builder()
//                .code(errorCode.getCode())
//                .message(errorCode.getMessage())
//
//                .build());
//    }
//
//    @ExceptionHandler(value = Exception.class)
//    ResponseEntity<ApiResponse> handlingUncategorizedException(Exception exception){
//        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
//        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.builder()
//                        .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
//                        .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
//
//                .build());
//    }
//
//    @ExceptionHandler(value = IOException.class)
//    ResponseEntity<ApiResponse> handlingIOException(IOException exception) {
//        ErrorCode errorCode = ErrorCode.UPLOAD_FAIL;
//        return ResponseEntity.status(errorCode.getHttpStatus())
//                .body(ApiResponse.builder()
//                        .code(errorCode.getCode())
//                        .message(exception.getMessage())
//                        .build());
//    }
//
//
//
//}
