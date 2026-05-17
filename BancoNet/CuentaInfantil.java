public class CuentaInfantil extends CuentaBancaria {
    private double limite = 50;

    public CuentaInfantil(double saldo) {
        super(saldo);
    }

    @Override
    public void depositar(double cantidad) {
        super.setSaldo(super.getSaldo() + cantidad);
    }

    @Override
    public void retirar(double cantidad) {
        if (cantidad <= limite) {
            super.setSaldo(super.getSaldo() - cantidad);
        } else {
            System.out.println("Límite de retiro alcanzado");
        }
    }
}
