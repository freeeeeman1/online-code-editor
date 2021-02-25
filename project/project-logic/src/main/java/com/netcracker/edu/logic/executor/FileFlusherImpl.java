package com.netcracker.edu.logic.executor;

import com.netcracker.edu.logic.event.Event;
import com.netcracker.edu.logic.exception.FlusherException;
import com.netcracker.edu.project.repository.FileRepository;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileFlusherImpl implements FileFlusher {

    private final FileRepository fileRepository;
    private final BlockingQueue<Event> events;

    public FileFlusherImpl(FileRepository fileRepository, BlockingQueue<Event> events) {
        this.events = events;
        this.fileRepository = fileRepository;
    }

    @Override
    public void start() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            while (events.size() > 0) {
                Event event = events.poll();
                fileRepository.updateContentFile(event.getPath(), event.getContent());
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void addEvent(Event event) throws FlusherException {
        String path = event.getPath();
        events.removeIf((existsEvent) -> existsEvent.getPath().equals(path));

        try {
            events.put(event);
        } catch (InterruptedException exception) {
            throw new FlusherException(path, " was not added to save in database");
        }
    }
}
