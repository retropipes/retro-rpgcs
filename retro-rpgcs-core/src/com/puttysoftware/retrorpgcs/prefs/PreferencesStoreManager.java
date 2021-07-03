/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.prefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

class PreferencesStoreManager {
    // Fields
    private final Properties store;

    // Constructors
    public PreferencesStoreManager() {
        this.store = new Properties();
    }

    public boolean getBoolean(final String key, final boolean defaultValue) {
        final var strVal = this.getString(key,
                Boolean.toString(defaultValue));
        return Boolean.parseBoolean(strVal);
    }

    public int getInteger(final String key, final int defaultValue) {
        final var strVal = this.getString(key,
                Integer.toString(defaultValue));
        return Integer.parseInt(strVal);
    }

    // Methods
    public String getString(final String key, final String defaultValue) {
        return this.store.getProperty(key, defaultValue);
    }

    public void loadStore(final InputStream source) throws IOException {
        this.store.loadFromXML(source);
    }

    public void saveStore(final OutputStream dest) throws IOException {
        this.store.storeToXML(dest, null);
    }

    public void setBoolean(final String key, final boolean newValue) {
        this.setString(key, Boolean.toString(newValue));
    }

    public void setInteger(final String key, final int newValue) {
        this.setString(key, Integer.toString(newValue));
    }

    public void setString(final String key, final String newValue) {
        this.store.setProperty(key, newValue);
    }
}
