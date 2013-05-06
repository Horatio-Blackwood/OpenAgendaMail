package openagendamail.data;

import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the EmailAgendaItemProiderTest.
 * @author adam
 */
public class EmailAgendaItemProviderTest {

    public EmailAgendaItemProviderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.out.println("EmailAgendaItemProviderTest");
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

    /**
     * Test of getItems method, of class EmailAgendaItemProvider.
     */
    @Test
    public void testGetItems() {
        System.out.println(" --- getItems");
        AgendaItem item1 = new AgendaItem("oam.test@gmail.com", "OAM Test", "Test Email 1", "This is the body of the first test email.");
        AgendaItem item2 = new AgendaItem("oam.test@gmail.com", "OAM Test", "Test Email 2", "This is the body of the second test email.");

        EmailAgendaItemProvider provider = new EmailAgendaItemProvider(true);
        Collection<AgendaItem> result = provider.getItems();

        assertTrue(result.contains(item1));
        assertTrue(result.contains(item2));
    }
}