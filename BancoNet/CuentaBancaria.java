import java.util.ArrayList;

public abstract class CuentaBancaria implements Operar {
    protected String iban;
    protected int id_cuenta;
    protected String correo;
    protected String contrasenya;
    protected double saldo;
    protected ArrayList<Tarjeta> tarjetas = new ArrayList<>();
    protected ArrayList<Movimiento> movs = new ArrayList<>();
    protected Usuario user;
    protected boolean activo;

    public int getId_cuenta() {
        return this.id_cuenta;
    }

    public void setId_cuenta(int id_cuenta) {
        this.id_cuenta = id_cuenta;
    }

    public String getIban() {
        return this.iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public boolean isActivo() {
        return this.activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Usuario getUser() {
        return this.user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public ArrayList<Movimiento> getMovs() {
        return this.movs;
    }

    public void setMovs(ArrayList<Movimiento> movs) {
        this.movs = movs;
    }

    public String getContrasenya() {
        return this.contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    public String getCorreo() {
        return this.correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public CuentaBancaria(String iban, String correo, String contrasenya, double saldo, boolean activo, Usuario user) {
        this.iban = iban;
        this.correo = correo;
        this.contrasenya = contrasenya;
        this.saldo = saldo;
        this.activo = activo;
        this.user = user;

    }

    public double getSaldo() {
        return this.saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public CuentaBancaria(double saldo) {
        this.saldo = saldo;
    }

    public void addTarjeta(Tarjeta t) {
        tarjetas.add(t);
    }

    public ArrayList<Tarjeta> getTarjetas() {
        return tarjetas;
    }

    public abstract void depositar(double monto);

    public abstract void retirar(double monto);
}
