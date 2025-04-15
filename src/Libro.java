public class Libro {
    private String id;
    private String titulo;
    private String autor;
    private int totalCopias;
    private int copiasDisponibles;

    public Libro(String id, String titulo, String autor, int totalCopias) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.totalCopias = totalCopias;
        this.copiasDisponibles = totalCopias; // Al crearlo, todas las copias están disponibles
    }

    // Sobrecarga para compatibilidad con libros antiguos (sin stock)
    public Libro(String id, String titulo, String autor) {
        this(id, titulo, autor, 1); // Valor por defecto: 1 copia
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getTotalCopias() {
        return totalCopias;
    }

    public int getCopiasDisponibles() {
        return copiasDisponibles;
    }

    public boolean isDisponible() {
        return copiasDisponibles > 0;
    }

    public void prestarCopia() {
        if (copiasDisponibles > 0) {
            copiasDisponibles--;
        }
    }

    public void devolverCopia() {
        if (copiasDisponibles < totalCopias) {
            copiasDisponibles++;
        }
    }
    public void setTotalCopias(int total) {
        this.totalCopias = total;
    }
    public void setCopiasDisponibles(int disponibles) {
        this.copiasDisponibles = disponibles;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }


}
