/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import accesors.CustomerAccesor;
import accesors.UserAccesor;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import dao.CustomerDAO;
import dao.UserDAO;
import entities.CassandraConnector;
import entities.Customer;
import entities.Customer.Estado;
import entities.CustomerRepository;
import entities.KeyspaceRepository;
import entities.User;
import entities.UserRepository;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author alu2015018
 */
public class AccesForm {

    CassandraConnector cassandraConnector;
    Session session;
    KeyspaceRepository keyspaceRepository;
    User userLogged, user;
    UserDAO userdao;
    Mapper mapper;
    UserRepository userRepository;
    Customer customer;
    CustomerAccesor customerAccesor;
    CustomerDAO customerdao;
    CustomerRepository customerRepository;
    UserAccesor userAccesor;
    //SCANNER PARA LEER EL TECLADO
    Scanner opcion = new Scanner(System.in);
    /*PARAMETROS DE CONEXION CON CASSANDRA*/
    static final String IP_ADDRESS = "127.0.0.1";
    static final int port = 9042;
    String email = "";
    String pass = "";
    Result<User> userSet;
    Result<Customer> customerSet;

    public static void main(String[] args) {

        AccesForm accesForm = new AccesForm();
        //establecemos la conexion a la bbdd
        accesForm.connectToCassandra();
        //creamos las tablas al iniciar el programa, no supone problema ya en la sentencia CQL
        //incorporamos un IF EXISTS
        accesForm.createKeyspace();
        accesForm.createUserTable();
        accesForm.createCustomerTable();
        //iniciamos las entidades dao que utilizaremos
        accesForm.initializeDAOEntities();

        accesForm.loginUser();

        int o;

        //switch para mostrar los diferentes menus
        switch (accesForm.userLogged.getGrupo()) {
            case "Admin":
                do {

                    o = accesForm.showAdminMenu(accesForm);
                    accesForm.adminAction(accesForm, o);

                } while (o < 1 || o != 5);
                break;
            case "empleado":
                do {

                    o = accesForm.showEmpleadoMenu(accesForm);
                    accesForm.menuAction(accesForm, o);

                } while (o < 1 || o != 5);

                break;
            default:
                System.err.println("Grupo no registrado.");
        }

    }

    //funcion igual que adminAction
    public void menuAction(AccesForm af, int opcion) {
        switch (opcion) {
            //alta cliente
            case 1:
                customer = af.altaCustomer();

                customerdao.insertCustomer(customer);

                System.out.println("Cliente insertado!");
                break;
            //modificar cliente
            case 2:

                customer = af.updateCustomer();

                customerdao.insertCustomer(customer);

                System.out.println("Cliente actualizado.");
                break;
            //eliminar cliente
            case 3:
                af.deleteCustomer();

                System.out.println("Cliente eliminado.");
                break;
            //ver clientes
            case 4:

                af.showCustomer();
                break;
            //salir
            case 5:
                System.out.println("Adeu empleado.");
                break;
            //error
            default:
                System.err.println("Error Menu Action");
        }
    }

    public void deleteCustomer() {
        String deletedCIF;
        showCustomer();
        System.out.println("\nEscribe el cif del registro que desea eliminar: ");
        deletedCIF = opcion.next();
        //obtenemos el customer que queremos borrar
        Customer deletedCustomer = getCustomerByCIF(deletedCIF);
        if (deletedCustomer == null) {
            System.out.println("No hay clientes con el CIF especificado.");
        } else {
            customerdao.deleteCustomer(deletedCustomer);
            System.out.println("Cliente borrado.");
        }
    }

    public Customer altaCustomer() {
        String nombre, cif, emailCust, estado, fechaAlta;
        int resu;
        Customer customerAux;
        do {
            System.out.println("Introduzca el nombre del cliente: ");
            nombre = opcion.next();
            System.out.println("Introduzca el cif: ");
            cif = opcion.next();
            System.out.println("Introduzca el email del cliente: ");
            emailCust = opcion.next();
            //inicializamos el estado del cliente
            estado = Estado.ABIERTO.toString();
            //obtenemos fecha actual
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();

            //transformamos la fecha a formato string
            fechaAlta = dateFormat.format(date);
            System.out.println(fechaAlta);

            resu = checkCustomerExists(cif);
            if (resu == 1) {
                System.out.println("El cliente proporcionado ya existe.");
            }

        } while (resu == 1);

        //si el usuario no existe instanciamos un nuevo customer para insertarlo en la bbd
        customerAux = new Customer(UUIDs.timeBased(), userLogged.getId(), nombre, cif, emailCust, estado, fechaAlta);

        return customerAux;

    }

