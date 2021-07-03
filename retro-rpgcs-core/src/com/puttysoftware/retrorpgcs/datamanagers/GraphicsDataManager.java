/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.datamanagers;

import java.io.IOException;
import java.util.ArrayList;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;

public class GraphicsDataManager {
    public static String[] getObjectGraphicsData() {
        try (final ResourceStreamReader rsr = new ResourceStreamReader(
                GraphicsDataManager.class.getResourceAsStream(
                        "/com/puttysoftware/retrorpgcs/resources/data/graphics/objects.txt"))) {
            // Fetch data
            final ArrayList<String> rawData = new ArrayList<>();
            String line = "";
            while (line != null) {
                line = rsr.readString();
                if (line != null) {
                    rawData.add(line);
                }
            }
            return rawData.toArray(new String[rawData.size()]);
        } catch (final IOException e) {
            RetroRPGCS.getInstance().handleError(e);
            return null;
        }
    }

    public static String[] getStatGraphicsData() {
        try (final ResourceStreamReader rsr = new ResourceStreamReader(
                GraphicsDataManager.class.getResourceAsStream(
                        "/com/puttysoftware/retrorpgcs/resources/data/graphics/stats.txt"))) {
            // Fetch data
            final ArrayList<String> rawData = new ArrayList<>();
            String line = "";
            while (line != null) {
                line = rsr.readString();
                if (line != null) {
                    rawData.add(line);
                }
            }
            return rawData.toArray(new String[rawData.size()]);
        } catch (final IOException e) {
            RetroRPGCS.getInstance().handleError(e);
            return null;
        }
    }
}
