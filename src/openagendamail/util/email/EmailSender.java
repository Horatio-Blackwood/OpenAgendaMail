package openagendamail.util.email;

import com.sun.mail.imap.IMAPStore;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
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

/**
 * A class that handles the sending of emails from the specified {@link EmailAccount}.
 *
 * @author adam
 * @date May 5th, 2013
 */
public class EmailSender {

    /** The email account to send emails from. */
    private EmailAccount m_account;

    /** The Properties  object that contains informMaation for sending emails. */
    private Properties m_properties;

    /**
     * Constructs a new EmailSender.
     * @param account the {@link EmailAccount} to send the emails from.
     */
    public EmailSender(EmailAccount account){
        if (account == null){
            throw new IllegalArgumentException("Parameter 'account' may not be null.");
        }
        m_account = account;

        // Add special properties for using SMTP / sending mail.
        m_properties = new Properties();
        m_properties.put("mail.store.protocol", "imaps");
        m_properties.put("mail.smtp.starttls.enable", "true");
        m_properties.put("mail.smtp.auth", "true");
        m_properties.put("mail.smtp.host", "smtp.gmail.com");
        m_properties.put("mail.transport.protocol", "smtp");
        m_properties.put("mail.smtp.user", m_account.getAddress());
        m_properties.put("mail.smtp.password", m_account.getPassword());
    }

    /**
     * Sends the provided {@link Email}.
     * @param email the Email to send.
     */
    public boolean sendEmail(Email email){
        try {
            // --- Connect to the email account.
            LogFile.getLogFile().log("Connecting to email account...");
            Session session = Session.getDefaultInstance(m_properties);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", m_account.getAddress(), m_account.getPassword());
            LogFile.getLogFile().log("Connected successfully.");

            // Get the email message store.
            if (store instanceof IMAPStore){
                IMAPStore imapStore = (IMAPStore)store;
                Folder inbox = imapStore.getFolder("inbox");
                inbox.open(Folder.READ_WRITE);

                // Assemble the message to be sent.
                MimeMessage message = buildEmail(session, email);

                // Close the message store.
                LogFile.getLogFile().log("Closing Message store.");
                store.close();

                // Send the message.
                LogFile.getLogFile().log("Sending the message...");
                Transport transport = session.getTransport();

                // Uses port 587 because we're using TLS/STARTTLS, its 465 for SSL
                transport.connect("smtp.gmail.com", 587, m_account.getAddress(), m_account.getPassword());
                transport.sendMessage(message, message.getAllRecipients());
                LogFile.getLogFile().log("Message(s) sent successfully.");

                return true;

            } else {
                LogFile.getLogFile().log("Message store was not an IMAP Message store.  No messages retrieved.");

                return false;
            }
        } catch (NoSuchProviderException ex) {
            LogFile.getLogFile().log("Couldn't find the mail provider.", ex);
            return false;
        } catch (MessagingException ex) {
            LogFile.getLogFile().log("Message exception while initializing store", ex);
            return false;
        }
    }


    /**
     * Builds the message to be sent.
     *
     * @param session The email session to use to build and send the messages.
     *
     * @return a fully assembled and ready to send MimeMessage.
     * @throws MessagingException if an error occurs when assembling a message.
     */
    private MimeMessage buildEmail(Session session, Email email) throws MessagingException{
        // --- Define message
        LogFile.getLogFile().log("Constructing email message....");
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(m_account.getAddress()));

        // Add Subject, Body, and Attachments.
        message.setSubject(email.getSubject());
        MimeMultipart multipart = new MimeMultipart("related");
        LogFile.getLogFile().log("Adding body text to email...");

        MimeBodyPart bodyText = new MimeBodyPart();
        bodyText.setText(email.getBody());
        multipart.addBodyPart(bodyText);

        LogFile.getLogFile().log("Attaching attachment files...");
        for (String attachment : email.getAttachments()){
            try {
                MimeBodyPart att = new MimeBodyPart();
                att.attachFile(attachment);
                multipart.addBodyPart(att);
            } catch (IOException ex) {
                LogFile.getLogFile().log("Error attaching '" + attachment + "' file to email.", ex);
            }
        }
        message.setContent(multipart);

        // Add Recipients
        LogFile.getLogFile().log("Adding " + email.getAllRecipients().size() + " email recipients...");
        for (String address : email.getRecipients(RecipientType.BCC)){
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(address));
        }
        for (String address : email.getRecipients(RecipientType.CC)) {
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(address));
        }
        for (String address : email.getRecipients(RecipientType.TO)) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
        }
        return message;
    }
}