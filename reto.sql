drop database if exists banco;
CREATE DATABASE banco;
USE banco;

CREATE TABLE usuario (
    id_usuario INT PRIMARY KEY auto_increment,
    nombre VARCHAR(100),
    dni VARCHAR(20) UNIQUE,
    num_tlf VARCHAR(20),
    pais varchar(30)
);

CREATE TABLE cuenta (
    id_cuenta INT PRIMARY KEY auto_increment,
    iban varchar (60),
    correo VARCHAR(100),
    contrasena VARCHAR(100),
    saldo varchar(100),
    activo boolean, 
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) on delete cascade
);


CREATE TABLE tarjeta (
    numero_tarjeta VARCHAR(20) PRIMARY KEY,
    fecha_vencimiento varchar(40),
    cvv VARCHAR(4),
    id_cuenta INT,
    tipo ENUM('Tarjeta Debito', 'Tarjeta Credito'),
    FOREIGN KEY (id_cuenta) REFERENCES cuenta(id_cuenta)
);
CREATE TABLE tarjeta_prepago (
    numero_tarjeta VARCHAR(20) PRIMARY KEY,
    saldo DECIMAL(10,2),
    FOREIGN KEY (numero_tarjeta) REFERENCES tarjeta(numero_tarjeta)
);
CREATE TABLE tarjeta_credito (
    numero_tarjeta VARCHAR(20) PRIMARY KEY,
    credito DECIMAL(10,2),
    credito_prestado DECIMAL(10,2),
    FOREIGN KEY (numero_tarjeta) REFERENCES tarjeta(numero_tarjeta)
);
CREATE TABLE tarjeta_debito (
    numero_tarjeta VARCHAR(20) PRIMARY KEY,
    FOREIGN KEY (numero_tarjeta) REFERENCES tarjeta(numero_tarjeta)
);
CREATE TABLE tarjeta_virtual (
    numero_tarjeta VARCHAR(20) PRIMARY KEY,
    FOREIGN KEY (numero_tarjeta) REFERENCES tarjeta(numero_tarjeta)
);
CREATE TABLE cuenta_corriente (
    id_cuenta INT PRIMARY KEY,
    FOREIGN KEY (id_cuenta) REFERENCES cuenta(id_cuenta)
);
CREATE TABLE cuenta_infantil (
	id_cuenta INT PRIMARY KEY,
    FOREIGN KEY (id_cuenta) REFERENCES cuenta(id_cuenta)
);
CREATE TABLE paypal (
    id_paypal INT PRIMARY KEY AUTO_INCREMENT,
    correo VARCHAR(100),
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE bizum (
    id_bizum INT PRIMARY KEY AUTO_INCREMENT,
    num_tlf VARCHAR(20),
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);
CREATE TABLE cuenta_administrador (
	id_cuenta_admin INT primary key,
    
    
    FOREIGN KEY (id_cuenta_admin) REFERENCES cuenta(id_cuenta)
);
 CREATE TABLE movimiento (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    importe       DOUBLE,
    asunto        VARCHAR(255),
    fecha         DATETIME DEFAULT NOW(),
    id_cuenta     INT,
    FOREIGN KEY (id_cuenta) REFERENCES cuenta(id_cuenta) ON DELETE CASCADE
);