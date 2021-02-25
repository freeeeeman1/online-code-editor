package com.netcracker.edu.logic.event;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class EventRouterImpl implements EventRouter {

    private final Queue<Event> events;

    private final Map<String, Set<String>> pathSubscriptions;
    private final Map<String, String> subscriberToPathIndex;

    public EventRouterImpl() {
        this.events = new ArrayBlockingQueue<>(100);
        this.pathSubscriptions = new ConcurrentHashMap<>();
        this.subscriberToPathIndex = new ConcurrentHashMap<>();
    }

    @Override
    public void push(Event event) {
        String path = event.getPath();

        event.withReceivers(pathSubscriptions.get(path));

        events.add(event);
    }

    @Override
    public Event next() {
        return events.poll();
    }

    @Override
    public void subscribe(String subscriber, String path) {
        if(hasAnotherSubscription(subscriber, path)) {
            unsubscribe(subscriber);
        }

        if(!pathSubscriptions.containsKey(path)) {
            pathSubscriptions.put(path, new HashSet<>(Collections.singleton(subscriber)));
        } else {
            Set<String> subscribers = pathSubscriptions.get(path);

            subscribers.add(subscriber);
        }

        subscriberToPathIndex.put(subscriber, path);
    }

    @Override
    public void unsubscribe(String subscriber) {
        String path = subscriberToPathIndex.remove(subscriber);

        if(path != null) {
            pathSubscriptions.get(path).remove(subscriber);
        }
    }

    @Override
    public boolean hasAnotherSubscription(String subscriber, String newPath) {
        String previousPath = subscriberToPathIndex.get(subscriber);

        return previousPath != null && !previousPath.equals(newPath);
    }
}
