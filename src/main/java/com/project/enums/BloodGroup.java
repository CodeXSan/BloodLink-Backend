package com.project.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BloodGroup {
	A_POSITIVE("A+"),
	A_NEGATIVE("A-"),
	B_POSITIVE("B+"),
	B_NEGATIVE("B-"),
	AB_POSITIVE("AB+"),
	AB_NEGATIVE("AB-"),
	O_POSITIVE("O+"),
	O_NEGATIVE("O-");
	
	private final String value;
	BloodGroup(String value) {
		this.value = value;
	}
	@JsonValue
	public String getValue() {
		return value;
	}
	@JsonCreator
	public static BloodGroup fromString(String type) {
		for(BloodGroup bg : BloodGroup.values()) {
			if(bg.value.equalsIgnoreCase(type)) {
				return bg;
			}
		}
		throw new IllegalArgumentException("No blood group found : " + type);
	}
	
}
