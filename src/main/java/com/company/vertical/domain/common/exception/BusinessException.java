package com.company.vertical.domain.common.exception;

public class BusinessException extends RuntimeException {

  private BusinessException(final String message) {
    super(message);
  }

  public static BusinessException of(final String message) {
    return new BusinessException(message);
  }
}
