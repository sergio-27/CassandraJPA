/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.Session;

/**
 *
 * @author alu2015018
 */
public class CassandraConnector {

    //instanciamos un objeto del tipo Cluster
    private Cluster cluster;

    //lo mismo pero con session
    private Session session;

    //metodo para conectar con cassandra
    /**
     * para establecer una conexion con cassadnra tenemos que pasarle la ip del
     * nodo y el puerto en el que escucha.
     */
    public void connect(final String node, final Integer port) {
        //creamos un objeto del tipo CluisterBuilder y le pasamos el nodo y especficamos el  protocolo que vamos a utilizar
        Cluster.Builder clusterBuilder = Cluster.builder().addContactPoint(node).withProtocolVersion(ProtocolVersion.V4);

        //comprobamos que el valor que recibimos como puerto sea correcto
        if (port != null) {
            clusterBuilder.withPort(port);
        }
        
        cluster = clusterBuilder.build();
        
        session = cluster.connect();

    }

    public Session getSession() {
        return session;
    }
    
    public void close(){
        session.close();
        cluster.close();
    }
}
