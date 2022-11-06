package com.company.vertical.domain.common.usecase;


import com.company.vertical.domain.common.model.UseCase;

public interface UseCasePublisher {

  <R, T extends UseCase> R publish(Class<R> returnClass, T useCase);

  @SuppressWarnings("unused")
  <R, T extends UseCase> void publish(T useCase);
}
