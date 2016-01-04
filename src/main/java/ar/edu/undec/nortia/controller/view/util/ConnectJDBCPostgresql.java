/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller.view.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author Hugo
 */
public class ConnectJDBCPostgresql {
    Connection conn = null;
    
    
    public void connect( String host, String port, String database, String user, String password) throws SQLException{
            try
            {
              Class.forName("org.postgresql.Driver");
              String url = "jdbc:postgresql://"+host+"/"+database;
              conn = DriverManager.getConnection(url,user, password);
            }
            catch (ClassNotFoundException e)
            {
                System.out.println("No se encuentra la Clase "+e);
              //System.exit(1);
            }
            catch (SQLException e)
            {
                System.out.println("Error sql "+e);
            }
            
  }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
    
    
    
}

