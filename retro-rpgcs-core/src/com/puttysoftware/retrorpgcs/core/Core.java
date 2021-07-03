package com.puttysoftware.retrorpgcs.core;

import com.puttysoftware.gameshell.ExceptionMessageConfiguration;
import com.puttysoftware.gameshell.GameShell;
import com.puttysoftware.retrorpgcs.core.security.SandboxManager;

public abstract class Core extends GameShell {
    public Core(String name, ExceptionMessageConfiguration errorSettings,
            ExceptionMessageConfiguration warningSettings) {
        super(name, errorSettings, warningSettings);
        System.setSecurityManager(new CoreSecurityManager());
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
