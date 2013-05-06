package openagendamail.util.email;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class that represents an Email.  It includes its subject, body and a list of email
 * addresses, (recipients).
 *
 * @author adam
 * @date May 5th, 2013
 */
public class Email {

    /** The subject of the email. */
    private String m_subject;

    /** The body of the email message. */
    private String m_body;

    /** A Map of {@link RecipientType}s to Sets of email addresses (as Strings). */
    private Map<RecipientType, Set<String>> m_recipients;

    /** A list of paths to files to attach. */
    private Set<String> m_attachments;

    /**
     * Constructs a new Email.
     *
     * @param subject the subject of the Email message.
     * @param body the body of the Email message, plaintext.
     */
    public Email(String subject, String body){
        if (subject == null || subject.isEmpty()){
            m_subject = "<no subject>";
        } else {
            m_subject = subject;
        }

        if (body == null || body.isEmpty()){
            m_body = "<no body>";
        } else {
            m_body = body;
        }

        // Initialize data structures.
        m_attachments = new HashSet<>();
        m_recipients = new HashMap<>();
        m_recipients.put(RecipientType.TO, new HashSet<String>());
        m_recipients.put(RecipientType.CC, new HashSet<String>());
        m_recipients.put(RecipientType.BCC, new HashSet<String>());
    }

    /**
     * Returns the subject of the email.
     * @return the subject of the email.
     */
    public String getSubject(){
        return m_subject;
    }

    /**
     * Returns the body of this email in plaintext.
     * @return the body of this email in plaintext.
     */
    public String getBody(){
        return m_body;
    }

    /**
     * Add a recipient to this email.
     *
     * @param recipient the email address to send this email to.
     * @param type the type of recipient to attach.
     */
    public void addRecipient(String recipient, RecipientType type){
        m_recipients.get(type).add(recipient);
    }

    /**
     * Returns the recipients that match the appropriate type.
     *
     * @param type the type of recipient to get.
     * @return a Set of email addresses.
     */
    public Set<String> getRecipients(RecipientType type){
        return Collections.unmodifiableSet(m_recipients.get(type));
    }

    /**
     * Returns a Set of all recipients to this email, regardless of RecipientType.
     * @return a Set of all recipients to this email, regardless of RecipientType.
     */
    public Set<String> getAllRecipients(){
        Set<String> recipients = new HashSet<>();
        recipients.addAll(m_recipients.get(RecipientType.TO));
        recipients.addAll(m_recipients.get(RecipientType.CC));
        recipients.addAll(m_recipients.get(RecipientType.BCC));

        return Collections.unmodifiableSet(recipients);
    }

    /**
     * Adds a file to be attached to the email.
     * @param file the path to the file to add.
     */
    public void addAttachment(String file){
        m_attachments.add(file);
    }

    /**
     * Returns the attachments for this email, if any exist.  If none exist an empty list is returned.
     *
     * @return a Set of Strings that represent {@link Path}s to files to be attached to this email, if any exist.
     * If none exist an empty list is returned.
     */
    public Set<String> getAttachments(){
        return Collections.unmodifiableSet(m_attachments);
    }
}