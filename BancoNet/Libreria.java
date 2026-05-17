import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.*;

public class Libreria {
    public static String pedirString(Scanner sc, String dato) {
        String texto = "";
        try {
            System.out.println("Introduce " + dato);
            texto = sc.nextLine();
        } catch (Exception e) {
            sc.nextLine();
            System.out.println("Error inesperado, por favor intentalo de nuevo");
            pedirString(sc, dato);
        }
        return texto;
    }

    public static int pedirInt(Scanner sc, String dato) {
        int num = 0;
        try {
            System.out.println("Introduce " + dato);
            num = sc.nextInt();
        } catch (InputMismatchException e) {
            sc.nextLine();
            System.out.println("Error, introduce un número sin decimales");
            pedirInt(sc, dato);
        } catch (Exception e) {
            sc.nextLine();
            System.out.println("Error inesperado, por favor intentalo de nuevo");
            pedirInt(sc, dato);
        }
        sc.nextLine();
        return num;
    }

    public static double pedirDouble(Scanner sc, String dato) {
        double num = 0;
        try {
            System.out.println("Introduce " + dato);
            num = sc.nextDouble();
        } catch (InputMismatchException e) {
            sc.nextLine();
            System.out.println("Error, introduce un número con decimales");
            pedirDouble(sc, dato);
        } catch (Exception e) {
            sc.nextLine();
            System.out.println("Error inesperado, por favor intentalo de nuevo");
            pedirDouble(sc, dato);
        }
        sc.nextLine();
        return num;
    }

    public static boolean validarCorreo(String texto) {
        Pattern pat = Pattern.compile("^[A-Za-z0-9-._!?]+[\\@][A-Za-z0-9-._!?]+[\\.][A-Za-z0-9-._!?]+$");

        Matcher match = pat.matcher(texto);

        return match.matches();
    }

    public static boolean validarMatricula(String texto) {
        Pattern pat = Pattern.compile("^[0-9]{4}[A-Z]{3}$");

        Matcher match = pat.matcher(texto);

        return match.matches();
    }

    public static boolean validarDoc(String texto, String pais) {

        Map<String, String> patterns = new HashMap<>();

        patterns.put("ES", "^[0-9]{8}[A-Z]$");
        patterns.put("DE", "^[A-Z0-9]{9,11}$");
        patterns.put("FR", "^[A-Z0-9]{8,15}$");
        patterns.put("IT", "^[A-Z0-9]{16}$");
        patterns.put("PT", "^[0-9]{9}$");
        patterns.put("BE", "^[0-9]{11}$");
        Pattern pat = Pattern.compile(patterns.get(pais));
        Matcher match = pat.matcher(texto);

        return match.matches();
    }

}
