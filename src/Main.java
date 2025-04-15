import java.util.InputMismatchException;
import java.util.Scanner;




public class Main {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        Scanner scanner = new Scanner(System.in);

        biblioteca.cargarLibros("libros.txt");
        biblioteca.cargarUsuarios("usuarios.txt");
        biblioteca.cargarPrestamos("prestamos.txt");

        int opcion;

        do {
            System.out.println("===== MENÚ BIBLIOTECA =====");
            System.out.println("1. 👤 Gestión de usuarios");
            System.out.println("2. 📚 Gestión de libros");
            System.out.println("3. 📄 Ver préstamos activos");
            System.out.println("0. 🚪 Salir");
            System.out.print("👉 Selecciona una opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        biblioteca.gestionarUsuarios();
                        break;
                    case 2:
                        biblioteca.gestionarLibros();
                        break;
                    case 3:
                        biblioteca.mostrarPrestamosActivosConMultasYExport();
                        break;
                    case 0:
                        System.out.println("👋 ¡Hasta luego!");
                        break;
                    default:
                        System.out.println("⚠️ Opción no válida");
                }


            } catch (InputMismatchException e) {
                System.out.println("⚠️ Entrada inválida. Por favor, ingresa un número.");
                scanner.nextLine();
                opcion = -1;
            }

            System.out.println("\n─────────────────────────────────────\n");

        } while (opcion != 0);
    }
}
