package openagendamail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import openagendamail.data.AgendaItem;
import openagendamail.data.AgendaItemProvider;
import openagendamail.util.OamTools;
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
public class BuildAgendaRunnableTest {

    /** Tests for the BuildAgendaRunnable. */
    public BuildAgendaRunnableTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.out.println("BuildAgendaRunnableTest");
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
     * Test of run method, of class BuildAgendaRunnable.
     *
     * This test cannot verify the contents of the PDF files, so it simply creates the Agenda documents and verifies
     * that the files are created.  It is up to the individual running the tests to verify the contents of the agendas
     * are created appropriately.
     */
    @Test
    public void testRun() throws IOException {
        System.out.println(" --- run");

        // Simple.
        AgendaItemProvider provider1 = new AgendaItemProvider(){
            @Override
            public Collection<AgendaItem> getItems() {
                Collection<AgendaItem> items = new ArrayList<>();

                items.add(new AgendaItem("email1", "Danny Boy", "Agenda Item 1", "This is the body."));
                items.add(new AgendaItem("email2", "Jonny Boy", "Agenda Item 2", "This is the body."));
                items.add(new AgendaItem("email3", "Jimmy Boy", "Agenda Item 3", "This is the body."));
                items.add(new AgendaItem("email4", "Donny Boy", "Agenda Item 4", "This is the body."));

                return items;
            }
        };
        OamTools.PROPS.setProperty("doc.name", "agenda1.pdf");
        BuildAgendaRunnable builder1 = new BuildAgendaRunnable(provider1);
        builder1.run();
        assertTrue(Files.exists(Paths.get("agenda1.pdf")));

        // Test with newlines.
        AgendaItemProvider provider2 = new AgendaItemProvider(){
            @Override
            public Collection<AgendaItem> getItems() {
                Collection<AgendaItem> items = new ArrayList<>();

                items.add(new AgendaItem("email1", "Danny Boy", "Agenda Item 1", "This is the body.\n After a slash-n."));
                items.add(new AgendaItem("email2", "Jonny Boy", "Agenda Item 2", "This is the body." + System.lineSeparator() + "After a line separator."));
                items.add(new AgendaItem("email3", "Jimmy Boy", "Agenda Item 3", "This is the body. \n\r After slash n slash r."));
                items.add(new AgendaItem("email4", "Donny Boy", "Agenda Item 4", "This is the body. \r\n After slash r slash n"));

                return items;
            }
        };
        OamTools.PROPS.setProperty("doc.name", "agenda2.pdf");
        BuildAgendaRunnable builder2 = new BuildAgendaRunnable(provider2);
        builder2.run();
        assertTrue(Files.exists(Paths.get("agenda2.pdf")));
    }
}