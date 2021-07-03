/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.creatures.genders;

public class Gender {
    private final int genderID;

    Gender(final int gid) {
	this.genderID = gid;
    }

    public int getGenderID() {
	return this.genderID;
    }
}
