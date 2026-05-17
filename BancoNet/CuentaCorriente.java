
public class CuentaCorriente extends CuentaBancaria {
    public CuentaCorriente(String iban, String nombre, String contrasenya, boolean activo, double saldo, Usuario user) {
        super(iban, nombre, contrasenya, saldo, activo, user);
    }

    @Override
    public void depositar(double monto) {
        super.setSaldo(super.getSaldo() + monto);
    }

    @Override
    public void retirar(double monto) {
        if (super.getSaldo() >= monto) {
            super.setSaldo(super.getSaldo() - monto);
        } else {
            System.out.println("Saldo insuficiente");
        }
    }
}