    //funcion en la que recibimos un parametro para realizar la accion escogida
    public void adminAction(AccesForm af, int opcion) {
        switch (opcion) {
            //alta
            case 1:
                user = af.altaUserForm();

                //insertamos el usuario a cassadnra 
                userdao.insertUser(user);

                System.out.println("Usuario insertado!");
                break;
            //ver
            case 2:
                //mostramos usuarios
                af.showUsers();
                break;
            //borrar
            case 3:
                //eliminaoms el usuario
                af.deleteUser();

                break;
            //modificar
            case 4:
                user = af.updateUser();

                userdao.insertUser(user);

                System.out.println("Usuario actualizado.");
                break;
            //salir
            case 5:
                System.out.println("Adeu admin.");
                break;
            default:
                System.err.println("Error Admin Menu");
        }

    }

    public Customer updateCustomer() {
        Customer c;
        String updatedCustCIF = "";
        do {

            showCustomer();
            System.out.println("\nIndicia el CIF del cliente que desea modificar.");
            try {
                updatedCustCIF = opcion.next();
            } catch (InputMismatchException nullPointer) {

            }

            //obtenemos el customer seleccionado
            c = getCustomerByCIF(updatedCustCIF);
            if (c != null) {

                c = modifyCustomerProperties(c);

            } else {
                System.err.println("Escriba un CIF válido.");
            }

        } while (c == null);

        return c;
    }

    public User updateUser() {

        User u;
        String updatedUserEmail = "";
        do {

            showUsers();
            System.out.println("Indica el email del usuario que quiere modificar: ");

            try {
                updatedUserEmail = opcion.next();
            } catch (InputMismatchException nullPointer) {

            }
            //obtenemos el usuario seleccionado por el usuario
            u = getUserByeEmail(updatedUserEmail);
            if (u != null) {
                u = modifyUserProperties(u);
            } else {
                System.err.println("Escriba un correo válido.");
            }

        } while (u == null);

        return u;

    }

    public Customer modifyCustomerProperties(Customer c) {
        String newName, newCIF, newEmail, newEstado;
        int opcionAux;
        do {
            System.out.println("Seleccione que campo desea modificar: ");
            
            System.out.println("[1] - Nombre");
            System.out.println("[2] - CIF");
            System.out.println("[3] - Estado");
            System.out.println("[4] - Email");
            System.out.println("[5] - Salir");

            opcionAux = opcion.nextInt();

            switch (opcionAux) {
                //nombre cliente
                case 1:

                    System.out.println("Escriba el nuevo nombre:");
                    newName = opcion.next();
                    c.setNombre(newName);

                    break;
                //cif
                case 2:

                    System.out.println("Escriba el nuevo CIF: ");
                    newCIF = opcion.next();
                    c.setCif(newCIF);

                    break;
                //estado
                case 3:
                    do {
                        System.out.println("Escriba el nuevo estado: "
                                + "PENDIENTE, ABIERTO, CERRADO.");
                        newEstado = opcion.next();

                    } while (!newEstado.equalsIgnoreCase("pendiente") && !newEstado.equalsIgnoreCase("abierto") && !newEstado.equalsIgnoreCase("cerrado"));

                    c.setEstado(newEstado);

                    break;
                //email
                case 4:

                    System.out.println("Escriba el nuevo email: ");
                    newEmail = opcion.next();
                    c.setEmail(newEmail);

                    break;
                //salir
                case 5:
                    System.out.println("Volviendo al menu principal.");
                    break;
                default:
                    System.err.println("Error Show Customer Properties");

            }

        } while (opcionAux < 1 || opcionAux != 5);

        //una vez el usuario presione 5 salimos del menu de actualizar campos y devolvemos el objeto
        return c;
    }

