package com.puttysoftware.retrorpgcs;

import com.puttysoftware.diane.ErrorHandler;
import com.puttysoftware.diane.ErrorLogger;
import com.puttysoftware.diane.gui.CommonDialogs;

final class GameErrorHandler implements ErrorHandler {
    private static final String LOG_NAME = "RetroRPGCS.Core";
    private static final String ERROR_MESSAGE = "An unrecoverable error has occurred.\n"
            + "The details have been recorded.\n"
            + "The program will now exit.";
    private static final String ERROR_TITLE = "FATAL ERROR";
    private static final String WARNING_MESSAGE = "Something has gone wrong.\n"
            + "The details have been recorded.";
    private static final String WARNING_TITLE = "Warning";
    private final ErrorLogger logger;

    GameErrorHandler() {
        this.logger = new ErrorLogger(GameErrorHandler.LOG_NAME);
    }

    @Override
    public void uncaughtException(final Thread inT, final Throwable inE) {
        this.handle(inE);
    }

    void handle(final Throwable inE) {
        if (inE instanceof RuntimeException) {
            try {
                this.logWarning(inE);
            } catch (final Throwable inE2) {
                inE.addSuppressed(inE2);
                this.logWarningDirectly(inE);
            }
        } else {
            try {
                this.logError(inE);
            } catch (final Throwable inE2) {
                inE.addSuppressed(inE2);
                this.logErrorDirectly(inE);
            }
        }
    }

    void silentlyLog(final Throwable inE) {
        this.logger.logWarning(inE);
    }

    private void logError(final Throwable t) {
        CommonDialogs.showErrorDialog(GameErrorHandler.ERROR_MESSAGE,
                GameErrorHandler.ERROR_TITLE);
        this.logger.logError(t);
    }

    private void logErrorDirectly(final Throwable t) {
        this.logger.logError(t);
    }

    private void logWarning(final Throwable t) {
        CommonDialogs.showErrorDialog(GameErrorHandler.WARNING_MESSAGE,
                GameErrorHandler.WARNING_TITLE);
        this.logger.logWarning(t);
    }

    private void logWarningDirectly(final Throwable t) {
        this.logger.logWarning(t);
    }

    void handleWithMessage(final Throwable inE, final String msg) {
        if (inE instanceof RuntimeException) {
            try {
                this.logWarningWithMessage(inE, msg);
            } catch (final Throwable inE2) {
                inE.addSuppressed(inE2);
                this.logWarningDirectly(inE);
            }
        } else {
            try {
                this.logErrorWithMessage(inE, msg);
            } catch (final Throwable inE2) {
                inE.addSuppressed(inE2);
                this.logErrorDirectly(inE);
            }
        }
    }

    private void logErrorWithMessage(final Throwable t, final String msg) {
        CommonDialogs.showErrorDialog(msg, GameErrorHandler.ERROR_TITLE);
        this.logger.logError(t);
    }

    private void logWarningWithMessage(final Throwable t, final String msg) {
        CommonDialogs.showErrorDialog(msg, GameErrorHandler.WARNING_TITLE);
        this.logger.logWarning(t);
    }
}
