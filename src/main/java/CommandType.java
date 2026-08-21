public enum CommandType {
    LIST,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    BYE,
    UNKNOWN;

    /**
     * Parse the command
     *
     * @param command
     * @return the value of command type
     */
    public static CommandType from(String command) {
        try {
            return CommandType.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException error) {
            return CommandType.UNKNOWN;
        }
    }
}