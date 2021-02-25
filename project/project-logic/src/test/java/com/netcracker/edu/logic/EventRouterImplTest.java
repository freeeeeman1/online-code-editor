package com.netcracker.edu.logic;

import com.netcracker.edu.logic.event.Event;
import com.netcracker.edu.logic.event.EventRouterImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventRouterImplTest {

    private EventRouterImpl eventRouter;

    @Before
    public void beforeTest() {
        this.eventRouter = new EventRouterImpl();
    }

    @Test
    public void addToQueue() {
        eventRouter.subscribe("Max", "a/a.txt");

        eventRouter.push(new Event("TEXT1", "a/a.txt"));
        Event actual = eventRouter.next();

        assertEquals("TEXT1", actual.getContent());
    }

    @Test
    public void hasAnotherSubscriptionFirst() {
        eventRouter.subscribe("Max", "a/a.txt");

        boolean answer = eventRouter.hasAnotherSubscription("Max", "a/a.txt");

        assertFalse(answer);
    }

    @Test
    public void hasAnotherSubscriptionSecond() {
        eventRouter.subscribe("Max", "a/b.txt");

        boolean answer = eventRouter.hasAnotherSubscription("Max", "a/a.txt");

        assertTrue(answer);
    }

    @Test
    public void subscribe() {
        eventRouter.subscribe("Max", "a/b.txt");

        boolean answer = eventRouter.hasAnotherSubscription("Max", "a/a.txt");

        assertTrue(answer);
    }

    @Test
    public void unsubscribe() {
        eventRouter.subscribe("Max", "a/a.txt");
        eventRouter.unsubscribe("Max");

        boolean answer = eventRouter.hasAnotherSubscription("Max", "a/a.txt");

        assertFalse(answer);
    }
}
