/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.spells;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public class SpellCaster {
    // Fields
    private static boolean NO_SPELLS_FLAG = false;

    public static boolean castSpell(final Spell cast, final Creature caster) {
        if (cast != null) {
            final var casterMP = caster.getCurrentMP();
            final var cost = cast.getCost();
            if (casterMP >= cost) {
                // Cast Spell
                caster.drain(cost);
                final var b = cast.getEffect();
                // Play spell's associated sound effect, if it has one
                final var snd = cast.getSound();
                SoundManager.playSound(snd);
                b.resetEffect();
                final var target = SpellCaster.resolveTarget(cast,
                        caster.getTeamID());
                if (target.isEffectActive(b)) {
                    target.extendEffect(b, b.getInitialRounds());
                } else {
                    b.restoreEffect();
                    target.applyEffect(b);
                }
                return true;
            } else {
                // Not enough MP
                return false;
            }
        } else {
            return false;
        }
    }

    private static Creature resolveTarget(final Spell cast, final int teamID) {
        final var target = cast.getTarget();
        switch (target) {
        case SELF:
            if (teamID == Creature.TEAM_PARTY) {
                return PartyManager.getParty().getLeader();
            } else {
                return RetroRPGCS.getInstance().getBattle().getEnemy();
            }
        case ENEMY:
            if (teamID == Creature.TEAM_PARTY) {
                return RetroRPGCS.getInstance().getBattle().getEnemy();
            } else {
                return PartyManager.getParty().getLeader();
            }
        default:
            return null;
        }
    }

    public static boolean selectAndCastSpell(final Creature caster) {
        var result = false;
        SpellCaster.NO_SPELLS_FLAG = false;
        final var s = SpellCaster.selectSpell(caster);
        if (s != null) {
            result = SpellCaster.castSpell(s, caster);
            if (!result && !SpellCaster.NO_SPELLS_FLAG) {
                CommonDialogs.showErrorDialog(
                        "You try to cast a spell, but realize you don't have enough MP!",
                        "Select Spell");
            }
        }
        return result;
    }

    private static Spell selectSpell(final Creature caster) {
        final var book = caster.getSpellBook();
        if (book != null) {
            final var names = book.getAllSpellNames();
            final var displayNames = book.getAllSpellNamesWithCosts();
            if (names != null && displayNames != null) {
                // Play casting spell sound
                SoundManager.playSound(SoundConstants.SOUND_SPELL);
                String dialogResult;
                dialogResult = CommonDialogs.showInputDialog(
                        "Select a Spell to Cast", "Select Spell", displayNames,
                        displayNames[0]);
                if (dialogResult != null) {
                    int index;
                    for (index = 0; index < displayNames.length; index++) {
                        if (dialogResult.equals(displayNames[index])) {
                            break;
                        }
                    }
                    return book.getSpellByName(names[index]);
                } else {
                    return null;
                }
            } else {
                SpellCaster.NO_SPELLS_FLAG = true;
                CommonDialogs.showErrorDialog(
                        "You try to cast a spell, but realize you don't know any!",
                        "Select Spell");
                return null;
            }
        } else {
            SpellCaster.NO_SPELLS_FLAG = true;
            CommonDialogs.showErrorDialog(
                    "You try to cast a spell, but realize you don't know any!",
                    "Select Spell");
            return null;
        }
    }

    // Private Constructor
    private SpellCaster() {
        // Do nothing
    }
}
