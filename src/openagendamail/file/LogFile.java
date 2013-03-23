package openagendamail.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


/**
 * A simple class for creating and writing to log files.  This class initializes a file in the current directory and appends
 * the message to the file.
 * 
 * @author adam
 * @date Aug 16, 2011
 */
public class LogFile {
    
    /** A buffered writer for writing to the file. */
    private BufferedWriter m_writer;
    
    /** The instance of the logger. */
    private static LogFile INSTANCE;
    
    /** A constant six-space indentation String. */
    private static final String SPACE = "      ";
    
    /** A constant that contains a system independent newline character. */
    private static final String NEWLINE = System.getProperty("line.separator");
    
    /** The logging directory. */
    private static final String DIRECTORY = "logs/";
    
    /** True if the log directory exists, false otherwise. */
    private static boolean m_directoryExists = false;

    /**
     * Creates an instance of LogFile.
     * @param fileName the name of the log file.
     */
    private LogFile(String fileName) throws IOException{
        init(fileName);
    }
    
    /**
     * Gets a reference to the LogFile.  If the log file does not exist, one is created.
     * @return Returns the current log file.  If a log file does not already exist for this
     * application run, a new one is created.
     */
    public static LogFile getLogFile(){
        try {
            if (INSTANCE == null){
                String date = new Date().toString().replaceAll("[^a-zA-Z0-9\\s]", "-");
                
                if (!m_directoryExists){
                    m_directoryExists = new File("logs").mkdirs();
                }
                
                INSTANCE = new LogFile(DIRECTORY + "log-" + date +  ".txt");
                return INSTANCE;
            }
        } catch (IOException ex){
            logLoggingError(ex);
        }
        return INSTANCE;
    }
    
    
    /**
     * Logs a single message to the LogFile.
     * @param message The message to log, as a {@link String}
     */
    public void log(String message){
        try {
            StringBuilder msg = new StringBuilder(new Date().toString());
            msg.append(NEWLINE);
            msg.append(SPACE);
            msg.append(message);
             for (int i = 0; i < 3; i++){
                msg.append(NEWLINE);
            }
            m_writer.write(msg.toString());
            m_writer.flush();
        } catch (IOException ioEx){
            logLoggingError(ioEx);
        }
    }
    
    /**
     * Logs a message along with a stack trace.
     * @param message the message to log.
     * @param ex the exception to extract the stack trace from.
     */
    public void log(String message, Throwable ex) {
        try {
            StringBuilder msg = new StringBuilder(new Date().toString());
            msg.append(NEWLINE);
            msg.append(SPACE);
            msg.append(message);
            msg.append(NEWLINE);
            msg.append(NEWLINE);
            msg.append(StackTraceHandler.handleStackTrace(ex));
             for (int i = 0; i < 3; i++){
                msg.append(NEWLINE);
            }
            m_writer.write(msg.toString());
            m_writer.flush();
        } catch (IOException ioEx){
            logLoggingError(ioEx);
        }
    }

    /**
     * Initializes the file writer for this LogFile.
     * @param fileName the name of the log file to write.
     * @throws IOException if errors occur when creating the file writer.
     */
    private void init(String fileName) throws IOException{
        m_writer = new BufferedWriter(new FileWriter(fileName));
    }
    
    /**
     * Creates and logs an error report when a logging message fails to write to the file.  This method is entirely self-
     * contained in that it creates its own {@link FileWriter} and attempts to write the error report to a new file.  If an
     * error occurs when writing this file, 
     * 
     * @param ex the IOException that was thrown by one of the other writes to a LogFile.
     */
    private static void logLoggingError(IOException ex){
        try {
            StringBuilder msg = new StringBuilder(new Date().toString());
            msg.append(NEWLINE);
            msg.append(SPACE);
            msg.append("An error occured writing to a log file.");
            msg.append(NEWLINE);
            msg.append(NEWLINE);
            msg.append(StackTraceHandler.handleStackTrace(ex));
             for (int i = 0; i < 3; i++){
                msg.append(NEWLINE);
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter("LoggingErrorReport_" + System.currentTimeMillis() + ".txt"));
            writer.write(msg.toString());
            writer.flush();
        } catch (IOException exception){
            System.out.println("Error while creating a logging error report.");
        }   
    }
}