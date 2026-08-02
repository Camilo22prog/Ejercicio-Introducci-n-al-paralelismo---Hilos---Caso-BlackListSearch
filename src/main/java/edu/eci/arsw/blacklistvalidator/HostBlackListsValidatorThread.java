package edu.eci.arsw.blacklistvalidator;

import java.util.List;

public class HostBlackListsValidatorThread extends Thread {

    private static final long MAX_IPV4_VALUE = 0xFFFFFFFFL;

    private long startIp;
    private long endIp;
    private int checkedIpCount;

    public HostBlackListsValidatorThread(long startIp, long endIp) {
        this.startIp = startIp;
        this.endIp = endIp;
    }

    @Override
    public void run() {
        HostBlackListsValidator validator = new HostBlackListsValidator();

        for (long currentIp = startIp; currentIp <= endIp; currentIp++) {
            String ip = numberToIp(currentIp);
            List<Integer> blackListOccurrences = validator.checkHost(ip);
            System.out.println("La IP " + ip + " fue encontrada en las siguientes listas negras: "
                    + blackListOccurrences);
            checkedIpCount++;
        }
    }

    public int getCheckedIpCount() {
        return checkedIpCount;
    }

    public static long ipToNumber(String ip) {
        String[] parts = ip.trim().split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("La IP debe tener el formato a.b.c.d");
        }

        long number = 0;
        for (String part : parts) {
            int octet = Integer.parseInt(part);
            if (octet < 0 || octet > 255) {
                throw new IllegalArgumentException("Cada octeto debe estar entre 0 y 255");
            }
            number = (number << 8) + octet;
        }
        return number;
    }

    public static String numberToIp(long number) {
        if (number < 0 || number > MAX_IPV4_VALUE) {
            throw new IllegalArgumentException("La IP esta fuera del rango IPv4 valido");
        }

        return ((number >> 24) & 0xFF) + "."
                + ((number >> 16) & 0xFF) + "."
                + ((number >> 8) & 0xFF) + "."
                + (number & 0xFF);
    }
}
