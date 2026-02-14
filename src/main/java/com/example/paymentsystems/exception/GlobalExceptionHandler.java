package com.example.paymentsystems.exception;

import com.example.paymentsystems.dto.ApiResponse;
import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;  // ✅ ADD THIS

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ Handles RuntimeException
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleRuntimeException(RuntimeException ex) {
        return new ApiResponse(false, ex.getMessage(), null);
    }

    // ✅ Handles Optimistic Locking conflicts
    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse handleOptimisticLock(OptimisticLockException ex) {
        return new ApiResponse(
                false,
                "Transaction failed due to concurrent update. Please retry.",
                null
        );
    }

    // ✅ Handles validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleValidation(MethodArgumentNotValidException ex) {

        String error = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return new ApiResponse(false, error, null);
    }
}
