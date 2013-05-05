package openagendamail.data;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adam
 */
public class AgendaItemTest {

    private AgendaItem m_item1;
    private AgendaItem m_item2;

    public AgendaItemTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.out.println("Running tests for AgendaItem");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        m_item1 = new AgendaItem("email", "user", "title", "body");
        m_item2 = new AgendaItem("zemail", "zuser", "ztitle", "zbody");
    }

    @After
    public void tearDown() {
    }

    /** Test of getEmail method, of class AgendaItem. */
    @Test
    public void testGetEmail() {
        System.out.println(" --- getEmail");

        String expResult1 = "email";
        String expResult2 = "zemail";
        String result1 = m_item1.getEmail();
        String result2 = m_item2.getEmail();
        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);
    }

    /** Test of getUser method, of class AgendaItem. */
    @Test
    public void testGetUser() {
        System.out.println(" --- getUser");

        String expResult1 = "user";
        String expResult2 = "zuser";
        String result1 = m_item1.getUser();
        String result2 = m_item2.getUser();
        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);
    }

    /** Test of getTitle method, of class AgendaItem. */
    @Test
    public void testGetTitle() {
        System.out.println(" --- getTitle");

        String expResult1 = "title";
        String expResult2 = "ztitle";
        String result1 = m_item1.getTitle();
        String result2 = m_item2.getTitle();
        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);
    }

    /** Test of getBody method, of class AgendaItem. */
    @Test
    public void testGetBody() {

        String expResult1 = "body";
        String expResult2 = "zbody";
        String result1 = m_item1.getBody();
        String result2 = m_item2.getBody();
        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);
    }

    /** Test of compareTo method, of class AgendaItem. */
    @Test
    public void testCompareTo() {
        System.out.println(" --- compareTo");

        assertTrue(0 > m_item1.compareTo(m_item2));
        assertEquals(0, m_item1.compareTo(m_item1));
        assertEquals(0, m_item2.compareTo(m_item2));
        assertTrue(0 < m_item2.compareTo(m_item1));
    }

    /** Test of hashCode method, of class AgendaItem. */
    @Test
    public void testHashCode() {
        System.out.println(" --- hashCode");
        assertEquals(m_item1.hashCode(), m_item1.hashCode());
        assertEquals(m_item2.hashCode(), m_item2.hashCode());
        assertFalse(m_item1.hashCode() == m_item2.hashCode());
    }

    /** Test of equals method, of class AgendaItem. */
    @Test
    public void testEquals() {
        assertEquals(m_item1, new AgendaItem("email", "user", "title", "body"));
        assertEquals(m_item2, new AgendaItem("zemail", "zuser", "ztitle", "zbody"));
    }
}
