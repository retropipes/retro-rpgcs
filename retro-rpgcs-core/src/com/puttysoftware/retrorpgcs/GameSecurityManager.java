package com.puttysoftware.retrorpgcs;

import java.io.FilePermission;
import java.net.InetAddress;
import java.security.Permission;

import com.puttysoftware.retrorpgcs.security.SandboxManager;

final class GameSecurityManager extends SecurityManager {
    // Fields
    private final SandboxManager sandbox;

    // Constructor
    public GameSecurityManager() {
        this.sandbox = SandboxManager.getSandboxManager();
    }

    @Override
    public void checkAccept(final String host, final int port) {
        // Always allowed
    }

    @Override
    public void checkConnect(final String host, final int port) {
        // Always allowed
    }

    @Override
    public void checkConnect(final String host, final int port,
            final Object context) {
        // Always allowed
    }

    @Override
    public void checkCreateClassLoader() {
        // Always allowed
    }

    @Override
    public void checkDelete(final String file) {
        if (!this.sandbox.checkPath(file)) {
            // Deny
            super.checkDelete(file);
        }
        // Allow
    }

    @Override
    public void checkExec(final String cmd) {
        // Always allowed
    }

    @Override
    public void checkExit(final int status) {
        // Always allowed
    }

    @Override
    public void checkLink(final String lib) {
        // Always allowed
    }

    @Override
    public void checkListen(final int port) {
        // Always allowed
    }

    @Override
    public void checkMulticast(final InetAddress maddr) {
        // Always allowed
    }

    @Override
    public void checkPermission(final Permission perm) {
        if (perm instanceof FilePermission) {
            if (perm.getActions().contains("read")
                    || perm.getActions().contains("write")
                    || perm.getActions().contains("delete")) {
                if (!perm.getName().startsWith("/Users/ericahnell/Repos")
                        && !perm.getName()
                                .startsWith("/Library/Java/JavaVirtualMachines")
                        && !this.sandbox.checkPath(perm.getName())) {
                    // Deny
                    super.checkPermission(perm);
                }
                // Allow
            } else {
                // Deny
                super.checkPermission(perm);
            }
        }
        // Allow anything else
    }

    @Override
    public void checkPermission(final Permission perm, final Object context) {
        if (perm instanceof FilePermission) {
            if (perm.getActions().contains("read")
                    || perm.getActions().contains("write")
                    || perm.getActions().contains("delete")) {
                if (!perm.getName().startsWith("/Users/ericahnell/Repos")
                        && !perm.getName()
                                .startsWith("/Library/Java/JavaVirtualMachines")
                        && !this.sandbox.checkPath(perm.getName())) {
                    // Deny
                    super.checkPermission(perm, context);
                }
                // Allow
            } else {
                // Deny
                super.checkPermission(perm, context);
            }
        }
        // Allow anything else
    }

    @Override
    public void checkPrintJobAccess() {
        // Always allowed
    }

    @Override
    public void checkPropertiesAccess() {
        // Always allowed
    }

    @Override
    public void checkPropertyAccess(final String key) {
        // Always allowed
    }

    @Override
    public void checkRead(final String file) {
        if (!this.sandbox.checkPath(file)) {
            // Deny
            super.checkRead(file);
        }
        // Allow
    }

    @Override
    public void checkRead(final String file, final Object context) {
        if (!this.sandbox.checkPath(file)) {
            // Deny
            super.checkRead(file, context);
        }
        // Allow
    }

    @Override
    public void checkSecurityAccess(final String target) {
        // Always allowed
    }

    @Override
    public void checkSetFactory() {
        // Always allowed
    }

    @Override
    public void checkWrite(final String file) {
        if (!this.sandbox.checkPath(file)) {
            // Deny
            super.checkWrite(file);
        }
        // Allow
    }
}
