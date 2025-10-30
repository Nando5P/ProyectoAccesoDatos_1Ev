package trenzadosmarinos.main;

import trenzadosmarinos.dao.DAOFactory;
import trenzadosmarinos.dao.StorageType;
import trenzadosmarinos.service.SincronizadorService;
import trenzadosmarinos.ui.MenuConsola;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        StorageType tipoPersistencia = elegirPersistencia(scanner);

        System.out.println("Iniciando sistema con persistencia: " + tipoPersistencia);

        DAOFactory factory = new DAOFactory(tipoPersistencia);

        // Iniciar el servicio de sincronización
        SincronizadorService sincronizador = new SincronizadorService();

        MenuConsola menu = new MenuConsola(scanner, factory, sincronizador);
        menu.iniciar();

        System.out.println("Cerrando aplicación.");
        scanner.close();
    }

    // Método para elegir el tipo de persistencia
    private static StorageType elegirPersistencia(Scanner scanner) {
        int opcion = -1;
        while (opcion == -1) {
            try {
                System.out.println("\n--- BIENVENIDO A TRENZADOS MARINOS ---");
                System.out.println("Seleccione el modo de persistencia inicial:");
                System.out.println("1. Usar Ficheros (CSV)");
                System.out.println("2. Usar Base de Datos (MySQL)");
                System.out.print("Opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine();

                if (opcion == 1) return StorageType.FILE;
                if (opcion == 2) return StorageType.JDBC;

                System.out.println("Opción no válida.");
                opcion = -1;
            } catch (InputMismatchException e) {
                System.out.println("Error: Debe ingresar un número.");
                scanner.nextLine();
            }
        }
        return StorageType.FILE;
    }
}
