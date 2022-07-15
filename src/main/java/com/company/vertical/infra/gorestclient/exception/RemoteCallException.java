package com.company.vertical.infra.gorestclient.exception;

public class RemoteCallException extends RuntimeException {

  private RemoteCallException(final String message) {
    super(message);
  }

  public static RemoteCallException of(final String message) {
    return new RemoteCallException(message);
  }
}
