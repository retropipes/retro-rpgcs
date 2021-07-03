/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.datamanagers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.names.NamesConstants;

public class NamesDataManager {
    private static final String DIR = "Names";

    public static String[] getNamesData() {
        final File overrideData = NamesDataManager.getNamesOverrideFile();
        if (overrideData.exists()) {
            return NamesDataManager.getNamesOverrideData();
        } else {
            return NamesDataManager.getNamesDefaultData();
        }
    }

    private static String[] getNamesDefaultData() {
        try (ResourceStreamReader rsr = new ResourceStreamReader(
                NamesDataManager.class.getResourceAsStream(
                        "/com/puttysoftware/retrorpgcs/resources/data/names/names.txt"))) {
            // Load default
            final ArrayList<String> data = new ArrayList<>();
            // Ignore first line
            String raw = rsr.readString();
            while (raw != null) {
                raw = rsr.readString();
                data.add(raw);
            }
            final Object[] arr = data.toArray();
            final String[] tempres = new String[arr.length];
            int count = 0;
            for (int x = 0; x < arr.length; x++) {
                if (arr[x] != null) {
                    tempres[x] = arr[x].toString();
                    count++;
                }
            }
            final String[] res = new String[count];
            count = 0;
            for (final String tempre : tempres) {
                if (tempre != null) {
                    res[count] = tempre;
                    count++;
                }
            }
            return res;
        } catch (final IOException e) {
            RetroRPGCS.getInstance().handleError(e);
            return null;
        }
    }

    private static String[] getNamesOverrideData() {
        try {
            final File overrideData = NamesDataManager.getNamesOverrideFile();
            // Version check
            if (overrideData.exists() && !NamesDataManager
                    .isNamesFileCorrectVersion(overrideData)) {
                final boolean success = overrideData.delete();
                if (!success) {
                    throw new IOException("Deleting override failed!");
                }
            }
            try (FileInputStream fis = new FileInputStream(overrideData);
                    ResourceStreamReader rsr = new ResourceStreamReader(fis)) {
                final ArrayList<String> data = new ArrayList<>();
                // Ignore first line
                String raw = rsr.readString();
                while (raw != null) {
                    raw = rsr.readString();
                    data.add(raw);
                }
                final Object[] arr = data.toArray();
                final String[] tempres = new String[arr.length];
                int count = 0;
                for (int x = 0; x < arr.length; x++) {
                    if (arr[x] != null) {
                        tempres[x] = arr[x].toString();
                        count++;
                    }
                }
                final String[] res = new String[count];
                count = 0;
                for (final String tempre : tempres) {
                    if (tempre != null) {
                        res[count] = tempre;
                        count++;
                    }
                }
                return res;
            }
        } catch (final IOException e) {
            RetroRPGCS.getInstance().handleError(e);
            return null;
        }
    }

    private static String getNamesDirPrefix() {
        return RetroRPGCS.getSupportDirectory();
    }

    private static String getNamesDirectory() {
        return NamesDataManager.DIR;
    }

    public static File getNamesOverrideFile() {
        final StringBuilder b = new StringBuilder();
        b.append(NamesDataManager.getNamesDirPrefix());
        b.append(File.pathSeparator);
        b.append(NamesDataManager.getNamesDirectory());
        b.append(File.pathSeparator);
        b.append("names.txt");
        return new File(b.toString());
    }

    private static boolean isNamesFileCorrectVersion(final File f) {
        try (FileInputStream fis = new FileInputStream(f);
                ResourceStreamReader rsr = new ResourceStreamReader(fis)) {
            final int version = rsr.readInt();
            return version == NamesConstants.NAMES_VERSION;
        } catch (final IOException e) {
            return false;
        }
    }
}
