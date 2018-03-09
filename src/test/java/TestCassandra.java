/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import accesors.UserAccesor;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import dao.UserDAO;
import entities.CassandraConnector;
import entities.KeyspaceRepository;
import entities.User;
import entities.UserRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author alu2015018
 */
public class TestCassandra {

    public TestCassandra() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    CassandraConnector cassandraConnector;
    Session session;
    KeyspaceRepository keyspaceRepository;
    static final String IP_ADDRESS = "127.0.0.1";
    static final int port = 9042;
    User user;
    UserDAO userdao;
    Mapper mapper;
    UserRepository userRepository;
    UserAccesor userAccesor;

    @Before
    public void setUp() {
        cassandraConnector = new CassandraConnector();
        cassandraConnector.connect(IP_ADDRESS, port);
        session = cassandraConnector.getSession();
        keyspaceRepository = new KeyspaceRepository(session);
        keyspaceRepository.createKeyspace("testspace", "SimpleStrategy", 1);
        keyspaceRepository.useKeyspace("testspace");
        userRepository = new UserRepository(session);
        userRepository.createTableUsuarios();
        //obtenemos userdao
        userdao = new UserDAO(session);
        mapper = userdao.getClassMapper();
        user = new User(UUIDs.timeBased(), "ruiz", "ssoo++", "sergio", 21, "Empleado");
        userAccesor = new MappingManager(session).createAccessor(UserAccesor.class);
    }

    @After
    public void tearDown() {
        session.close();
        cassandraConnector.close();

    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void testCreateKeyspace() {
        //keyspaceRepository.deleteKeyspace("testspace");
        userdao.insertUser(user);
        
        userdao.getUserByUUID(user.getId());

        Result<User> userSet = userAccesor.getAll();
        
        for (User u : userSet){
            System.out.println("Id: " + u.getId());
        }
    }

}
