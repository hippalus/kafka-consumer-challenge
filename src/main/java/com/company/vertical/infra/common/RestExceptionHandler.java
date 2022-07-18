package com.company.vertical.infra.common;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {


  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> handleException(final Exception exception) {
    log.error("An error occurred! Details: ", exception);
    return ResponseEntity.internalServerError().body(new ErrorResponse("500", exception.getMessage()));
  }


  @ExceptionHandler(WebExchangeBindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleRequestPropertyBindingError(
      final WebExchangeBindException webExchangeBindException) {
    log.debug("Bad request!", webExchangeBindException);
    return createFieldErrorResponse(webExchangeBindException.getBindingResult());
  }

  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleBindException(final BindException bindException) {
    log.debug("Bad request!", bindException);
    return createFieldErrorResponse(bindException.getBindingResult());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleInvalidArgumentException(
      final MethodArgumentNotValidException methodArgumentNotValidException) {
    log.debug("Method argument not valid. Message: $methodArgumentNotValidException.message", methodArgumentNotValidException);
    return createFieldErrorResponse(methodArgumentNotValidException.getBindingResult());
  }


  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handle(final MissingServletRequestParameterException exception) {
    return ResponseEntity.badRequest().body(new ErrorResponse("23", exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
      final MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
    log.trace("MethodArgumentTypeMismatchException occurred", methodArgumentTypeMismatchException);
    return ResponseEntity.unprocessableEntity()
        .body(new ErrorResponse("422", methodArgumentTypeMismatchException.getMessage()));
  }


  private static ResponseEntity<ErrorResponse> createFieldErrorResponse(final Errors bindingResult) {
    final String errorMessage = bindingResult
        .getFieldErrors().stream()
        .map(FieldError::getField)
        .collect(Collectors.joining(" && "));

    log.debug("Exception occurred while request validation: {}", errorMessage);
    return ResponseEntity.badRequest().body(new ErrorResponse("400", "Wrong fields " + errorMessage));
  }
}