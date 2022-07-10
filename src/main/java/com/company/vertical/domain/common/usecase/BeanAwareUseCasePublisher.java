package com.company.vertical.domain.common.usecase;

import com.company.vertical.domain.common.model.UseCase;
import java.util.Objects;

public interface BeanAwareUseCasePublisher extends UseCasePublisher {

  @Override
  @SuppressWarnings("unchecked")
  default <R, T extends UseCase> R publish(final Class<R> returnClass, final T useCase) {
    final var useCaseHandler = (UseCaseHandler<R, T>) UseCaseHandlerRegistry.INSTANCE.detectUseCaseHandler(useCase.getClass());
    validateUseCaseHandlerDetection(useCase, useCaseHandler);
    return useCaseHandler.handle(useCase);

  }

  @Override
  @SuppressWarnings("unchecked")
  default <R, T extends UseCase> void publish(final T useCase) {
    final Class<? extends UseCase> caseClass = useCase.getClass();

    final var voidUseCaseHandler = (VoidUseCaseHandler<T>) UseCaseHandlerRegistry.INSTANCE.detectVoidUseCaseHandler(caseClass);
    if (Objects.isNull(voidUseCaseHandler)) {
      final var useCaseHandler = (UseCaseHandler<R, T>) UseCaseHandlerRegistry.INSTANCE.detectUseCaseHandler(caseClass);
      validateUseCaseHandlerDetection(useCase, useCaseHandler);
      useCaseHandler.handle(useCase);
    } else {
      validateVoidUseCaseHandlerDetection(useCase, voidUseCaseHandler);
      voidUseCaseHandler.handle(useCase);
    }
  }

  static <R, T extends UseCase> void validateUseCaseHandlerDetection(final T useCase, final UseCaseHandler<R, T> handler) {
    if (Objects.isNull(handler)) {
      throw new IllegalStateException("Use case handler cannot be detected for the use case: " + useCase
          + ", handlers:" + UseCaseHandlerRegistry.INSTANCE.getRegistryForUseCaseHandlers());
    }
  }

  static <T extends UseCase> void validateVoidUseCaseHandlerDetection(final T useCase, final VoidUseCaseHandler<T> handler) {
    if (Objects.isNull(handler)) {
      throw new IllegalStateException(
          "Void Use case handler cannot be detected for the use case: " + useCase
              + ", handlers:" + UseCaseHandlerRegistry.INSTANCE.getRegistryForVoidUseCaseHandlers());
    }
  }

}
