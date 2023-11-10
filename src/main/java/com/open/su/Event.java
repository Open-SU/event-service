package com.open.su;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * Event stores all the information about an event in the system.
 *
 * @see EventService
 * @see EventController
 */
@Entity
public class Event extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @Column(length = 100, unique = true, nullable = false)
    String name;
    @Column(nullable = false)
    String description = "";
    @Min(0)
    @Column(nullable = false)
    Double price;
    @Column(length = 100, nullable = false)
    String location = "";
    @Column(name = "start_date", nullable = false)
    Date startDate;
    @Column(name = "end_date", nullable = false)
    Date endDate;
    @Column(name = "organizer_id", nullable = false)
    UUID organizerId;
    @Column(name = "creator_id", nullable = false)
    UUID creatorId = UUID.randomUUID();
    @Column(name = "created_at")
    @CreationTimestamp
    Date createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    Date updatedAt;

    /**
     * Create a new event from a {@link CreateEventRequest}
     *
     * @param request the grpc request
     */
    public Event(CreateEventRequest request) {
        this.name = request.getName();
        this.description = request.hasDescription() ? request.getDescription() : "";
        this.price = request.getPrice();
        this.location = request.hasLocation() ? request.getLocation() : "";
        this.startDate = Date.from(Instant.parse(request.getStartDate()));
        this.endDate = Date.from(Instant.parse(request.getEndDate()));
        this.organizerId = UUID.fromString(request.getOrganizerId());
    }

    /**
     * Create a new event from a {@link UpdateEventRequest}
     *
     * @param request the grpc request
     */
    public Event(UpdateEventRequest request) {
        this.id = UUID.fromString(request.getId());
        this.name = request.hasName() ? request.getName() : null;
        this.description = request.hasDescription() ? request.getDescription() : null;
        this.price = request.hasPrice() ? request.getPrice() : null;
        this.location = request.hasLocation() ? request.getLocation() : null;
        this.startDate = request.hasStartDate() ? Date.from(Instant.parse(request.getStartDate())) : null;
        this.endDate = request.hasEndDate() ? Date.from(Instant.parse(request.getEndDate())) : null;
        this.organizerId = request.hasOrganizerId() ? UUID.fromString(request.getOrganizerId()) : null;
    }

    public Event() {

    }

    /**
     * Update the event with the information from an {@link Event} with possible null values
     *
     * @param event the event to update from
     * @return the updated event
     */
    public Event update(Event event) {
        Optional.ofNullable(event.name).ifPresent(n -> this.name = n);
        Optional.ofNullable(event.description).ifPresent(d -> this.description = d);
        Optional.ofNullable(event.price).ifPresent(p -> this.price = p);
        Optional.ofNullable(event.location).ifPresent(l -> this.location = l);
        Optional.ofNullable(event.startDate).ifPresent(sd -> this.startDate = sd);
        Optional.ofNullable(event.endDate).ifPresent(ed -> this.endDate = ed);
        Optional.ofNullable(event.organizerId).ifPresent(oi -> this.organizerId = oi);

        return this;
    }

    /**
     * Convert the event to a {@link ListEventsResponse}
     *
     * @return the grpc response
     */
    public ListEventsResponse toListEventsResponse() {
        return ListEventsResponse.newBuilder()
                .setId(this.id.toString())
                .setName(this.name)
                .setDescription(this.description)
                .setPrice(this.price)
                .setLocation(this.location)
                .setStartDate(this.startDate.toInstant().toString())
                .setEndDate(this.endDate.toInstant().toString())
                .build();
    }

    /**
     * Convert the event to a {@link GetEventDetailsResponse}
     *
     * @return the grpc response
     */
    public GetEventDetailsResponse toGetEventDetailsResponse() {
        return GetEventDetailsResponse.newBuilder()
                .setId(this.id.toString())
                .setName(this.name)
                .setDescription(this.description)
                .setPrice(this.price)
                .setLocation(this.location)
                .setStartDate(this.startDate.toInstant().toString())
                .setEndDate(this.endDate.toInstant().toString())
                .setOrganizerId(this.organizerId.toString())
                .setCreatorId(this.creatorId.toString())
                .setCreatedAt(this.createdAt.toInstant().toString())
                .setUpdatedAt(this.updatedAt.toInstant().toString())
                .build();
    }
}
