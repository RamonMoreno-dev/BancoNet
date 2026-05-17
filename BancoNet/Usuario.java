import java.util.ArrayList;

public class Usuario {

    private int idUsuario;
    private ArrayList<CuentaBancaria> cuentas;
    private String nombre;
    private String dni;
    private String numTlf;
    private String pais;

    public String getPais() {
        return this.pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    // Constructor vacío
    public Usuario(String nombre, String dni) {
        this.nombre = nombre;
        this.dni = dni;
    }

    // Constructor completo (sin id si lo genera la BD)
    public Usuario(String nombre, String dni, String numTlf, String pais) {
        this.nombre = nombre;
        this.dni = dni;
        this.numTlf = numTlf;
    }

    // Constructor completo con id
    public Usuario(int idUsuario, String nombre, String dni, String numTlf, String pais) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.dni = dni;
        this.numTlf = numTlf;
    }

    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNumTlf() {
        return numTlf;
    }

    public void setNumTlf(String numTlf) {
        this.numTlf = numTlf;
    }

    // toString (muy útil para debug)
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", dni='" + dni + '\'' +
                ", numTlf='" + numTlf + '\'' +
                '}';
    }
}