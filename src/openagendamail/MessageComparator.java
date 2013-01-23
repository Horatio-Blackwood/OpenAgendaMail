package openagendamail;

import com.sun.mail.imap.IMAPMessage;
import java.util.Comparator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import openagendamail.file.LogFile;

/**
 * A class for comparing the 
 * 
 * @author adam
 * @date Jan 6th, 2013
 */
public class MessageComparator implements Comparator<Message> {
   
    /** Constructor. */
    public MessageComparator(){
    }

    /** 
     * {@inheritDoc}
     * @param message1 the first message to compare.
     * @param message2 the second message to compare.
     */
    @Override
    public int compare(Message message1, Message message2) {
        if (message1 instanceof IMAPMessage && message2 instanceof IMAPMessage){
            IMAPMessage msg1 = (IMAPMessage)message1;
            IMAPMessage msg2 = (IMAPMessage)message2;
            
            try {
                if (msg1.getSender() instanceof InternetAddress && msg2.getSender() instanceof InternetAddress){
                    InternetAddress msg1Sender = (InternetAddress)msg1.getSender();
                    InternetAddress msg2Sender = (InternetAddress)msg2.getSender();
                    
                    // Compare by sender's email.  If the emails are the same, sort by subject line.
                    if (msg1Sender.getAddress().equals(msg2Sender.getAddress())){
                        return msg1.getSubject().compareTo(msg2.getSubject());
                    } else {
                        return msg1Sender.getAddress().compareTo(msg2Sender.getAddress().toString());
                    }
                    
                } else {
                    LogFile.getLogFile().log("Attempted to compare two IMAPMessages that had at least one non-InternetAddress Sender.");
                }
            } catch (MessagingException ex) {
                LogFile.getLogFile().log("Error comparing messages:  Message 1:  " + message1 + " and message 2 is:  " + message2, ex);
                return 0;
            }
        } else {
            LogFile.getLogFile().log("Attempted to compare two messages that were not IMAPMessages.");
        }
        
        // If we get here, just return that they are equal.
        return 0;
    }
}