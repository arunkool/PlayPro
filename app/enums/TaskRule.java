package enums;

public enum TaskRule {

	AS_SOON_AS_POSSIBLE("AS_SOON_AS_POSSIBLE"),//0
	AS_LATE_AS_POSSIBLE("AS_LATE_AS_POSSIBLE"),//1
	START_NO_EARLIER_THAN("START_NO_EARLIER_THAN"),//2
	START_NO_LATER_THAN("START_NO_LATER_THAN"),//3
	FINISH_NO_EARLIER_THAN("FINISH_NO_EARLIER_THAN"),//4
	FINISH_NO_LATER_THAN("FINISH_NO_LATER_THAN"),//5
	MUST_START_ON("MUST_START_ON"),//6
	FINISH_ON("FINISH_ON");//7

	// -----[ Manage string representation ]-----

	private String desc;

	private TaskRule(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return desc;
	}

	public static TaskRule fromInt(int enumId) {
		for (TaskRule elem : TaskRule.values()) {
			if (elem.ordinal() == enumId) {
				return elem;
			}
		}
		throw new IllegalArgumentException("Number <" + enumId + "> not valid for Rule");
	}

	public static TaskRule fromString(String enumStr) {
		for (TaskRule elem : TaskRule.values()) {
			if (elem.desc.equals(enumStr))
				return elem;
		}
		throw new IllegalArgumentException(
				"Value <" + (enumStr == null ? "null" : enumStr) + "> not valid for Rule");
	}


}
