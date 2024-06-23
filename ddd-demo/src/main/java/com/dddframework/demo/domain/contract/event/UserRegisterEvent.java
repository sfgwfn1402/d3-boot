package com.dddframework.demo.domain.contract.event;

import com.dddframework.core.contract.DomainEvent;

public class UserRegisterEvent extends DomainEvent {
    public <T> UserRegisterEvent(T source) {
        super(source);
    }
}
