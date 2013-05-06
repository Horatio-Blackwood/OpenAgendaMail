package openagendamail;

import java.util.List;
import openagendamail.file.LogFile;
import openagendamail.util.email.Email;
import openagendamail.util.email.EmailAccount;
import openagendamail.util.email.EmailSender;

/**
 * This class is a general purpose email sender.  It allows you to build and send emails with variable subjects, body
 * text and attachments to the email list.
 *
 * @author adam
 * @date February 17, 2012.
 */
public class EmailSenderRunnable implements Runnable {

    /** The EmailAccount to send the message from. */
    private EmailAccount m_account;

    /** The email message to send. */
    private Email m_message;

    /** A list of attachments (file paths) to add to the email when it is sent. */
    private List<String> m_attachments;

    /**
     * Constructs a new EmailSenderRunnable.
     *
     * @param subject the subject of the email to send.
     * @param body the body of the email, if any.  This value may be null.
     * @param attachments a list of Strings that are the paths to files that should be attached.  Null is a permitted
     * value for this parameter.
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
            messageSent = sender.sendEmail(m_message);
        } while ((messageSent == false) && (attempts < 3));
    }
}