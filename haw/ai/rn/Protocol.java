package haw.ai.rn;

public interface Protocol {
	public static final int SERVER_PORT = 50000;
	public static final int CLIENT_PORT = 50001;
	public static final String MSG_DELIMITER = "\n";
	public static final String NAME_PATTERN = "^[a-zA-Z]{1,20}$";
}
