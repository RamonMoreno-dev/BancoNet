public class TarjetaDebito extends Tarjeta {

    public TarjetaDebito(String numeroTarjeta, String fechaVencimiento, String cvv, String tipo) {
        super(numeroTarjeta, fechaVencimiento, cvv, tipo);
    }

    @Override
    public void pagar(double cantidad) {
        if (cantidad <= cuenta.getSaldo()) {
            cuenta.setSaldo(cuenta.getSaldo() - cantidad);
            System.out.println("Pago con débito realizado. Saldo: " + cuenta.getSaldo());
        } else {
            System.out.println("Saldo insuficiente");
        }
    }
}