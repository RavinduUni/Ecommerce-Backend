package com.ecom.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status != null ? status : 500);
        errorResponse.put("error", "Application Error");
        errorResponse.put("message", exception != null ? exception.toString() : "Internal crash wela thiyenne. web.stdout.log eka balanna.");

        int statusCode = status != null ? Integer.parseInt(status.toString()) : 500;
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(statusCode));
    }
}