    public User modifyUserProperties(User us) {
        String newEmail, newName, newGroup;
        int opcionAux, newEdad;
        do {
            
            System.out.println("Seleccione que campo desea modificar: ");
            
            System.out.println("[1] - Email. ");
            System.out.println("[2] - Nombre. ");
            System.out.println("[3] - Grupo.");
            System.out.println("[4] - Edad. ");
            System.out.println("[5] - Salir. ");

            

            opcionAux = opcion.nextInt();

            switch (opcionAux) {
                //email
                case 1:

                    System.out.println("Escribe el nuevo email: ");
                    newEmail = opcion.next();

                    us.setEmail(newEmail);

                    break;
                //nombre
                case 2:

                    System.out.println("Escribe el nuevo nombre: ");
                    newName = opcion.next();

                    us.setNombre(newName);

                    break;
                //grupo
                case 3:
                    do {
                        System.out.println("Escribe empleado o admin.");
                        newGroup = opcion.next();
                    } while (!newGroup.equalsIgnoreCase("admin") && !newGroup.equalsIgnoreCase("empleado"));

                    us.setGrupo(newGroup);

                    break;
                //edad
                case 4:

                    System.out.println("Escribe la nueva edad.");
                    newEdad = opcion.nextInt();

                    us.setEdad(newEdad);
                    break;
                case 5:
                    System.out.println("Volviendo al menú.");
                    break;
                default:
                    System.err.println("Error Show User Properties");
            }

        } while (opcionAux < 1 || opcionAux != 5);

        return us;

    }

    public void deleteUser() {
        String deletedEmail;
        User deletedUser;
        do {
            showUsers();
            System.out.println("\nEscribe el email del usuario que quieres borrar: ");
            deletedEmail = opcion.next();
            //obtenemos el usuario por el email
            deletedUser = getUserByeEmail(deletedEmail);
            if (deletedUser == null) {
                System.err.println("No hay usuarios con el email proporcionado.");
            } else {
                userdao.deleteUser(deletedUser.getId());
                System.out.println("Usuario Eliminado.");
            }
        } while (deletedUser == null);

    }

    public User getUserByeEmail(String email) {
        User userAux = null;
        for (User u : userAccesor.getAll()) {
            if (u.getEmail().equals(email)) {
                userAux = u;
            }
        }

        if (userAux == null) {
            System.err.println("\nNo se ha encontrado el usuario.");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException ex) {

            }
        }

