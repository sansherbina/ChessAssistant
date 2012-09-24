package util;

public class ErrorsList {
	public static final String [] OK = {"OK", ""};
	public static final String [] WRONG_MOVE_ERROR = {"WRONG_MOVE", ""};
	
	public static String getStatusCode(String [] errorConstantName) {
		return errorConstantName[0];
	}
	
	public static String getErrorMessage(String [] errorConstantName) {
		return errorConstantName[1];
	}
}
