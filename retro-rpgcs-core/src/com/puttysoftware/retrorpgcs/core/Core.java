package com.puttysoftware.retrorpgcs.core;

import com.puttysoftware.diane.Diane;
import com.puttysoftware.retrorpgcs.core.security.SandboxManager;

public class Core {
    private static final GameErrorHandler errorHandler = new GameErrorHandler();

    public static void initializeCore() {
        Diane.installErrorHandler(Core.errorHandler);
        System.setSecurityManager(new CoreSecurityManager());
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

    public static String getCachesDirectory() {
        return SandboxManager.getSandboxManager().getCachesDirectory();
    }

    public static String getDocumentsDirectory() {
        return SandboxManager.getSandboxManager().getDocumentsDirectory();
    }

    public static String getSupportDirectory() {
        return SandboxManager.getSandboxManager().getSupportDirectory();
    }
}
