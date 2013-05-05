package openagendamail.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import openagendamail.EmailSenderRunnable;
import openagendamail.file.LogFile;
import openagendamail.file.TextFileToolbox;

/**
 * Toolbox class for AgendaMail.
 *
 * @author adam
 * @date Jan 2, 2013
 * Last Updated:  Feb 2, 2013
 */
public class OamTools {

    /** The properties for this application. */
    public static final Properties PROPS = new Properties();

    /** The number of seconds in four hours. */
    public static final long SECONDS_IN_FOUR_HOURS = 60 * 60 * 4;

    /** A day as measured in seconds - pre-calculated for easy use.*/
    public static final long ONE_WEEK_IN_SECONDS = 60L * 60 * 24 * 7;

    /** A formatter for date objects used when generating the .doc object. */
    private static SimpleDateFormat m_dateFormat = new SimpleDateFormat("MMM.dd.YYYY");

    // Loads the properties for the application.
    static {
        try {
            FileInputStream app = new FileInputStream("./data/application.properties");
            FileInputStream email = new FileInputStream("./data/email.properties");
            FileInputStream schedule = new FileInputStream("./data/schedule.properties");
            PROPS.load(app);
            PROPS.load(email);
            PROPS.load(schedule);
        } catch (FileNotFoundException ex) {
            LogFile.getLogFile().log("Properties file not found.", ex);
        } catch (IOException ioex){
            LogFile.getLogFile().log("Failed to read in properties.", ioex);
        }
    }

    /** Private Constructor - this class should never be instantiated. */
    private OamTools(){
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
     *
     * @param filename The filename of the email list.
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
     *
     * @param day a string representing the day of the week.  The only valid values are:  'mon', 'tue', 'wed', 'thu',
     * 'fri', 'sat' and 'sun'.  If any other value is provided, an IllegalStateException will be thrown.
     *
     * @throws IllegalStateException if an invalid day string is provided.
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

    /**
     * Returns true if the provided date is a first or 3rd Sunday, false otherwise.  Assumes the date supplied IS in
     * fact a valid day.  This method provides no error checking on its input.
     *
     * @param date the date of the next upcoming day of interest.
     * @return true if first or third date provided, false otherwise.
     */
    public static boolean isFirstOrThirdDay(int date){
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


    /**
     * Builds and returns an email sender that sends out the agenda.
     *
     * @param props the properties object for sending the email.
     * @param subject The subject of the email to be sent.  This parameter can be null.  If it is, a default subject
     * will be used:  "This week's [Agenda Title]", or "This week's agenda" if no agenda title is specified in the
     * program's properties file.
     *
     * @return an email sender that sends out the agenda.
     */
    public static EmailSenderRunnable buildAgendaEmailSender(String subject){
        if (subject == null){
            subject = "This week's " + PROPS.getProperty("agenda.title", "agenda");
        }
        String body = "Here is this week's agenda.  This is an automated email.  Please to not reply to this message.";

        List<String> attachments = new ArrayList<>();
        attachments.add(PROPS.getProperty("doc.name", "agenda.pdf"));

        return new EmailSenderRunnable(subject, body, attachments);
    }

    /**
     * Builds and returns an email sender that sends out the agenda.
     *
     * @param props the properties object for sending the email.
     * @return an email sender that sends out the agenda.
     */
    public static EmailSenderRunnable buildReminderSender(){
        System.out.println("Building reminder sender...");
        String agendaName = PROPS.getProperty("agenda.title");
        String subject = PROPS.getProperty("reminder.subject", agendaName + " reminder.");
        String body = PROPS.getProperty("reminder.body");

        return new EmailSenderRunnable(subject, body, null);
    }

    /**
     * Formats and returns a string that represents the current date in this format:  MMM.dd.YYYY.
     *
     * @param date the {@link java.util.Date} to format.  If a null date is provided the empty String is returned.
     * @return a formatted date string for presentation to a user.
     */
    public static String getFormattedDateString(Date date){
        if (date == null){
            return "";
        }
        return m_dateFormat.format(date);
    }

}