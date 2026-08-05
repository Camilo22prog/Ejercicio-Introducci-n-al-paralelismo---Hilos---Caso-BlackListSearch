/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.Scanner;

/**
 *
 * @author hcadavid
 */
public class Main {

    public static void main(String[] args) {
        String initialIp;
        String finalIp;
        int threadsCount;

        if (args.length > 0 && "benchmark".equalsIgnoreCase(args[0])) {
            if (args.length >= 3) {
                initialIp = args[1];
                finalIp = args[2];
            } else {
                Scanner scanner = new Scanner(System.in);
                initialIp = readIp(scanner, "Ingrese la IP inicial: ");
                finalIp = readIp(scanner, "Ingrese la IP final: ");
                scanner.close();
            }

            runPerformanceExperiments(initialIp, finalIp);
            return;
        }

        if (args.length >= 3) {
            initialIp = args[0];
            finalIp = args[1];
            threadsCount = Integer.parseInt(args[2]);
        } else {
            Scanner scanner = new Scanner(System.in);
            initialIp = readIp(scanner, "Ingrese la IP inicial: ");
            finalIp = readIp(scanner, "Ingrese la IP final: ");
            threadsCount = readPositiveInt(scanner, "Ingrese el numero de hilos: ");
            scanner.close();
        }

        validateIpRange(initialIp, finalIp, threadsCount);
    }

    private static void runPerformanceExperiments(String initialIp, String finalIp) {
        long startIp = HostBlackListsValidatorThread.ipToNumber(initialIp);
        long endIp = HostBlackListsValidatorThread.ipToNumber(finalIp);

        if (endIp < startIp) {
            throw new IllegalArgumentException("La IP final debe ser mayor o igual a la IP inicial.");
        }

        long totalIps = endIp - startIp + 1;
        if (totalIps < 100) {
            throw new IllegalArgumentException("El rango debe tener al menos 100 IPs para la prueba de 100 hilos.");
        }

        int cores = Runtime.getRuntime().availableProcessors();
        int[] threadCounts = {1, cores, cores * 2, 50, 100};

        System.out.println("Nucleos disponibles: " + cores);
        System.out.println("Rango evaluado: " + initialIp + ".." + finalIp);

        for (int threadCount : threadCounts) {
            System.out.println("\nPrueba con " + threadCount + " hilo(s):");
            long startTime = System.nanoTime();
            validateIpRange(initialIp, finalIp, threadCount);
            long endTime = System.nanoTime();
            double elapsedMilliseconds = (endTime - startTime) / 1_000_000.0;

            System.out.printf("Resultado: %d hilo(s), %.3f ms%n", threadCount, elapsedMilliseconds);
        }
    }

    private static void validateIpRange(String initialIp, String finalIp, int threadsCount) {
        if (threadsCount <= 0) {
            throw new IllegalArgumentException("El numero de hilos debe ser mayor que cero.");
        }

        long startIp = HostBlackListsValidatorThread.ipToNumber(initialIp);
        long endIp = HostBlackListsValidatorThread.ipToNumber(finalIp);

        if (endIp < startIp) {
            throw new IllegalArgumentException("La IP final debe ser mayor o igual a la IP inicial.");
        }

        long totalIps = endIp - startIp + 1;
        int actualThreadsCount = (int) Math.min(threadsCount, totalIps);
        HostBlackListsValidatorThread[] threads = new HostBlackListsValidatorThread[actualThreadsCount];
        long baseSize = totalIps / actualThreadsCount;
        long remainder = totalIps % actualThreadsCount;
        long currentStart = startIp;

        for (int i = 0; i < actualThreadsCount; i++) {
            long currentSize = baseSize + (i < remainder ? 1 : 0);
            long currentEnd = currentStart + currentSize - 1;
            System.out.println("Hilo " + (i + 1) + ": Rango ["
                    + HostBlackListsValidatorThread.numberToIp(currentStart) + ".."
                    + HostBlackListsValidatorThread.numberToIp(currentEnd) + "]");

            threads[i] = new HostBlackListsValidatorThread(currentStart, currentEnd);
            threads[i].start();
            currentStart = currentEnd + 1;
        }

        for (HostBlackListsValidatorThread thread : threads) {
            try {
                thread.join();
                System.out.println(thread.getName() + " valido " + thread.getCheckedIpCount() + " IP(s).");
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("La validacion del rango de IPs fue interrumpida.", ex);
            }
        }
    }

    private static String readIp(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String ipAddress = scanner.nextLine();
            try {
                HostBlackListsValidatorThread.ipToNumber(ipAddress);
                return ipAddress;
            } catch (IllegalArgumentException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private static int readPositiveInt(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine();
            try {
                int number = Integer.parseInt(value);
                if (number > 0) {
                    return number;
                }
                System.out.println("Error: El numero debe ser mayor que cero.");
            } catch (NumberFormatException ex) {
                System.out.println("Error: Debe ingresar un numero entero valido.");
            }
        }
    }
}
