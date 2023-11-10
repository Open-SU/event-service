package com.open.su;

import com.open.su.exceptions.EventServiceException;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing events
 *
 * @see Event
 * @see EventServiceException
 */
@ApplicationScoped
@WithTransaction
public class EventService {

    private static final Logger LOGGER = Logger.getLogger(EventService.class);

    /**
     * List events with pagination and sorting
     *
     * @param page page number and size
     * @param sort sort by field and direction
     * @return a {@link Uni} with the list of events (with minimal information)
     */
    public Uni<List<Event>> listEvents(Page page, Sort sort) {
        LOGGER.trace("Listing events with page " + page + " and sort " + sort);
        return Event.<Event>findAll(sort).page(page).list()
                .onFailure().transform(t -> {
                    String message = "Failed to list events";
                    LOGGER.error("[" + Method.LIST + "] " + message, t);
                    return EventServiceException.DATABASE_ERROR.withCause(t).withMessage(message);
                });
    }

    /**
     * Get event details
     *
     * @param id the id of the event
     * @return a {@link Uni} with the event details
     */
    public Uni<Event> getEventDetails(UUID id) {
        LOGGER.trace("Getting event details for event with id " + id);
        return findEventOrFail(id, Method.DETAILS);
    }

    /**
     * Create an event
     *
     * @param event the event to create
     * @return a {@link Uni} with the id of the created event
     */
    public Uni<UUID> createEvent(Event event) {
        LOGGER.trace("Creating event " + event);
        if (event.price <= 0) {
            return Uni.createFrom().failure(EventServiceException.INVALID_ARGUMENT.withMessage("Price must be greater than 0"));
        }

        // Make sure the id is null to avoid problems
        event.id = null;

        return checkNameConflict(event, Method.CREATE)
                .onItem().transformToUni(e -> persistEventOrFail(event, Method.CREATE))
                .onItem().transform(e -> e == null ? null : e.id);
    }

    /**
     * Update an event
     *
     * @param event the event to update
     * @return a {@link Uni} with the id of the updated event
     */
    public Uni<UUID> updateEvent(Event event) {
        LOGGER.trace("Updating event " + event);
        if (event.price != null && event.price <= 0) {
            return Uni.createFrom().failure(EventServiceException.INVALID_ARGUMENT.withMessage("Price must be greater than 0"));
        }

        return checkNameConflict(event, Method.UPDATE)
                .onItem().transformToUni(e -> findEventOrFail(event.id, Method.UPDATE)
                        .onItem().transformToUni(existingEvent -> persistEventOrFail(existingEvent.update(event), Method.UPDATE)))
                .onItem().transform(e -> e == null ? null : e.id);
    }

    /**
     * Delete an event
     *
     * @param id the id of the event to delete
     * @return a {@link Uni} of Void
     */
    public Uni<Void> deleteEvent(UUID id) {
        LOGGER.trace("Deleting event with id " + id);
        return findEventOrFail(id, Method.DELETE)
                .onItem().transformToUni(existingEvent ->
                        existingEvent.delete()
                                .onFailure().transform(t -> {
                                    String message = "Failed to delete event with id " + id;
                                    LOGGER.error("[" + Method.DELETE + "] " + message, t);
                                    return EventServiceException.DATABASE_ERROR.withCause(t).withMessage(message);
                                })
                                .onItem().invoke(() -> LOGGER.debug("[" + Method.DELETE + "] " + "Deleted event with id " + id)));
    }

    /**
     * Check if an event with the same name already exists
     *
     * @param event  the event to check
     * @param method the context in which the check is performed (for logging purposes)
     * @return a failed {@link Uni} if an event with the same name already exists, otherwise a {@link Uni} with null item
     */
    Uni<Event> checkNameConflict(Event event, Method method) {
        return Event.<Event>find("name=?1", event.name).firstResult()
                .onFailure().transform(t -> {
                    String message = "Failed to get event with name " + event.name;
                    LOGGER.error("[" + method + "] " + message, t);
                    return EventServiceException.DATABASE_ERROR.withCause(t).withMessage(message);
                })
                .onItem().ifNotNull().transformToUni(existingEvent -> {
                    if (!existingEvent.id.equals(event.id)) {
                        String message = "Event with name " + event.name + " already exists";
                        LOGGER.debug("[" + method + "] " + message);
                        return Uni.createFrom().failure(EventServiceException.CONFLICT.withMessage(message));
                    }
                    return Uni.createFrom().nullItem();
                });
    }

    /**
     * Find an event by id or fail
     *
     * @param id     the id of the event
     * @param method the context in which the find is performed (for logging purposes)
     * @return a {@link Uni} with the event, otherwise a failed {@link Uni}
     */
    Uni<Event> findEventOrFail(UUID id, Method method) {
        return Event.<Event>findById(id)
                .onFailure().transform(t -> {
                    String message = "Failed to get event with id " + id;
                    LOGGER.error("[" + method + "] " + message, t);
                    return EventServiceException.DATABASE_ERROR.withCause(t).withMessage(message);
                })
                .onItem().ifNull().failWith(() -> {
                    String message = "Event with id " + id + " does not exist";
                    LOGGER.debug("[" + method + "] " + message);
                    return EventServiceException.NOT_FOUND.withMessage(message);
                });
    }

    /**
     * Persist an event or fail
     *
     * @param event  the event to persist
     * @param method the context in which the persist is performed (for logging purposes)
     * @return a {@link Uni} with the persisted event, otherwise a failed {@link Uni}
     */
    Uni<Event> persistEventOrFail(Event event, Method method) {
        return event.<Event>persist()
                .onFailure().transform(t -> {
                    String message = "Failed to persist event with name " + event.name;
                    LOGGER.error("[" + method + "] " + message, t);
                    return EventServiceException.DATABASE_ERROR.withCause(t).withMessage(message);
                })
                .onItem().ifNotNull().invoke(existingEvent -> LOGGER.debug("[" + method + "] Persisted event with id " + existingEvent.id));
    }

    /**
     * Lis of methods for logging purposes
     */
    enum Method {
        LIST,
        DETAILS,
        CREATE,
        UPDATE,
        DELETE,
    }
}
