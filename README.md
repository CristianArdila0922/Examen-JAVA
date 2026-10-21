# TecnoStore - Sistema de Venta de Celulares (Java)

Sistema de consola en Java para gestionar el catálogo de celulares, clientes,
ventas y reportes de la tienda **TecnoStore**. Desarrollado aplicando
Programación Orientada a Objetos, colecciones, la Stream API, manejo de
excepciones, persistencia con JDBC (MySQL) y patrones de diseño.

---

## Características

- **Gestión de celulares**: registrar, actualizar, eliminar y listar. Validación de
  precio y stock positivos, y gama controlada por un `enum`.
- **Gestión de clientes**: registro con validación de formato de correo e
  identificación única.
- **Gestión de ventas**: selección de cliente y uno o varios celulares, cálculo del
  total con IVA del 19%, descuento automático del stock y persistencia transaccional.
- **Reportes y análisis**:
  - Celulares con stock bajo (menor a 5 unidades).
  - Top 3 de celulares más vendidos.
  - Ventas totales por mes.
- **Persistencia**: JDBC contra MySQL (`tecnostore_db`). Incluye además una
  implementación en memoria (modo demo) para ejecutar la app sin base de datos.
- **Archivo de reporte**: genera automáticamente `reporte_ventas.txt` con el
  resumen de todas las ventas.
- **Patrones de diseño**:
  - **Singleton** en `ConexionDB`.
  - **Factory** en `FactoryCelular`.
  - **Strategy** en el sistema de descuentos (`EstrategiaDescuento` + `ContextoDescuento`).
- **Principios SOLID**: encapsulamiento en los modelos, herencia de interfaces en
  los DAOs y composición en `Venta`/`ItemVenta`.

---

## Estructura del proyecto

```
TecnoStore_Project/
├── src/tecnostore/
│   ├── modelo/                 # Entidades del negocio
│   │   ├── CategoriaGama.java        # enum (Alta, Media, Baja)
│   │   ├── Celular.java
│   │   ├── Cliente.java
│   │   ├── ItemVenta.java
│   │   └── Venta.java
│   ├── persistencia/           # Acceso a datos (JDBC + memoria)
│   │   ├── ConexionDB.java           # Singleton de conexión MySQL
│   │   ├── ICelularDAO.java          # Interface
│   │   ├── IClienteDAO.java          # Interface
│   │   ├── IVentaDAO.java            # Interface
│   │   ├── CelularDAOMySQL.java      # Implementación JDBC
│   │   ├── ClienteDAOMySQL.java      # Implementación JDBC
│   │   ├── VentaDAOMySQL.java        # Implementación JDBC (transaccional)
│   │   ├── CelularDAOMemoria.java    # Implementación en memoria (demo)
│   │   ├── ClienteDAOMemoria.java
│   │   └── VentaDAOMemoria.java
│   ├── servicio/               # Lógica de negocio (controladores)
│   │   ├── GestorCelulares.java
│   │   ├── GestorClientes.java
│   │   ├── GestorVentas.java
│   │   └── ReporteServicio.java
│   ├── utilidades/             # Funciones de apoyo
│   │   ├── Validador.java            # Validaciones (correo, positivos, etc.)
│   │   ├── ArchivoUtils.java         # Lectura/escritura de archivos (try-with-resources)
│   │   └── ReporteUtils.java         # Cálculos con Stream API
│   ├── patron/                 # Patrones de diseño
│   │   ├── FactoryCelular.java       # Factory Method
│   │   └── descuento/
│   │       ├── EstrategiaDescuento.java   # Strategy (interface)
│   │       ├── DescuentoNinguno.java
│   │       ├── DescuentoPorcentaje.java
│   │       ├── DescuentoFijo.java
│   │       └── ContextoDescuento.java
│   ├── vista/                  # Capa de presentación
│   │   └── VistaConsola.java         # Menú de consola (Modelo-Vista-Controlador)
│   └── Main.java               # Punto de entrada
├── sql/
│   └── tecnostore_db.sql             # Script de creación de tablas + datos demo
└── README.md
```

### Arquitectura (MVC + capas)

```
Vista (VistaConsola)  ─►  Servicio (Gestores)  ─►  Persistencia (DAOs)  ─►  MySQL / Memoria
        │                      │
        │                      └─► Modelo (entidades) + Patrones + Utilidades
        └─► ArchivoUtils / ReporteUtils
```

- **Modelo**: entidades del negocio con encapsulamiento y validaciones.
- **Vista**: `VistaConsola` (entrada/salida por consola, sin lógica de negocio).
- **Controlador**: los gestores (`servicio`) coordinan el modelo y la persistencia.
- **Persistencia**: los DAOs implementan interfaces; los servicios dependen de la
  abstracción (inversión de dependencias), no de una implementación concreta.

