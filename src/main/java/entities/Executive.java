/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.util.UUID;

/**
 *
 * @author alu2015018
 */
public class Executive extends User{
    
    private String nombrePuesto;
    
    public Executive(UUID id, String email, String password, String nombre, int edad, String grupo, String nombrePuesto) {
        super(id, email, password, nombre, edad, grupo);
        
        this.nombrePuesto = nombrePuesto;
    }
    
}
