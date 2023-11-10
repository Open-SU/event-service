package com.open.su;

import com.open.su.exceptions.EventServiceException;
import io.grpc.Status;
import io.quarkus.grpc.GrpcService;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.UUID;

/**
 * Controller that exposes the gRPC endpoints to manage events.
 *
 * @see EventGrpc
 * @see EventService
 */
@GrpcService
public class EventController implements EventGrpc {

    private static final Logger LOGGER = Logger.getLogger(EventController.class);

    private final EventService eventService;

    @Inject
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Get a paginated list of events with minimal information.
     *
     * @param request the gRPC request
     * @return the list events response
     */
    @Override
    public Multi<ListEventsResponse> listEvents(ListEventsRequest request) {
        LOGGER.info(request.getSize());
        Page page = Page.of(request.hasPage() ? request.getPage() : 0, request.hasSize() ? request.getSize() : 10);
        Sort sort = Sort.by(request.hasSort() ? request.getSort() : "name", request.hasOrder() ? Sort.Direction.valueOf(request.getOrder()) : Sort.Direction.Ascending);

        return eventService.listEvents(page, sort)
                .onFailure().transform(t -> {
                    if (t instanceof EventServiceException serviceException) {
                        return (serviceException.toGrpcException());
                    }
                    String message = "Unhandled error while listing events";
                    LOGGER.error(message, t);
                    return Status.UNKNOWN.withCause(t).withDescription(message).asRuntimeException();
                })
                .onItem().transformToMulti(events -> Multi.createFrom().iterable(events))
                .map(Event::toListEventsResponse);
    }

    /**
     * Get an event by its ID.
     *
     * @param request the gRPC request
     * @return the get event details response
     */
    @Override
    public Uni<GetEventDetailsResponse> getEventDetails(GetEventDetailsRequest request) {
        return eventService.getEventDetails(UUID.fromString(request.getId()))
                .onFailure().transform(t -> {
                    if (t instanceof EventServiceException serviceException) {
                        return (serviceException.toGrpcException());
                    }
                    String message = "Unhandled error while getting event details";
                    LOGGER.error(message, t);
                    return Status.UNKNOWN.withCause(t).withDescription(message).asRuntimeException();
                })
                .onItem().transform(Event::toGetEventDetailsResponse);
    }

    /**
     * Create a new event.
     *
     * @param request the gRPC request
     * @return the create event response
     */
    @Override
    public Uni<CreateEventResponse> createEvent(CreateEventRequest request) {
        return eventService.createEvent(new Event(request))
                .onFailure().transform(t -> {
                    if (t instanceof EventServiceException serviceException) {
                        return (serviceException.toGrpcException());
                    }
                    String message = "Unhandled error while creating event";
                    LOGGER.error(message, t);
                    return Status.UNKNOWN.withCause(t).withDescription(message).asRuntimeException();
                })
                .onItem().transform(id -> CreateEventResponse.newBuilder().setId(id.toString()).build());
    }

    /**
     * Update an existing event.
     *
     * @param request the gRPC request
     * @return the update event response
     */
    @Override
    public Uni<UpdateEventResponse> updateEvent(UpdateEventRequest request) {
        return eventService.updateEvent(new Event(request))
                .onFailure().transform(t -> {
                    if (t instanceof EventServiceException serviceException) {
                        return (serviceException.toGrpcException());
                    }
                    String message = "Unhandled error while updating event";
                    LOGGER.error(message, t);
                    return Status.UNKNOWN.withCause(t).withDescription(message).asRuntimeException();
                })
                .onItem().transform(id -> UpdateEventResponse.newBuilder().setId(id.toString()).build());
    }

    /**
     * Delete an existing event.
     *
     * @param request the gRPC request
     * @return the delete event response
     */
    @Override
    public Uni<DeleteEventResponse> deleteEvent(DeleteEventRequest request) {
        return eventService.deleteEvent(UUID.fromString(request.getId()))
                .onFailure().transform(t -> {
                    if (t instanceof EventServiceException serviceException) {
                        return (serviceException.toGrpcException());
                    }
                    String message = "Unhandled error while deleting event";
                    LOGGER.error(message, t);
                    return Status.UNKNOWN.withCause(t).withDescription(message).asRuntimeException();
                })
                .onItem().transform(id -> DeleteEventResponse.newBuilder().build());
    }
}
