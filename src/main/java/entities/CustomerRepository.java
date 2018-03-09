/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.datastax.driver.core.Session;

/**
 *
 * @author alu2015018
 */
public class CustomerRepository {

    private static final String TABLE_NAME = "customers";
    private Session session;

    public CustomerRepository(Session session) {
        this.session = session;
    }

    public void createTableCustomer() {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).append("(").append("customerId uuid PRIMARY KEY, ").append("user_id uuid,").append("nombre text,").append("cif text,").append("email text,").append("estado text,").append("fechaAlta text);");

        final String query = sb.toString();
        session.execute(query);
    }

    public void alterTableCustomer(String columnName, String columnType) {
        StringBuilder sb = new StringBuilder("ALTER TABLE ").append(TABLE_NAME).append(" ADD ").append(columnName).append(" ").append(columnType).append(";");

        final String query = sb.toString();
        session.execute(query);
    }
}
