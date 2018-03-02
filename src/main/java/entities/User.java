/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import java.util.UUID;

/**
 *
 * @author alu2015018
 */

//anotacion para crear tabla
@Table(keyspace = "", name = "users",
        readConsistency = "QUORUM",
        writeConsistency = "QUOEUM",
        caseSensitiveKeyspace = false,
        caseSensitiveTable = false)
public class User {
    
    @PartitionKey(0)
    @Column(name = "user_id")
    private UUID id;
    private String email;
    private String password;
    private String nombre;
    private int edad;
    @PartitionKey(1)
    private String grupo;

    private User(){}
    
    public User(UUID id, String email, String password, String nombre, int edad, String grupo) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.edad = edad;
        this.grupo = grupo;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
    
    
    
    
    
    
}
