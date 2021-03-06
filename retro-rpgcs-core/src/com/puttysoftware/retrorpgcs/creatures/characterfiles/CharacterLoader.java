/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.characterfiles;

import java.io.File;
import java.io.IOException;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.VersionException;
import com.puttysoftware.retrorpgcs.creatures.party.PartyMember;
import com.puttysoftware.retrorpgcs.maze.Extension;
import com.puttysoftware.xio.UnexpectedTagException;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class CharacterLoader {
    static void deleteCharacter(final String name, final boolean showResults) {
        final var basePath = CharacterRegistration.getBasePath();
        final var characterFile = basePath + File.separator + name
                + Extension.getCharacterExtensionWithPeriod();
        final var toDelete = new File(characterFile);
        if (toDelete.exists()) {
            final var success = toDelete.delete();
            if (success) {
                if (showResults) {
                    CommonDialogs.showDialog("Character removed.");
                } else {
                    CommonDialogs.showDialog("Character " + name
                            + " autoremoved due to version change.");
                }
            } else {
                if (showResults) {
                    CommonDialogs.showDialog("Character removal failed!");
                } else {
                    CommonDialogs.showDialog(
                            "Character " + name + " failed to autoremove!");
                }
            }
        } else if (showResults) {
            CommonDialogs.showDialog(
                    "The character to be removed does not have a corresponding file.");
        } else {
            CommonDialogs.showDialog(
                    "The character to be autoremoved does not have a corresponding file.");
        }
    }

    public static PartyMember[] loadAllRegisteredCharacters() {
        final var registeredNames = CharacterRegistration
                .getCharacterNameList();
        if (registeredNames != null) {
            final var res = new PartyMember[registeredNames.length];
            // Load characters
            for (var x = 0; x < registeredNames.length; x++) {
                final var name = registeredNames[x];
                final var characterWithName = CharacterLoader
                        .loadCharacter(name);
                if (characterWithName != null) {
                    res[x] = characterWithName;
                } else {
                    // Auto-removed character
                    return CharacterLoader.loadAllRegisteredCharacters();
                }
            }
            return res;
        }
        return null;
    }

    private static PartyMember loadCharacter(final String name) {
        final var basePath = CharacterRegistration.getBasePath();
        final var loadPath = basePath + File.separator + name
                + Extension.getCharacterExtensionWithPeriod();
        try (var loader = new XDataReader(loadPath, "character")) {
            return PartyMember.read(loader);
        } catch (VersionException | UnexpectedTagException e) {
            CharacterRegistration.autoremoveCharacter(name);
            return null;
        } catch (final IOException e) {
            RetroRPGCS.getInstance().handleError(e);
            return null;
        }
    }

    public static void saveCharacter(final PartyMember character) {
        final var basePath = CharacterRegistration.getBasePath();
        final var name = character.getName();
        final var characterFile = basePath + File.separator + name
                + Extension.getCharacterExtensionWithPeriod();
        try (var saver = new XDataWriter(characterFile, "character")) {
            character.write(saver);
        } catch (final IOException e) {
            RetroRPGCS.getInstance().handleError(e);
        }
    }
}
