package com.company.vertical.domain.user.migration.usecase;

import com.company.vertical.domain.common.model.UseCase;
import lombok.NonNull;

public record MigrateUserUseCase(@NonNull Long userId) implements UseCase {

}
