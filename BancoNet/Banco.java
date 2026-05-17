import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.control.ToggleButton;
import java.util.Map;
import java.util.Random;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.scene.chart.*;

public class Banco extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Usuario userAdmin = new Usuario("PEPE", "30300603N", "123213", "España");
        CuentaBancaria cuentaAdminSuprema = new Administrador("", "rmg@gmail.com", "123", true, 0, userAdmin);

        ArrayList<Usuario> users = cargarUsuariosBDD(new ArrayList<>());
        ArrayList<CuentaBancaria> cuentas = cargarCuentasBDD(new ArrayList<>(), "cuenta");

        ArrayList<Movimiento> movimientos = cargarMovimientosBDD();

        users.add(userAdmin);
        cuentas.add(cuentaAdminSuprema);

        inicarSesion(cuentas, users, movimientos);
    }

    private void bancoApp(CuentaBancaria cuenta, ArrayList<CuentaBancaria> cuentas) {
        BorderPane root = new BorderPane();

        VBox menu = new VBox(40);
        menu.setPadding(new Insets(20));
        menu.setPrefWidth(170);
        menu.getStyleClass().add("menu");

        Label lblBienvenido = new Label("Bienvenido, " + cuenta.getUser().getNombre());
        lblBienvenido.getStyleClass().add("menu-titulo");

        Button btnSaldo = new Button("Saldo");
        Button btnMover = new Button("Transferir dinero");
        Button btnTarjetas = new Button("Tarjetas");
        Button btnGastosIngresos = new Button("Gráfica");
        Button btnSalir = new Button("🔒 CERRAR SESIÓN");

        Button[] botones = { btnSaldo, btnMover, btnTarjetas, btnGastosIngresos };
        for (Button b : botones) {
            b.getStyleClass().add("menu-btn");
        }
        btnSalir.getStyleClass().add("menu-btn-salir");
        VBox.setMargin(btnSalir, new Insets(220, 0, 0, 0));

        menu.getChildren().addAll(lblBienvenido, btnSaldo, btnMover, btnTarjetas, btnGastosIngresos, btnSalir);

        StackPane contenido = new StackPane();
        contenido.getStyleClass().add("contenido");

        // VER EL SALDO
        VBox vistaSaldo = new VBox(20);
        vistaSaldo.setPadding(new Insets(40));
        vistaSaldo.setAlignment(Pos.TOP_CENTER);

        Label tituloSaldo = new Label("Saldo actual");
        tituloSaldo.getStyleClass().add("titulo-saldo");

        Label saldoLabel = new Label(Double.toString(cuenta.getSaldo()));
        saldoLabel.getStyleClass().add("saldo-label");

        Region sepSaldo = new Region();
        sepSaldo.setPrefHeight(1);
        sepSaldo.setMaxWidth(500);
        sepSaldo.getStyleClass().add("separador");

        Label tituloMov = new Label("Últimos movimientos");
        tituloMov.getStyleClass().add("subtitulo");

        VBox movimientosBox = new VBox(8);

        System.out.println(cuenta.getMovs().size());

        for (Movimiento mov : cuenta.getMovs()) {

            HBox fila = new HBox(15);
            fila.setPadding(new Insets(10));
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.getStyleClass().add("mov-fila");

            Label lImporte = new Label(String.format("%.2f €", mov.getImporte()));

            boolean esIngreso = mov.getImporte() > 0;
            lImporte.setPrefWidth(100);

            if (esIngreso) {
                lImporte.getStyleClass().add("mov-ingreso");
            } else {
                lImporte.getStyleClass().add("mov-gasto");
            }

            Label lConcepto = new Label(mov.getAsunto());
            lConcepto.setPrefWidth(250);
            lConcepto.getStyleClass().add("mov-concepto");

            Label lFecha = new Label(mov.getFecha());
            lFecha.getStyleClass().add("mov-fecha");

            fila.getChildren().addAll(lImporte, lConcepto, lFecha);
            movimientosBox.getChildren().add(fila);
        }
        ScrollPane scrollMov = new ScrollPane(movimientosBox);

        scrollMov.getStyleClass().add("scroll-transparente");

        vistaSaldo.getChildren().addAll(tituloSaldo, saldoLabel, sepSaldo, tituloMov, scrollMov);

        VBox vistaMover = new VBox(15);
        vistaMover.setPadding(new Insets(40));
        vistaMover.setMaxWidth(450);

        Label tituloMover = new Label("Mover dinero");
        tituloMover.getStyleClass().add("titulo");

        // Transferir
        Label lblTransferir = new Label("Transferir a otra cuenta");
        lblTransferir.getStyleClass().add("label-seccion");

        TextField tfCuenta = new TextField();
        tfCuenta.setPromptText("Número de cuenta destino");
        tfCuenta.setMaxWidth(280);

        TextField tfTransferir = new TextField();
        tfTransferir.setPromptText("Cantidad en €");
        tfTransferir.setMaxWidth(280);

        Button btnTransferir = new Button("Transferir");
        btnTransferir.getStyleClass().add("btn-azul");

        Label msgMover = new Label();
        msgMover.getStyleClass().add("msg-error");

        btnTransferir.setOnAction(e -> {
            boolean existe = false;
            String ibanDestino = tfCuenta.getText().trim();
            String cantidadStr = tfTransferir.getText().trim();
            double cantidad = Double.parseDouble(cantidadStr);
            if (cantidad <= 0) {
                msgMover.setText("La cantidad debe ser mayor que 0");
            }
            if (cantidad > cuenta.getSaldo()) {
                msgMover.setText("Saldo insuficiente");

            } else {
                for (CuentaBancaria c : cuentas) {
                    if (tfCuenta.getText().equals(c.getIban())) {
                        String q = "UPDATE cuenta SET saldo = saldo + ? WHERE iban = ?";
                        String q2 = "UPDATE cuenta SET saldo = saldo - ? WHERE iban = ?";
                        String qMov = "INSERT INTO movimiento(importe, asunto, fecha, id_cuenta) VALUES (?, ?, NOW(), (SELECT id_cuenta FROM cuenta WHERE iban = ?))";
                        try (Connection con = Conectar.conectar()) {
                            PreparedStatement ps = con.prepareStatement(q);
                            ps.setDouble(1, cantidad);
                            ps.setString(2, ibanDestino);
                            ps.executeUpdate();

                            PreparedStatement ps2 = con.prepareStatement(q2);
                            ps2.setDouble(1, cantidad);
                            ps2.setString(2, cuenta.getIban());
                            ps2.executeUpdate();

                            PreparedStatement psMov = con.prepareStatement(qMov);
                            psMov.setDouble(1, cantidad);
                            psMov.setString(2, "Transferencia recibida");
                            psMov.setString(3, ibanDestino);
                            psMov.executeUpdate();

                            PreparedStatement psMovOrigen = con.prepareStatement(qMov);
                            psMovOrigen.setDouble(1, -cantidad);
                            psMovOrigen.setString(2, "Transferencia enviada a " + ibanDestino);
                            psMovOrigen.setString(3, cuenta.getIban());
                            psMovOrigen.executeUpdate();

                            cuenta.retirar(cantidad);
                            c.depositar(cantidad);
                            msgMover.setText("Dinero transferido");
                            existe = true;
                        } catch (Exception a) {
                            msgMover.setText("Ha ocurrido un error");
                        }
                    }

                }
                if (!existe) {
                    msgMover.setText("La cuenta destino no existe");
                } else {
                    msgMover.setText("Dinero transferido");
                }
            }
        });

        Region sep2 = crearSeparador();

        // Bizum
        Label lblBizum = new Label("Enviar por Bizum");
        lblBizum.getStyleClass().add("label-seccion");

        TextField tfTelefono = new TextField();
        tfTelefono.setPromptText("Número de teléfono");
        tfTelefono.setMaxWidth(280);

        TextField tfBizum = new TextField();
        tfBizum.setPromptText("Cantidad en €");
        tfBizum.setMaxWidth(280);

        Button btnBizum = new Button("Enviar Bizum");
        btnBizum.getStyleClass().add("btn-bizum");

        btnBizum.setOnAction(e -> {
            boolean existe = false;
            String num_tlf = tfTelefono.getText().trim();
            String cantidadStr = tfBizum.getText().trim();
            double cantidad = Double.parseDouble(cantidadStr);

            if (cantidad <= 0) {
                msgMover.setText("La cantidad debe ser mayor que 0");
            }
            if (cantidad > cuenta.getSaldo()) {
                msgMover.setText("Saldo insuficiente");

            } else {

                String iban = null;
                for (CuentaBancaria c : cuentas) {
                    if (c.getUser().getNumTlf().equals(num_tlf)) {
                        iban = c.getIban();
                    }
                }
                if (iban == null) {
                    msgMover.setText("No se encontró ningún usuario con este teléfono");
                } else {
                    for (CuentaBancaria c : cuentas) {
                        if (iban.equals(c.getIban())) {
                            String q = "UPDATE cuenta SET saldo = saldo + ? WHERE iban = ?";
                            String q2 = "UPDATE cuenta SET saldo = saldo - ? WHERE iban = ?";
                            String qMov = "INSERT INTO movimiento(importe, asunto, fecha, id_cuenta) VALUES (?, ?, NOW(), (SELECT id_cuenta FROM cuenta WHERE iban = ?))";
                            try (Connection con = Conectar.conectar()) {
                                PreparedStatement ps = con.prepareStatement(q);
                                ps.setDouble(1, cantidad);
                                ps.setString(2, iban);
                                ps.executeUpdate();

                                PreparedStatement ps2 = con.prepareStatement(q2);
                                ps2.setDouble(1, cantidad);
                                ps2.setString(2, cuenta.getIban());
                                ps2.executeUpdate();

                                PreparedStatement psMov = con.prepareStatement(qMov);
                                psMov.setDouble(1, cantidad);
                                psMov.setString(2, "Transferencia recibida");
                                psMov.setString(3, iban);
                                psMov.executeUpdate();

                                PreparedStatement psMovOrigen = con.prepareStatement(qMov);
                                psMovOrigen.setDouble(1, -cantidad);
                                psMovOrigen.setString(2, "Transferencia enviada a " + iban);
                                psMovOrigen.setString(3, cuenta.getIban());
                                psMovOrigen.executeUpdate();

                                cuenta.retirar(cantidad);
                                c.depositar(cantidad);
                                msgMover.setText("Dinero transferido");
                                existe = true;
                            } catch (Exception a) {
                                msgMover.setText("Ha ocurrido un error");
                            }
                        }
                    }

                }
                if (!existe) {
                    msgMover.setText("La cuenta destino no existe");
                } else {
                    msgMover.setText("Dinero transferido");
                }
            }
        });

        Region sep3 = crearSeparador();

        // PayPal
        Label lblPaypal = new Label("Pagar con PayPal");
        lblPaypal.getStyleClass().add("label-seccion");

        TextField tfEmailPaypal = new TextField();
        tfEmailPaypal.setPromptText("Email de PayPal destino");
        tfEmailPaypal.setMaxWidth(280);

        TextField tfPaypal = new TextField();
        tfPaypal.setPromptText("Cantidad en €");
        tfPaypal.setMaxWidth(280);

        Button btnPaypal = new Button("Pagar PayPal");
        btnPaypal.getStyleClass().add("btn-paypal");

        btnPaypal.setOnAction(e -> {
            boolean existe = false;
            String email = tfEmailPaypal.getText().trim();
            String cantidadStr = tfPaypal.getText().trim();
            double cantidad = Double.parseDouble(cantidadStr);

            if (cantidad <= 0) {
                msgMover.setText("La cantidad debe ser mayor que 0");
            }
            if (cantidad > cuenta.getSaldo()) {
                msgMover.setText("Saldo insuficiente");

            } else {
                String iban = null;
                for (CuentaBancaria c : cuentas) {
                    if (c.getCorreo().equals(email)) {
                        iban = c.getIban();
                    }
                }
                if (iban == null) {
                    msgMover.setText("No se encontró ningún usuario con este teléfono");
                } else {
                    for (CuentaBancaria c : cuentas) {
                        if (iban.equals(c.getIban())) {
                            String q = "UPDATE cuenta SET saldo = saldo + ? WHERE iban = ?";
                            String q2 = "UPDATE cuenta SET saldo = saldo - ? WHERE iban = ?";
                            String qMov = "INSERT INTO movimiento(importe, asunto, fecha, id_cuenta) VALUES (?, ?, NOW(), (SELECT id_cuenta FROM cuenta WHERE iban = ?))";
                            try (Connection con = Conectar.conectar()) {
                                PreparedStatement ps = con.prepareStatement(q);
                                ps.setDouble(1, cantidad);
                                ps.setString(2, iban);
                                ps.executeUpdate();

                                PreparedStatement ps2 = con.prepareStatement(q2);
                                ps2.setDouble(1, cantidad);
                                ps2.setString(2, cuenta.getIban());
                                ps2.executeUpdate();

                                PreparedStatement psMov = con.prepareStatement(qMov);
                                psMov.setDouble(1, cantidad);
                                psMov.setString(2, "Transferencia recibida");
                                psMov.setString(3, iban);
                                psMov.executeUpdate();

                                PreparedStatement psMovOrigen = con.prepareStatement(qMov);
                                psMovOrigen.setDouble(1, -cantidad);
                                psMovOrigen.setString(2, "Transferencia enviada a " + iban);
                                psMovOrigen.setString(3, cuenta.getIban());
                                psMovOrigen.executeUpdate();

                                cuenta.retirar(cantidad);
                                c.depositar(cantidad);
                                msgMover.setText("Dinero transferido");
                                existe = true;
                            } catch (Exception a) {
                                msgMover.setText("Ha ocurrido un error");
                            }
                        }
                    }

                }
                if (!existe) {
                    msgMover.setText("La cuenta destino no existe");
                } else {
                    msgMover.setText("Dinero transferido");
                }
            }
        });

        vistaMover.getChildren().addAll(
                tituloMover,
                lblTransferir, tfCuenta, tfTransferir, btnTransferir,
                sep2,
                lblBizum, tfTelefono, tfBizum, btnBizum,
                sep3,
                lblPaypal, tfEmailPaypal, tfPaypal, btnPaypal,
                msgMover);

        ScrollPane scrollMover = new ScrollPane(vistaMover);
        scrollMover.setFitToWidth(true);
        scrollMover.getStyleClass().add("scroll-transparente");

        VBox vistaTarjetas = new VBox(25);
        vistaTarjetas.setPadding(new Insets(40));
        vistaTarjetas.setAlignment(Pos.TOP_CENTER);

        Label tituloTarjetas = new Label("Mis tarjetas");
        tituloTarjetas.getStyleClass().add("titulo");

        vistaTarjetas.getChildren().addAll(tituloTarjetas);

        if (cuenta.getTarjetas().isEmpty()) {
            vistaTarjetas.getChildren().add(new Label("No tienes tarjetas asociadas"));
        } else {
            for (Tarjeta t : cuenta.getTarjetas()) {
                String tipo = t.getTipo();
                String css;
                if (tipo.equals("Tarjeta Credito")) {
                    css = "tarjeta-azul";
                } else {
                    css = "tarjeta-gris";
                }
                VBox tarjeta = crearTarjeta(tipo, t.getNumeroTarjeta(), css);
                vistaTarjetas.getChildren().add(tarjeta);
            }
        }
        VBox gastos = new VBox(30);

        ComboBox<Integer> anyos = new ComboBox<>();
        anyos.getItems().addAll(2023, 2024, 2025, 2026);
        anyos.setValue(2026);
        int anyo = anyos.getValue();
        anyos.getStyleClass().add("anyos");

        gastos.getChildren().addAll(crearGrafica(cuenta, cuentas, anyo), anyos);

        anyos.valueProperty().addListener((obs, anterior, nuevo) -> {
            if (nuevo != null) {
                gastos.getChildren().remove(0);
                gastos.getChildren().addFirst(crearGrafica(cuenta, cuentas, nuevo));
            }
        });

        // Acciones menu
        btnSaldo.setOnAction(e -> contenido.getChildren().setAll(vistaSaldo));
        btnMover.setOnAction(e -> contenido.getChildren().setAll(scrollMover));
        btnTarjetas.setOnAction(e -> contenido.getChildren().setAll(vistaTarjetas));
        btnGastosIngresos.setOnAction(e -> contenido.getChildren().setAll(gastos));
        btnSalir.setOnAction(e -> ((Stage) root.getScene().getWindow()).close());

        contenido.getChildren().add(vistaSaldo);

        root.setLeft(menu);
        root.setCenter(contenido);

        Stage bancoStage = new Stage();

        Scene scene = new Scene(root, 1000, 680);
        scene.getStylesheets().add(getClass().getResource("/CSS/usuario.css").toExternalForm());

        bancoStage.setTitle("BancoNeetJumper");
        bancoStage.setScene(scene);
        bancoStage.show();
    }

    private LineChart crearGrafica(CuentaBancaria cuenta, ArrayList<CuentaBancaria> cuentas, int anyo) {
        // Gráfica
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<Number, Number> grafica = new LineChart<>(xAxis, yAxis);
        VBox.setMargin(grafica, new Insets(120, 10, 10, 10));

        double[] ingresos = new double[12];
        double[] gastos = new double[12];

        for (Movimiento m : cuenta.getMovs()) {
            LocalDate fechaForm = LocalDate.parse(m.getFecha(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if (fechaForm.getYear() == anyo) {
                int mes = fechaForm.getMonthValue() - 1;
                if (m.getImporte() > 0) {
                    ingresos[mes] += m.getImporte();
                } else {
                    gastos[mes] += Math.abs(m.getImporte());
                }
            }

        }

        XYChart.Series<Number, Number> serieIngresos = new XYChart.Series<>();
        serieIngresos.setName("Ingresos");

        XYChart.Series<Number, Number> serieGastos = new XYChart.Series<>();
        serieGastos.setName("Gastos");

        for (int i = 0; i < 12; i++) {
            serieIngresos.getData().add(new XYChart.Data<>(i + 1, ingresos[i]));
            serieGastos.getData().add(new XYChart.Data<>(i + 1, gastos[i]));
        }

        grafica.getData().addAll(serieIngresos, serieGastos);

        return grafica;
    }

    private VBox crearTarjeta(String nombre, String numero, String claseCSS) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(25));
        card.setPrefWidth(340);
        card.getStyleClass().addAll("tarjeta", claseCSS);

        Label lblNombre = new Label(nombre);
        lblNombre.getStyleClass().add("tarjeta-nombre");

        Label lblNumero = new Label(numero);
        lblNumero.getStyleClass().add("tarjeta-numero");

        card.getChildren().addAll(lblNombre, lblNumero);
        return card;
    }

    private Region crearSeparador() {
        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setMaxWidth(Double.MAX_VALUE);
        sep.getStyleClass().add("separador");
        return sep;
    }

    private void inicarSesion(ArrayList<CuentaBancaria> cuentas, ArrayList<Usuario> users, ArrayList<Movimiento> mov) {

        System.out.println(users.size() + " " + cuentas.size() + " " + mov.size());
        Label lblBienvenido = new Label("NETBANK");
        lblBienvenido.getStyleClass().add("titulo");

        Label correoLabel = new Label("Introduce tu correo");
        TextField correo = new TextField();
        correo.setPromptText("Introduce tu correo");
        correoLabel.getStyleClass().add("label-sesion");

        Label contrasenyaLabel = new Label("Introduce tu contraseña ");
        PasswordField contrasenya = new PasswordField();
        contrasenya.setPromptText("Introduce tu contraseña");

        contrasenyaLabel.getStyleClass().add("label-sesion");

        Button botonIniciar = new Button("Iniciar sesión");
        Label lblError = new Label();
        lblError.getStyleClass().add("lbl_error");
        botonIniciar.getStyleClass().add("boton_sesion");
        System.out.println(generateIbans("ES"));
        botonIniciar.setOnAction(e -> {
            if (contrasenya.getText().isEmpty() || correo.getText().isEmpty()) {
                lblError.setText("Rellena todos los campos primero");

            } else {
                boolean esAdmin = false;
                boolean existe = false;
                CuentaBancaria cuenta = null;
                for (CuentaBancaria c : cuentas) {
                    if (c.getCorreo().equals(correo.getText()) && c.getContrasenya().equals(contrasenya.getText())) {
                        existe = true;
                        cuenta = c;
                        if (c instanceof Administrador) {
                            esAdmin = true;
                        }
                    }
                }
                if (!existe) {
                    lblError.setText("Correo o contraseña no válido");
                } else if (!cuenta.isActivo()) {
                    lblError.setText(
                            "Tu cuenta fue suspendida por un administrador por favor contacta al: 683 13 59 28");
                } else if (existe && esAdmin) {
                    panelAdmin(cuentas, users, cuenta);
                } else if (existe) {
                    bancoApp(cuenta, cuentas);
                }
            }
        });

        Button botonRegistrar = new Button("Registrarse");
        botonRegistrar.getStyleClass().add("boton_sesion");

        GridPane grid = new GridPane();
        GridPane.setMargin(botonIniciar, new Insets(10));
        grid.setVgap(10);

        grid.add(lblBienvenido, 0, 0);
        grid.add(correoLabel, 0, 1);
        grid.add(correo, 0, 2);
        grid.add(contrasenyaLabel, 0, 3);
        grid.add(contrasenya, 0, 4);
        grid.add(lblError, 0, 5);
        grid.add(botonIniciar, 0, 6);
        grid.add(botonRegistrar, 0, 7);

        GridPane.setHalignment(correoLabel, HPos.CENTER);
        GridPane.setMargin(lblBienvenido, new Insets(0, 0, 50, 0));
        GridPane.setHalignment(lblBienvenido, HPos.CENTER);

        GridPane.setHalignment(botonIniciar, HPos.CENTER);
        GridPane.setHalignment(botonRegistrar, HPos.CENTER);

        grid.setMaxSize(500, 600);
        grid.getStyleClass().add("grid");

        BorderPane menu = new BorderPane();
        menu.setCenter(grid);

        Image imagen = new Image("Imagenes/fondo_banco.jpg");

        BackgroundSize size = new BackgroundSize(10, 10, true, true, false, true);

        BackgroundImage fondo = new BackgroundImage(imagen, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                size);

        menu.setBackground(new Background(fondo));

        Scene inicio = new Scene(menu, 1000, 600);
        inicio.getStylesheets().add("/CSS/bienvenida.css");

        Stage window = new Stage();
        window.setScene(inicio);
        window.setTitle("BancoNeetJumper");
        window.show();
    }

    private ArrayList<Usuario> cargarUsuariosBDD(ArrayList<Usuario> users) {
        String q1 = "select * from usuario";

        try (Connection con = Conectar.conectar()) {
            PreparedStatement ps = con.prepareStatement(q1);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String dni = rs.getString("dni");
                String num_tlf = rs.getString("num_tlf");
                String pais = rs.getString("pais");

                Usuario usuario = new Usuario(nombre, dni, num_tlf, pais);

                users.add(usuario);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    private ArrayList<CuentaBancaria> cargarCuentasBDD(ArrayList<CuentaBancaria> cuentas, String cuenta_string) {
        String q = "SELECT c.iban, c.id_cuenta, c.correo, c.contrasena, c.saldo, c.activo, " +
                "ca.id_cuenta_admin, " +
                "u.nombre, u.dni, u.num_tlf, u.pais, " +
                "m.importe, m.asunto, m.fecha, " + "t.numero_tarjeta, fecha_vencimiento, cvv, tipo " +
                "FROM " + cuenta_string + " c " +
                "LEFT JOIN cuenta_administrador ca ON c.id_cuenta = ca.id_cuenta_admin " +
                "INNER JOIN usuario u ON c.id_cuenta = u.id_usuario " +
                "LEFT JOIN movimiento m ON c.id_cuenta = m.id_cuenta " +
                "LEFT JOIN tarjeta t ON c.id_cuenta = t.id_cuenta";

        try (Connection con = Conectar.conectar()) {
            PreparedStatement ps = con.prepareStatement(q);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idCuenta = rs.getInt("id_cuenta");

                CuentaBancaria cuentaExistente = null;

                for (CuentaBancaria c : cuentas) {
                    if (c.getId_cuenta() == idCuenta) {
                        cuentaExistente = c;
                    }
                }

                if (cuentaExistente == null) {
                    int id_cuenta = rs.getInt("id_cuenta");
                    String iban = rs.getString("iban");
                    String correo = rs.getString("correo");
                    String contrasenya = rs.getString("contrasena");
                    Double saldo = rs.getDouble("saldo");
                    String nombre = rs.getString("nombre");
                    String dni = rs.getString("dni");
                    String num_tlf = rs.getString("num_tlf");
                    String pais = rs.getString("pais");
                    boolean activo = rs.getBoolean("activo");

                    Usuario user = new Usuario(nombre, dni, num_tlf, pais);

                    if (rs.getObject("id_cuenta_admin") != null) {
                        cuentaExistente = new Administrador(iban, correo, contrasenya, activo, saldo, user);

                    } else {
                        cuentaExistente = new CuentaCorriente(iban, correo, contrasenya, activo, saldo, user);
                    }
                    cuentaExistente.setId_cuenta(id_cuenta);

                    cuentas.add(cuentaExistente);
                }
                if (rs.getObject("importe") != null) {

                    Double importe = rs.getDouble("importe");
                    String asunto = rs.getString("asunto");
                    String fecha = rs.getString("fecha");

                    Movimiento mov = new Movimiento(
                            cuentaExistente.getUser(),
                            importe,
                            asunto,
                            fecha);

                    cuentaExistente.getMovs().add(mov);
                }
                if (rs.getObject("numero_tarjeta") != null) {
                    String num_tarjeta = rs.getString("numero_tarjeta");
                    String cvv = rs.getString("cvv");
                    String fecha = rs.getString("fecha_vencimiento");
                    String tipo = rs.getString("tipo");
                    Tarjeta tarjeta;
                    if (tipo.equals("Tarjeta Credito")) {
                        tarjeta = new TarjetaCredito(num_tarjeta, cvv, fecha, tipo, 20);
                    } else {
                        tarjeta = new TarjetaDebito(num_tarjeta, cvv, fecha, tipo);
                    }
                    cuentaExistente.getTarjetas().add(tarjeta);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cuentas;
    }

    private void panelAdmin(ArrayList<CuentaBancaria> cuentas, ArrayList<Usuario> users, CuentaBancaria cuenta) {
        BorderPane root = new BorderPane();

        // MENU LATERAL
        VBox menu = new VBox(40);
        menu.getStyleClass().add("menu-lateral");

        Label lblAdmin = new Label("ADMIN");
        lblAdmin.getStyleClass().add("label-admin");

        Button btnUsuarios = new Button("Usuarios");
        Button btnMovimientos = new Button("Movimientos");
        Button btnImprimir = new Button("Impirimir información cuentas");
        Button btnCrearTarjetas = new Button("Crear tarjetas");

        Button btnSalir = new Button("🔒 CERRAR SESIÓN");
        btnUsuarios.getStyleClass().add("btn-menu");
        btnMovimientos.getStyleClass().add("btn-menu");
        btnImprimir.getStyleClass().add("btn-menu");
        btnCrearTarjetas.getStyleClass().add("btn-menu");
        btnSalir.getStyleClass().add("btn-salir");

        menu.getChildren().addAll(lblAdmin, btnUsuarios, btnMovimientos, btnImprimir, btnCrearTarjetas, btnSalir);

        // CONTENIDO PRINCIPAL
        StackPane contenido = new StackPane();
        contenido.getStyleClass().add("contenido-fondo");

        // VISTA USUARIOS
        VBox vistaUsuarios = new VBox(20);
        vistaUsuarios.getStyleClass().add("vista-seccion");

        HBox registrarUsuarios = new HBox(40);
        registrarUsuarios.getStyleClass().add("vista-seccion");

        Label lblError = new Label();
        lblError.getStyleClass().add("subtitulo-rojo");

        Label tituloUsuarios = new Label("Gestión de usuarios");
        tituloUsuarios.getStyleClass().add("titulo-seccion");

        // Crear usuario
        Label lblCrear = new Label("Crear nuevo usuario");
        lblCrear.getStyleClass().add("subtitulo-rojo");

        TextField tfNombre = new TextField();
        tfNombre.setPromptText("Nombre del usuario");
        tfNombre.getStyleClass().add("tf-crear");

        PasswordField tfPass = new PasswordField();
        tfPass.setPromptText("Contraseña");
        tfPass.getStyleClass().add("tf-crear");

        TextField tfCorreo = new TextField();
        tfCorreo.setPromptText("Correo");
        tfCorreo.getStyleClass().add("tf-crear");

        TextField tfTlf = new TextField();
        tfTlf.setPromptText("Número de teléfono");
        tfTlf.getStyleClass().add("tf-crear");

        TextField tfDni = new TextField();
        tfDni.setPromptText("DNI");
        tfDni.getStyleClass().add("tf-crear");

        TextField tfSaldoInicial = new TextField("0.0");
        tfSaldoInicial.getStyleClass().add("tf-crear");

        Button btnCrear = new Button("Crear usuario");
        btnCrear.getStyleClass().add("btn-crear");

        ComboBox<String> paises = new ComboBox<>();
        paises.getItems().addAll(
                "España",
                "Francia",
                "Italia",
                "Alemania",
                "Portugal",
                "Bélgica");
        paises.getStyleClass().add("paises");
        Map<String, String> paisesMap = new HashMap<>();
        paisesMap.put("España", "ES");
        paisesMap.put("Francia", "FR");
        paisesMap.put("Italia", "IT");
        paisesMap.put("Alemania", "DE");
        paisesMap.put("Portugal", "PT");
        paisesMap.put("Bélgica", "BE");

        paises.setValue("España");

        ToggleButton btnAdmin = new ToggleButton("Admin");
        btnAdmin.getStyleClass().add("btn-admin");

        Region sep1 = new Region();
        sep1.getStyleClass().add("separador");

        Label lblTabla = new Label("Usuarios registrados");
        lblTabla.getStyleClass().add("subtitulo-rojo");

        // Tabla usuarios
        HBox cabecera = new HBox(10);
        cabecera.getStyleClass().add("cabecera-tabla");
        cabecera.getChildren().addAll(
                celdaAdmin("NOMBRE", 180, true),
                celdaAdmin("IBAN", 270, true),
                celdaAdmin("SALDO", 90, true),
                celdaAdmin("DNI", 90, true),
                celdaAdmin("ESTADO", 90, true),
                celdaAdmin("INGRESAR", 130, true),
                celdaAdmin("RETIRAR", 130, true),
                celdaAdmin("ACCIONES", 160, true));

        VBox tablaUsuarios = new VBox(6);
        tablaUsuarios.getStyleClass().add("tabla-rows");
        tablaUsuarios.getChildren().add(cabecera);

        for (CuentaBancaria c : cuentas) {
            tablaUsuarios.getChildren().add(crearFilaUsuario(c, cuentas, tablaUsuarios));
        }

        TextField filterUser = new TextField();
        filterUser.setPromptText("Filtrar por DNI 🔎");
        filterUser.textProperty().addListener((obs, old, nuevo) -> {
            tablaUsuarios.getChildren().clear();
            tablaUsuarios.getChildren().add(cabecera);

            tablaUsuarios.getChildren().addAll(
                    cuentas.stream()
                            .filter(c -> {
                                if (nuevo.isBlank()) {
                                    return true;
                                } else {
                                    return c.getUser().getDni().toLowerCase().contains(nuevo.toLowerCase());
                                }
                            })
                            .map(c -> {
                                return crearFilaUsuario(c, cuentas, tablaUsuarios);
                            })
                            .toList());
        });
        vistaUsuarios.getChildren().addAll(
                tituloUsuarios,
                lblCrear,
                tfCorreo,
                tfPass,
                tfNombre,
                tfDni,
                tfTlf,
                tfSaldoInicial,
                paises,
                lblError,
                btnAdmin,
                btnCrear,
                sep1,
                lblTabla, filterUser,
                tablaUsuarios);

        ScrollPane scrollUsuarios = new ScrollPane(vistaUsuarios);
        scrollUsuarios.setFitToWidth(true);

        // ════ VISTA MOVIMIENTOS ════
        ArrayList<Movimiento> todosMovs = cargarMovimientosBDD();

        VBox vistaMovimientos = new VBox(12);
        vistaMovimientos.getStyleClass().add("vista-seccion");
        HBox buscadorFiltro = new HBox(40);
        TextField filter = new TextField();
        filter.setPromptText("Filtrar por DNI 🔎");

        Label tituloMov = new Label("Todos los movimientos");
        tituloMov.getStyleClass().add("titulo-seccion");
        buscadorFiltro.getChildren().addAll(tituloMov, filter);

        HBox cabeceraMovs = new HBox(10);
        cabeceraMovs.getStyleClass().add("cabecera-tabla");
        cabeceraMovs.getChildren().addAll(
                celdaAdmin("FECHA", 110, true),
                celdaAdmin("USUARIO", 160, true),
                celdaAdmin("IMPORTE", 90, true),
                celdaAdmin("CONCEPTO", 240, true));

        VBox tablaMov = new VBox(6);
        tablaMov.getStyleClass().add("tabla-rows");
        tablaMov.getChildren().add(cabeceraMovs);

        for (Movimiento mov : todosMovs) {
            tablaMov.getChildren().add(crearFilaMov(mov));
        }

        filter.textProperty().addListener((obs, old, nuevo) -> {
            tablaMov.getChildren().clear();
            tablaMov.getChildren().add(cabeceraMovs);

            tablaMov.getChildren().addAll(
                    todosMovs.stream()
                            .filter(m -> {
                                if (nuevo.isBlank()) {
                                    return true;
                                } else {
                                    return m.getUser().getDni().toLowerCase().contains(nuevo.toLowerCase());
                                }
                            })
                            .map(m -> crearFilaMov(m))
                            .toList());

        });

        vistaMovimientos.getChildren().addAll(buscadorFiltro, tablaMov);

        ScrollPane scrollMovimientos = new ScrollPane(vistaMovimientos);
        scrollMovimientos.setFitToWidth(true);

        VBox vistaTarjetas = new VBox(40);
        vistaTarjetas.getStyleClass().add("vista-seccion");
        Label titLabel = new Label("CREAR TARJETAS");
        titLabel.getStyleClass().add("titulo-seccion");

        TextField tfIban = new TextField();
        tfIban.getStyleClass().add("tf-crear");

        ComboBox<String> box = new ComboBox<>();
        box.getItems().addAll(
                "Tarjeta Debito",
                "Tarjeta Credito");
        box.getStyleClass().add("paises");
        box.setValue("Tarjeta Debito");

        Label lblErrorTarjeta = new Label();
        lblErrorTarjeta.getStyleClass().add("lbl_error");

        tfIban.setPromptText(" 🔎");

        Button btnTarjeta = new Button("Crear Tarjeta");

        btnTarjeta.getStyleClass().add("btn-crear");
        btnTarjeta.setOnAction(e -> {
            System.out.println("Buscando: '" + tfIban.getText() + "'");
            for (CuentaBancaria c : cuentas) {
                System.out.println("Comparando con: '" + c.getIban() + "'");
            }
            int id_cuenta = 0;
            String q = "INSERT INTO tarjeta(numero_tarjeta, fecha_vencimiento, cvv, id_cuenta, tipo) values(?,?,?,?,?)";
            for (CuentaBancaria c : cuentas) {
                if (tfIban.getText().trim().equals(c.getIban())) {
                    id_cuenta = c.getId_cuenta();
                }
            }

            try (Connection con = Conectar.conectar()) {
                PreparedStatement ps = con.prepareStatement(q);
                LocalDate fecha = LocalDate.now().plusYears(10);

                if (id_cuenta != 0) {
                    ps.setString(1, generateNumTarj());
                    ps.setString(2, fecha.toString());
                    ps.setString(3, generateCvv());
                    ps.setInt(4, id_cuenta);
                    ps.setString(5, box.getValue());
                    ps.executeUpdate();
                    lblErrorTarjeta.setText("Tarjeta creada correctamente");
                } else {
                    lblErrorTarjeta.setText("El iban no existe");
                }
            } catch (Exception a) {
                // TODO: handle exception
            }
        });

        vistaTarjetas.getChildren().addAll(titLabel,
                tfIban,
                box,
                lblErrorTarjeta,
                btnTarjeta);

        btnCrear.setOnAction(e -> {
            String nombre = tfNombre.getText().trim();
            String pass = tfPass.getText().trim();
            String correo = tfCorreo.getText().trim();
            String doc = tfDni.getText().trim();
            String num_tlf = tfTlf.getText().trim();
            String saldo = tfSaldoInicial.getText().trim();
            String pais = paises.getValue();
            pais = paisesMap.get(pais);
            String iban = generateIbans(pais);
            cuentas.clear();
            cargarCuentasBDD(cuentas, "cuenta");

            for (CuentaBancaria c : cuentas) {
                if (c.getIban().equals(iban)) {
                    iban = generateIbans(pais);
                }
            }

            boolean correoExists = false;
            boolean tlfExists = false;

            for (CuentaBancaria c : cuentas) {
                if (c.getCorreo().equals(correo)) {
                    correoExists = true;
                }
            }
            for (Usuario u : users) {
                if (u.getNumTlf().equals(num_tlf)) {
                    tlfExists = true;
                }
            }

            if (nombre.isEmpty() || pass.isEmpty() || doc.isEmpty() || num_tlf.isEmpty()) {
                lblError.setText("Rellena todos los campos primero");

            } else if (!Libreria.validarDoc(doc, pais)) {
                lblError.setText("El formato del DNI no es válido");
            } else if (!Libreria.validarCorreo(correo)) {
                lblError.setText("El formato del correo no es válido");
            } else if (correoExists) {
                lblError.setText("Este correo ya está registrado");
            } else if (tlfExists) {
                lblError.setText("Este teléfono ya está registrado");
            } else {
                lblError.setText("");
                String opcion = "cuenta";
                String q1 = "INSERT INTO usuario(nombre, dni, num_tlf, pais) VALUES (?, ?, ?, ?)";
                String q2 = "insert into " + opcion
                        + "(iban, correo, contrasena, saldo, activo, id_usuario) VALUES (?,?,?,?,?,?)";

                String q3 = "insert into " + opcion + "_administrador "
                        + " (id_cuenta_admin) VALUES (?)";
                String qBuscar = "select id_usuario from usuario where dni = ?";
                int id = -2;
                try (Connection connection = Conectar.conectar()) {
                    PreparedStatement psBuscar = connection.prepareStatement(qBuscar);
                    psBuscar.setString(1, doc);
                    ResultSet rsBuscar = psBuscar.executeQuery();

                    if (rsBuscar.next()) {
                        id = rsBuscar.getInt("id_usuario");
                    }
                    if (id == -2) {
                        PreparedStatement stmt = connection.prepareStatement(q1, Statement.RETURN_GENERATED_KEYS);

                        stmt.setString(1, nombre);
                        stmt.setString(2, doc);
                        stmt.setString(3, num_tlf);
                        stmt.setString(4, pais);
                        stmt.executeUpdate();

                        try (ResultSet rs = stmt.getGeneratedKeys()) {
                            rs.next();
                            id = rs.getInt(1);
                        }

                        cargarUsuariosBDD(users);

                    }

                    PreparedStatement stmt2 = connection.prepareStatement(q2, Statement.RETURN_GENERATED_KEYS);

                    PreparedStatement stmt3 = connection.prepareStatement(q3);

                    if (!btnAdmin.isSelected()) {
                        stmt2.setString(1, iban);
                        stmt2.setString(2, correo);
                        stmt2.setString(3, pass);
                        stmt2.setString(4, saldo);
                        stmt2.setBoolean(5, true);
                        stmt2.setInt(6, id);
                        stmt2.executeUpdate();

                    } else {
                        stmt2.setString(1, iban);
                        stmt2.setString(2, correo);
                        stmt2.setString(3, pass);
                        stmt2.setString(4, saldo);
                        stmt2.setBoolean(5, true);
                        stmt2.setInt(6, id);
                        stmt2.executeUpdate();

                        int idCuenta;
                        try (ResultSet rs = stmt2.getGeneratedKeys()) {
                            rs.next();
                            idCuenta = rs.getInt(1);
                        }

                        stmt3.setInt(1, idCuenta);
                        stmt3.executeUpdate();
                    }

                    lblError.setText("Cuenta creada correctamente!! 😁");

                } catch (SQLException ex) {
                    System.out.println("SQLException: " + ex.getMessage());
                    System.out.println("SQLState: " + ex.getSQLState());
                    System.out.println("VendorError: " + ex.getErrorCode());
                }

            }
        });
        // NAVEGACION
        btnUsuarios.setOnAction(e -> {
            cuentas.clear();

            cargarCuentasBDD(cuentas, "cuenta");

            tablaUsuarios.getChildren().clear();
            tablaUsuarios.getChildren().add(cabecera);

            for (CuentaBancaria c : cuentas) {
                tablaUsuarios.getChildren().add(crearFilaUsuario(c, cuentas, tablaUsuarios));
            }

            contenido.getChildren().setAll(scrollUsuarios);
        });
        btnMovimientos.setOnAction(e -> {
            todosMovs.clear();
            todosMovs.addAll(cargarMovimientosBDD());

            tablaMov.getChildren().clear();
            tablaMov.getChildren().add(cabeceraMovs);
            todosMovs.forEach(m -> tablaMov.getChildren().add(crearFilaMov(m)));

            contenido.getChildren().setAll(scrollMovimientos);
        });
        btnImprimir.setOnAction(e -> generarFicheroCuentas(cuentas));
        btnCrearTarjetas.setOnAction(e -> contenido.getChildren().setAll(vistaTarjetas));
        btnSalir.setOnAction(e -> ((Stage) root.getScene().getWindow()).close());

        contenido.getChildren().add(scrollUsuarios);

        root.setLeft(menu);
        root.setCenter(contenido);

        Scene scene = new Scene(root, 1050, 680);
        scene.getStylesheets().add("/CSS/admin.css");

        Stage adminStage = new Stage();
        adminStage.setTitle("BancoNeetJumper — Administrador");
        adminStage.setScene(scene);
        adminStage.show();
    }

    private String generateIbans(String country) {
        Map<String, Integer> lengths = new HashMap<>();
        lengths.put("ES", 24);
        lengths.put("DE", 22);
        lengths.put("FR", 27);
        lengths.put("IT", 27);
        lengths.put("PT", 25);
        lengths.put("BE", 16);
        int total = lengths.get(country);

        StringBuilder iban = new StringBuilder();
        Random rm = new Random();
        String checkDigits = String.format("%02d", rm.nextInt(97) + 1);

        for (int i = 1; i <= total - 4; i++) {
            iban.append(rm.nextInt(10));
            if (i % 4 == 0) {
                iban.append(" ");
            }
        }

        return (country + checkDigits + " " + iban).trim();
    }

    private String generateNumTarj() {
        StringBuilder numTarj = new StringBuilder();
        Random rm = new Random();

        for (int i = 1; i <= 16; i++) {
            numTarj.append(rm.nextInt(10));
            if (i % 4 == 0) {
                numTarj.append(" ");
            }
        }

        return numTarj.toString();
    }

    private String generateCvv() {
        StringBuilder cvv = new StringBuilder();
        Random rm = new Random();

        for (int i = 1; i <= 4; i++) {
            cvv.append(rm.nextInt(10));
        }

        return cvv.toString();
    }

    private HBox crearFilaUsuario(CuentaBancaria c, ArrayList<CuentaBancaria> cuentas, VBox tablaUsuarios) {
        HBox fila = new HBox(10);
        fila.getStyleClass().add("fila-tabla");

        Label lEstado = new Label("Activo");
        lEstado.getStyleClass().add("estado-label");

        Label lNombre = celdaAdmin(c.getUser().getNombre(), 180, false);
        Label lIban = celdaAdmin(c.getIban(), 270, false);
        Label lSaldo = celdaAdmin(String.format("%.2f €", c.getSaldo()), 90, false);
        Label lDni = celdaAdmin(c.getUser().getDni(), 90, false);

        // -- Ingresar --
        TextField tfIng = new TextField();
        tfIng.setPromptText("€");
        tfIng.getStyleClass().add("tf-importe");

        Button btnIng = new Button("+");
        btnIng.getStyleClass().add("btn-ingresar");

        btnIng.setOnAction(e -> {
            try (Connection con = Conectar.conectar()) {
                double cantidad = Double.parseDouble(tfIng.getText().trim());
                if (cantidad <= 0) {

                } else {
                    String q = "UPDATE cuenta SET saldo = saldo + ? where iban = ?";
                    String qMov = "INSERT INTO movimiento(importe, asunto, fecha, id_cuenta) VALUES (?, ?, NOW(), (SELECT id_cuenta FROM cuenta WHERE iban = ?))";
                    PreparedStatement ps = con.prepareStatement(q);
                    ps.setDouble(1, cantidad);
                    ps.setString(2, c.getIban());
                    ps.executeUpdate();
                    PreparedStatement psMove = con.prepareStatement(qMov);
                    psMove.setDouble(1, cantidad);
                    psMove.setString(2, "Ingresado por admin");
                    psMove.setString(3, c.getIban());
                    psMove.executeUpdate();
                    c.depositar(cantidad);
                    lSaldo.setText(String.format("%.2f €", c.getSaldo()));
                    tfIng.clear();

                }
            } catch (Exception a) {
                // TODO: handle exception
            }
        });

        HBox boxIng = new HBox(4, tfIng, btnIng);
        boxIng.getStyleClass().add("box-accion");

        // -- Retirar --
        TextField tfRet = new TextField();
        tfRet.setPromptText("€");
        tfRet.getStyleClass().add("tf-importe");

        Button btnRet = new Button("−");
        btnRet.getStyleClass().add("btn-retirar");

        btnRet.setOnAction(e -> {
            try (Connection con = Conectar.conectar()) {
                double cantidad = Double.parseDouble(tfRet.getText().trim());
                if (cantidad <= 0) {

                } else {
                    String q = "UPDATE cuenta SET saldo = saldo - ? where iban = ?";
                    String qMov = "INSERT INTO movimiento(importe, asunto, fecha, id_cuenta) VALUES (?, ?, NOW(), (SELECT id_cuenta FROM cuenta WHERE iban = ?))";
                    PreparedStatement ps = con.prepareStatement(q);
                    ps.setDouble(1, cantidad);
                    ps.setString(2, c.getIban());
                    ps.executeUpdate();
                    PreparedStatement psMove = con.prepareStatement(qMov);
                    psMove.setDouble(1, -cantidad);
                    psMove.setString(2, "Ingresado por admin");
                    psMove.setString(3, c.getIban());
                    psMove.executeUpdate();
                    c.retirar(cantidad);
                    lSaldo.setText(String.format("%.2f €", c.getSaldo()));
                    tfIng.clear();

                }
            } catch (Exception a) {
                // TODO: handle exception
            }
        });

        HBox boxRet = new HBox(4, tfRet, btnRet);
        boxRet.getStyleClass().add("box-accion");

        // -- Bloquear / Eliminar --
        Button btnBloquear = new Button("Bloquear");
        if (c.isActivo()) {
            btnBloquear.setText("Bloquear");
            btnBloquear.getStyleClass().add("btn-bloquear");
        } else {
            btnBloquear.setText("Desbloquear");
            btnBloquear.getStyleClass().add("btn-desbloquear");
        }
        btnBloquear.setOnAction(e -> {
            try (Connection con = Conectar.conectar()) {
                String q = "UPDATE cuenta SET activo = ? WHERE id_cuenta = ?";
                PreparedStatement ps = con.prepareStatement(q);
                ps.setBoolean(1, !c.isActivo());
                ps.setInt(2, c.getId_cuenta());
                ps.executeUpdate();
                if (c.isActivo()) {
                    btnBloquear.setText("Desbloquear");
                    btnBloquear.getStyleClass().remove("btn-bloquear");
                    btnBloquear.getStyleClass().add("btn-desbloquear");
                    c.setActivo(false);
                } else {
                    btnBloquear.setText("Bloquear");
                    btnBloquear.getStyleClass().remove("btn-desbloquear");
                    btnBloquear.getStyleClass().add("btn-bloquear");
                    c.setActivo(true);
                }
            } catch (Exception a) {
                a.printStackTrace();
            }
        });

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.getStyleClass().add("btn-eliminar");

        btnEliminar.setOnAction(e -> {
            try (Connection con = Conectar.conectar()) {
                String q = "DELETE FROM cuenta WHERE id_cuenta = ?";

                PreparedStatement ps = con.prepareStatement(q);
                System.out.println(c.getId_cuenta());
                ps.setInt(1, c.getId_cuenta());
                ps.executeUpdate();
                tablaUsuarios.getChildren().remove(fila);
            } catch (Exception a) {
                // TODO: handle exception
            }
        });

        HBox boxAcciones = new HBox(6, btnBloquear, btnEliminar);
        boxAcciones.getStyleClass().add("box-acciones");

        fila.getChildren().addAll(lNombre, lIban, lSaldo, lDni, lEstado, boxIng, boxRet, boxAcciones);

        return fila;
    }

    private Label celdaAdmin(String texto, double ancho, boolean cabecera) {
        Label celda = new Label(texto);
        celda.setPrefWidth(ancho);
        if (cabecera) {
            celda.getStyleClass().add("celda-cabecera");
        } else {
            celda.getStyleClass().add("celda-normal");
        }

        return celda;
    }

    private ArrayList<Movimiento> cargarMovimientosBDD() {
        ArrayList<Movimiento> movs = new ArrayList<>();
        String q = "SELECT u.nombre, u.dni, m.importe, m.asunto, m.fecha " +
                "FROM movimiento m " +
                "INNER JOIN cuenta c ON m.id_cuenta = c.id_cuenta " +
                "INNER JOIN usuario u ON c.id_usuario = u.id_usuario ";
        try (Connection con = Conectar.conectar()) {
            PreparedStatement ps = con.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                double importe = rs.getDouble("importe");
                String dni = rs.getString("dni");
                String asunto = rs.getString("asunto");
                String fecha = rs.getString("fecha");
                Usuario user = new Usuario(nombre, dni);
                Movimiento mov = new Movimiento(user, importe, asunto, fecha);

                movs.add(mov);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movs;
    }

    private HBox crearFilaMov(Movimiento mov) {
        HBox fila = new HBox(10);
        fila.getStyleClass().add("fila-tabla");

        boolean esIngreso = false;

        if (mov.getImporte() > 0) {
            esIngreso = true;
        }

        Label lFecha = celdaAdmin(mov.getFecha(), 110, false);
        Label lUsuario = celdaAdmin(mov.getUser().getNombre(), 160, false);

        Label lImporte = new Label(String.format("%.2f €", mov.getImporte()));
        if (esIngreso) {
            lImporte.getStyleClass().add("importe-ingreso");
        } else {
            lImporte.getStyleClass().add("importe-retirada");
        }

        Label lConcepto = celdaAdmin(mov.getAsunto(), 240, false);

        fila.getChildren().addAll(lFecha, lUsuario, lImporte, lConcepto);
        return fila;
    }

    private void generarFicheroCuentas(ArrayList<CuentaBancaria> cuentas) {
        StringBuffer sb = new StringBuffer();

        sb.append("LISTA DE CUENTAS EN BANKNET\n");
        sb.append("===========================\n\n");
        for (CuentaBancaria c : cuentas) {
            sb.append("Nombre: " + c.getUser().getNombre() + " IBAN: " + c.getIban() + " Saldo: " + c.getSaldo()
                    + " IDENTIFICACIÓN: " + c.getUser().getDni() + " TELÉFONO: " + c.getUser().getNumTlf()
                    + "\n");
        }

        try (FileWriter fw = new FileWriter("cuentas.txt")) {
            fw.write(sb.toString());
            System.out.println("Fichero generado: cuentas.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