---

## Requisitos

- Java 17 o superior (se usan `switch` lambda y `record`-like patterns del switch).
- MySQL 8.x (opcional para persistencia real; sin él, la app corre en modo demo).
- Driver JDBC de MySQL (`mysql-connector-j`).

---

## Configuración de la conexión MySQL

1. Crea la base de datos ejecutando el script:
   ```bash
   mysql -u root -p < sql/tecnostore_db.sql
   ```
2. Edita las credenciales en `src/tecnostore/persistencia/ConexionDB.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/tecnostore_db?useSSL=false&serverTimezone=UTC";
   private static final String USUARIO = "root";
   private static final String CLAVE = "tu_contraseña";
   ```
3. Añade el driver `mysql-connector-j-<version>.jar` al classpath al compilar
   y ejecutar (ver sección siguiente).

> Si MySQL no está disponible, la aplicación inicia automáticamente en
> **modo demo** (datos en memoria) y carga celulares, clientes y ventas de
> ejemplo para que puedas probar todas las funciones.

---

## Compilación y ejecución

### Sin Maven/Gradle (compilación manual)

Desde la raíz del proyecto:

```powershell
# 1. Compilar todos los .java (deja los .class en la carpeta out)
javac -d out -cp "lib\mysql-connector-j-8.x.x.jar" src\tecnostore\**\*.java

# En PowerShell, compilar recursivamente:
Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName } | Out-File sources.txt
javac -d out -cp "lib\mysql-connector-j-8.x.x.jar" "@sources.txt"

# 2. Ejecutar (incluye el driver en el classpath)
java -cp "out;lib\mysql-connector-j-8.x.x.jar" tecnostore.Main
```

### Con un IDE (VS Code / IntelliJ / Eclipse)

- Importa el proyecto y añade `mysql-connector-j-*.jar` como librería externa.
- Configura la clase principal como `tecnostore.Main`.

---

## Ejemplo de ejecución

```
╔══════════════════════════════════════════════════════╗
║        SISTEMA DE VENTAS - TECNOSTORE                ║
╚══════════════════════════════════════════════════════╝
⚠ No se pudo conectar a MySQL. Modo DEMO activado (datos en memoria).
Cargando datos de demostración...
Datos de demostración cargados: 5 celulares, 3 clientes, 3 ventas.

══════════════════════════════════════════════════════════
           TECNOSTORE - Sistema de Ventas
══════════════════════════════════════════════════════════
 1. Gestión de Celulares
 2. Gestión de Clientes
 3. Registrar Venta
 4. Reportes y Análisis
 5. Generar reporte_ventas.txt
 6. Configurar Descuento (Strategy)
 0. Salir
══════════════════════════════════════════════════════════
Seleccione una opción: 4

--- Reportes y Análisis ---
 1. Celulares con stock bajo (< 5)
 2. Top 3 celulares más vendidos
 3. Ventas totales por mes
 4. Listar ventas
 0. Volver
Opción: 2

--- Top 3 celulares más vendidos ---
1. Samsung Galaxy S24 - 3 unidades vendidas (gama Alta)
2. Xiaomi Redmi Note 13 - 2 unidades vendidas (gama Media)
3. Apple iPhone 15 - 1 unidades vendidas (gama Alta)
```

El archivo `reporte_ventas.txt` se genera en la raíz del proyecto con la
opción 5 del menú principal.

---

## Patrones y principios aplicados

| Principio / Patrón | Dónde se aplica |
|---|---|
| Encapsulamiento | Setters validados en `Celular`, `Cliente`, `ItemVenta` |
| Herencia / Polimorfismo | Interfaces `ICelularDAO`, `IClienteDAO`, `IVentaDAO` con varias implementaciones |
| Composición | `Venta` compone `ItemVenta`; `ItemVenta` compone `Celular` |
| Singleton | `ConexionDB` |
| Factory | `FactoryCelular` |
| Strategy | `EstrategiaDescuento` + `ContextoDescuento` (descuentos intercambiables) |
| Dependency Inversion | Los servicios dependen de interfaces DAO, no de implementaciones |
| Open/Closed | Nuevas estrategias de descuento o DAOs sin modificar el código existente |
| Stream API | Reportes en `ReporteUtils` (stock bajo, top 3, ventas por mes) |
| Excepciones | `try-with-resources` en DAOs y `ArchivoUtils`; rollback transaccional en `VentaDAOMySQL` |

---

## Entregables

- Código fuente completo (`.java`) en `src/tecnostore/`.
- Script SQL `sql/tecnostore_db.sql` con la creación de tablas y datos de ejemplo.
- Archivo `reporte_ventas.txt` generado automáticamente desde el menú (opción 5).
- Este `README.md` con descripción, estructura, ejemplo e instrucciones de conexión.
