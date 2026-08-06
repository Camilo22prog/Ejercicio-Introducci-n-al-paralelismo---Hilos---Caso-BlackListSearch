package edu.eci.arsw.blacklistvalidator;

import java.util.List;
import java.util.Scanner;

/**
 *
 * @author hcadavid
 */
public class Main {

    public static void main(String[] args) {
        String ip;
        int threadsCount = 1;

        if (args.length > 0 && "benchmark".equalsIgnoreCase(args[0])) {
            if (args.length >= 2) {
                ip = args[1];
            } else {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Ingrese la IP para el benchmark: ");
                ip = scanner.nextLine();
                scanner.close();
            }

            runPerformanceExperiments(ip);
            return;
        }

        if (args.length >= 2) {
            ip = args[0];
            threadsCount = Integer.parseInt(args[1]);
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese la IP a verificar: ");
            ip = scanner.nextLine();
            threadsCount = readPositiveInt(scanner, "Ingrese el numero de hilos: ");
            scanner.close();
        }

        System.out.println("Verificando IP " + ip + " con " + threadsCount + " hilos...");
        HostBlackListsValidator validator = new HostBlackListsValidator();
        List<Integer> occurrences = validator.checkHost(ip, threadsCount);
        
        System.out.println("La IP fue reportada en " + occurrences.size() + " listas negras.");
        if (!occurrences.isEmpty()) {
            System.out.println("Listas: " + occurrences);
        }
    }

    private static void runPerformanceExperiments(String ip) {
        int cores = Runtime.getRuntime().availableProcessors();
        int[] threadCounts = {1, cores, cores * 2, 50, 100};

        System.out.println("Nucleos disponibles: " + cores);
        System.out.println("Evaluando IP: " + ip);

        for (int threadCount : threadCounts) {
            System.out.println("\nPrueba con " + threadCount + " hilo(s):");
            long startTime = System.nanoTime();
            
            HostBlackListsValidator validator = new HostBlackListsValidator();
            validator.checkHost(ip, threadCount);
            
            long endTime = System.nanoTime();
            double elapsedMilliseconds = (endTime - startTime) / 1_000_000.0;

            System.out.printf("Resultado: %d hilo(s), %.3f ms%n", threadCount, elapsedMilliseconds);

            try {
                System.out.println("Pausando 3 segundos para visualizacion en jVisualVM...");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
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
