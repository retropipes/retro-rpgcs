/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.spells;

import com.puttysoftware.retrorpgcs.creatures.castes.CasteConstants;
import com.puttysoftware.retrorpgcs.creatures.castes.predefined.AnnihilatorSpellBook;
import com.puttysoftware.retrorpgcs.creatures.castes.predefined.BufferSpellBook;
import com.puttysoftware.retrorpgcs.creatures.castes.predefined.CurerSpellBook;
import com.puttysoftware.retrorpgcs.creatures.castes.predefined.DebufferSpellBook;

public class SpellBookLoader {
    // Methods
    public static SpellBook loadSpellBook(final int sbid) {
        switch (sbid) {
        case CasteConstants.CASTE_ANNIHILATOR:
            return new AnnihilatorSpellBook();
        case CasteConstants.CASTE_BUFFER:
            return new BufferSpellBook();
        case CasteConstants.CASTE_CURER:
            return new CurerSpellBook();
        case CasteConstants.CASTE_DEBUFFER:
            return new DebufferSpellBook();
        default:
            // Invalid caste name
            return null;
        }
    }

    // Constructors
    private SpellBookLoader() {
        // Do nothing
    }
}
