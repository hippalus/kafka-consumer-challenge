package com.company.vertical.domain.common.usecase;

import com.company.vertical.domain.common.model.UseCase;

public interface ObservableUseCasePublisher extends BeanAwareUseCasePublisher {

  default <R, T extends UseCase> void register(final Class<T> useCaseClass, final UseCaseHandler<R, ? super T> useCaseHandler) {
    UseCaseHandlerRegistry.INSTANCE.register(useCaseClass, useCaseHandler);
  }

  default <T extends UseCase> void register(final Class<? extends T> useCaseClass, final VoidUseCaseHandler<T> useCaseHandler) {
    UseCaseHandlerRegistry.INSTANCE.register(useCaseClass, useCaseHandler);
  }
}
