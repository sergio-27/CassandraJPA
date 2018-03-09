/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accesors;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import entities.User;

/**
 *
 * @author alu2015018
 */
@Accessor
public interface UserAccesor {
    @Query("SELECT * FROM users")
    Result<User> getAll();
    
}
