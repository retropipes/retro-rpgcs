/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.characterfiles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.Extension;

public class CharacterRegistration {
    // Fields
    private static boolean ANY_FOUND = false;
    private static final String DIR = "Characters";

    public static void autoregisterCharacter(final String name) {
        // Load character list
        final var characterNameList = CharacterRegistration
                .getCharacterNameList();
        if (name != null) {
            // Verify that character is not already registered
            var alreadyRegistered = false;
            if (characterNameList != null) {
                for (final String element : characterNameList) {
                    if (element.equalsIgnoreCase(name)) {
                        alreadyRegistered = true;
                        break;
                    }
                }
            }
            if (!alreadyRegistered) {
                // Register it
                if (characterNameList != null) {
                    final var newCharacterList = new String[characterNameList.length
                            + 1];
                    for (var x = 0; x < newCharacterList.length; x++) {
                        if (x < characterNameList.length) {
                            newCharacterList[x] = characterNameList[x];
                        } else {
                            newCharacterList[x] = name;
                        }
                    }
                    CharacterRegistration
                            .writeCharacterRegistry(newCharacterList);
                } else {
                    CharacterRegistration.writeCharacterRegistry(name);
                }
            }
        }
    }

    public static void autoremoveCharacter(final String res) {
        // Load character list
        final var characterNameList = CharacterRegistration
                .getCharacterNameList();
        // Check for null list
        if (characterNameList == null) {
            return;
        }
        // Pick character to unregister
        if (res != null) {
            // Find character index
            var index = -1;
            for (var x = 0; x < characterNameList.length; x++) {
                if (characterNameList[x].equals(res)) {
                    index = x;
                    break;
                }
            }
            if (index != -1) {
                // Unregister it
                if (characterNameList.length > 1) {
                    characterNameList[index] = null;
                    final var newCharacterList = new String[characterNameList.length
                            - 1];
                    var offset = 0;
                    for (var x = 0; x < characterNameList.length; x++) {
                        if (characterNameList[x] != null) {
                            newCharacterList[x - offset] = characterNameList[x];
                        } else {
                            offset++;
                        }
                    }
                    CharacterRegistration
                            .writeCharacterRegistry(newCharacterList);
                } else {
                    CharacterRegistration
                            .writeCharacterRegistry((String[]) null);
                }
                CharacterLoader.deleteCharacter(res, false);
            }
        }
    }

    static String getBasePath() {
        final var b = new StringBuilder();
        b.append(CharacterRegistration.getDirPrefix());
        b.append(File.pathSeparator);
        b.append(CharacterRegistration.getDirectory());
        b.append(File.pathSeparator);
        return b.toString();
    }

    static String[] getCharacterNameList() {
        final var registeredNames = CharacterRegistration
                .readCharacterRegistry();
        CharacterRegistration.ANY_FOUND = false;
        String[] characterList = null;
        if (registeredNames.size() > 0) {
            CharacterRegistration.ANY_FOUND = true;
        }
        // Load character list
        if (CharacterRegistration.ANY_FOUND) {
            registeredNames.trimToSize();
            characterList = new String[registeredNames.size()];
            for (var x = 0; x < registeredNames.size(); x++) {
                final var name = registeredNames.get(x);
                characterList[x] = name;
            }
        }
        return characterList;
    }

    private static String getDirectory() {
        return CharacterRegistration.DIR;
    }

    private static String getDirPrefix() {
        return RetroRPGCS.getDocumentsDirectory();
    }

    private static ArrayList<String> readCharacterRegistry() {
        final var basePath = CharacterRegistration.getBasePath();
        // Load character registry file
        final var registeredNames = new ArrayList<String>();
        try (var fis = new FileInputStream(
                basePath + "CharacterRegistry"
                        + Extension.getRegistryExtensionWithPeriod());
                var rsr = new ResourceStreamReader(fis)) {
            var input = "";
            while (input != null) {
                input = rsr.readString();
                if (input != null) {
                    registeredNames.add(input);
                }
            }
        } catch (final IOException io) {
            // Abort
            return new ArrayList<>();
        }
        return registeredNames;
    }

