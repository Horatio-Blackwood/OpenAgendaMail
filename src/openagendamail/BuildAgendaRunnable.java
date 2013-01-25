package openagendamail;

import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPStore;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import openagendamail.file.LogFile;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * A runnable that build the agenda document from the emails.
 * 
 * @author adam
 * @date Dec 30, 2012
 * Last updated:  Jan 25, 2013
 */
public class BuildAgendaRunnable implements Runnable {
    
    /** A constant for the Content Type. */
    private static final String PLAIN_TEXT = "TEXT/PLAIN";

    /** Application Properties. */
    private static Properties m_props;
    
    /** True if the emails should be deleted after generating the agenda document. */
    private static boolean m_deleteEmails;
    
    /** The messages from the email account. */
    private static Message[] m_messages;
    
    /** A formatter for date objects used when generating the .doc object. */
    private static SimpleDateFormat m_dateFormat;
    
    /**
     * Constructor.  Creates a new CheckMailRunnable.
     * @param properties the application's properties.
     * @param deleteEmails true if the emails should be deleted after building the agenda, false otherwise.
     */
    public BuildAgendaRunnable(Properties properties, boolean deleteEmails) {
        if (properties == null) {
            throw new IllegalArgumentException("Parameter 'properties' cannot be null.");
        }
        m_props = properties;
        m_deleteEmails = deleteEmails;
        m_props.put("mail.store.protocol", "imaps");
        m_dateFormat = new SimpleDateFormat("MMM.DD.YYY");
    }
    
    /** {@inheritDoc} */
    @Override
    public void run() {
        Store store = null;
        try {
            // Fetch the mail from the account.
            LogFile.getLogFile().log("Connecting to email account...");
            Session session = Session.getDefaultInstance(m_props, null);
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", m_props.getProperty("email"), m_props.getProperty("password"));
            LogFile.getLogFile().log("Successfully connected.");
            
            if (store instanceof IMAPStore){
                IMAPStore imapStore = (IMAPStore)store;
                
                LogFile.getLogFile().log("Retrieving emails from inbox...");
                Folder inbox = imapStore.getFolder("inbox");
                inbox.open(Folder.READ_WRITE);
                m_messages = inbox.getMessages();
                LogFile.getLogFile().log(m_messages.length + " messages successfully retrieved.\n\n");
    
            } else {
                LogFile.getLogFile().log("Message store was not an IMAP Message store.  No messages retrieved.");
            }
        } catch (NoSuchProviderException ex) {
            LogFile.getLogFile().log("Couldn't find the mail provider.", ex);
        } catch (MessagingException ex) {
            LogFile.getLogFile().log("Message exception while initializing store", ex);
        }
        
        // Using the messages retrieved, build the word doc.
        LogFile.getLogFile().log("Generating Agenda Word document.");
        generateAgendaDocx(store);
        
        // DELETE MESSAGES
        deleteEmails(store);

    }
    
    /**
     * Deletes the emails in the account and closes the message store.
     * @param store the message store to close.
     */
    private static void deleteEmails(Store store){
        try {
            if (m_deleteEmails){
                for (Message msg : m_messages){
                    msg.setFlag(Flags.Flag.DELETED, true);
                }                
            }
        } catch (MessagingException ex) {
            LogFile.getLogFile().log("Error deleting mesesages", ex);
        }

        // Close the store if it was initialized.
        try {
            if (store != null){
                LogFile.getLogFile().log("Closing the connection to the email account.\n\n");
                store.close();            
            }
        } catch (MessagingException ex) {
                LogFile.getLogFile().log("Error closing message store.", ex);
        }
    }
    
