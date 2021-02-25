package com.netcracker.edu.logic.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class Event {

    private final String content;
    private final String path;
    private final Collection<String> receivers;

    public Event(String content, String path) {
        this.content = content;
        this.path = path;
        this.receivers = new ArrayList<>();
    }

    public String getContent() {
        return content;
    }

    public String getPath() {
        return path;
    }

    public Collection<String> getReceivers() {
        return receivers;
    }

    public void withReceivers(Collection<String> subscribers) {
        if(subscribers != null) {
            receivers.addAll(subscribers);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return content.equals(event.content) && path.equals(event.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, path);
    }
}
