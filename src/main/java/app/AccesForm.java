/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

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
import entities.CustomerRepository;
import entities.KeyspaceRepository;
import entities.User;
import entities.UserRepository;
import java.util.List;
import java.util.Scanner;

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
            case "Empleado":
                accesForm.showEmpleadoMenu(accesForm);

                break;
            default:
                System.err.println("Grupo no registrado.");
        }

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
                af.updateUser();
                break;
            //salir
            case 5:
                System.out.println("Adeu");
                break;
            default:
        }

    }

    public void updateUser() {

        String updatedUserEmail = "";
        do {

            showUsers();
            System.out.println("Indica el email del usuario que quiere modificar: ");

            try {
                updatedUserEmail = opcion.next();
            } catch (NumberFormatException nullPointer) {
                System.err.println("Escriba un correo válido.");
            }

        } while (!updatedUserEmail.equals(""));

        //obtenemos el usuario seleccionado por el usuario
        User u = getUserByeEmail(updatedUserEmail);
        showUserProperties(u);

    }

    public void showUserProperties(User us) {
        String newEmail, newName, newGroup;
        int opcionAux, newEdad;
        do {
            System.out.println("[1] - Email. ");
            System.out.println("[2] - Nombre. ");
            System.out.println("[3] - Grupo.");
            System.out.println("[4] - Edad. ");
            System.out.println("[5] - Salir. ");

            System.out.println("Datos actuales: ");
            us.toString();
            opcionAux = opcion.nextInt();

            switch (opcionAux) {
                //email
                case 1:

                    System.out.println("Escribe el nuevo email: ");
                    newEmail = opcion.next();

                    break;
                //nombre
                case 2:

                    System.out.println("Escribe el nueov nombre: ");
                    newName = opcion.next();

                    break;
                //grupo
                case 3:
                    do {
                        System.out.println("Escribe empleado o admin.");
                        newGroup = opcion.next();
                    } while (!newGroup.equalsIgnoreCase("admin") && !newGroup.equalsIgnoreCase("empleado"));

                    break;
                //edad
                case 4:

                    System.out.println("Escribe la nueva edad.");
                    newEdad = opcion.nextInt();
                    break;
                case 5:
                    System.out.println("Volviendo al menú.");
                    break;
                default:
                    System.err.println("Error Show User Properties");
            }

        } while (opcionAux < 1 || opcionAux != 5);

    }

    public void deleteUser() {
        String deletedEmail;
        showUsers();
        System.out.println("\nEscribe el email del usuario que quieres borrar: ");
        deletedEmail = opcion.next();
        //obtenemos el usuario por el email
        User deletedUser = getUserByeEmail(deletedEmail);
        if (deletedUser == null) {
            System.err.println("No hay usuarios con el email escrito.");
        } else {
            userdao.deleteUser(deletedUser.getId());
            System.out.println("Usuario Eliminado");
        }

    }

    public User getUserByeEmail(String deletedEmail) {
        User userAux = null;
        for (User u : userAccesor.getAll()) {
            if (u.getEmail().equals(deletedEmail)) {
                userAux = u;
            }
        }

        if (userAux == null) {
            System.err.println("No se ha encontrado el usuario.");
        }

        return userAux;
    }

    public void showUsers() {
        int position = 0;

        for (User u : userAccesor.getAll()) {
            position++;
            System.out.println("\n[" + position + "]");
            System.out.println("\nUser Id: " + u.getId());
            System.out.println("User Email: " + u.getEmail());
            System.out.println("User Name: " + u.getNombre());
            System.out.println("User Group: " + u.getGrupo());
            System.out.println("User Age: " + u.getEdad() + "\n");
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

    public void showEmpleadoMenu(AccesForm accesFormAux) {
        int opcion2;
        do {

            System.out.println("\n************ " + accesFormAux.userLogged.getNombre() + " ***************");
            System.out.println("*** 1 - Insertar Clientes.  ***");
            System.out.println("*** 2 - Modificar Clientes. ***");
            System.out.println("*** 3 - Eliminar Clientes.  ***");
            System.out.println("*** 4 - Ver Clientes.       ***");
            System.out.println("*** 5 - Salir.              ***");

            opcion2 = this.opcion.nextInt();

        } while (opcion2 < 1 || opcion2 > 4);
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
        ;

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
    }

}
