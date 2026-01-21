package com.example.jobnest.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String ERROR_ATTR = "error";
    private static final String ERROR_TITLE_ATTR = "errorTitle";
    private static final String VALIDATION_ERROR_TITLE = "Validation Error";
    private static final String ERROR_VIEW = "error";

    @Value("${app.show-error-details:false}")
    private boolean showErrorDetails;

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
        model.addAttribute(ERROR_ATTR, ex.getMessage());
        model.addAttribute(ERROR_TITLE_ATTR, "Not Found");
        return ERROR_VIEW;
    }

    @ExceptionHandler(ValidationException.class)
    public String handleValidationException(ValidationException ex, Model model) {
        model.addAttribute(ERROR_ATTR, ex.getMessage());
        model.addAttribute(ERROR_TITLE_ATTR, VALIDATION_ERROR_TITLE);
        return ERROR_VIEW;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorizedException(UnauthorizedException ex, Model model) {
        model.addAttribute(ERROR_ATTR, ex.getMessage());
        model.addAttribute(ERROR_TITLE_ATTR, "Access Denied");
        return "redirect:/login";
    }

    @ExceptionHandler(FileStorageException.class)
    public String handleFileStorageException(FileStorageException ex, Model model) {
        model.addAttribute(ERROR_ATTR, ex.getMessage());
        model.addAttribute(ERROR_TITLE_ATTR, "File Upload Error");
        return ERROR_VIEW;
    }

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException ex, Model model) {
        model.addAttribute(ERROR_ATTR, ex.getMessage());
        model.addAttribute(ERROR_TITLE_ATTR, "Business Error");
        return ERROR_VIEW;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(MethodArgumentNotValidException ex, Model model) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        StringBuilder errorMessage = new StringBuilder("Validation errors: ");
        errors.values().forEach(msg -> errorMessage.append(msg).append(" "));

        model.addAttribute(ERROR_ATTR, errorMessage.toString());
        model.addAttribute(ERROR_TITLE_ATTR, VALIDATION_ERROR_TITLE);
        model.addAttribute("validationErrors", errors);
        return ERROR_VIEW;
    }

    @ExceptionHandler(BindException.class)
    public String handleBindException(BindException ex, Model model) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        StringBuilder errorMessage = new StringBuilder("Validation errors: ");
        errors.values().forEach(msg -> errorMessage.append(msg).append(" "));

        model.addAttribute(ERROR_ATTR, errorMessage.toString());
        model.addAttribute(ERROR_TITLE_ATTR, VALIDATION_ERROR_TITLE);
        model.addAttribute("validationErrors", errors);
        return ERROR_VIEW;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolationException(ConstraintViolationException ex, Model model) {
        String violations = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        model.addAttribute(ERROR_ATTR, "Validation error: " + violations);
        model.addAttribute(ERROR_TITLE_ATTR, VALIDATION_ERROR_TITLE);
        return ERROR_VIEW;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        model.addAttribute(ERROR_ATTR, ex.getMessage());
        model.addAttribute(ERROR_TITLE_ATTR, "Invalid Parameter");
        return ERROR_VIEW;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFoundException(NoHandlerFoundException ex, Model model) {
        model.addAttribute(ERROR_ATTR, "Page not found: " + ex.getRequestURL());
        model.addAttribute(ERROR_TITLE_ATTR, "404 - Page Not Found");
        return ERROR_VIEW;
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("An unexpected error occurred: ", ex);

        model.addAttribute(ERROR_ATTR, "An unexpected system error occurred. Please try again.");
        model.addAttribute(ERROR_TITLE_ATTR, "System Error");

        if (showErrorDetails) {
            model.addAttribute("errorDetails", ex.getMessage());
        }

        return ERROR_VIEW;
    }
}
