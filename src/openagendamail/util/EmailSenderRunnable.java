package openagendamail;

import java.util.logging.Level;
import java.util.logging.Logger;
import openagendamail.file.LogFile;
import openagendamail.util.email.Email;
import openagendamail.util.email.EmailAccount;
import openagendamail.util.email.EmailSender;

/**
 * This class is a general purpose email sender.  It allows you to build and send emails with variable subjects, body
 * text and attachments to the email list.
 *
 * @author adam
 * @date February 17, 2013.
 * Last Modified:  May 6th, 2013
 */
public class EmailSenderRunnable implements Runnable {

    /** The EmailAccount to send the message from. */
    private EmailAccount m_account;

    /** The email message to send. */
    private Email m_message;

    /**
     * Constructs a new EmailSenderRunnable.
     *
     * @param account the email account to send the email from.
     * @param message the message to send.
     */
    public EmailSenderRunnable(EmailAccount account, Email message){
        if (account == null){
            throw new IllegalArgumentException("Parameter 'EmailAccount' cannot be null.");
        }
        if (message == null){
            throw new IllegalArgumentException("Parameter 'message' cannot be null.");
        }
        m_account = account;
        m_message = message;
        LogFile.getLogFile().log("Initializing the email sender for " + message.getSubject());
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        EmailSender sender = new EmailSender(m_account);

        // Try to send the email three times.
        int attempts = 0;
        boolean messageSent = false;
        do {
            attempts += 1;
            if (attempts > 0 && attempts < 3) {
                try {
                    LogFile.getLogFile().log("Failed to send email on first attempt.  Will retry after five seconds.");
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    // Do nothing.
                }
            }
            messageSent = sender.sendEmail(m_message);
        } while ((messageSent == false) && (attempts < 3));
    }
}