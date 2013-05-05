package openagendamail.data;

import java.util.Collection;

/**
 * An interface that specifies the required signature to be an AgendaItemProvider.  This class is intended to make it
 * easier to create repeatable tests for parts of the system that would otherwise require access to email accounts etc.
 *
 * @author adam
 * @date April 28, 2013
 */
public interface AgendaItemProvider {

    /**
     * Fetches the {@link AgendaItems} this provider has access to.
     * @return the AgendaItems this Provider can provide.
     */
    public Collection<AgendaItem> getItems();
}
