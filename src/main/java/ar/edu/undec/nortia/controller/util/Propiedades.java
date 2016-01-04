package ar.edu.undec.nortia.controller.util;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author gongono
 */
public class Propiedades {
    
    Statement stmt = null;
    Connection result = null;
    ResultSet rs = null;
   
    public String getUserMapuche(){
       /* Properties p = new Properties();
                    Reader myReader = null ;
            try {
                myReader = new InputStreamReader(new FileInputStream("src\\main\\resources\\config.properties"));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Propiedades.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                p.load(myReader);
            } catch (IOException ex) {
                Logger.getLogger(Propiedades.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            //return p.getProperty("mapucheuser");
            // return ResourceBundle.getBundle("/config").getString("mapucheuser");
            String query = "select valor from ap.configuracion where clave like 'mapucheuser' ;" ;
        try {
            rs = this.getStatement().executeQuery(query) ;
            rs.next();
           return rs.getString("valor");
        } catch (SQLException ex) {
            Logger.getLogger(Propiedades.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }finally {
            if (result != null) {
               try {
                   result.close();
               } catch (Exception e) {
                   /* handle close exception, quite usually ignore */
               } 
            }
        }
        
    }
    
    public String getPasswordMapuche(){
         String query = "select valor from ap.configuracion where clave like 'mapuchepassword' ;" ;
        try {
            rs = this.getStatement().executeQuery(query) ;
            rs.next();
           return rs.getString("valor");
        } catch (SQLException ex) {
            Logger.getLogger(Propiedades.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }finally {
            if (result != null) {
               try {
                   result.close();
               } catch (Exception e) {
                   /* handle close exception, quite usually ignore */
               } 
            }
        }
    }
    
    public String getPortMapuche(){
         String query = "select valor from ap.configuracion where clave like 'mapucheport' ;" ;
        try {
            rs = this.getStatement().executeQuery(query) ;
            rs.next();
           return rs.getString("valor");
        } catch (SQLException ex) {
            Logger.getLogger(Propiedades.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }finally {
            if (result != null) {
               try {
                   result.close();
               } catch (Exception e) {
                   /* handle close exception, quite usually ignore */
               } 
            }
        }
    }
    
    public String getHostMapuche(){
          String query = "select valor from ap.configuracion where clave like 'mapuchehost' ;" ;
        try {
            rs = this.getStatement().executeQuery(query) ;
            rs.next();
           return rs.getString("valor");
        } catch (SQLException ex) {
            Logger.getLogger(Propiedades.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }finally {
            if (result != null) {
               try {
                   result.close();
               } catch (Exception e) {
                   /* handle close exception, quite usually ignore */
               } 
            }
        }
    }
    
    public String getDatabaseMapuche(){
         String query = "select valor from ap.configuracion where clave like 'mapuchedatabase' ;" ;
        try {
            rs = this.getStatement().executeQuery(query) ;
            rs.next();
           return rs.getString("valor");
        } catch (SQLException ex) {
            Logger.getLogger(Propiedades.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }finally {
            if (result != null) {
               try {
                   result.close();
               } catch (Exception e) {
                   /* handle close exception, quite usually ignore */
               } 
            }
        }
    }
    
    public Statement getStatement(){
        //java:jboss/datasources/SISGAP
        
        try {
            Context initialContext = new InitialContext();
            DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/nortia");
            result = datasource.getConnection();
            stmt = result.createStatement() ;

        } catch (Exception ex) {
            System.out.println("Exception: " + ex + ex.getMessage());
        }
        
        return stmt;

    }
    
    public void closeConnection(){
        try {
            rs.close();
            stmt.close();
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(Propiedades.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
