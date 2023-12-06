package com.open.su;


import com.open.su.exceptions.EventServiceException;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.hibernate.reactive.panache.TransactionalUniAsserter;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@QuarkusTest
class EventServiceTest {
    @Inject
    EventService eventService;

    @RunOnVertxContext
    @Test
    void testListEvents(TransactionalUniAsserter asserter) {
        asserter.execute(() -> {
            Event event1 = new Event();
            event1.name = "00testListEvents 1";
            event1.description = "Test description";
            event1.price = 10.0;
            event1.location = "Test location";
            event1.startDate = new Date();
            event1.endDate = new Date();
            event1.organizerId = UUID.randomUUID();
            event1.creatorId = UUID.randomUUID();
            event1.createdAt = new Date();
            event1.updatedAt = new Date();
            Event event2 = new Event();
            event2.name = "00testListEvents 2";
            event2.description = "Test description";
            event2.price = 10.0;
            event2.location = "Test location";
            event2.startDate = new Date();
            event2.endDate = new Date();
            event2.organizerId = UUID.randomUUID();
            event2.creatorId = UUID.randomUUID();
            event2.createdAt = new Date();
            event2.updatedAt = new Date();
            Event event3 = new Event();
            event3.name = "00testListEvents 3";
            event3.description = "Test description";
            event3.price = 10.0;
            event3.location = "Test location";
            event3.startDate = new Date();
            event3.endDate = new Date();
            event3.organizerId = UUID.randomUUID();
            event3.creatorId = UUID.randomUUID();
            event3.createdAt = new Date();
            event3.updatedAt = new Date();

            asserter.putData("events", List.of(event1, event2, event3));

            return event1.persist().chain(event2::persist).chain(event3::persist);
        });

        asserter.assertThat(() -> {
            Page page = Page.of(0, 10);
            Sort sort = Sort.by("name", Sort.Direction.Ascending);

            return eventService.listEvents(page, sort);
        }, response -> {
            List<Event> events = (List<Event>) asserter.getData("events");
            Assertions.assertEquals(events.get(0), response.get(0));
            Assertions.assertEquals(events.get(1), response.get(1));
            Assertions.assertEquals(events.get(2), response.get(2));
        });

        asserter.execute(() -> Event.deleteAll());

        asserter.surroundWith(u -> Panache.withSession(() -> u));
    }

    @RunOnVertxContext
    @Test
    void testGetEventDetails(TransactionalUniAsserter asserter) {
        asserter.execute(() -> {
            Event event = new Event();
            event.name = "testGetEventDetails 1";
            event.description = "Test description";
            event.price = 10.0;
            event.location = "Test location";
            event.startDate = new Date();
            event.endDate = new Date();
            event.organizerId = UUID.randomUUID();
            event.creatorId = UUID.randomUUID();
            event.createdAt = new Date();
            event.updatedAt = new Date();

            asserter.putData("event", event);

            return event.persist();
        });

        asserter.assertThat(() -> {
            Event event = (Event) asserter.getData("event");

            return eventService.getEventDetails(event.id);
        }, response -> {
            Event event = (Event) asserter.getData("event");
            Assertions.assertEquals(event, response);
        });

        asserter.assertFailedWith(() -> eventService.getEventDetails(UUID.randomUUID())
                , e -> Assertions.assertSame(EventServiceException.Type.NOT_FOUND, ((EventServiceException) e).getType()));

        asserter.execute(() -> Event.deleteAll());

        asserter.surroundWith(u -> Panache.withSession(() -> u));
    }

    @RunOnVertxContext
    @Test
    void testCreateEvent(TransactionalUniAsserter asserter) {
        asserter.execute(() -> {
            Event event1 = new Event();
            event1.name = "testCreateEvent 1";
            event1.description = "Test description";
            event1.price = 10.0;
            event1.location = "Test location";
            event1.startDate = new Date();
            event1.endDate = new Date();
            event1.organizerId = UUID.randomUUID();
            event1.creatorId = UUID.randomUUID();
            event1.createdAt = new Date();
            event1.updatedAt = new Date();
            Event event2 = new Event();
            event2.name = "testCreateEvent 2";
            event2.description = "Test description";
            event2.price = 10.0;
            event2.location = "Test location";
            event2.startDate = new Date();
            event2.endDate = new Date();
            event2.organizerId = UUID.randomUUID();
            event2.creatorId = UUID.randomUUID();
            event2.createdAt = new Date();
            event2.updatedAt = new Date();

            asserter.putData("event1", event1);
            asserter.putData("event2", event2);

            return event1.persist();
        });

        asserter.assertThat(() -> {
            Event event = (Event) asserter.getData("event2");

            return eventService.createEvent(event);
        }, response -> {
            Event event = (Event) asserter.getData("event2");
            Assertions.assertEquals(event.id, response);
        });

        asserter.assertThat(() -> {
            Event event = (Event) asserter.getData("event2");

            return Event.findById(event.id);
        }, response -> {
            Event event = (Event) asserter.getData("event2");
            Assertions.assertEquals(event, response);
        });

        asserter.assertFailedWith(() -> {
            Event event = new Event();
            event.name = "testCreateEvent 3";
            event.description = "Test description";
            event.price = -1.0;
            event.location = "Test location";
            event.startDate = new Date();
            event.endDate = new Date();
            event.organizerId = UUID.randomUUID();
            event.creatorId = UUID.randomUUID();
            event.createdAt = new Date();
            event.updatedAt = new Date();

            return eventService.createEvent(event);
        }, e -> Assertions.assertSame(EventServiceException.Type.INVALID_ARGUMENT, ((EventServiceException) e).getType()));

        asserter.assertFailedWith(() -> {
            Event event = new Event();
            event.name = ((Event) asserter.getData("event1")).name;
            event.description = "Test description";
            event.price = 10.0;
            event.location = "Test location";
            event.startDate = new Date();
            event.endDate = new Date();
            event.organizerId = UUID.randomUUID();
            event.creatorId = UUID.randomUUID();
            event.createdAt = new Date();
            event.updatedAt = new Date();

            return eventService.createEvent(event);
        }, e -> Assertions.assertSame(EventServiceException.Type.CONFLICT, ((EventServiceException) e).getType()));

        asserter.execute(() -> Event.deleteAll());

        asserter.surroundWith(u -> Panache.withSession(() -> u));
    }

