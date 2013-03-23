package openagendamail.file;

/**
 * Handles stack traces.  This will return a string that has a stack trace neatly and cleanly word-wrapped and indented.
 * @author adam
 * @date Aug 6, 2011
 */
class StackTraceHandler {
    
    /** A constant twelve-space indentation String. */
    private static final String SPACE = "      ";
    
    /** A constant that contains a system independent newline character. */
    private static final String NEWLINE = System.getProperty("line.separator");

    /**
     * A method for compiling and returning a stack trace into a single string.
     * @param ex the exception to grab the stack trace from.
     */
    static String handleStackTrace(Throwable ex){
        // add the stack trace message
        StringBuilder builder = new StringBuilder(SPACE);
        builder.append(ex.getMessage());
        builder.append(NEWLINE);
        
        // add each line of the stack trace.
        for (StackTraceElement element : ex.getStackTrace()){
            builder.append(SPACE);
            builder.append(element.toString());
            builder.append(NEWLINE);
        }
        return builder.toString();
    }
}
