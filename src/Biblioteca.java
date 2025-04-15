import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class Biblioteca {
    private ArrayList<Libro> libros = new ArrayList<>();
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private ArrayList<Prestamo> prestamos = new ArrayList<>();
    private final int MAX_PRESTAMOS_POR_USUARIO = 3;

    public void reservarLibro(Usuario usuario, String idLibro) {
        if (idLibro == null || idLibro.trim().isEmpty()) {
            System.out.println("❌ El ID del libro no puede estar vacío.");
            return;
        }

        if (contarPrestamosDeUsuario(usuario) >= MAX_PRESTAMOS_POR_USUARIO) {
            System.out.println("⚠️ El usuario ya tiene el máximo de libros prestados (" + MAX_PRESTAMOS_POR_USUARIO + ").");
            return;
        }

        Libro libro = buscarLibroPorId(idLibro.trim());

        if (libro == null) {
            System.out.println("❌ Libro no encontrado.");
            return;
        }

        for (Prestamo p : prestamos) {
            if (p.getUsuario().getId().equals(usuario.getId()) &&
                    p.getLibro().getId().equals(libro.getId())) {
                System.out.println("⚠️ El usuario ya tiene este libro en préstamo.");
                return;
            }
        }

        if (!libro.isDisponible()) {
            System.out.println("⚠️ No hay copias disponibles para este libro.");
            return;
        }


        Prestamo prestamo = new Prestamo(usuario, libro);
        prestamos.add(prestamo);
        libro.prestarCopia();

        System.out.println("✅ Libro reservado exitosamente: " + libro.getTitulo());

        try (PrintWriter pw = new PrintWriter(new FileWriter("prestamos.txt", true))) {
            pw.println(usuario.getId() + "," + libro.getId() + "," +
                    prestamo.getFechaPrestamo() + "," + prestamo.getFechaVencimiento());
        } catch (IOException e) {
            System.out.println("❌ Error al guardar el préstamo en archivo: " + e.getMessage());
        }
    }

    public void agregarLibro(String titulo, String autor) {
        if (titulo == null || titulo.trim().isEmpty() ||
                autor == null || autor.trim().isEmpty()) {
            System.out.println("❌ El título y el autor no pueden estar vacíos.");
            return;
        }

        System.out.print("📦 ¿Cuántas copias desea registrar?: ");
        Scanner scanner = new Scanner(System.in);
        int copias;
        try {
            copias = Integer.parseInt(scanner.nextLine().trim());
            if (copias <= 0) {
                System.out.println("❌ La cantidad de copias debe ser mayor que cero.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Debe ingresar un número válido.");
            return;
        }

        int nuevoIdNum = libros.size() + 1;
        String nuevoId = String.format("l%03d", nuevoIdNum);

        Libro nuevoLibro = new Libro(nuevoId, titulo.trim(), autor.trim(), copias);
        libros.add(nuevoLibro);

        try (PrintWriter pw = new PrintWriter(new FileWriter("libros.txt", true))) {
            pw.println(nuevoId + "," + titulo + "," + autor + "," + copias);
            System.out.println("✅ Libro agregado correctamente con ID: " + nuevoId);
        } catch (IOException e) {
            System.out.println("❌ Error al guardar el libro en archivo: " + e.getMessage());
        }
    }

    public void devolverLibro(Usuario usuario, String idLibro) {
        if (idLibro == null || idLibro.trim().isEmpty()) {
            System.out.println("❌ El ID del libro no puede estar vacío.");
            return;
        }

        Prestamo prestamoEncontrado = null;

        for (Prestamo prestamo : prestamos) {
            if (prestamo.getUsuario().getId().equals(usuario.getId()) &&
                    prestamo.getLibro().getId().equals(idLibro.trim())) {
                prestamoEncontrado = prestamo;
                break;
            }
        }

        if (prestamoEncontrado != null) {
            prestamos.remove(prestamoEncontrado);
            prestamoEncontrado.getLibro().devolverCopia();
            System.out.println("✅ Libro devuelto correctamente: " + prestamoEncontrado.getLibro().getTitulo());
            actualizarArchivoPrestamos();
        } else {
            System.out.println("❌ El usuario no tiene ese libro en préstamo.");
        }
    }

    public Usuario registrarNuevoUsuario(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.out.println("❌ El nombre no puede estar vacío.");
            return null;
        }

        int nuevoIdNum = usuarios.size() + 1;
        String nuevoId = String.format("u%03d", nuevoIdNum);
        Usuario nuevoUsuario = new Usuario(nuevoId, nombre);
        usuarios.add(nuevoUsuario);

        try (PrintWriter pw = new PrintWriter(new FileWriter("usuarios.txt", true))) {
            pw.println(nuevoId + "," + nombre);
        } catch (IOException e) {
            System.out.println("❌ Error al guardar el nuevo usuario: " + e.getMessage());
        }

        return nuevoUsuario;
    }

    public Libro buscarLibroPorId(String id) {
        for (Libro libro : libros) {
            if (libro.getId().equals(id)) {
                return libro;
            }
        }
        return null;
    }

    public Usuario buscarUsuarioPorId(String id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equalsIgnoreCase(id)) {
                return usuario;
            }
        }
        return null;
    }

    private int contarPrestamosDeUsuario(Usuario usuario) {
        int contador = 0;
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getUsuario().getId().equals(usuario.getId())) {
                contador++;
            }
        }
        return contador;
    }

    private void actualizarArchivoPrestamos() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("prestamos.txt"))) {
            for (Prestamo prestamo : prestamos) {
                pw.println(prestamo.getUsuario().getId() + "," +
                        prestamo.getLibro().getId() + "," +
                        prestamo.getFechaPrestamo() + "," +
                        prestamo.getFechaVencimiento());
            }
        } catch (IOException e) {
            System.out.println("❌ Error al actualizar el archivo de préstamos: " + e.getMessage());
        }
    }

    public static String quitarTildes(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public void cargarLibros(String archivoRuta) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivoRuta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length < 3) continue;

                String id = partes[0].trim();
                String titulo = partes[1].trim();
                String autor = partes[2].trim();

                // Si incluye cantidad de copias:
                if (partes.length == 4) {
                    int copias = Integer.parseInt(partes[3].trim());
                    libros.add(new Libro(id, titulo, autor, copias));
                } else {
                    libros.add(new Libro(id, titulo, autor)); // default: 1 copia
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("❌ Error al leer libros: " + e.getMessage());
        }
    }

    public void cargarUsuarios(String archivoRuta) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivoRuta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length != 2) continue;
                String id = partes[0].trim();
                String nombre = partes[1].trim();
                usuarios.add(new Usuario(id, nombre));
            }
        } catch (IOException e) {
            System.out.println("Error al leer usuarios: " + e.getMessage());
        }
    }

    public void cargarPrestamos(String archivoRuta) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivoRuta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length != 2) continue;
                String idUsuario = partes[0].trim();
                String idLibro = partes[1].trim();
                Usuario usuario = buscarUsuarioPorId(idUsuario);
                Libro libro = buscarLibroPorId(idLibro);
                if (usuario != null && libro != null) {
                    Prestamo prestamo = new Prestamo(usuario, libro);
                    prestamos.add(prestamo);
                    libro.prestarCopia();

                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ No se pudo cargar préstamos anteriores: " + e.getMessage());
        }
    }

    public void mostrarLibrosConEstadoYUsuario() {
        System.out.println("\n===== LISTADO DE LIBROS =====\n");
        for (Libro libro : libros) {
            String estado = libro.isDisponible()
                    ? "✅ " + libro.getCopiasDisponibles() + "/" + libro.getTotalCopias() + " disponibles"
                    : "❌ Sin copias disponibles";
            System.out.println(libro.getId() + " - " + libro.getTitulo()
                    + " (" + libro.getAutor() + ") - " + estado);
        }
    }

    public void mostrarTodosLosUsuarios() {
        System.out.println("\n===== USUARIOS REGISTRADOS =====\n");
        if (usuarios.isEmpty()) {
            System.out.println("⚠️ No hay usuarios registrados.");
        } else {
            for (Usuario u : usuarios) {
                System.out.println(u.getId() + " - " + u.getNombre());
            }
        }
    }

    public ArrayList<Usuario> buscarUsuariosPorNombre(String nombre) {
        ArrayList<Usuario> resultados = new ArrayList<>();
        String nombreBuscado = quitarTildes(nombre.toLowerCase());
        for (Usuario usuario : usuarios) {
            String nombreUsuario = quitarTildes(usuario.getNombre().toLowerCase());
            if (nombreUsuario.contains(nombreBuscado)) {
                resultados.add(usuario);
            }
        }
        return resultados;
    }

    public void mostrarSoloLibrosDisponibles() {
        System.out.println("\n📚 Libros disponibles para reservar:\n");
        boolean hayDisponibles = false;
        for (Libro libro : libros) {
            if (libro.isDisponible()) {
                System.out.println(libro.getId() + " - " + libro.getTitulo() + " (" + libro.getAutor() + ")");
                hayDisponibles = true;
            }
        }
        if (!hayDisponibles) {
            System.out.println("⚠️ No hay libros disponibles en este momento.");
        }
    }

    public void mostrarPrestamosDeUsuario(Usuario usuario) {
        System.out.println("\n📦 Préstamos de " + usuario.getNombre() + ":\n");
        boolean tiene = false;
        LocalDate hoy = LocalDate.now();

        for (Prestamo p : prestamos) {
            if (p.getUsuario().getId().equals(usuario.getId())) {
                LocalDate vencimiento = p.getFechaVencimiento();
                long dias = ChronoUnit.DAYS.between(hoy, vencimiento);


                String estado = (dias >= 0)
                        ? "🕒 Vence en " + dias + " días"
                        : "⚠️ Atrasado " + Math.abs(dias) + " días";

                System.out.println("- " + p.getLibro().getId() + ": " + p.getLibro().getTitulo()
                        + " | Vence: " + vencimiento + " | " + estado);

                tiene = true;
            }
        }

        if (!tiene) {
            System.out.println("📭 No tiene libros en préstamo.");
        }
    }

    public ArrayList<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void editarStockLibro() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("🔍 Ingrese ID del libro a editar: ");
        String idLibro = scanner.nextLine().trim();

        Libro libro = buscarLibroPorId(idLibro);

        if (libro == null) {
            System.out.println("❌ Libro no encontrado.");
            return;
        }

        System.out.println("📘 Libro encontrado: " + libro.getTitulo() +
                " (" + libro.getAutor() + ") - Stock actual: " +
                libro.getCopiasDisponibles() + "/" + libro.getTotalCopias());

        System.out.print("✏️ Ingrese el nuevo total de copias: ");
        try {
            int nuevoTotal = Integer.parseInt(scanner.nextLine());

            if (nuevoTotal < 0 || nuevoTotal < (libro.getTotalCopias() - libro.getCopiasDisponibles())) {
                System.out.println("⚠️ No puedes establecer un stock menor al número de copias actualmente prestadas.");
                return;
            }

            int prestadas = libro.getTotalCopias() - libro.getCopiasDisponibles();
            libro.setTotalCopias(nuevoTotal);
            libro.setCopiasDisponibles(nuevoTotal - prestadas);

            actualizarArchivoLibros();
            System.out.println("✅ Stock actualizado correctamente.");

        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada no válida. Solo se aceptan números.");
        }
    }

    public void eliminarLibro() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("🗑️ Ingrese ID del libro a eliminar: ");
        String idLibro = scanner.nextLine().trim();

        Libro libro = buscarLibroPorId(idLibro);

        if (libro == null) {
            System.out.println("❌ Libro no encontrado.");
            return;
        }

        for (Prestamo p : prestamos) {
            if (p.getLibro().getId().equals(libro.getId())) {
                System.out.println("⚠️ No se puede eliminar un libro que está prestado.");
                return;
            }
        }

        libros.remove(libro);
        actualizarArchivoLibros();
        System.out.println("✅ Libro eliminado correctamente.");
    }

    private void actualizarArchivoLibros() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("libros.txt"))) {
            for (Libro libro : libros) {
                pw.println(libro.getId() + "," + libro.getTitulo() + "," + libro.getAutor() + "," + libro.getTotalCopias());
            }
        } catch (IOException e) {
            System.out.println("❌ Error al actualizar el archivo de libros: " + e.getMessage());
        }
    }

    public void gestionarLibros() {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n===== GESTIÓN DE LIBROS =====");
            System.out.println("1. 📚 Ver todos los libros");
            System.out.println("2. ➕ Agregar nuevo libro");
            System.out.println("3. ✏️ Editar stock");
            System.out.println("4. 🖋️ Editar título o autor");
            System.out.println("5. 🗑️ Eliminar libro");
            System.out.println("6. 🔙 Volver al menú principal");
            System.out.print("👉 Selecciona una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine(); // limpiar salto

            switch (opcion) {
                case 1:
                    mostrarLibrosConEstadoYUsuario();
                    break;

                case 2:
                    System.out.print("📖 Ingrese título: ");
                    String titulo = scanner.nextLine();
                    System.out.print("✍️ Ingrese autor: ");
                    String autor = scanner.nextLine();
                    agregarLibro(titulo, autor);
                    break;

                case 3:
                    mostrarLibrosConEstadoYUsuario();
                    editarStockLibro();
                    break;

                case 4:
                    mostrarLibrosConEstadoYUsuario();
                    editarTituloAutorLibro();
                    break;

                case 5:
                    mostrarLibrosConEstadoYUsuario();
                    eliminarLibro();
                    break;

                case 6:
                    System.out.println("🔙 Volviendo al menú principal...");
                    break;

                default:
                    System.out.println("⚠️ Opción no válida.");
            }

        } while (opcion != 6);
    }

    public void editarTituloAutorLibro() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("✏️ Ingrese ID del libro a editar: ");
        String idLibro = scanner.nextLine().trim();

        Libro libro = buscarLibroPorId(idLibro);
        if (libro == null) {
            System.out.println("❌ Libro no encontrado.");
            return;
        }

        System.out.println("📘 Editando: " + libro.getTitulo() + " (" + libro.getAutor() + ")");
        System.out.print("Nuevo título (presiona Enter para mantener el actual): ");
        String nuevoTitulo = scanner.nextLine().trim();
        System.out.print("Nuevo autor (presiona Enter para mantener el actual): ");
        String nuevoAutor = scanner.nextLine().trim();

        if (!nuevoTitulo.isEmpty()) {
            libro.setTitulo(nuevoTitulo);
        }
        if (!nuevoAutor.isEmpty()) {
            libro.setAutor(nuevoAutor);
        }

        actualizarArchivoLibros();
        System.out.println("✅ Libro actualizado correctamente.");
    }

    public void gestionarUsuarios() {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n===== GESTIÓN DE USUARIOS =====");
            System.out.println("1. 👥 Ver todos los usuarios");
            System.out.println("2. ➕ Registrar nuevo usuario");
            System.out.println("3. 🖊️ Editar nombre de usuario");
            System.out.println("4. 🗑️ Eliminar usuario");
            System.out.println("5. 🔍 Buscar usuario y realizar acciones");
            System.out.println("6. 🔙 Volver al menú principal");
            System.out.print("👉 Selecciona una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    mostrarTodosLosUsuarios();
                    break;

                case 2:
                    System.out.print("🆕 Ingrese nombre completo: ");
                    String nombreNuevo = scanner.nextLine();
                    Usuario nuevo = registrarNuevoUsuario(nombreNuevo);
                    if (nuevo != null) {
                        System.out.println("✅ Usuario creado con ID: " + nuevo.getId());
                    }
                    break;

                case 3:
                    editarNombreUsuario();
                    break;

                case 4:
                    eliminarUsuario();
                    break;

                case 5:
                    buscarUsuarioYRealizarAcciones();
                    break;

                case 6:
                    System.out.println("🔙 Volviendo al menú principal...");
                    break;

                default:
                    System.out.println("⚠️ Opción no válida.");
            }

        } while (opcion != 6);
    }
    public void editarNombreUsuario() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("✏️ Ingrese el ID del usuario que desea editar: ");
        String id = scanner.nextLine().trim();

        Usuario usuario = buscarUsuarioPorId(id);

        if (usuario == null) {
            System.out.println("❌ Usuario no encontrado.");
            return;
        }

        System.out.println("👤 Usuario actual: " + usuario.getNombre());
        System.out.print("🖊️ Ingrese el nuevo nombre: ");
        String nuevoNombre = scanner.nextLine().trim();

        if (nuevoNombre.isEmpty()) {
            System.out.println("⚠️ No se realizaron cambios.");
            return;
        }

        usuario.setNombre(nuevoNombre);
        actualizarArchivoUsuarios();
        System.out.println("✅ Nombre actualizado correctamente.");
    }

    public void eliminarUsuario() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("🗑️ Ingrese el ID del usuario a eliminar: ");
        String id = scanner.nextLine().trim();

        Usuario usuario = buscarUsuarioPorId(id);

        if (usuario == null) {
            System.out.println("❌ Usuario no encontrado.");
            return;
        }

        for (Prestamo p : prestamos) {
            if (p.getUsuario().getId().equals(usuario.getId())) {
                System.out.println("⚠️ No se puede eliminar. El usuario tiene libros prestados.");
                return;
            }
        }

        usuarios.remove(usuario);
        actualizarArchivoUsuarios();
        System.out.println("✅ Usuario eliminado correctamente.");
    }
    public void buscarUsuarioYRealizarAcciones() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("🔍 Ingrese nombre del usuario: ");
        String nombre = scanner.nextLine().trim();
        ArrayList<Usuario> encontrados = buscarUsuariosPorNombre(nombre);

        if (encontrados.isEmpty()) {
            System.out.println("❌ No se encontraron usuarios con ese nombre.");
            return;
        }

        System.out.println("👥 Usuarios encontrados:");
        for (Usuario u : encontrados) {
            System.out.println("- " + u.getId() + ": " + u.getNombre());
        }

        System.out.print("👉 Ingrese el ID del usuario correcto: ");
        String id = scanner.nextLine().trim();
        Usuario usuario = buscarUsuarioPorId(id);

        if (usuario == null) {
            System.out.println("❌ ID inválido.");
            return;
        }

        int opcion;
        do {
            System.out.println("\n👤 Usuario: " + usuario.getNombre());
            System.out.println("1. 📖 Reservar libro");
            System.out.println("2. 📦 Devolver libro");
            System.out.println("3. 👁️ Ver préstamos activos");
            System.out.println("4. 🔙 Volver");
            System.out.print("👉 Selecciona una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    mostrarSoloLibrosDisponibles();
                    System.out.print("📘 Ingrese ID del libro: ");
                    String idLibro = scanner.nextLine();
                    reservarLibro(usuario, idLibro);
                    break;
                case 2:
                    mostrarPrestamosDeUsuario(usuario);
                    System.out.print("📦 Ingrese ID del libro a devolver: ");
                    String idDevolver = scanner.nextLine();
                    devolverLibro(usuario, idDevolver);
                    break;
                case 3:
                    mostrarPrestamosDeUsuario(usuario);
                    break;
                case 4:
                    System.out.println("🔙 Volviendo...");
                    break;
                default:
                    System.out.println("⚠️ Opción no válida.");
            }
        } while (opcion != 4);
    }

    private void actualizarArchivoUsuarios() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("usuarios.txt"))) {
            for (Usuario u : usuarios) {
                pw.println(u.getId() + "," + u.getNombre());
            }
        } catch (IOException e) {
            System.out.println("❌ Error al actualizar archivo de usuarios: " + e.getMessage());
        }
    }

    public void mostrarPrestamosActivosConMultasYExport() {
        System.out.println("\n===== PRÉSTAMOS ACTIVOS =====\n");

        LocalDate hoy = LocalDate.now();
        ArrayList<String> reporte = new ArrayList<>();
        int contador = 0;

        for (Prestamo p : prestamos) {
            Usuario usuario = p.getUsuario();
            Libro libro = p.getLibro();
            LocalDate vencimiento = p.getFechaVencimiento();
            LocalDate prestado = p.getFechaPrestamo();

            long dias = ChronoUnit.DAYS.between(vencimiento, hoy);

            String estado;
            String multa = "";

            if (dias > 0) {
                long valorMulta = dias * 500; // Puedes cambiar el valor
                estado = "⚠️ Atrasado " + dias + " días";
                multa = " | Multa: $" + valorMulta;
            } else {
                estado = "🕒 Vence en " + Math.abs(dias) + " días";
            }

            String linea = String.format("📘 %s → %s | Préstamo: %s | Vence: %s | %s%s",
                    libro.getTitulo(),
                    usuario.getNombre(),
                    prestado,
                    vencimiento,
                    estado,
                    multa);

            System.out.println(linea);
            reporte.add(linea);
            contador++;
        }

        if (contador == 0) {
            System.out.println("📭 No hay préstamos activos.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("\n💾 ¿Desea exportar este reporte a 'reporte.txt'? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();

        if (respuesta.equals("s")) {
            try (PrintWriter pw = new PrintWriter(new FileWriter("reporte.txt"))) {
                pw.println("===== REPORTE DE PRÉSTAMOS ACTIVOS =====\n");
                for (String linea : reporte) {
                    pw.println(linea);
                }
                System.out.println("✅ Reporte exportado correctamente.");
            } catch (IOException e) {
                System.out.println("❌ Error al exportar el reporte: " + e.getMessage());
            }
        } else {
            System.out.println("📁 Exportación cancelada.");
        }
    }
}
