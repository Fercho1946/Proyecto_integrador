package proyecto_integrador;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

public class procesos{

    private int NOTIFICATION_DAYS = 30;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private ResultSet rs = null;
    private conexion con = new conexion();
    private PreparedStatement pst = null;
    private int id_cliente;
    private String nombre_cliente;
    private String correo;
    private Double celular;
    private String fecha_compra;
    private String nombre_producto;
    private int id_producto;
    private Double precio;
    private LocalDate fecha_vence;
    private int id_notificacion;
    private static final String emailfrom = "munozlugonicolas4@gmail.com";
    private static final String passwordfrom = "rssarcxslrjoqkfw";
    private String subject = "Renovación de licencia";   

    public int getId() {
        return id_cliente;
    }

    public void setId(int id) {
        this.id_cliente = id_cliente;
    }

    public String getNombre_cliente() {
        return nombre_cliente;
    }

    public void setNombre_cliente(String nombre_cliente) {
        this.nombre_cliente = nombre_cliente;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public Double getCelular() {
        return celular;
    }    

    public void setCelular(Double celular) {
        this.celular = celular;
    }
    
    public String getFecha_compra() {
        return fecha_compra;
    }

    public void setFecha_compra(String fecha_compra) {
        this.fecha_compra = fecha_compra;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }
    
    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public LocalDate getFechaVence() {
        return fecha_vence;
    }

    public void setFechaVence(LocalDate fecha_vence){
        this.fecha_vence = fecha_vence;
    }
    
    public int getId_notificacion() {
        return id_notificacion;
    }

    public void setId_notificacion(int id_notificacion){
        this.id_notificacion = id_notificacion;
    }

    //Creación de la consulta de la base datos (Busca la tabla donde está la base de datos)
    public procesos() throws SQLException{
        
        String consulta = "select * from todo";
        pst = con.getConexion().prepareStatement(consulta);
        rs = pst.executeQuery();
        
    //Aquí se crean las propiedades requeridas para hacer la conexión y configuración con el correo
    
        Properties propiedad = new Properties();
        propiedad.put("mail.smtp.host", "smtp.gmail.com");
        propiedad.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        propiedad.setProperty("mail.smtp.starttls.enable", "true");
        propiedad.setProperty("mail.smtp.port", "587");
        propiedad.setProperty("mail.smtp.user", emailfrom);
        propiedad.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        propiedad.setProperty("mail.smtp.auth", "true");

    //Inicia la sesión para enviar el correo al usuario
        Session sesion = Session.getDefaultInstance(propiedad);

    //Se crea la lista para almacenar todos los procesos
        List<procesos> todos = new ArrayList<>();

    //Se itera para pasar los datos almacenados en la base de datos y así pueden guardarse en get y set
        while (rs.next()) {
            procesos todo = new procesos();
            todo.setId(rs.getInt(1));
            todo.setNombre_cliente(rs.getString(2));
            todo.setCorreo(rs.getString(3));
            todo.setCelular(rs.getDouble(4));
            todo.setFecha_compra(rs.getString(5));
            todo.setNombre_producto(rs.getString(6));
            todo.setId_producto(rs.getInt(7));
            todo.setPrecio(rs.getDouble(8));
            todo.setPrecio(rs.getDouble(9));
            todo.setFechaVence(LocalDate.parse(rs.getString(10).replace("-", "/"), formatter));
            todo.setId_notificacion(rs.getInt(11));
            todos.add(todo);
        }

        //For programado para el envio de correo y notificación
        for (procesos todo : todos) {
            try {
        //Aquí se programa la notificación que será enviada cuando se aproxima la fecha de expiración
            long daysUntilExpiration = ChronoUnit.DAYS.between(LocalDate.now(), todo.getFechaVence());
        //Se usa para crear el mensaje que será enviado por correo
            MimeMessage mCorreo = new MimeMessage(sesion);
            mCorreo.setFrom(new InternetAddress(emailfrom));

        //Separa los datos para poder enviar los correos a cada usuario, esto en base a la información de cada uno
                String[] recipients = todo.getCorreo().split(",");
                for (String recipient : recipients) {
                 mCorreo.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient.trim()));
                }
                mCorreo.setSubject(subject);               

            //Es la condición la cual revisa si se acerca la fecha de expiración para enviar la notificación
                if (daysUntilExpiration <= NOTIFICATION_DAYS) {
                    try{
                        Transport mTransport = sesion.getTransport("smtp");
                        mTransport.connect(emailfrom, passwordfrom);
                        mTransport.sendMessage(mCorreo, mCorreo.getRecipients(Message.RecipientType.TO));
                        mTransport.close();
                        JOptionPane.showMessageDialog(null, "Listo, revise su correo");
                    } catch (NoSuchProviderException ex) {
                        Logger.getLogger(procesos.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MessagingException ex) {
                        Logger.getLogger(procesos.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (MessagingException ex) {
                Logger.getLogger(procesos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    } 

}
