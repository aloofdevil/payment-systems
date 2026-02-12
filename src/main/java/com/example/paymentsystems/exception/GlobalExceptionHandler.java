package com.example.paymentsystems.exception;

import com.example.paymentsystems.dto.ApiResponse;
import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleRuntimeException(RuntimeException ex) {
        return new ApiResponse(false, ex.getMessage(), null);
    }

    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse handleOptimisticLock(OptimisticLockException ex) {
        return new ApiResponse(
                false,
                "Transaction failed due to concurrent update. Please retry.",
                null
        );
    }
}

