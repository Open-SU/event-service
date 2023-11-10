package com.open.su.exceptions;

import com.open.su.EventService;
import io.grpc.Status;

/**
 * Exception thrown by the event service.
 *
 * @see EventService
 */
public class EventServiceException extends RuntimeException {

    /**
     * Predefined exception for database errors.
     */
    public static final EventServiceException DATABASE_ERROR = new EventServiceException(Type.DATABASE_ERROR, "Database error");

    /**
     * Predefined exception for not found errors.
     */
    public static final EventServiceException NOT_FOUND = new EventServiceException(Type.NOT_FOUND, "Not found");

    /**
     * Predefined exception for conflict errors.
     */
    public static final EventServiceException CONFLICT = new EventServiceException(Type.CONFLICT, "Conflict");

    /**
     * Predefined exception for invalid argument errors.
     */
    public static final EventServiceException INVALID_ARGUMENT = new EventServiceException(Type.INVALID_ARGUMENT, "Invalid argument");

    final Type type;

    EventServiceException(Type type, String message) {
        super(message);
        this.type = type;
    }

    EventServiceException(Type type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public EventServiceException withMessage(String message) {
        return new EventServiceException(type, message, this);
    }

    public EventServiceException withCause(Throwable cause) {
        return new EventServiceException(type, getMessage(), cause);
    }

    /**
     * Converts this exception to a {@link RuntimeException} that is gRPC suitable.
     *
     * @return the gRPC suitable exception
     */
    public RuntimeException toGrpcException() {
        return switch (type) {
            case DATABASE_ERROR ->
                    Status.INTERNAL.withDescription(getMessage()).withCause(getCause()).asRuntimeException();
            case NOT_FOUND -> Status.NOT_FOUND.withDescription(getMessage()).withCause(getCause()).asRuntimeException();
            case CONFLICT ->
                    Status.ALREADY_EXISTS.withDescription(getMessage()).withCause(getCause()).asRuntimeException();
            case INVALID_ARGUMENT ->
                    Status.INVALID_ARGUMENT.withDescription(getMessage()).withCause(getCause()).asRuntimeException();
        };
    }

    /**
     * Types of possible {@link EventServiceException}.
     */
    public enum Type {
        DATABASE_ERROR,
        NOT_FOUND,
        CONFLICT,
        INVALID_ARGUMENT
    }
}
