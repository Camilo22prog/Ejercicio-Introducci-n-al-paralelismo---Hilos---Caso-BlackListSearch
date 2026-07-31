package edu.eci.arsw.threads;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Scanner;

public class CountThreadsMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int a = 0;
        int b = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print("Ingrese el valor inicial (A): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Error: Debe ingresar un número entero válido.");
                System.out.print("Ingrese el valor inicial (A): ");
                scanner.next();
            }
            a = scanner.nextInt();
            System.out.print("Ingrese el valor final (B): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Error: Debe ingresar un número entero válido.");
                System.out.print("Ingrese el valor final (B): ");
                scanner.next();
            }
            b = scanner.nextInt();
            if (b < a) {
                System.out.println("Error: El valor final (B) debe ser mayor o igual al valor inicial (A).\n");
                continue;
            }
            int totalCount = b - a + 1;
            if (totalCount <= 3) {
                System.out.println(
                        "Error: El rango debe contener más de 3 números (mínimo 4 números) para distribuirse entre 3 hilos. Intente de nuevo.\n");
                continue;
            }

            valid = true;
        }

        int totalCount = b - a + 1;
        int baseSize = totalCount / 3;
        int remainder = totalCount % 3;

        int start1 = a;
        int end1 = start1 + baseSize + (remainder > 0 ? 1 : 0) - 1;

        int start2 = end1 + 1;
        int end2 = start2 + baseSize + (remainder > 1 ? 1 : 0) - 1;

        int start3 = end2 + 1;
        int end3 = b;

        System.out.println("Hilo 1: Rango [" + start1 + ".." + end1 + "]");
        System.out.println("Hilo 2: Rango [" + start2 + ".." + end2 + "]");
        System.out.println("Hilo 3: Rango [" + start3 + ".." + end3 + "]");

        CountThread thread1 = new CountThread(start1, end1);
        CountThread thread2 = new CountThread(start2, end2);
        CountThread thread3 = new CountThread(start3, end3);

        thread1.start();
        thread2.start();
        thread3.start();

        scanner.close();
    }
}
