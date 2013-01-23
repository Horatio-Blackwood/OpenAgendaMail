package openagendamail.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A file that provides useful methods to be used when working with text files.
 * 
 * @author adam
 * @date Oct 2, 2011
 */
public class TextFileToolbox {
    
    /** A constant that contains a system independent newline character. */
    private static final String NEWLINE = System.getProperty("line.separator");
    
    /**
     * Writes out a text file with the specified name and contents (data).
     * 
     * @param fileName the name of the file, extensions should be included here.
     * @param data the data to be contained in the text file.
     */
    public static void writeTextFile(String fileName, String data){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(data);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            StringBuilder bldr = new StringBuilder();
            bldr.append("Failed to write file.");
            bldr.append(NEWLINE);
            bldr.append("File Name:  ");
            bldr.append(fileName);
            bldr.append(NEWLINE);
            bldr.append("Contents:");
            bldr.append(NEWLINE);
            bldr.append(data);
            LogFile.getLogFile().log("Failed to write file:  " + fileName, ex);
        }
    }
    
    /**
     * Reads in a file line by line and returns those lines in an ordered list.
     * 
     * @param filename the file you want to read in.
     * @return the lines of the file in a list of lines in the order they were read in.
     * 
     * @throws IOException if an error is encountered when reading in the file.
     */
    public static List<String> readLinesFromFile(String filename) throws IOException{
        try {
            BufferedReader inFile = new BufferedReader(new FileReader(filename));
            List<String> lines = new ArrayList<String>();
            String line;
            while ((line = inFile.readLine()) != null){
                lines.add(line);
            }
            inFile.close();
            return lines;
            
        } catch (IOException ex){
            LogFile.getLogFile().log("Error reading lines from file:  " + filename, ex);
            throw ex;
        }
    }
}