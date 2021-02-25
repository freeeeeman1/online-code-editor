package com.netcracker.edu.logic.event;

public interface EventRouter {

    void push(Event event);

    Event next();

    void subscribe(String subscriber, String path);

    void unsubscribe(String subscriber);

    boolean hasAnotherSubscription(String subscriber, String newPath);
}
