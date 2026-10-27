package com.flottevehicule.flottevehicule.exception;

import com.flottevehicule.flottevehicule.utils.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response<Void>> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.error(e.getMessage()));
    }

    @ExceptionHandler(RessourceUtiliseeException.class)
    public ResponseEntity<Response<Void>> handleRessourceUtilisee(RessourceUtiliseeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Response.error(e.getMessage()));
    }

    @ExceptionHandler(DoublonRessourceException.class)
    public ResponseEntity<Response<Void>> handleDoublon(DoublonRessourceException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Response.error(e.getMessage()));
    }

    @ExceptionHandler(RequeteInvalideException.class)
    public ResponseEntity<Response<Void>> handleRequeteInvalide(RequeteInvalideException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error(e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Response<Void>> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Response.error(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleGeneral(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Erreur interne : " + e.getMessage()));
    }
}