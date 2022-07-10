package com.company.vertical.domain.user.migration.event;

import com.company.vertical.domain.common.model.Event;

public record MigrateUser(Long userId) implements Event {

}

