package openagendamail;

import com.sun.mail.imap.IMAPStore;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import openagendamail.file.LogFile;
import openagendamail.util.OpenAgendaMailTools;

/**
 * Sends The Agenda generated by the BuildAgendaRunnable.
 *
 * @author adam
 * Created:       Jan 28, 2013
 * Last Updated:  Feb 5th, 2013
 */
public class SendAgendaRunnable implements Runnable {

    /** Application Properties. */
    private Properties m_props;

    /** The subject for the email. */
    private String m_emailSubject;

    /**
     * Constructor.
     * @param props the application properties.
     */
    public SendAgendaRunnable(Properties props){
        this(props, null);
    }

    /**
     * Constructor.
     * @param props the properties required for this application.
     * @param emailSubject the subject line for the email to be sent.
     */
    public SendAgendaRunnable(Properties props, String emailSubject){
        if (props == null){
            throw new IllegalArgumentException("Parameter 'props' cannot be null.");
        }
        m_props = props;

        // Set Subject for email being sent.
        if (emailSubject == null){
            m_emailSubject = "This week's Agenda for " + m_props.getProperty("agenda.title", "Agenda");
        } else {
            m_emailSubject = emailSubject;
        }

        // Add special properties for using SMTP / sending mail.
        m_props.put("mail.store.protocol", "imaps");
        m_props.put("mail.smtp.starttls.enable", "true");
        m_props.put("mail.smtp.auth", "true");
        m_props.put("mail.smtp.host", "smtp.gmail.com");
        m_props.put("mail.transport.protocol", "smtp");
        m_props.put("mail.smtp.user", m_props.get("email"));
        m_props.put("mail.smtp.password", m_props.get("password"));
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        try {
            // SEND AGENDA EMAIL
            List<String> emails = OpenAgendaMailTools.readEmails(m_props.getProperty("email.list.filename", "emails.txt"));

            // --- Connect to the email account.
            LogFile.getLogFile().log("Connecting to email account...");
            Session session = Session.getDefaultInstance(m_props);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", m_props.getProperty("email"), m_props.getProperty("password"));
            LogFile.getLogFile().log("Connected successfully.");

            if (store instanceof IMAPStore){
                IMAPStore imapStore = (IMAPStore)store;
                Folder inbox = imapStore.getFolder("inbox");
                inbox.open(Folder.READ_WRITE);

                // --- Define message
                LogFile.getLogFile().log("Constructing email message....");
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(m_props.getProperty("email")));


                LogFile.getLogFile().log("Adding email recipients...");
                for (String email : emails){
                    message.addRecipient(Message.RecipientType.BCC, new InternetAddress(email));
                }
                message.setSubject(m_emailSubject);


                // --- Build Email Message
                LogFile.getLogFile().log("Adding body text to email...");
                MimeBodyPart bodyText = new MimeBodyPart();
                bodyText.setText("Here is this week's agenda.  This is an automated email.  Please to not reply to this message.");

                LogFile.getLogFile().log("Attaching agenda...");
                MimeBodyPart attachment = new MimeBodyPart();
                attachment.attachFile(m_props.getProperty("doc.name", "agenda.docx"));

                MimeMultipart multipart = new MimeMultipart("related");
                multipart.addBodyPart(bodyText);
                multipart.addBodyPart(attachment);
                message.setContent(multipart);

                LogFile.getLogFile().log("Closing the email account.");
                store.close();

                // --- Send the message.
                LogFile.getLogFile().log("Sending the message...");
                Transport transport = session.getTransport();
                // Uses port 587 because we're using TLS/STARTTLS, its 465 for SSL
                transport.connect("smtp.gmail.com", 587, m_props.getProperty("email"), m_props.getProperty("password"));
                transport.sendMessage(message, message.getAllRecipients());
                LogFile.getLogFile().log("Message(s) sent successfully.");

            } else {
                LogFile.getLogFile().log("Message store was not an IMAP Message store.  No messages retrieved.");
            }
        } catch (NoSuchProviderException ex) {
            LogFile.getLogFile().log("Couldn't find the mail provider.", ex);
        } catch (MessagingException ex) {
            LogFile.getLogFile().log("Message exception while initializing store", ex);
        } catch (IOException ex) {
            LogFile.getLogFile().log("Error reading emails from file.", ex);
        }
    }
}