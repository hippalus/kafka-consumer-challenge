package com.company.vertical.domain.common.usecase;

import com.company.vertical.domain.common.model.UseCase;

public interface UseCaseHandler<R, T extends UseCase> {

  R handle(T useCase);
}
