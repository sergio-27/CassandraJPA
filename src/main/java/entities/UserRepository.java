package entities;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;


import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static final String TABLE_NAME = "users";

    private Session session;

    public UserRepository(Session session) {
        this.session = session;
    }

    public void createTableUsuarios() {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).append("(").append("user_id uuid PRIMARY KEY, ").append("email text,").append("password text,").append("nombre text,").append("edad int,").append("grupo text);");

        final String query = sb.toString();
        session.execute(query);
    }

    public void alterTableUsuarios(String columnName, String columnType) {
        StringBuilder sb = new StringBuilder("ALTER TABLE ").append(TABLE_NAME).append(" ADD ").append(columnName).append(" ").append(columnType).append(";");

        final String query = sb.toString();
        session.execute(query);
    }

    public void insertUsuario(User usuario) {
        StringBuilder sb = new StringBuilder("INSERT INTO ").append(TABLE_NAME).append("(id, email, password, nombre, edad, grupo) ").append("VALUES (").append(usuario.getId()).append(", '").append(usuario.getEmail()).append("', '").append(usuario.getPassword()).append("', '").append(usuario.getNombre()).append("', ").append(usuario.getEdad()).append(", '").append(usuario.getGrupo()).append("');");

        final String query = sb.toString();
        session.execute(query);
    }

    public User selectByName(String nombre) {
        StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_NAME).append(" WHERE nombre = '").append(nombre).append("';");

        final String query = sb.toString();

        ResultSet rs = session.execute(query);

        List<User> usuarios = new ArrayList<>();

        for (Row r : rs) {
            User u = new User(r.getUUID("id"), null, null, r.getString("nombre"), 0, null);
            usuarios.add(u);
        }

        return usuarios.get(0);
    }

    public List<User> selectAll() {
        StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_NAME);

        final String query = sb.toString();
        ResultSet rs = session.execute(query);

        List<User> usuarios = new ArrayList<>();

        for (Row r : rs) {
            User usuario = new User(r.getUUID("id"), r.getString("email"), r.getString("password"), r.getString("nombre"), r.getInt("edad"), r.getString("grupo"));
            usuarios.add(usuario);
        }
        return usuarios;
    }

    public void deleteUserByName(String nombre) {
        StringBuilder sb = new StringBuilder("DELETE FROM ").append(TABLE_NAME).append(" WHERE nombre = '").append(nombre).append("';");

        final String query = sb.toString();
        session.execute(query);
    }

    public void deleteTable(String tableName) {
        StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS ").append(tableName);

        final String query = sb.toString();
        session.execute(query);
    }
}