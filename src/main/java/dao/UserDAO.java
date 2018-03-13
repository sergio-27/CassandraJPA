/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import entities.User;
import java.util.UUID;

/**
 *
 * @author alu2015018
 */
public class UserDAO {

    private MappingManager mappingManager;
    private Mapper<User> mapper;
    private User user;
    private final Session session;
    
    public UserDAO(Session session){
        this.session = session;
    }

    //creamos objeto mappingmanager para poder realizar el CRUD de usuario
    public Mapper getClassMapper() {
        
        mappingManager = new MappingManager(session);

        mapper = mappingManager.mapper(User.class);
        
        return mapper;
    }
    
    public void insertUser(User userAux){
        //obtenemos a partir del mappingmanager un mapper que es el que maneja las entidades
        //con este save guardamos los daros durante 5 segundos, pasado el tiempo se borraran
        //mapper.save(userAux, Mapper.Option.ttl(10));
        
        //con esta guardamos el dato sin que se borre
        mapper.save(userAux);
        
    }
    
    public User getUserByUUID(UUID userId){
        
        User u = mapper.get(userId);
        
        return u;
    }
    
    //cassandra nos permite eliminar un registro de 
    //la base de datos ya se proporcionando una pk o el objeto qu euqeremos eliminar
    public void deleteUser(Object userOrUUID){
        mapper.delete(userOrUUID);
    }

}
