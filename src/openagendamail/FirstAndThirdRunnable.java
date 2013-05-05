package openagendamail;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import openagendamail.data.EmailAgendaItemProvider;
import openagendamail.file.LogFile;
import openagendamail.util.OamTools;

/**
 * A runnable for figuring out first and third Sunday agendas and scheduling the agenda building and sending.
 *
 * @author adam
 * @date Jan 8, 2012
 * Last Updated March 15, 2013
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

    /** {@inheritDoc} */
    @Override
    public void run() {

        Date now = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(now);

        int firstAndThirdDay = OamTools.getDayOfWeek(m_props.getProperty("first.and.third.day", "thu"));

        while (cal.get(Calendar.DAY_OF_WEEK) != firstAndThirdDay){
            cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
        }

        long secondUntilSendDay = OamTools.getSecondsUntilSpecifiedDay(OamTools.getDayOfWeek(m_props.getProperty("send.day", "fri")));
        if (OamTools.isFirstOrThirdDay(cal.get(Calendar.DATE))){
            LogFile.getLogFile().log("This week's meeting day (" + (cal.get(Calendar.MONTH) + 1) + " " +
                    cal.get(Calendar.DATE) + ", " + cal.get(Calendar.YEAR) + ") _IS_ a first or third week.");

            // Schedule the agenda building.
            ScheduledExecutorService buildExecutor = Executors.newSingleThreadScheduledExecutor();
            buildExecutor.schedule(new BuildAgendaRunnable(new EmailAgendaItemProvider(true)), secondUntilSendDay, TimeUnit.SECONDS);

            // Schedule sending the agenda for four hours after the agenda is built.
            EmailSenderRunnable sender = OamTools.buildAgendaEmailSender(null);
            ScheduledExecutorService sendExecutor = Executors.newSingleThreadScheduledExecutor();
            sendExecutor.schedule(sender, secondUntilSendDay + OamTools.SECONDS_IN_FOUR_HOURS, TimeUnit.SECONDS);

            // if enabled, schedule the reminder email
            if ((m_props.getProperty("reminders.on", "false")).toLowerCase().equals("true")){
                long secondsUntilReminder = OamTools.getSecondsUntilSpecifiedDay(OamTools.getDayOfWeek(m_props.getProperty("reminder.day", "mon")));
                EmailSenderRunnable reminder = OamTools.buildReminderSender();
                ScheduledExecutorService reminderExecutor = Executors.newSingleThreadScheduledExecutor();
                reminderExecutor.schedule(reminder, secondsUntilReminder, TimeUnit.SECONDS);
            }

        } else {
            // Schedule the agenda building but dont delete agenda items.
            ScheduledExecutorService buildExecutor = Executors.newSingleThreadScheduledExecutor();
            buildExecutor.schedule(new BuildAgendaRunnable(new EmailAgendaItemProvider(false)), secondUntilSendDay, TimeUnit.SECONDS);

            // Schedule an agenda to be sent out on the off-week.
            ScheduledExecutorService sendExecutor = Executors.newSingleThreadScheduledExecutor();
            sendExecutor.schedule(OamTools.buildAgendaEmailSender("Off-Week Agenda Preview"), secondUntilSendDay + OamTools.SECONDS_IN_FOUR_HOURS, TimeUnit.SECONDS);

            LogFile.getLogFile().log("This week's send date (" + OamTools.getFormattedDateString(cal.getTime()) + ") is _NOT_ a first or third of that day time this month.");
        }
    }
}