        return userAux;
    }

    public Customer getCustomerByCIF(String cif) {
        Customer cust = null;

        for (Customer c : customerAccesor.getAllCustomer()) {
            if (c.getCif().equals(cif)) {
                cust = c;
            }
        }

        if (cust == null) {
            System.err.println("\nNo se ha encontrado el cif proporcionado.");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ex) {

            }
        }

        return cust;
    }

    public void showUsers() {
        int position = 0;

        for (User u : userAccesor.getAll()) {
            position++;
            System.out.println("\n[" + position + "]");
            System.out.println("User Id: " + u.getId());
            System.out.println("User Email: " + u.getEmail());
            System.out.println("User Name: " + u.getNombre());
            System.out.println("User Group: " + u.getGrupo());
            System.out.println("User Age: " + u.getEdad() + "\n");
        }

    }

    public void showCustomer() {
        int position = 0;

        for (Customer c : customerAccesor.getAllCustomer()) {
            position++;
            System.out.println("\n[" + position + "]");
            System.out.println("Customer Id: " + c.getCustomerId());
            System.out.println("Customer Name: " + c.getNombre());
            System.out.println("Customer CIF: " + c.getCif());
            System.out.println("Customer Email: " + c.getEmail());
            System.out.println("Customer State: " + c.getEstado());
            System.out.println("Customer Date: " + c.getFechaAlta() + "\n");
        }
    }

    public User altaUserForm() {
        User userAux;
        String correo, passAux, nombre, grupo;
        int edad, resu;
        do {
            System.out.println("Correo: ");
            correo = opcion.next();
            System.out.println("Pass: ");
            passAux = opcion.next();
            System.out.println("Nombre: ");
            nombre = opcion.next();
            do {
                System.out.println("Escribe empleado o admin.");
                grupo = opcion.next();
            } while (!grupo.equalsIgnoreCase("admin") && !grupo.equalsIgnoreCase("empleado"));

            System.out.println("Edad: ");
            edad = opcion.nextInt();

            resu = checkSignIn(correo);
            if (resu == 1) {
                System.err.println("El email proporcionado ya esta registrado.");
            }

        } while (resu == 1);

        //inicializamos el usuarios
        userAux = new User(UUIDs.timeBased(), correo, passAux, nombre, edad, grupo);

        return userAux;
    }

    public int showAdminMenu(AccesForm accesForm) {
        int opcion2 = 0;
        do {

            System.out.println("\n************ " + accesForm.userLogged.getNombre() + " ***************");
            System.out.println("*** 1 - Alta Usuarios. ***");
            System.out.println("*** 2 - Ver Usuarios. ***");
            System.out.println("*** 3 - Borrar Usuarios. ***");
            System.out.println("*** 4 - Modificar Empleado. ***");
            System.out.println("*** 5 - Salir. ***");

            opcion2 = this.opcion.nextInt();

        } while (opcion2 < 1 || opcion2 > 5);

        return opcion2;
    }

    public int showEmpleadoMenu(AccesForm accesFormAux) {
        int opcion2 = 0;
        do {

            System.out.println("\n************ " + accesFormAux.userLogged.getNombre() + " ***************");
            System.out.println("*** 1 - Insertar Clientes.  ***");
            System.out.println("*** 2 - Modificar Clientes. ***");
            System.out.println("*** 3 - Eliminar Clientes.  ***");
            System.out.println("*** 4 - Ver Clientes.       ***");
            System.out.println("*** 5 - Salir.              ***");

            opcion2 = this.opcion.nextInt();

        } while (opcion2 < 1 || opcion2 > 4);

        return opcion2;
    }

    public void loginUser() {
        int resu;
        do {
            System.out.println("Introduce tu email:");
            email = opcion.next();
            //System.out.println(email);
            System.out.println("Introduce tu contraseña:");
            pass = opcion.next();
            //System.out.println(pass);
            resu = checkSignIn(email, pass);
            if (resu == 0) {
                System.err.println("Datos errones, intentelo de nuevo.");
            }
        } while (resu == 0);

    }

    public int checkSignIn(String... param) {
        //0 no existe el usuario, 1 existe
        int num = 0;
        //obtenemos todos los usuarios

        //comprobamos que el nombre y pass proporcionados existan ya en la bbd
        //si el tamaño de param es dos, estamos llamando la funcion desde el login ya que pasamos email y pass
        if (param.length == 2) {
            for (User u : userSet) {
                if (u.getEmail().equals(param[0]) && u.getPassword().equals(param[1])) {
                    userLogged = u;
                    num = 1;
                }
            }
            //si param es uno estamos llamando la funcion desde alta user
        } else if (param.length == 1) {
            for (User u : userSet) {
                if (u.getEmail().equals(param[0])) {
                    num = 1;
                }
            }
        }

        return num;

    }

    public int checkCustomerExists(String cif) {
        //0 el customer no existe
        int num = 0;

        for (Customer c : customerSet) {
            if (c.getCif().equalsIgnoreCase(cif)) {
                //customer existe
                num = 1;
            }
        }

        return num;
    }

    public void connectToCassandra() {
        cassandraConnector = new CassandraConnector();
        cassandraConnector.connect(IP_ADDRESS, port);

        session = cassandraConnector.getSession();
    }

    public void createKeyspace() {
        //inicializamos keyrepository para crear el keyspace
        keyspaceRepository = new KeyspaceRepository(session);
        keyspaceRepository.createKeyspace("CassandraDB", "SimpleStrategy", 1);
        keyspaceRepository.useKeyspace("CassandraDB");
    }

    public void createUserTable() {
        //inicializamos userrepository para poder crear la tabla
        userRepository = new UserRepository(session);
        userRepository.createTableUsuarios();
    }

    public void createCustomerTable() {
        //inicializamos customerrepository y creamos la tabla
        customerRepository = new CustomerRepository(session);
        customerRepository.createTableCustomer();
    }

    public void initializeDAOEntities() {
        userdao = new UserDAO(session);
        mapper = userdao.getClassMapper();
        userAccesor = new MappingManager(session).createAccessor(UserAccesor.class);
        userSet = userAccesor.getAll();
        customerdao = new CustomerDAO(session);
        mapper = customerdao.getClassMapper();
        customerAccesor = new MappingManager(session).createAccessor(CustomerAccesor.class);
        customerSet = customerAccesor.getAllCustomer();
    }

}
