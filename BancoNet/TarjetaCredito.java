
public class TarjetaCredito extends Tarjeta {

    private double limiteCredito;
    private double gastadoCredito;

    public TarjetaCredito(String numeroTarjeta, String fechaVencimiento, String cvv, String tipo,
            double limiteCredito) {
        super(numeroTarjeta, fechaVencimiento, cvv, tipo);
        this.limiteCredito = limiteCredito;
    }

    @Override
    public void pagar(double cantidad) {
        if (limiteCredito >= cantidad) {
            limiteCredito -= cantidad;
            gastadoCredito += cantidad;
            System.out.println("Pago con crédito realizado. Saldo: " + limiteCredito + "€");
        } else {
            cuenta.setSaldo(cuenta.getSaldo() - cantidad);
            System.out.println("Pago con crédito realizado. Saldo disponible: " + limiteCredito + "€");
            System.out.println("Pago con crédito realizado. Saldo por devolver: " + gastadoCredito + "€");
            System.out.println("Límite de crédito superado ");
        }
    }
}