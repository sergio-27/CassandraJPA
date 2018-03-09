/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import entities.Customer;
import java.util.UUID;

/**
 *
 * @author alu2015018
 */
public class CustomerDAO {

    private MappingManager mappingManager;
    private Mapper<Customer> mapper;
    private Customer customer;
    private final Session session;

    public CustomerDAO(Session session) {
        this.session = session;
    }
    
    public Mapper getClassMapper(){
        
        mappingManager = new MappingManager(session);
        
        mapper = mappingManager.mapper(Customer.class);
        
        return mapper;
    }
    
    public void insertCustomer(Customer customerAux){
        
        mapper.save(customerAux, Mapper.Option.ttl(5));
        
        //mapper.save(customerAux);
    }
    
    public Customer getCustomerByUUID(UUID customerId){
        Customer c = mapper.get(customerId);
        
        return c;
    }
    
    public void deleteCustomer(Object customerOrUUID){
        mapper.delete(customerOrUUID);
    }
    
    
    

}
