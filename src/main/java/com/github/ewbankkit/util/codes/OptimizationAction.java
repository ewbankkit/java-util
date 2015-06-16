/**
 *
 */
package com.github.ewbankkit.util.codes;

/**
 * @author bmills
 *
 */
public enum OptimizationAction {
	APPLY(1, "APPLY"),
	PAUSE(2, "PAUSE"),
	DELETE(3, "DELETE");

	private int code;
	private String name;

	OptimizationAction(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public static OptimizationAction getActionByCode(int code) {
		if (code == APPLY.code) {
			return APPLY;
		} else if (code == PAUSE.code) {
			return PAUSE;
		} else if (code == DELETE.code) {
			return DELETE;
		} else {
			return null;
		}
	}

	public static OptimizationAction getActionByName(String name) {
		if (name == null) return null;

		if (APPLY.name.equals(name)) {
			return APPLY;
		} else if (PAUSE.name.equals(name)) {
			return PAUSE;
		} else if (DELETE.name.equals(name)) {
			return DELETE;
		} else {
			return null;
		}
	}
}
