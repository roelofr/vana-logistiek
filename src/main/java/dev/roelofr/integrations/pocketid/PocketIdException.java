package dev.roelofr.integrations.pocketid;

import io.vertx.ext.web.handler.HttpException;

public class PocketIdException extends Throwable {
    public PocketIdException(String message, HttpException previous) {
        super(message, previous);
    }
}