    /**
     * Generates the agenda document as a .docx file and writes it out to file.
     * @param store the message store that contains the email messages.
     */
    private static void generateAgendaDocx(Store store){
        LogFile.getLogFile().log("Inside generate doc method...");
        try {
            XWPFDocument document = new XWPFDocument();
            XWPFParagraph p1 = document.createParagraph();
            p1.setAlignment(ParagraphAlignment.LEFT);

            LogFile.getLogFile().log("Adding header...");
            XWPFRun p1r1 = p1.createRun();
            p1r1.setBold(true);
            p1r1.setFontSize(16);
            p1r1.setText(m_props.getProperty("agenda.title", "Agenda"));
            p1r1.addBreak();
            
            XWPFRun p1r2 = p1.createRun();
            p1r2.setFontSize(10);
            p1r2.setText("This document generated By OpenAgendaMail v" + OpenAgendaMail.VERSION + " on:  " + m_dateFormat.format(new Date()));
            p1r2.addBreak();
            p1r2.addBreak();
            
            LogFile.getLogFile().log("Generating agenda topics from emails...");
            if (m_messages.length == 0){
                XWPFRun tmpRun = p1.createRun();
                tmpRun.setText("No agenda items have been submitted this week.");
                tmpRun.addBreak();
            } else {
                
                // Sort the Messages by sender.
                List<Message> messages = Arrays.asList(m_messages);
                Collections.sort(Arrays.asList(m_messages), new MessageComparator());
                
                for (Message message : messages){
                    if (message instanceof IMAPMessage){
                        IMAPMessage msg = (IMAPMessage)message;
                        XWPFParagraph tmpPar = document.createParagraph();
                        try {
                            // AGENDA TOPIC (email Subject Line)
                            XWPFRun tpr1 = tmpPar.createRun();
                            tpr1.setBold(true);
                            tpr1.setFontSize(14);
                            tpr1.setText(msg.getSubject());
                            tpr1.addBreak();

                            // SUBMITTED BY (Who sent the email)
                            if (msg.getSender() instanceof InternetAddress){
                                InternetAddress address = (InternetAddress)msg.getSender();
                                XWPFRun tmpRun = tmpPar.createRun();
                                tmpRun.setFontSize(10);
                                
                                // Fetch sender's name.
                                String name = "";
                                if (address.getPersonal() != null){
                                    name = address.getPersonal();
                                }
                                
                                tmpRun.setText("Submitted By:  " + name + " (" + address.getAddress() + ")");
                                tmpRun.addBreak();
                                tmpRun.addBreak();
                            } else {
                                XWPFRun tmp = tmpPar.createRun();
                                tmp.setText(msg.getSender().toString());
                                tmp.addBreak();
                                tmp.addBreak();
                            }

                            // MESSAGE CONTENTS (If any)
                            if (msg.getContent() != null){

                                if (msg.getContent() instanceof MimeMultipart){
                                    MimeMultipart mmp = (MimeMultipart)msg.getContent();
                                    int bodyParts = mmp.getCount();

                                    for (int i = 0; i < bodyParts; i++){
                                        BodyPart bp = mmp.getBodyPart(i);
                                        if (bp.getContentType().trim().startsWith(PLAIN_TEXT)){
                                            XWPFRun contents = tmpPar.createRun();
                                            contents.setText(bp.getContent().toString());        
                                        }
                                    }
                                }
                            }

                            XWPFRun tpr2 = tmpPar.createRun();
                            tpr2.addBreak();
                            tpr2.addBreak();
                        } catch (MessagingException ex) {
                            LogFile.getLogFile().log("Error parsing messages to create agenda word doc.", ex);
                        }
                    }
                }                
            }
            LogFile.getLogFile().log("Document generated.");
            
            // WRITE WORD DOC OUT TO DISK
            LogFile.getLogFile().log("Writing word doc to file...");
            OutputStream out = new FileOutputStream(m_props.getProperty("doc.name", "agenda.docx"));
            document.write(out); 
            out.flush();
            out.close();
            LogFile.getLogFile().log("Document successfully written to file.");

        } catch (IOException ioex) {
            LogFile.getLogFile().log("Error writing word document.", ioex);
        }
        
        LogFile.getLogFile().log("Exiting agenda generation method.");
    }
}