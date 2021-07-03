package com.puttysoftware.retrorpgcs;

import com.puttysoftware.retrorpgcs.security.SandboxManager;

final class GameSecurityManager extends SecurityManager {
    // Fields
    private final SandboxManager sandbox;

    // Constructor
    public GameSecurityManager() {
        super();
        this.sandbox = SandboxManager.getSandboxManager();
    }

    @Override
    public void checkRead(String file) {
        if (!this.sandbox.checkPath(file)) {
            super.checkRead(file);
        }
    }

    @Override
    public void checkRead(String file, Object context) {
        if (!this.sandbox.checkPath(file)) {
            super.checkRead(file, context);
        }
    }

    @Override
    public void checkWrite(String file) {
        if (!this.sandbox.checkPath(file)) {
            super.checkWrite(file);
        }
    }

    @Override
    public void checkDelete(String file) {
        if (!this.sandbox.checkPath(file)) {
            super.checkDelete(file);
        }
    }
}
