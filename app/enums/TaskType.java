package enums;

public enum TaskType {
	PARENT("PARENT"), // 0
	CHILD("CHILD"); // 1

	// -----[ Manage string representation ]-----

	private String desc;

	private TaskType(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return desc;
	}

	public static TaskType fromInt(int enumId) {
		for (TaskType elem : TaskType.values()) {
			if (elem.ordinal() == enumId) {
				return elem;
			}
		}
		throw new IllegalArgumentException("Number <" + enumId + "> not valid for Task Type");
	}

	public static TaskType fromString(String enumStr) {
		for (TaskType elem : TaskType.values()) {
			if (elem.desc.equals(enumStr))
				return elem;
		}
		throw new IllegalArgumentException(
				"Value <" + (enumStr == null ? "null" : enumStr) + "> not valid for Task Type");
	}
}