    @RunOnVertxContext
    @Test
    void testUpdateEvent(TransactionalUniAsserter asserter) {
        asserter.execute(() -> {
            Event event1 = new Event();
            event1.name = "testUpdateEvent 1";
            event1.description = "Test description";
            event1.price = 10.0;
            event1.location = "Test location";
            event1.startDate = new Date();
            event1.endDate = new Date();
            event1.organizerId = UUID.randomUUID();
            event1.creatorId = UUID.randomUUID();
            event1.createdAt = new Date();
            event1.updatedAt = new Date();
            Event event2 = new Event();
            event2.id = UUID.randomUUID();
            event2.name = "testUpdateEvent 2";
            event2.description = "Test description";
            event2.price = 10.0;
            event2.location = "Test location";
            event2.startDate = new Date();
            event2.endDate = new Date();
            event2.organizerId = UUID.randomUUID();
            event2.creatorId = UUID.randomUUID();
            event2.createdAt = new Date();
            event2.updatedAt = new Date();

            asserter.putData("event1", event1);
            asserter.putData("event2", event2);

            return event1.persist();
        });

        asserter.assertThat(() -> {
            Event event1 = (Event) asserter.getData("event1");
            Event event2 = (Event) asserter.getData("event2");
            event1.name = event2.name;

            return eventService.updateEvent(event1);
        }, response -> {
            Event event = (Event) asserter.getData("event1");
            Assertions.assertEquals(event.id, response);
        });

        asserter.assertThat(() -> {
            Event event = (Event) asserter.getData("event1");

            return Event.findById(event.id);
        }, response -> {
            Event event = (Event) asserter.getData("event2");
            Assertions.assertEquals(event.name, ((Event) response).name);
        });

        asserter.assertFailedWith(() -> {
            Event event = new Event();
            event.id = UUID.randomUUID();
            event.name = "testUpdateEvent 3";
            event.description = "Test description";
            event.price = -1.0;
            event.location = "Test location";
            event.startDate = new Date();
            event.endDate = new Date();
            event.organizerId = UUID.randomUUID();
            event.creatorId = UUID.randomUUID();
            event.createdAt = new Date();
            event.updatedAt = new Date();

            return eventService.updateEvent(event);
        }, e -> Assertions.assertSame(EventServiceException.Type.INVALID_ARGUMENT, ((EventServiceException) e).getType()));

        asserter.assertFailedWith(() -> {
            Event event = new Event();
            event.id = UUID.randomUUID();
            event.name = "testUpdateEvent 1";
            event.description = "Test description";
            event.price = 10.0;
            event.location = "Test location";
            event.startDate = new Date();
            event.endDate = new Date();
            event.organizerId = UUID.randomUUID();
            event.creatorId = UUID.randomUUID();
            event.createdAt = new Date();
            event.updatedAt = new Date();

            return eventService.updateEvent(event);
        }, e -> Assertions.assertSame(EventServiceException.Type.NOT_FOUND, ((EventServiceException) e).getType()));

        asserter.assertFailedWith(() -> {
            Event event = new Event();
            event.id = UUID.randomUUID();
            event.name = ((Event) asserter.getData("event2")).name;
            event.description = "Test description";
            event.price = 10.0;
            event.location = "Test location";
            event.startDate = new Date();
            event.endDate = new Date();
            event.organizerId = UUID.randomUUID();
            event.creatorId = UUID.randomUUID();
            event.createdAt = new Date();
            event.updatedAt = new Date();

            return eventService.updateEvent(event);
        }, e -> Assertions.assertSame(EventServiceException.Type.CONFLICT, ((EventServiceException) e).getType()));

        asserter.execute(() -> Event.deleteAll());

        asserter.surroundWith(u -> Panache.withSession(() -> u));
    }

    @RunOnVertxContext
    @Test
    void testDeleteEvent(TransactionalUniAsserter asserter) {
        asserter.execute(() -> {
            Event event = new Event();
            event.name = "testDeleteEvent 1";
            event.description = "Test description";
            event.price = 10.0;
            event.location = "Test location";
            event.startDate = new Date();
            event.endDate = new Date();
            event.organizerId = UUID.randomUUID();
            event.creatorId = UUID.randomUUID();
            event.createdAt = new Date();
            event.updatedAt = new Date();

            asserter.putData("event", event);

            return event.persist();
        });

        asserter.assertFailedWith(() -> eventService.deleteEvent(UUID.randomUUID())
                , e -> Assertions.assertSame(EventServiceException.Type.NOT_FOUND, ((EventServiceException) e).getType()));

        // Assert that calling delete does not fail (delete returns void if not failing)
        asserter.assertThat(() -> {
            Event event = (Event) asserter.getData("event");

            return eventService.deleteEvent(event.id);
        }, Assertions::assertNull);

        asserter.assertFailedWith(() -> eventService.deleteEvent(((Event) asserter.getData("event")).id)
                , e -> Assertions.assertSame(EventServiceException.Type.NOT_FOUND, ((EventServiceException) e).getType()));

        asserter.execute(() -> Event.deleteAll());

        asserter.surroundWith(u -> Panache.withSession(() -> u));
    }
}
