package com.company.vertical.domain.common.usecase;

import com.company.vertical.domain.common.model.UseCase;

public interface VoidUseCaseHandler<T extends UseCase> {

  void handle(T useCase);
}
