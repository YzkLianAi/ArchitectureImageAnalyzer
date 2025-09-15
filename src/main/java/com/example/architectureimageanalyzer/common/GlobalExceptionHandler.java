package com.example.architectureimageanalyzer.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//全局异常处理器
@ControllerAdvice(annotations = {RestController.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ImageProcessingException.class)
    public Result<String> handleImageProcessingException(ImageProcessingException e) {
        return Result.fail("图片处理错误" + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleGeneralException(Exception e) {
        return Result.fail("服务器错误" + e.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException exception) {
        log.error(exception.getMessage());

        return Result.fail("自定义业务异常" + exception.getMessage());
    }
}
