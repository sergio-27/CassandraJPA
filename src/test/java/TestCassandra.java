/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.datastax.driver.core.Session;
import entities.CassandraConnector;
import entities.KeyspaceRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
    static final String IP_ADDRESS = "127.0.1";
    static final int port = 9042;

    @Before
    public void setUp() {
        cassandraConnector = new CassandraConnector();
        cassandraConnector.connect(IP_ADDRESS, port);
        session = cassandraConnector.getSession();
        keyspaceRepository = new KeyspaceRepository(session);
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
           keyspaceRepository.createKeyspace("TestSpace", "SimpleStrategy", 3);
           keyspaceRepository.useKeyspace("TestSpace");
        //keyspaceRepository.deleteKeyspace("TestSpace");
    }
}
