DROP DATABASE IF EXISTS sistema_pedidos_fidelizacion;

CREATE DATABASE sistema_pedidos_fidelizacion
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE sistema_pedidos_fidelizacion;


CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    saldo_puntos INT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (saldo_puntos >= 0)
);


CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    categoria VARCHAR(30) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    existencia INT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (precio >= 0),
    CHECK (existencia >= 0)
);


CREATE TABLE menus (
    id_menu INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    id_sandwich INT NOT NULL,
    id_bebida INT NOT NULL,
    id_acompanamiento INT NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_menu_sandwich
        FOREIGN KEY (id_sandwich)
        REFERENCES productos(id_producto),

    CONSTRAINT fk_menu_bebida
        FOREIGN KEY (id_bebida)
        REFERENCES productos(id_producto),

    CONSTRAINT fk_menu_acompanamiento
        FOREIGN KEY (id_acompanamiento)
        REFERENCES productos(id_producto),

    CHECK (precio >= 0)
);


CREATE TABLE pedidos (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    total DECIMAL(10,2) NOT NULL DEFAULT 0,

    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente),

    CHECK (total >= 0)
);


CREATE TABLE detalle_pedido (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    tipo_item VARCHAR(20) NOT NULL,
    id_producto INT NULL,
    id_menu INT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_detalle_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedidos(id_pedido),

    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (id_producto)
        REFERENCES productos(id_producto),

    CONSTRAINT fk_detalle_menu
        FOREIGN KEY (id_menu)
        REFERENCES menus(id_menu),

    CHECK (cantidad > 0),
    CHECK (precio_unitario >= 0),
    CHECK (subtotal >= 0),

    CHECK (
        (tipo_item = 'PRODUCTO' AND id_producto IS NOT NULL AND id_menu IS NULL)
        OR
        (tipo_item = 'MENU' AND id_menu IS NOT NULL AND id_producto IS NULL)
    )
);


CREATE TABLE pagos (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodo VARCHAR(20) NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    monto_recibido DECIMAL(10,2) NULL,
    cambio DECIMAL(10,2) NULL,
    estado VARCHAR(20) NOT NULL,
    referencia VARCHAR(50) NULL UNIQUE,

    CONSTRAINT fk_pago_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedidos(id_pedido),

    CHECK (monto >= 0)
);


CREATE TABLE movimientos_inventario (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo VARCHAR(30) NOT NULL,
    cantidad INT NOT NULL,
    referencia VARCHAR(100),

    CONSTRAINT fk_movimiento_producto
        FOREIGN KEY (id_producto)
        REFERENCES productos(id_producto),

    CHECK (cantidad > 0)
);


CREATE TABLE recompensas (
    id_recompensa INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    puntos_requeridos INT NOT NULL,
    tipo_recompensa VARCHAR(20) NOT NULL,
    id_producto INT NULL,
    id_menu INT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_recompensa_producto
        FOREIGN KEY (id_producto)
        REFERENCES productos(id_producto),

    CONSTRAINT fk_recompensa_menu
        FOREIGN KEY (id_menu)
        REFERENCES menus(id_menu),

    CHECK (puntos_requeridos > 0),

    CHECK (
        (tipo_recompensa = 'PRODUCTO' AND id_producto IS NOT NULL AND id_menu IS NULL)
        OR
        (tipo_recompensa = 'MENU' AND id_menu IS NOT NULL AND id_producto IS NULL)
    )
);


CREATE TABLE canjes (
    id_canje INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_recompensa INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    puntos_utilizados INT NOT NULL,

    CONSTRAINT fk_canje_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente),

    CONSTRAINT fk_canje_recompensa
        FOREIGN KEY (id_recompensa)
        REFERENCES recompensas(id_recompensa),

    CHECK (puntos_utilizados > 0)
);


CREATE TABLE movimientos_puntos (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo VARCHAR(30) NOT NULL,
    puntos INT NOT NULL,
    saldo_resultante INT NOT NULL,
    id_pedido INT NULL,
    id_canje INT NULL,

    CONSTRAINT fk_puntos_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente),

    CONSTRAINT fk_puntos_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedidos(id_pedido),

    CONSTRAINT fk_puntos_canje
        FOREIGN KEY (id_canje)
        REFERENCES canjes(id_canje),

    CHECK (puntos > 0),
    CHECK (saldo_resultante >= 0)
);


INSERT INTO clientes (codigo, nombre, telefono)
VALUES
('CLI001', 'Carlos López', '55551234'),
('CLI002', 'Ana Hernández', '55552345'),
('CLI003', 'José García', '55553456');


INSERT INTO productos (codigo, categoria, nombre, precio, existencia)
VALUES
('SAN001', 'SANDWICH', 'Sándwich de pollo', 25.00, 20),
('SAN002', 'SANDWICH', 'Sándwich de jamón', 22.00, 20),
('BEB001', 'BEBIDA', 'Coca-Cola', 10.00, 30),
('BEB002', 'BEBIDA', 'Agua pura', 8.00, 30),
('ACO001', 'ACOMPANAMIENTO', 'Papas fritas', 12.00, 25),
('ACO002', 'ACOMPANAMIENTO', 'Ensalada', 10.00, 25);


INSERT INTO menus (
    codigo,
    nombre,
    id_sandwich,
    id_bebida,
    id_acompanamiento,
    precio
)
SELECT
    'MEN001',
    'Menú Clásico',
    s.id_producto,
    b.id_producto,
    a.id_producto,
    39.00
FROM productos s
JOIN productos b ON b.codigo = 'BEB001'
JOIN productos a ON a.codigo = 'ACO001'
WHERE s.codigo = 'SAN001';


INSERT INTO menus (
    codigo,
    nombre,
    id_sandwich,
    id_bebida,
    id_acompanamiento,
    precio
)
SELECT
    'MEN002',
    'Menú Ligero',
    s.id_producto,
    b.id_producto,
    a.id_producto,
    36.00
FROM productos s
JOIN productos b ON b.codigo = 'BEB002'
JOIN productos a ON a.codigo = 'ACO002'
WHERE s.codigo = 'SAN002';


INSERT INTO recompensas (
    codigo,
    nombre,
    puntos_requeridos,
    tipo_recompensa,
    id_producto
)
SELECT
    'REC001',
    'Bebida gratuita',
    10,
    'PRODUCTO',
    id_producto
FROM productos
WHERE codigo = 'BEB001';


INSERT INTO recompensas (
    codigo,
    nombre,
    puntos_requeridos,
    tipo_recompensa,
    id_producto
)
SELECT
    'REC002',
    'Acompañamiento gratuito',
    15,
    'PRODUCTO',
    id_producto
FROM productos
WHERE codigo = 'ACO001';


INSERT INTO recompensas (
    codigo,
    nombre,
    puntos_requeridos,
    tipo_recompensa,
    id_producto
)
SELECT
    'REC003',
    'Sándwich gratuito',
    30,
    'PRODUCTO',
    id_producto
FROM productos
WHERE codigo = 'SAN001';


INSERT INTO recompensas (
    codigo,
    nombre,
    puntos_requeridos,
    tipo_recompensa,
    id_menu
)
SELECT
    'REC004',
    'Menú gratuito',
    50,
    'MENU',
    id_menu
FROM menus
WHERE codigo = 'MEN001';


SELECT * FROM clientes;
SELECT * FROM productos;
SELECT * FROM menus;
SELECT * FROM recompensas;
