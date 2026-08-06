package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT = 5;
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());

    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     *
     * @param ipaddress suspicious host's IP address.
     * @param N         number of threads to use.
     * @return Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int N) {
        LinkedList<Integer> blackListOccurrences = new LinkedList<>();
        int occurrencesCount = 0;
        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
        int checkedListsCount = 0;

        int totalServers = skds.getRegisteredServersCount();
        HostBlackListsValidatorThread[] threads = new HostBlackListsValidatorThread[N];

        int baseSize = totalServers / N;
        int remainder = totalServers % N;
        int currentStart = 0;

        // Crear e iniciar hilos
        for (int i = 0; i < N; i++) {
            int currentSize = baseSize + (i < remainder ? 1 : 0);
            int currentEnd = currentStart + currentSize - 1;

            threads[i] = new HostBlackListsValidatorThread(ipaddress, currentStart, currentEnd);
            threads[i].start();
            currentStart = currentEnd + 1;
        }

        // Esperar a que terminen y recolectar resultados
        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
                checkedListsCount += threads[i].getCheckedServersCount();
                
                for (Integer serverId : threads[i].getOccurrences()) {
                    if (occurrencesCount < BLACK_LIST_ALARM_COUNT) {
                        blackListOccurrences.add(serverId);
                        occurrencesCount++;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.log(Level.SEVERE, "Thread was interrupted", e);
            }
        }

        if (occurrencesCount >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipaddress);
        } else {
            skds.reportAsTrustworthy(ipaddress);
        }

        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, totalServers});

        return blackListOccurrences;
    }
}
