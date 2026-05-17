public class Movimiento {
    private Usuario user;
    private double importe;
    private String asunto;
    private String fecha;

    public double getImporte() {
        return this.importe;
    }

    public Usuario getUser() {
        return this.user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public String getAsunto() {
        return this.asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getFecha() {
        return this.fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Movimiento(Usuario usuario, double importe, String asunto, String fecha) {
        this.user = usuario;
        this.importe = importe;
        this.asunto = asunto;
        this.fecha = fecha;

    }
}
