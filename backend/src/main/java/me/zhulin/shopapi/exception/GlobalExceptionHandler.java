package me.zhulin.shopapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MyException.class)
    public ResponseEntity<Map<String, Object>> handleMyException(MyException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", ex.getCode() != null ? ex.getCode() : 400);
        body.put("message", ex.getMessage());

        // Bắt lỗi chuyển trạng thái đơn hàng không hợp lệ và ép về HTTP 400 Bad Request
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}