package com.netcracker.edu.logic;

import com.netcracker.edu.logic.event.Event;
import com.netcracker.edu.logic.event.EventRouter;
import com.netcracker.edu.logic.event.EventRouterImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class EventServiceImplTest {

    private EventService eventService;
    private EventRouter eventRouter;

    @Before
    public void beforeTest() {
        this.eventRouter = new EventRouterImpl();
        this.eventService = new EventServiceImpl(eventRouter);
    }

    @Test
    public void Should_ReadEvent_When_PushedEvent() {
        eventRouter.subscribe("Maxim", "/projectName/readme.txt");
        eventRouter.push(new Event("TEXT", "/projectName/readme.txt"));

        Event event = eventService.next();
        String actualContent = event.getContent();

        assertEquals("TEXT", actualContent);
    }

    @Test
    public void Should_ReturnNull_When_WasNotPushedEvent() {
        Event event = eventService.next();

        assertNull(event);
    }

    @Test
    public void Should_Unsubscribe_When_Subscribe() {
        eventRouter.subscribe("Maxim", "/projectName/readme.txt");

        eventService.onDisconnect("Maxim");

        assertFalse(eventRouter.hasAnotherSubscription("Maxim", "/projectName"));
    }

    @Test
    public void Should_ReturnFalse_When_notSubscribed() {
        eventService.onDisconnect("Maxim");

        assertFalse(eventRouter.hasAnotherSubscription("Maxim", "/projectName"));
    }
}
