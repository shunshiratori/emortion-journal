package com.example.emortion_journal.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Map;

public class GlobalExceptionHandler extends RuntimeException {
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<?> handleNotFound(ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<?> handleForbidden(AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }

        @ExceptionHandler({ IllegalArgumentException.class, MethodArgumentNotValidException.class })
        public ResponseEntity<?> handleBadRequest(Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<?> handleOthers(Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal error"));
        }
}
