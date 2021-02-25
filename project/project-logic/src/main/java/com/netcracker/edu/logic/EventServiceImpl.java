package com.netcracker.edu.logic;

import com.netcracker.edu.logic.event.Event;
import com.netcracker.edu.logic.event.EventRouter;

public class EventServiceImpl implements EventService {

    private final EventRouter eventRouter;

    public EventServiceImpl(EventRouter eventRouter) {
        this.eventRouter = eventRouter;
    }

    @Override
    public Event next() {
        return eventRouter.next();
    }

    @Override
    public void onDisconnect(String username) {
        eventRouter.unsubscribe(username);
    }
}
