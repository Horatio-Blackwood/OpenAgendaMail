package openagendamail.data;

import java.util.Objects;

/**
 * A class that represents an agenda item.  It is comprised of four strings, the email address, the name of the person
 * who submitted the item, the item's title and its body.
 *
 * @author adam
 * @date Feb 8, 2013.
 */
public class AgendaItem implements Comparable<AgendaItem> {

    /** The email of the user that submitted this AgendaItem. */
    private String m_email;

    /** The name of the user who submitted this AgendaItem. */
    private String m_user;

    /** The title of the AgendaItem.  */
    private String m_title;

    /** The body of the email AgendaItem.  */
    private String m_body;

    /**
     * Constructor. Creates an AgendaItem with the provided email, user and title.  AgendaItem's constructed with this
     * constructor will not have a body.
     *
     * @param email the email address of the person who submitted this AgendaItem.
     * @param user the name associated with the email account of the person who submitted this AgendaItem.
     * @param title the title of this AgendaItem.
     */
    public AgendaItem(String email, String user, String title){
        this(email, user, title, "");
    }

    /**
     * Constructs a new AgendaItem with the supplied email, username, title and body.
     *
     * @param email the email address of the person who submitted this AgendaItem.
     * @param user the name associated with the email account of the person who submitted this AgendaItem.
     * @param title the title of this AgendaItem.
     * @param body the body of the AgendaItem if any.
     */
    public AgendaItem(String email, String user, String title, String body){
        m_email = email;
        m_user = user;
        m_title = title;
        m_body = body;
    }

    /**
     * Returns the email of this AgendaItem.  This method may return null.
     * @return the email of this AgendaItem.
     */
    public String getEmail(){
        return m_email;
    }

    /**
     * Returns the user associated with this AgendaItem.  This method may return null.
     * @return the user.
     */
    public String getUser(){
        return m_user;
    }

    /**
     * Returns the title of this AgendaItem.  This method may return null.
     * @return the title.
     */
    public String getTitle(){
        return m_title;
    }

    /**
     * Returns the body of this AgendaItem, if any.  This method may return null.
     * @return the body.
     */
    public String getBody(){
        return m_body;
    }

    /** {@link inheritDoc} */
    @Override
    public int compareTo(AgendaItem otherItem) {
        // Compare by sender's email.  If the emails are the same, sort by subject line.
         if (getEmail().equals(otherItem.getEmail())){
             return getTitle().compareTo(otherItem.getTitle());
         } else {
             return getEmail().compareTo(otherItem.getEmail());
         }
    }

    /** {@link inheritDoc} */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.m_email);
        hash = 97 * hash + Objects.hashCode(this.m_user);
        hash = 97 * hash + Objects.hashCode(this.m_title);
        hash = 97 * hash + Objects.hashCode(this.m_body);
        return hash;
    }

    /** {@link inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AgendaItem other = (AgendaItem) obj;
        if (!Objects.equals(this.m_email, other.m_email)) {
            return false;
        }
        if (!Objects.equals(this.m_user, other.m_user)) {
            return false;
        }
        if (!Objects.equals(this.m_title, other.m_title)) {
            return false;
        }
        if (!Objects.equals(this.m_body, other.m_body)) {
            return false;
        }
        return true;
    }
}