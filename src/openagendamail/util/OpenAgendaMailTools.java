package openagendamail.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import openagendamail.file.LogFile;
import openagendamail.file.TextFileToolbox;

/**
 * Toolbox class for AgendaMail.
 * 
 * @author adam
 * @date Jan 2, 2013
 * Last Updated:  Feb 2, 2013
 */
public class OpenAgendaMailTools {
    
    /** The number of seconds in four hours. */
    public static final long SECONDS_IN_FOUR_HOURS = 60 * 60 * 4;
    
    /** A day as measured in seconds - pre-calculated for easy use.*/
    public static final long ONE_WEEK_IN_SECONDS = 60L * 60 * 24 * 7;

    // Private Constructor.
    private OpenAgendaMailTools(){
    }
    
    /**
     * Calculates and returns the number of seconds between now and the next Friday at 12am.
     * 
     * @param dayOfWeek the day of the week using the constants in Calendar (for example Calendar.FRIDAY).
     * @return seconds between now and Friday.
     */
    public static long getSecondsUntilSpecifiedDay(int dayOfWeek){
        // Calculate time between now and the next specified day of the week. at 12am
        Date now = new Date();
        
        Calendar nowCal = new GregorianCalendar();
        nowCal.setTime(now);
        
        
        // If you're starting the application on the day it is intended to run,
        // this check forces it to look forward to next week and prevents it from 
        // immediately firing off an email to its intended recipients.
        if (nowCal.get(Calendar.DAY_OF_WEEK) == dayOfWeek){
            nowCal.set(Calendar.DATE, nowCal.get(Calendar.DATE) + 1);
        }
        
        // Advance until the desired day....
        while (nowCal.get(Calendar.DAY_OF_WEEK) != dayOfWeek){
            nowCal.set(Calendar.DATE, nowCal.get(Calendar.DATE) + 1);            
        }
        
        Calendar fridayCal = new GregorianCalendar();
        fridayCal.clear();
        fridayCal.set(Calendar.YEAR, nowCal.get(Calendar.YEAR));
        fridayCal.set(Calendar.MONTH, nowCal.get(Calendar.MONTH));
        fridayCal.set(Calendar.DATE, nowCal.get(Calendar.DATE));
       
        return (fridayCal.getTimeInMillis() - new Date().getTime()) / 1000L;
    }
    
    
    /**
     * Helper method reads and filters emails from email file.
     * @return a list of emails read in from the email file.
     */
    public static List<String> readEmails(String filename){
        List<String> emails = new ArrayList<>();
        
        try {
            List<String> temp = TextFileToolbox.readLinesFromFile(filename);
            for (String email : temp){
                email = email.trim();
                if (!email.startsWith("#") && !email.isEmpty()){
                    emails.add(email);
                }
            }
        } catch (IOException ex) {
            LogFile.getLogFile().log("Error reading emails from email file.", ex);
        }
        
        return emails;
    }
    
    /** 
     * Gets the {@link Calendar} day of the week constant for the provided 3 character, lower-case day of the 
     * week string.
     */
    public static int getDayOfWeek(String day){
        switch (day.toLowerCase()) {
            case "mon":
                return Calendar.MONDAY;
            case "tue":
                return Calendar.TUESDAY;
            case "wed":
                return Calendar.WEDNESDAY;
            case "thu":
                return Calendar.THURSDAY;
            case "fri":
                return Calendar.FRIDAY;
            case "sat":
                return Calendar.SATURDAY;
            case "sun":
                return Calendar.SUNDAY;
            default:
                throw new IllegalStateException("Invalid day of week provided.  Must be mon, tue, wed, thu, fri, "
                        + "sat or sun.  Value provided was " + day);
        }
    }
}