    // Methods
    public static void registerCharacter() {
        // Load character list
        final var characterNameList = CharacterRegistration
                .getCharacterNameList();
        final var characterNames = new File(
                CharacterRegistration.getBasePath())
                        .list(new CharacterFilter());
        if (characterNames != null && characterNames.length > 0) {
            // Strip extension
            final var stripCount = Extension.getCharacterExtensionWithPeriod()
                    .length();
            for (var x = 0; x < characterNames.length; x++) {
                final var temp = characterNames[x];
                characterNames[x] = temp.substring(0,
                        temp.length() - stripCount);
            }
            // Pick character to register
            final var res = CommonDialogs.showInputDialog(
                    "Register Which Character?", "Register Character",
                    characterNames, characterNames[0]);
            if (res != null) {
                // Verify that character is not already registered
                var alreadyRegistered = false;
                if (characterNameList != null) {
                    for (final String element : characterNameList) {
                        if (element.equalsIgnoreCase(res)) {
                            alreadyRegistered = true;
                            break;
                        }
                    }
                }
                if (!alreadyRegistered) {
                    // Verify that character file exists
                    if (new File(CharacterRegistration.getBasePath() + res
                            + Extension.getCharacterExtensionWithPeriod())
                                    .exists()) {
                        // Register it
                        if (CharacterRegistration.ANY_FOUND
                                && characterNameList != null) {
                            final var newCharacterList = new String[characterNameList.length
                                    + 1];
                            for (var x = 0; x < newCharacterList.length; x++) {
                                if (x < characterNameList.length) {
                                    newCharacterList[x] = characterNameList[x];
                                } else {
                                    newCharacterList[x] = res;
                                }
                            }
                            CharacterRegistration
                                    .writeCharacterRegistry(newCharacterList);
                        } else {
                            CharacterRegistration.writeCharacterRegistry(res);
                        }
                    } else {
                        CommonDialogs.showDialog(
                                "The character to register is not a valid character.");
                    }
                } else {
                    CommonDialogs.showDialog(
                            "The character to register has been registered already.");
                }
            }
        } else {
            CommonDialogs.showDialog("No characters found to register!");
        }
    }

    public static void removeCharacter() {
        // Load character list
        final var characterNameList = CharacterRegistration
                .getCharacterNameList();
        // Check for null list
        if (characterNameList == null) {
            CommonDialogs.showTitledDialog("No Characters Registered!",
                    "Remove Character");
            return;
        }
        // Pick character to unregister
        final var res = CommonDialogs.showInputDialog(
                "Remove Which Character?", "Remove Character",
                characterNameList, characterNameList[0]);
        if (res != null) {
            // Find character index
            var index = -1;
            for (var x = 0; x < characterNameList.length; x++) {
                if (characterNameList[x].equals(res)) {
                    index = x;
                    break;
                }
            }
            if (index != -1) {
                // Unregister it
                if (characterNameList.length > 1) {
                    characterNameList[index] = null;
                    final var newCharacterList = new String[characterNameList.length
                            - 1];
                    var offset = 0;
                    for (var x = 0; x < characterNameList.length; x++) {
                        if (characterNameList[x] != null) {
                            newCharacterList[x - offset] = characterNameList[x];
                        } else {
                            offset++;
                        }
                    }
                    CharacterRegistration
                            .writeCharacterRegistry(newCharacterList);
                } else {
                    CharacterRegistration
                            .writeCharacterRegistry((String[]) null);
                }
                CharacterLoader.deleteCharacter(res, true);
            }
        }
    }

    public static void unregisterCharacter() {
        // Load character list
        final var characterNameList = CharacterRegistration
                .getCharacterNameList();
        // Check for null list
        if (characterNameList == null) {
            CommonDialogs.showTitledDialog("No Characters Registered!",
                    "Unregister Character");
            return;
        }
        // Pick character to unregister
        final var res = CommonDialogs.showInputDialog(
                "Unregister Which Character?", "Unregister Character",
                characterNameList, characterNameList[0]);
        if (res != null) {
            // Find character index
            var index = -1;
            for (var x = 0; x < characterNameList.length; x++) {
                if (characterNameList[x].equals(res)) {
                    index = x;
                    break;
                }
            }
            if (index != -1) {
                // Unregister it
                if (characterNameList.length > 1) {
                    characterNameList[index] = null;
                    final var newCharacterList = new String[characterNameList.length
                            - 1];
                    var offset = 0;
                    for (var x = 0; x < characterNameList.length; x++) {
                        if (characterNameList[x] != null) {
                            newCharacterList[x - offset] = characterNameList[x];
                        } else {
                            offset++;
                        }
                    }
                    CharacterRegistration
                            .writeCharacterRegistry(newCharacterList);
                } else {
                    CharacterRegistration
                            .writeCharacterRegistry((String[]) null);
                }
            }
        }
    }

    private static void writeCharacterRegistry(
            final String... newCharacterList) {
        final var basePath = CharacterRegistration.getBasePath();
        // Check if registry is writable
        final var regFile = new File(basePath + "CharacterRegistry"
                + Extension.getRegistryExtensionWithPeriod());
        if (!regFile.exists()) {
            // Not writable, probably because needed folders don't exist
            final var regParent = regFile.getParentFile();
            if (!regParent.exists()) {
                final var res = regParent.mkdirs();
                if (!res) {
                    // Creating the needed folders failed, so abort
                    return;
                }
            }
        }
        // Save character registry file
        try (var bw = new BufferedWriter(new FileWriter(regFile))) {
            if (newCharacterList != null) {
                for (var x = 0; x < newCharacterList.length; x++) {
                    if (x != newCharacterList.length - 1) {
                        bw.write(newCharacterList[x] + "\n");
                    } else {
                        bw.write(newCharacterList[x]);
                    }
                }
            }
        } catch (final IOException io) {
            // Abort
        }
    }
}
