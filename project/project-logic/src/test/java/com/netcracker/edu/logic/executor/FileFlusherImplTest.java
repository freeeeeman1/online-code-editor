package com.netcracker.edu.logic.executor;

import com.netcracker.edu.logic.event.Event;
import com.netcracker.edu.logic.exception.FlusherException;
import com.netcracker.edu.project.repository.FileRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class FileFlusherImplTest {
    private FileFlusher fileFlusher;
    private BlockingQueue<Event> events;
    private FutureTask<Boolean> futureTask;

    @Before
    public void beforeTest() {
        FileRepository fileRepository = mock(FileRepository.class);
        this.events = new LinkedBlockingQueue<>(3);

        this.fileFlusher = new FileFlusherImpl(fileRepository, events);

        this.futureTask = new FutureTask<>(()-> {
            fileFlusher.start();
            while(true) {
                Thread.sleep(40);
                if (events.size() == 0) {
                    return true;
                }
            }
        });
    }

    @Test
    public void Should_BeEmpty_When_ReadAllEvents() throws FlusherException, InterruptedException, ExecutionException {
        fileFlusher.addEvent(new Event("TEXT", "a/b.txt"));
        fileFlusher.addEvent(new Event("TEXT2", "a/c.txt"));
        fileFlusher.addEvent(new Event("TEXT3", "a/d.txt"));

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(futureTask);

        assertTrue(futureTask.get());
    }
}
