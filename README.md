
# 📚 Sistema de Biblioteca en Java

Este proyecto consiste en una aplicación de consola escrita en **Java**, que permite gestionar una biblioteca con múltiples funcionalidades, ideal para portafolios o prácticas académicas.

---

## 🚀 Funcionalidades principales

- 📖 Registro, edición y eliminación de libros con control de stock
- 👤 Gestión de usuarios: crear, editar, eliminar y buscar
- 📦 Préstamos y devoluciones con control de fechas
- ⏱️ Cálculo de días restantes o atraso
- 💸 Cálculo de multas por atrasos (configurable)
- 📄 Exportación de reportes de préstamos activos
- 📁 Persistencia de datos mediante archivos `.txt`
- 🧭 Menús interactivos organizados y robustos
- 🔒 Validaciones de entrada para evitar errores del usuario

---

## 🗂️ Estructura del proyecto

```
/biblioteca
├── src/
│   ├── Main.java
│   ├── Biblioteca.java
│   ├── Libro.java
│   ├── Usuario.java
│   └── Prestamo.java
├── libros.txt
├── usuarios.txt
├── prestamos.txt
├── reporte.txt
└── README.md
```

---

## ⚙️ Requisitos

- Java JDK 17 o superior
- IntelliJ IDEA, VSCode u otro editor Java
- Sistema operativo compatible con Java (Windows, Linux, Mac)

---

## 🧪 Cómo ejecutar

1. Clona este repositorio o descarga el ZIP
2. Abre el proyecto en tu IDE
3. Asegúrate de tener los archivos `libros.txt`, `usuarios.txt` y `prestamos.txt` vacíos o con contenido válido
4. Ejecuta `Main.java`
5. ¡Listo! Interactúa mediante el menú

---

## 📷 Captura de consola

```bash
===== MENÚ BIBLIOTECA =====
1. 👤 Gestión de usuarios
2. 📚 Gestión de libros
3. 📄 Ver préstamos activos
0. 🚪 Salir
👉 Selecciona una opción:
```

---

## ✍️ Autor

Proyecto desarrollado por [J4RED1] como práctica para portafolio.  
📅 Fecha: 15/04/2025

---

## 📝 Licencia

Este proyecto se distribuye bajo la licencia MIT.
