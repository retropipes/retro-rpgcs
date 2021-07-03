/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.castes;

import com.puttysoftware.retrorpgcs.creatures.castes.predefined.Annihilator;
import com.puttysoftware.retrorpgcs.creatures.castes.predefined.Buffer;
import com.puttysoftware.retrorpgcs.creatures.castes.predefined.Curer;
import com.puttysoftware.retrorpgcs.creatures.castes.predefined.Debuffer;

class CasteLoader {
    // Constructors
    private CasteLoader() {
        // Do nothing
    }

    // Methods
    static Caste loadCaste(final String name) {
        if (name.equals(
                CasteConstants.CASTE_NAMES[CasteConstants.CASTE_ANNIHILATOR])) {
            return new Annihilator();
        } else if (name.equals(
                CasteConstants.CASTE_NAMES[CasteConstants.CASTE_BUFFER])) {
            return new Buffer();
        } else if (name.equals(
                CasteConstants.CASTE_NAMES[CasteConstants.CASTE_CURER])) {
            return new Curer();
        } else if (name.equals(
                CasteConstants.CASTE_NAMES[CasteConstants.CASTE_DEBUFFER])) {
            return new Debuffer();
        } else {
            // Invalid caste name
            return null;
        }
    }
}
