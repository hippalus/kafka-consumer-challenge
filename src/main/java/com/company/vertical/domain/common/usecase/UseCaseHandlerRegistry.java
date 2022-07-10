package com.company.vertical.domain.common.usecase;

import com.company.vertical.domain.common.model.UseCase;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class UseCaseHandlerRegistry {

  public static final UseCaseHandlerRegistry INSTANCE = Singleton.INSTANCE.useCaseHandlerRegistry;
  private final Map<Class<? extends UseCase>, UseCaseHandler<?, ? extends UseCase>> registryForUseCaseHandlers;
  private final Map<Class<? extends UseCase>, VoidUseCaseHandler<? extends UseCase>> registryForVoidUseCaseHandlers;

  private UseCaseHandlerRegistry() {
    this.registryForUseCaseHandlers = new ConcurrentHashMap<>();
    this.registryForVoidUseCaseHandlers = new ConcurrentHashMap<>();
  }

  public <R, T extends UseCase> void register(final Class<? extends T> key, final UseCaseHandler<R, T> useCaseHandler) {
    log.info("Use case {} is registered by handler {}", key.getSimpleName(), useCaseHandler.getClass().getSimpleName());
    this.registryForUseCaseHandlers.put(key, useCaseHandler);
  }

  public <T extends UseCase> void register(final Class<? extends T> key, final VoidUseCaseHandler<T> useCaseHandler) {
    log.info("Use case {} is registered by void handler {}", key.getSimpleName(), useCaseHandler.getClass().getSimpleName());
    this.registryForVoidUseCaseHandlers.put(key, useCaseHandler);
  }

  public UseCaseHandler<?, ? extends UseCase> detectUseCaseHandler(final Class<? extends UseCase> useCaseClass) {
    return this.registryForUseCaseHandlers.get(useCaseClass);
  }

  public VoidUseCaseHandler<? extends UseCase> detectVoidUseCaseHandler(final Class<? extends UseCase> useCaseClass) {
    return this.registryForVoidUseCaseHandlers.get(useCaseClass);
  }

  private enum Singleton {
    INSTANCE;

    public final UseCaseHandlerRegistry useCaseHandlerRegistry;

    Singleton() {
      this.useCaseHandlerRegistry = new UseCaseHandlerRegistry();
    }

  }

}
