package openagendamail.data;

/**
 * A class that represents an agenda item.  It is comprised of four strings, the email address, the name of the person
 * who submitted the item, the item's title and its body.
 *
 * @author adam
 * @date Feb 3, 2013.
 */
public class AgendaItem {

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
}