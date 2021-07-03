package com.puttysoftware.retrorpgcs.security;

import java.io.File;

public abstract class SandboxManager {
    // Static methods
    public static SandboxManager getSandboxManager() {
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            return new SandboxManagerOSX();
        } else if (System.getProperty("os.name").startsWith("Windows")) {
            return new SandboxManagerWin();
        } else {
            return new SandboxManagerAny();
        }
    }

    // Constructor
    protected SandboxManager() {
        new File(this.getDocumentsDirectory()).mkdirs();
        new File(this.getCachesDirectory()).mkdirs();
        new File(this.getSupportDirectory()).mkdirs();
    }

    public final boolean checkPath(final String path) {
        return path != null && (path.startsWith(this.getCachesDirectory())
                || path.startsWith(this.getDocumentsDirectory())
                || path.startsWith(this.getSupportDirectory()));
    }

    public abstract String getCachesDirectory();

    // Methods
    public abstract String getDocumentsDirectory();

    public abstract String getSupportDirectory();
}
