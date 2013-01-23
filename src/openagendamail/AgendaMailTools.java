package openagendamail;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Toolbox class for AgendaMail.
 * 
 * @author adam
 * @date Jan 2, 2012
 * 
 * Last Updated:  Jan 5, 2013
 */
class AgendaMailTools {
    
    /** The number of seconds in four hours. */
    static final long SECONDS_IN_FOUR_HOURS = 60 * 60 * 4;
    
    /** A day as measured in seconds - pre-calculated for easy use.*/
    static final long ONE_WEEK_IN_SECONDS = 60L * 60 * 24 * 7;

    // Private Constructor.
    private AgendaMailTools(){
    }
    
    /**
     * Calculates and returns the number of seconds between now and the next Friday at 12am.
     * 
     * @param dayOfWeek the day of the week using the constants in Calendar (for example Calendar.FRIDAY).
     * @return seconds between now and Friday.
     */
    static long getSecondsUntilSpecifiedDay(int dayOfWeek){
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
}