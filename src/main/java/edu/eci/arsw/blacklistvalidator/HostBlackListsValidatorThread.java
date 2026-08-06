package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;

public class HostBlackListsValidatorThread extends Thread {

    private final String ipAddress;
    private final int startServer;
    private final int endServer;
    private int checkedServersCount;
    private final LinkedList<Integer> occurrences;

    public HostBlackListsValidatorThread(String ipAddress, int startServer, int endServer) {
        this.ipAddress = ipAddress;
        this.startServer = startServer;
        this.endServer = endServer;
        this.checkedServersCount = 0;
        this.occurrences = new LinkedList<>();
    }

    @Override
    public void run() {
        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();

        for (int i = startServer; i <= endServer; i++) {
            checkedServersCount++;
            if (skds.isInBlackListServer(i, ipAddress)) {
                occurrences.add(i);
            }
        }
    }

    public List<Integer> getOccurrences() {
        return occurrences;
    }

    public int getCheckedServersCount() {
        return checkedServersCount;
    }
}
