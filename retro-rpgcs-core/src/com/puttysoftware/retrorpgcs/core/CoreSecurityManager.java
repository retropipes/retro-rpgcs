package com.puttysoftware.retrorpgcs.core;

import com.puttysoftware.retrorpgcs.core.security.SandboxManager;

class CoreSecurityManager extends SecurityManager {
    // Fields
    private final SandboxManager sandbox;

    // Constructor
    public CoreSecurityManager() {
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
