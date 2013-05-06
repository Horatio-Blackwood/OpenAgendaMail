package openagendamail.util.email;

/**
 * A class that encapsulates an email account and its required credentials.
 * @author adam
 * @date May 5th, 2013
 */
public class EmailAccount {

    /** The email address for this EmailAccount. */
    private String m_emailAddress;

    /** The password for this email account. */
    private String m_password;

    /**
     * Constructs a new EmailAccount.  Note:  no special consideration for security is provided within this class for
     * protecting passwords etc.  Use this code carefully.
     *
     * @param address the address for the email account.
     * @param password the password associated with this account.
     */
    public EmailAccount(String address, String password){
        if (address == null || address.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'address' cannot be null or empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'password' cannot be null or empty.");
        }
        m_emailAddress = address;
        m_password = password;
    }

    /**
     * Returns the email address stored in this EmailAccount.
     * @return the email address stored in this EmailAccount.
     */
    public String getAddress(){
        return m_emailAddress;
    }

    /**
     * Returns the password for this EmailAccount.
     * @return the password for this EmailAccount.
     */
    public String getPassword(){
        return m_password;
    }
}