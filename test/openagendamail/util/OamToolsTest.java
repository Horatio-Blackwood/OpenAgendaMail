package openagendamail.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for the OamTools class.
 * @author adam
 */
public class OamToolsTest {

    /** Constructor. */
    public OamToolsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.out.println("OamToolsTest");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /** Test of readEmails method, of class OamTools. */
    @Test
    public void testReadEmails() {
        System.out.println(" --- readEmails");
        String filename = "test/openagendamail/test-emails.txt";
        List<String> result = OamTools.readEmails(filename);
        assertTrue(result.contains("email_1@gmail.com"));
        assertTrue(result.contains("a_second_email@hotmail.com"));
        assertEquals(2, result.size());
    }

    /** Test of getDayOfWeek method, of class OamTools. */
    @Test
    public void testGetDayOfWeek() {
        System.out.println(" --- getDayOfWeek");
        assertEquals(Calendar.MONDAY, OamTools.getDayOfWeek("mon"));
        assertEquals(Calendar.TUESDAY, OamTools.getDayOfWeek("tue"));
        assertEquals(Calendar.WEDNESDAY, OamTools.getDayOfWeek("wed"));
        assertEquals(Calendar.THURSDAY, OamTools.getDayOfWeek("thu"));
        assertEquals(Calendar.FRIDAY, OamTools.getDayOfWeek("fri"));
        assertEquals(Calendar.SATURDAY, OamTools.getDayOfWeek("sat"));
        assertEquals(Calendar.SUNDAY, OamTools.getDayOfWeek("sun"));
    }

    /** Tests when invalid strings are provided to the getDayOfWeek method. */
    @Test (expected=IllegalStateException.class)
    public void testGetDayOfWeekInvalid(){
        OamTools.getDayOfWeek("bad-day");
    }

    /** Test of isFirstOrThirdDay method, of class OamTools. */
    @Test
    public void testIsFirstOrThirdDay() {
        System.out.println(" --- isFirstOrThirdDay");
        assertTrue(OamTools.isFirstOrThirdDay(1));
        assertTrue(OamTools.isFirstOrThirdDay(4));
        assertTrue(OamTools.isFirstOrThirdDay(15));
        assertTrue(OamTools.isFirstOrThirdDay(20));

        assertFalse(OamTools.isFirstOrThirdDay(8));
        assertFalse(OamTools.isFirstOrThirdDay(12));
        assertFalse(OamTools.isFirstOrThirdDay(22));
        assertFalse(OamTools.isFirstOrThirdDay(30));
    }

    /** Test of getFormattedDateString method, of class OamTools. */
    @Test
    public void testGetFormattedDateString() {
        System.out.println(" --- getFormattedDateString");

        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 10);
        cal.set(Calendar.YEAR, 1988);
        Date date = cal.getTime();

        String expResult = "Jan.10.1988";
        String result = OamTools.getFormattedDateString(date);
        assertEquals(result, expResult);
    }
    
    
    @Test
    public void testGetCountdownString(){
        System.out.println(" --- getCountdownString()");
        
        // one minute
        long one = 60L;
        assertEquals("0 days, 0 hours, 1 minute, and 0 seconds.", OamTools.getCountdownString(one));
        
        // one minute, 30 seconds
        long two = 90L;
        assertEquals("0 days, 0 hours, 1 minute, and 30 seconds.", OamTools.getCountdownString(two));
        
        // one hour
        long three = 60 * 60L;
        assertEquals("0 days, 1 hour, 0 minutes, and 0 seconds.", OamTools.getCountdownString(three));
        
        // One hour, one minute
        long four = 60 * 60 + 60L;
        assertEquals("0 days, 1 hour, 1 minute, and 0 seconds.", OamTools.getCountdownString(four));
        
        // One hour, one minute and 20 seconds
        long five = 60 * 60 + 80L;
        assertEquals("0 days, 1 hour, 1 minute, and 20 seconds.", OamTools.getCountdownString(five));
        
        // One day
        long six = 60 * 60 * 24L;
        assertEquals("1 day, 0 hours, 0 minutes, and 0 seconds.", OamTools.getCountdownString(six));
        
        // One day one hour
        long seven = 60 * 60 * 24 + (60 * 60);
        assertEquals("1 day, 1 hour, 0 minutes, and 0 seconds.", OamTools.getCountdownString(seven));
        
        // 3 days, sixteen hours, one minute and 15 seconds
        long eight = (3 * 60 * 60 * 24) + (60 * 60 * 16) + 75;
        assertEquals("3 days, 16 hours, 1 minute, and 15 seconds.", OamTools.getCountdownString(eight));
    }
}