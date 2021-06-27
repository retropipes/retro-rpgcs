package com.puttysoftware.retrorpgcs.core;

import com.puttysoftware.diane.Diane;

public class Core {
    private static final GameErrorHandler errorHandler = new GameErrorHandler();

    public static void installErrorHandler() {
        Diane.installErrorHandler(Core.errorHandler);
    }

    public static void exception(final Throwable t) {
        Core.errorHandler.handle(t);
    }

    public static void silentlyLog(final Throwable t) {
        final RuntimeException re = new RuntimeException(t);
        Core.errorHandler.silentlyLog(re);
    }

    public static void exceptionWithMessage(final Throwable t,
            final String message) {
        Core.errorHandler.handleWithMessage(t, message);
    }
}
