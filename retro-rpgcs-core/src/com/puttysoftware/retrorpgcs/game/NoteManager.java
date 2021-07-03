/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.game;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.retrorpgcs.RetroRPGCS;

public class NoteManager {
    public static void editNote() {
        final var m = RetroRPGCS.getInstance().getMazeManager().getMaze();
        final var x = m.getPlayerLocationX();
        final var y = m.getPlayerLocationY();
        final var z = m.getPlayerLocationZ();
        var defaultText = "Empty Note";
        if (m.hasNote(x, y, z)) {
            defaultText = m.getNote(x, y, z).getContents();
        }
        final var result = CommonDialogs.showTextInputDialogWithDefault(
                "Note Text:", "Edit Note", defaultText);
        if (result != null) {
            if (!m.hasNote(x, y, z)) {
                m.createNote(x, y, z);
            }
            final var mn = m.getNote(x, y, z);
            mn.setContents(result);
        }
    }

    private NoteManager() {
        // Do nothing
    }
}
