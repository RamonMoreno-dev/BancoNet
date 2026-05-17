
public class Administrador extends CuentaBancaria {

    public Administrador(String iban, String correo, String contrasenya, boolean activo, double saldo, Usuario user) {
        super(iban, correo, contrasenya, saldo, activo, user);
    }

    public void crearInforme() {
        System.out.println("Generando informe del sistema...");
    }

    public void depositar(double monto) {
    }

    public void retirar(double monto) {
    }
}