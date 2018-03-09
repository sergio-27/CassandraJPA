/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author alu2015018
 */
public class Employe extends User {

    private List<Customer> customerList = new ArrayList<>();

    public Employe(UUID id, String email, String password, String nombre, int edad, String grupo, List<Customer> customerList) {
        super(id, email, password, nombre, edad, grupo);

        this.customerList = customerList;
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public void addCustomer(Customer c) {
        customerList.add(c);
    }

    public void deleteCustomerFromList(Customer c) {
        customerList.remove(c);
    }

}
