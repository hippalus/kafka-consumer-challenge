package com.company.vertical.domain.common.event;


import com.company.vertical.domain.common.model.Event;

public interface EventPublisher<T extends Event> {

  void publish(T event);
}
