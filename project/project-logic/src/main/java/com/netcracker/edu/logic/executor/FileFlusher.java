package com.netcracker.edu.logic.executor;

import com.netcracker.edu.logic.event.Event;
import com.netcracker.edu.logic.exception.FlusherException;

public interface FileFlusher {

    /**
     @throws IllegalStateException when Executor not started
     */
    void start();

    void addEvent(Event event) throws FlusherException;
}
