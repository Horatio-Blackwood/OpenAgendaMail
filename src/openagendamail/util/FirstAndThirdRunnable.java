package openagendamail.util;

import openagendamail.util.EmailSenderRunnable;
import openagendamail.util.BuildAgendaRunnable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
 * Last Updated May 6, 2013
 */
public class FirstAndThirdRunnable implements Runnable {

    /** Constructor creates a new first and third runnable. */
    public FirstAndThirdRunnable(){
    }
    
    /** {@inheritDoc} */
    @Override
    public void run() {

        // Create a calendar and set the time/date to right now.
        Date now = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(now);

        // Get the day of the week of the meeting the agenda is for.
        int dayOfTheWeekOfTheMeeting = OamTools.getDayOfWeek(OamTools.PROPS.getProperty("meeting.day.of.week", "thu"));

        // Until the calendar created earlier is set to the correct day of the week that the meeting occurs on, 
        // increment the day of the Calendar object.
        while (cal.get(Calendar.DAY_OF_WEEK) != dayOfTheWeekOfTheMeeting){
            cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
        }

        // Gets the number of seconds until the next send day at midnight.  If this call is made on the day of the week
        // that it should be sent, the number of seconds is calculated until the next send day.
        long secondUntilSendDay = OamTools.getSecondsUntilSpecifiedDay(OamTools.getDayOfWeek(OamTools.PROPS.getProperty("send.day", "tue")));
        
        
        // If the next day of the week that matches the day of the week of the meeting is a first or third of that day
        // of the week in this calendar month, then we need to send out an agenda.
        if (OamTools.isFirstOrThirdDay(cal.get(Calendar.DATE))){
            LogFile.getLogFile().log("This week's meeting day (" + (cal.get(Calendar.MONTH) + 1) + " " +
                    cal.get(Calendar.DATE) + ", " + cal.get(Calendar.YEAR) + ") _IS_ a first or third week.");

            // Schedule the agenda building for the _next_ send day.
            ScheduledExecutorService buildExecutor = Executors.newSingleThreadScheduledExecutor();
            buildExecutor.schedule(new BuildAgendaRunnable(new EmailAgendaItemProvider(true)), secondUntilSendDay, TimeUnit.SECONDS);

            // Schedule sending the agenda for four hours after the agenda is built.
            EmailSenderRunnable sender = OamTools.buildAgendaEmailSender(null);
            ScheduledExecutorService sendExecutor = Executors.newSingleThreadScheduledExecutor();
            sendExecutor.schedule(sender, secondUntilSendDay + OamTools.SECONDS_IN_FOUR_HOURS, TimeUnit.SECONDS);

        } else {
            // Schedule the agenda building but dont delete agenda items.
            ScheduledExecutorService buildExecutor = Executors.newSingleThreadScheduledExecutor();
            buildExecutor.schedule(new BuildAgendaRunnable(new EmailAgendaItemProvider(false)), secondUntilSendDay, TimeUnit.SECONDS);

            // Schedule an agenda to be sent out on the off-week.
            ScheduledExecutorService sendExecutor = Executors.newSingleThreadScheduledExecutor();
            sendExecutor.schedule(OamTools.buildAgendaEmailSender("Off-Week Agenda Preview"), secondUntilSendDay + OamTools.SECONDS_IN_FOUR_HOURS, TimeUnit.SECONDS);

            LogFile.getLogFile().log("This week's send date (" + OamTools.getFormattedDateString(cal.getTime()) + ") is _NOT_ a first or third of that day time this month.");
        }
        

        // if enabled, schedule the reminder email for the next reminder email day.
        if ((OamTools.PROPS.getProperty("reminders.on", "false")).toLowerCase().equals("true")){
            scheduleReminder();
        }
    }

    
    /** Schedules a reminder email to be sent out. */
    private void scheduleReminder(){
        // Get the day of the week of the reminder email
        int reminderDayOfWeek = OamTools.getDayOfWeek(OamTools.PROPS.getProperty("reminder.day", "mon"));
        long secondsUntilReminder = OamTools.getSecondsUntilSpecifiedDay(reminderDayOfWeek);

        // Create the Runnable and schedule it.
        EmailSenderRunnable reminder = OamTools.buildReminderSender();
        ScheduledExecutorService reminderExecutor = Executors.newSingleThreadScheduledExecutor();
        reminderExecutor.schedule(reminder, secondsUntilReminder, TimeUnit.SECONDS);
    }
}