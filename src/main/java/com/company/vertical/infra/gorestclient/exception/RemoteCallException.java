package com.company.vertical.infra.gorestclient.exception;

public class RemoteCallException extends RuntimeException {

  private RemoteCallException(final String errorBody) {
    super(errorBody);
  }

  public static RemoteCallException of(final String errorBody) {
    return new RemoteCallException(errorBody);
  }
}
