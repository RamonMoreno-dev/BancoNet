# ------ BancoNet -------

Aplicación bancaria de escritorio desarrollada en **Java** con **JavaFX** y **MySQL**. Simula el funcionamiento de un banco real con panel de usuario y panel de administrador, permitiendo gestionar cuentas, transferencias, tarjetas y movimientos. Este proyecto cuenta con alrededor de unas 2400 líneas de código 

---

## --> Índice

- [Descripción del proyecto](#-descripción-del-proyecto)
- [Tecnologías utilizadas](#-tecnologías-utilizadas)
- [Estructura del código](#-estructura-del-código)
- [Diagrama de clases](#-diagrama-de-clases)
- [Diagrama Entidad-Relación](#-diagrama-entidad-relación)
- [Manual de usuario](#-manual-de-usuario)
- [Requisitos](#-requisitos)

---

## --> Descripción del proyecto

**BancoNet** es una aplicación bancaria de escritorio que permite:

- Iniciar sesión como **usuario** o **administrador**
- Consultar el **saldo** y el historial de **movimientos**
- Realizar **transferencias** por IBAN, **Bizum** por teléfono y **PayPal** por correo
- Visualizar las **tarjetas** asociadas (débito y crédito)
- Ver una **gráfica** de ingresos y gastos por año
- Desde el panel de **administrador**: crear usuarios, gestionar cuentas, crear tarjetas, ver todos los movimientos y exportar información

---

## --> Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 24+ | Lenguaje principal |
| JavaFX | Interfaz gráfica de usuario |
| MySQL | Base de datos relacional |
| JDBC | Conexión Java ↔ MySQL |
| CSS | Estilización de la interfaz |

---

## --> Estructura del código

```
BancoNet/
│
├── src/
│   ├── Banco.java              # Clase principal (Application JavaFX)
│   ├── CuentaBancaria.java     # Clase abstracta base para cuentas
│   ├── CuentaCorriente.java    # Cuenta corriente (hereda CuentaBancaria)
│   ├── Administrador.java      # Cuenta administrador (hereda CuentaBancaria)
│   ├── Usuario.java            # Datos personales del usuario
│   ├── Movimiento.java         # Representa una transacción
│   ├── Tarjeta.java            # Clase abstracta para tarjetas
│   ├── TarjetaCredito.java     # Tarjeta de crédito (hereda Tarjeta)
│   ├── TarjetaDebito.java      # Tarjeta de débito (hereda Tarjeta)
│   ├── Conectar.java           # Gestión de la conexión a MySQL
│   └── Libreria.java           # Utilidades: validación DNI, correo...
│
├── CSS/
│   ├── usuario.css             # Estilos del panel de usuario
│   ├── bienvenida.css          # Estilos de la pantalla de inicio de sesión
│   └── admin.css               # Estilos del panel de administrador
│
├── Imagenes/
│   └── fondo_banco.jpg         # Imagen de fondo de la pantalla de login
│
└── img/                        # Imágenes para la documentación
    ├── login.png
    ├── saldo.png
    ├── transferencia.png
    ├── tarjetas.png
    ├── grafica.png
    ├── ....
```

### Descripción de las clases principales

#### `Banco.java`
Clase principal que extiende `Application` de JavaFX. Contiene toda la lógica de la interfaz gráfica:
- `start()` — punto de entrada, carga datos de la BD y lanza la pantalla de login
- `iniciarSesion()` — pantalla de login con validación de usuario/admin
- `bancoApp()` — panel de usuario con saldo, transferencias, tarjetas y gráfica
- `panelAdmin()` — panel de administrador con gestión completa
- `cargarCuentasBDD()` — carga cuentas, movimientos y tarjetas desde MySQL
- `cargarUsuariosBDD()` — carga usuarios desde MySQL
- `cargarMovimientosBDD()` — carga todos los movimientos (para el panel admin)
- `generateIbans()` — genera IBANs aleatorios válidos por país
- `generarFicheroCuentas()` — exporta información de cuentas a un fichero `.txt`

#### `CuentaBancaria.java` *(abstracta)*
Clase base que representa cualquier cuenta bancaria. Contiene: `iban`, `correo`, `contraseña`, `activo`, `saldo`, `user`, listas de `movimientos` y `tarjetas`.

#### `CuentaCorriente.java`
Hereda `CuentaBancaria`. Representa una cuenta de usuario normal.

#### `Administrador.java`
Hereda `CuentaBancaria`. Representa una cuenta con privilegios de administrador.

#### `Usuario.java`
Almacena los datos personales: `nombre`, `dni`, `num_tlf`, `pais`.

#### `Movimiento.java`
Representa una transacción: `importe`, `asunto`, `fecha` y el `Usuario` asociado.

#### `Tarjeta.java` *(abstracta)*
Clase base para tarjetas: `numeroTarjeta`, `cvv`, `fechaVencimiento`, `tipo`.

#### `TarjetaCredito.java` / `TarjetaDebito.java`
Heredan `Tarjeta`. Añaden lógica específica de cada tipo.

#### `Conectar.java`
Proporciona el método estático `conectar()` que devuelve una conexión JDBC a MySQL.

#### `Libreria.java`
Métodos estáticos de utilidad: `validarDoc()` para DNI/NIF y `validarCorreo()` para correos electrónicos.

---

## --> Diagrama de clases

> El diagrama muestra la herencia y las relaciones entre las clases del proyecto.

![Diagrama de clases](BancoNet\img\reto.png)



---

## --> Diagrama Entidad-Relación

> Estructura de la base de datos MySQL del proyecto.

![Diagrama Entidad-Relación](BancoNet\img\sql.png)

```
usuario (id_usuario, nombre, dni, num_tlf, pais)
    │
    └──< cuenta (id_cuenta, iban, correo, contrasena, saldo, activo, id_usuario)
                │
                ├──< movimiento (id_movimiento, importe, asunto, fecha, id_cuenta)
                │
                ├──< tarjeta (id_tarjeta, numero_tarjeta, fecha_vencimiento, cvv, tipo, id_cuenta)
                │
                └──< cuenta_administrador (id_cuenta_admin)
```

### Tablas de la base de datos

| Tabla | Descripción |
|---|---|
| `usuario` | Datos personales de los titulares |
| `cuenta` | Cuentas bancarias con saldo y credenciales |
| `cuenta_administrador` | Marca qué cuentas son administradoras |
| `movimiento` | Historial de todas las transacciones |
| `tarjeta` | Tarjetas asociadas a cada cuenta |

---

## --> Manual de usuario

### 1. Pantalla de inicio de sesión

Al abrir la aplicación se muestra la pantalla de login.

![Login](BancoNet\img\login.png)

- Introduce tu **correo** y **contraseña**
- Pulsa **Iniciar sesión**
- Si las credenciales son incorrectas aparecerá un mensaje de error
- Si la cuenta está **suspendida**, se mostrará un aviso con un teléfono de contacto
- El administrador accede con el correo supremo `rmg@gmail.com` y contraseña `123` para poder crear más cuentas de administrador 

---

### 2. Panel de usuario — Saldo y movimientos

![Saldo](BancoNet\img\saldo.png)

- Visualiza el **saldo actual** de tu cuenta
- Consulta los **últimos movimientos** con fecha, concepto e importe
- Los **ingresos** aparecen en verde y los **gastos** en rojo

---

### 3. Transferir dinero

![Transferencia](BancoNet\img\transferencia.png)

Desde el menú lateral, pulsa **Transferir dinero**. Tienes tres opciones:

| Método | Cómo se identifica al destinatario |
|---|---|
| **Transferencia** | Número IBAN |
| **Bizum** | Número de teléfono |
| **PayPal** | Correo electrónico |

Introduce la cantidad y pulsa el botón correspondiente. El sistema comprueba que haya **saldo suficiente** y que el destinatario **exista**.

---

### 4. Tarjetas

![Tarjetas](BancoNet\img\tarjetas.png)

- Visualiza las tarjetas asociadas a tu cuenta
- Las **tarjetas de crédito** se muestran en azul
- Las **tarjetas de débito** se muestran en gris
- Si no tienes ninguna tarjeta, aparece un mensaje informativo

---

### 5. Gráfica de ingresos y gastos

![Gráfica](BancoNet\img\grafica.png)

- Selecciona el **año** en el desplegable
- La gráfica muestra los **ingresos** (línea verde) y los **gastos** (línea roja) mes a mes

---

### 6. Panel de administrador — Gestión de usuarios

![Admin Usuarios](BancoNet\img\usuarios.png)

Accesible si inicias sesión con una cuenta de administrador.

- **Crear usuario**: rellena el formulario con nombre, correo, contraseña, DNI, teléfono, país y saldo inicial. Activa el toggle *Admin* para crear un administrador.
- **Tabla de usuarios**: ve todas las cuentas registradas. Para cada una puedes:
  - **Ingresar** o **Retirar** dinero directamente
  - **Bloquear / Desbloquear** la cuenta
  - **Eliminar** la cuenta
- **Filtrar** por DNI con el campo de búsqueda

---

### 7. Panel de administrador — Movimientos

![Admin Movimientos](BancoNet\img\movs.png)

- Consulta **todos los movimientos** de todos los usuarios
- Filtra por DNI para ver los movimientos de un usuario concreto

---

### 8. Crear tarjetas (admin)

- Introduce el **IBAN** de la cuenta
- Selecciona el tipo: *Tarjeta Débito* o *Tarjeta Crédito*
- Pulsa **Crear Tarjeta** — el sistema genera número, CVV y fecha de vencimiento automáticamente

---

### 9. Exportar información

Pulsa **Imprimir información cuentas** en el panel admin para generar un fichero `cuentas.txt` con el resumen de todas las cuentas.

---

## ⚙️ Requisitos

- Java 24 o superior
- JavaFX SDK
- MySQL 8.0+
- Conector JDBC para MySQL (`mysql-connector-j`)
- Dependencias correspondientes

### Configuración de la base de datos

1. Crea la base de datos en MySQL
2. Ejecuta el script SQL para crear las tablas
3. Configura las credenciales de conexión en `Conectar.java`

---

## --> Autor

Proyecto desarrollado por **Ramón Moreno Gálvez** — Módulo de Programación — **Curso 2025/2026**
