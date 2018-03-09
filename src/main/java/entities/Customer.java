/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author alu2015018
 */
@Table(keyspace = "testspace", name = "customers",
        readConsistency = "QUORUM",
        writeConsistency = "QUORUM",
        caseSensitiveKeyspace = false,
        caseSensitiveTable = false)
public class Customer {

    @PartitionKey(0)
    private UUID customerId;
    @PartitionKey(1)
    private UUID employeId;
    private String nombre;
    private String cif;
    private String email;
    private Estado estado;
    private Date fechaAlta;
    
    private enum Estado {
        ABIERTO, CERRADO, PENDIENTE
    }

    public Customer() {
    }

    public Customer(UUID customerId, UUID employeId, String nombre, String cif, String email, Estado estado, Date fechaAlta) {
        this.customerId = customerId;
        this.employeId = employeId;
        this.nombre = nombre;
        this.cif = cif;
        this.email = email;
        this.estado = estado;
        this.fechaAlta = fechaAlta;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getEmployeId() {
        return employeId;
    }

    public void setEmployeId(UUID employeId) {
        this.employeId = employeId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

   
    
}
