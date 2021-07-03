/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.party;

import java.io.IOException;

import javax.swing.JFrame;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.retrorpgcs.creatures.castes.CasteManager;
import com.puttysoftware.retrorpgcs.creatures.characterfiles.CharacterLoader;
import com.puttysoftware.retrorpgcs.creatures.characterfiles.CharacterRegistration;
import com.puttysoftware.retrorpgcs.creatures.faiths.FaithManager;
import com.puttysoftware.retrorpgcs.creatures.genders.GenderManager;
import com.puttysoftware.retrorpgcs.creatures.personalities.PersonalityManager;
import com.puttysoftware.retrorpgcs.creatures.races.RaceManager;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class PartyManager {
    // Fields
    private static Party party;
    private static int bank = 0;
    private static final int PARTY_SIZE = 1;
    private final static String[] buttonNames = new String[] { "Done", "Create",
            "Pick" };

    public static void addGoldToBank(final int newGold) {
        PartyManager.bank += newGold;
    }

    private static String[] buildNameList(final PartyMember[] members) {
        final var tempNames = new String[1];
        var nnc = 0;
        for (var x = 0; x < tempNames.length; x++) {
            if (members != null) {
                tempNames[x] = members[x].getName();
                nnc++;
            }
        }
        final var names = new String[nnc];
        nnc = 0;
        for (final String tempName : tempNames) {
            if (tempName != null) {
                names[nnc] = tempName;
                nnc++;
            }
        }
        return names;
    }

    private static PartyMember createNewPC(final JFrame owner) {
        final var name = CommonDialogs.showTextInputDialog("Character Name",
                "Create Character");
        if (name != null) {
            final var race = RaceManager.selectRace(owner);
            if (race != null) {
                final var caste = CasteManager.selectCaste(owner);
                if (caste != null) {
                    final var faith = FaithManager.selectFaith(owner);
                    if (faith != null) {
                        final var personality = PersonalityManager
                                .selectPersonality(owner);
                        if (personality != null) {
                            final var gender = GenderManager.selectGender();
                            if (gender != null) {
                                return new PartyMember(race, caste, faith,
                                        personality, gender, name);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    // Methods
    public static boolean createParty(final JFrame owner) {
        PartyManager.party = new Party();
        var mem = 0;
        final var pickMembers = CharacterLoader
                .loadAllRegisteredCharacters();
        for (var x = 0; x < PartyManager.PARTY_SIZE; x++) {
            PartyMember pc = null;
            if (pickMembers == null) {
                // No characters registered - must create one
                pc = PartyManager.createNewPC(owner);
                if (pc != null) {
                    CharacterRegistration.autoregisterCharacter(pc.getName());
                    CharacterLoader.saveCharacter(pc);
                }
            } else {
                final var response = CommonDialogs.showCustomDialog(
                        "Pick, Create, or Done?", "Create Party",
                        PartyManager.buttonNames, PartyManager.buttonNames[2]);
                if (response == 2) {
                    pc = PartyManager.pickOnePartyMemberCreate(pickMembers);
                } else if (response == 1) {
                    pc = PartyManager.createNewPC(owner);
                    if (pc != null) {
                        CharacterRegistration
                                .autoregisterCharacter(pc.getName());
                        CharacterLoader.saveCharacter(pc);
                    }
                }
            }
            if (pc == null) {
                break;
            }
            PartyManager.party.addPartyMember(pc);
            mem++;
        }
        if (mem == 0) {
            return false;
        }
        return true;
    }

    public static int getGoldInBank() {
        return PartyManager.bank;
    }

    public static PartyMember getNewPCInstance(final int r, final int c,
            final int f, final int p, final int g, final String n) {
        final var race = RaceManager.getRace(r);
        final var caste = CasteManager.getCaste(c);
        final var faith = FaithManager.getFaith(f);
        final var personality = PersonalityManager.getPersonality(p);
        final var gender = GenderManager.getGender(g);
        return new PartyMember(race, caste, faith, personality, gender, n);
    }

    public static Party getParty() {
        return PartyManager.party;
    }

    public static void loadGameHook(final XDataReader partyFile)
            throws IOException {
        final var containsPCData = partyFile.readBoolean();
        if (containsPCData) {
            final var gib = partyFile.readInt();
            PartyManager.setGoldInBank(gib);
            PartyManager.party = Party.read(partyFile);
        }
    }

    private static PartyMember pickOnePartyMemberCreate(
            final PartyMember[] members) {
        final var pickNames = PartyManager.buildNameList(members);
        final var response = CommonDialogs.showInputDialog(
                "Pick 1 Party Member", "Create Party", pickNames, pickNames[0]);
        if (response != null) {
            for (final PartyMember member : members) {
                if (member.getName().equals(response)) {
                    return member;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    public static void removeGoldFromBank(final int cost) {
        PartyManager.bank -= cost;
        if (PartyManager.bank < 0) {
            PartyManager.bank = 0;
        }
    }

    public static void saveGameHook(final XDataWriter partyFile)
            throws IOException {
        if (PartyManager.party != null) {
            partyFile.writeBoolean(true);
            partyFile.writeInt(PartyManager.getGoldInBank());
            PartyManager.party.write(partyFile);
        } else {
            partyFile.writeBoolean(false);
        }
    }

    private static void setGoldInBank(final int newGold) {
        PartyManager.bank = newGold;
    }

    public static String showCreationDialog(final JFrame owner,
            final String labelText, final String title, final String[] input,
            final String[] descriptions) {
        return ListWithDescDialog.showDialog(owner, null, labelText, title,
                input, input[0], descriptions[0], descriptions);
    }

    public static void updatePostKill() {
        final var leader = PartyManager.getParty().getLeader();
        leader.initPostKill(leader.getRace(), leader.getCaste(),
                leader.getFaith(), leader.getPersonality(), leader.getGender());
    }

    // Constructors
    private PartyManager() {
        // Do nothing
    }
}
