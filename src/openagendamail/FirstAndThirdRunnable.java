package openagendamail;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import openagendamail.file.LogFile;
import openagendamail.util.OpenAgendaMailTools;

/**
 * A runnable for figuring out first and third Sunday agendas and scheduling the agenda building and sending.
 *
 * @author adam
 * @date Jan 8, 2012
 */
public class FirstAndThirdRunnable implements Runnable {

    /** System properties. */
    private Properties m_props;

    /**
     * Constructor creates a new first and third runnable.
     * @param properties the system properties.
     */
    public FirstAndThirdRunnable(Properties properties){
        if (properties == null){
            throw new IllegalArgumentException("Parameter 'properties' cannot be null.");
        }
        m_props = properties;
    }

    /**
     * Returns true if the provided date is a first or 3rd Sunday, false otherwise.  Assumes the date supplied IS in
     * fact a Sunday.  This method provides no error checking on its input.
     *
     * @param date the date of the next upcoming Sunday.
     * @return true if first or third Sunday date provided, false otherwise.
     */
    private boolean isFirstOrThirdSunday(int date){
        if (date < 1){
            return false;
        }

        if (date <= 7){
            return true;
        }

        if (date <= 14){
            return false;
        }

        if (date <= 21){
            return true;
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        Date now = new Date();
        Calendar sunday = new GregorianCalendar();
        sunday.setTime(now);

        while (sunday.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
            sunday.set(Calendar.DATE, sunday.get(Calendar.DATE) + 1);
        }

        long secondsUntilFriday = OpenAgendaMailTools.getSecondsUntilSpecifiedDay(Calendar.FRIDAY);
        if (isFirstOrThirdSunday(sunday.get(Calendar.DATE))){
            LogFile.getLogFile().log("This week's Sunday (" + sunday.get(Calendar.MONTH) + " " +
                    sunday.get(Calendar.DATE) + ", " + sunday.get(Calendar.YEAR) + ") _IS_ a first or third Sunday.");


            // Schedule the agenda building.
            ScheduledExecutorService buildExecutor = Executors.newSingleThreadScheduledExecutor();
            buildExecutor.schedule(new BuildAgendaRunnable(m_props, true), secondsUntilFriday, TimeUnit.SECONDS);

            // Schedule sending the agenda for four hours after the agenda is built.
            ScheduledExecutorService sendExecutor = Executors.newSingleThreadScheduledExecutor();
            sendExecutor.schedule(new SendAgendaRunnable(m_props), secondsUntilFriday + OpenAgendaMailTools.SECONDS_IN_FOUR_HOURS, TimeUnit.SECONDS);

        } else {
            // Schedule the agenda building but dont delete agenda items.
            ScheduledExecutorService buildExecutor = Executors.newSingleThreadScheduledExecutor();
            buildExecutor.schedule(new BuildAgendaRunnable(m_props, false), secondsUntilFriday, TimeUnit.SECONDS);

            // Schedule an agenda to be sent out on the off-week.
            ScheduledExecutorService sendExecutor = Executors.newSingleThreadScheduledExecutor();
            sendExecutor.schedule(new SendAgendaRunnable(m_props, "Off-Week Agenda Preview"), secondsUntilFriday + OpenAgendaMailTools.SECONDS_IN_FOUR_HOURS, TimeUnit.SECONDS);

            LogFile.getLogFile().log("This week's Sunday (" + sunday.get(Calendar.MONTH) + " " +
                    sunday.get(Calendar.DATE) + ", " + sunday.get(Calendar.YEAR) + ") is _NOT_ a first or third Sunday.");
        }
    }
}