package com.netcracker.edu.logic.event;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

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
    public void Should_ReadEvent_When_PushedEvent() {
        eventRouter.subscribe("Max", "a/a.txt");

        eventRouter.push(new Event("TEXT1", "a/a.txt"));
        Event actual = eventRouter.next();

        assertEquals("TEXT1", actual.getContent());
    }

    @Test
    public void Should_ReturnTrue_When_Subscribed() {
        eventRouter.subscribe("Max", "a/b.txt");

        boolean answer = eventRouter.hasAnotherSubscription("Max", "a/a.txt");

        assertTrue(answer);
    }

    @Test
    public void Should_GetAllEventSubscribers_When_SubscribedSeveralUsers() {
        eventRouter.subscribe("Max", "a/b.txt");
        eventRouter.subscribe("Kate", "a/b.txt");
        eventRouter.subscribe("Dan", "a/b.txt");
        eventRouter.push(new Event("TEXT", "a/b.txt"));

        Event event = eventRouter.next();
        Collection<String> receivers = event.getReceivers();

        assertEquals(3, receivers.size());
    }

    @Test
    public void Should_ReturnTrue_When_UserAlreadySubscribed() {
        eventRouter.subscribe("Max", "a/b.txt");
        eventRouter.subscribe("Max", "a/b.txt");

        boolean answer = eventRouter.hasAnotherSubscription("Max", "a/a.txt");

        assertTrue(answer);
    }

    @Test
    public void Should_BeUnsubscribed_When_Unsubscribe() {
        eventRouter.subscribe("Max", "a/a.txt");
        eventRouter.unsubscribe("Max");

        boolean answer = eventRouter.hasAnotherSubscription("Max", "a/a.txt");

        assertFalse(answer);
    }

    @Test
    public void Should_BeUnsubscribed_When_UnsubscribeNotExistentUser() {
        eventRouter.unsubscribe("Max");

        boolean answer = eventRouter.hasAnotherSubscription("Max", "a/b.txt");

        assertFalse(answer);
    }

    @Test
    public void Should_ReturnHasAnotherSubscription_When_NotSubscribedToAnother() {
        eventRouter.subscribe("Max", "a/a.txt");

        boolean answer = eventRouter.hasAnotherSubscription("Max", "a/a.txt");

        assertFalse(answer);
    }

    @Test
    public void Should_ReturnHasAnotherSubscription_When_SubscribedToAnother() {
        eventRouter.subscribe("Max", "a/b.txt");

        boolean answer = eventRouter.hasAnotherSubscription("Max", "a/a.txt");

        assertTrue(answer);
    }
}
