/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.spells;

import com.puttysoftware.retrorpgcs.creatures.castes.CasteConstants;
import com.puttysoftware.retrorpgcs.creatures.castes.predefined.AnnihilatorSpellBook;
import com.puttysoftware.retrorpgcs.creatures.castes.predefined.BufferSpellBook;
import com.puttysoftware.retrorpgcs.creatures.castes.predefined.CurerSpellBook;
import com.puttysoftware.retrorpgcs.creatures.castes.predefined.DebufferSpellBook;

public class SpellBookLoader {
    // Constructors
    private SpellBookLoader() {
	// Do nothing
    }

    // Methods
    public static SpellBook loadSpellBook(final int sbid) {
	if (sbid == CasteConstants.CASTE_ANNIHILATOR) {
	    return new AnnihilatorSpellBook();
	} else if (sbid == CasteConstants.CASTE_BUFFER) {
	    return new BufferSpellBook();
	} else if (sbid == CasteConstants.CASTE_CURER) {
	    return new CurerSpellBook();
	} else if (sbid == CasteConstants.CASTE_DEBUFFER) {
	    return new DebufferSpellBook();
	} else {
	    // Invalid caste name
	    return null;
	}
    }
}
