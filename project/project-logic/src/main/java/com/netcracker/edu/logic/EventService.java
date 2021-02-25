package com.netcracker.edu.logic;

import com.netcracker.edu.logic.event.Event;

public interface EventService {

    Event next();

    void onDisconnect(String username);
}
