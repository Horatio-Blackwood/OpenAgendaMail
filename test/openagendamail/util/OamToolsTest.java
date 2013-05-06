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
}