-- ============================================================
--  TecnoStore - Script de creación de la base de datos MySQL
--  Base de datos sugerida: tecnostore_db
-- ============================================================

CREATE DATABASE IF NOT EXISTS tecnostore_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE tecnostore_db;

-- ------------------------------------------------------------
-- Tabla: celulares
-- ------------------------------------------------------------
DROP TABLE IF EXISTS detalle_ventas;
DROP TABLE IF EXISTS ventas;
DROP TABLE IF EXISTS celulares;
DROP TABLE IF EXISTS clientes;

CREATE TABLE celulares (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    marca              VARCHAR(50)  NOT NULL,
    modelo             VARCHAR(80)  NOT NULL,
    precio             DOUBLE       NOT NULL CHECK (precio > 0),
    stock              INT          NOT NULL CHECK (stock >= 0),
    sistema_operativo  VARCHAR(30)  NOT NULL,
    gama               ENUM('Alta', 'Media', 'Baja') NOT NULL
);

-- ------------------------------------------------------------
-- Tabla: clientes
-- ------------------------------------------------------------
CREATE TABLE clientes (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    identificacion   VARCHAR(20)  NOT NULL UNIQUE,
    correo          VARCHAR(100) NOT NULL,
    telefono        VARCHAR(20)
);

-- ------------------------------------------------------------
-- Tabla: ventas
-- ------------------------------------------------------------
CREATE TABLE ventas (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente  INT NOT NULL,
    fecha       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total       DOUBLE NOT NULL,
    empleado    VARCHAR(100) NOT NULL,
    CONSTRAINT fk_ventas_cliente FOREIGN KEY (id_cliente)
        REFERENCES clientes(id) ON DELETE RESTRICT
);

-- ------------------------------------------------------------
-- Tabla: detalle_ventas
-- ------------------------------------------------------------
CREATE TABLE detalle_ventas (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    id_venta    INT NOT NULL,
    id_celular  INT NOT NULL,
    cantidad    INT NOT NULL CHECK (cantidad > 0),
    subtotal    DOUBLE NOT NULL,
    CONSTRAINT fk_detalle_venta FOREIGN KEY (id_venta)
        REFERENCES ventas(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_celular FOREIGN KEY (id_celular)
        REFERENCES celulares(id) ON DELETE RESTRICT
);

-- ============================================================
--  Datos de ejemplo (opcional)
-- ============================================================
INSERT INTO celulares (marca, modelo, precio, stock, sistema_operativo, gama) VALUES
('Samsung',  'Galaxy S24',     1199.99, 12, 'Android', 'Alta'),
('Apple',    'iPhone 15',      1299.00,  8, 'iOS',     'Alta'),
('Xiaomi',   'Redmi Note 13',   349.50, 25, 'Android', 'Media'),
('Motorola', 'Moto G Power',   279.00,  3, 'Android', 'Media'),
('Nokia',    'C100',            149.99,  2, 'Android', 'Baja');

INSERT INTO clientes (nombre, identificacion, correo, telefono) VALUES
('Ana Torres',   '100200300', 'ana@correo.com',   '3101234567'),
('Luis Gómez',   '100200301', 'luis@correo.com',  '3109876543'),
('María Pérez',  '100200302', 'maria@correo.com', '3115550011');

-- ============================================================
--  Usuarios de prueba (opcional, ajusta las credenciales)
-- ============================================================
-- El usuario/clave usados por la app se configuran en
-- tecnostore.persistencia.ConexionDB.
-- Crea el usuario en MySQL si tu instalación lo requiere:
--
-- CREATE USER 'tecnostore'@'localhost' IDENTIFIED BY 'tecnostore123';
-- GRANT ALL PRIVILEGES ON tecnostore_db.* TO 'tecnostore'@'localhost';
-- FLUSH PRIVILEGES;
