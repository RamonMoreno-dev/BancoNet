public abstract class Tarjeta implements Pagable {
    private String numeroTarjeta;
    private String fechaVencimiento;
    private String cvv;
    private String tipo;
    protected CuentaBancaria cuenta;

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNumeroTarjeta() {
        return this.numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getFechaVencimiento() {
        return this.fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getCvv() {
        return this.cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Tarjeta(String numeroTarjeta, String fechaVencimiento, String cvv, String tipo) {
        this.numeroTarjeta = numeroTarjeta;
        this.fechaVencimiento = fechaVencimiento;
        this.cvv = cvv;
        this.tipo = tipo;
    }

    @Override
    public abstract void pagar(double cantidad);
}
