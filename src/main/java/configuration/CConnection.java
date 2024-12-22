/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 *
 * @author darwin
 */
public class CConnection {
    
    Connection connection = null;
    
    String ip="localhost";
    String db="dbdar2";
    String user ="root";
    String password = "root";
    String port = "3306";
    
    
    String querry="jdbc:mysql://"+ip+":"+port+"/"+db;
    
    
    
    public Connection setConnection(){
    
        try {
           Class.forName("com.mysql.jdbc.Driver");
           connection = DriverManager.getConnection(querry,user,password);
     //       JOptionPane.showMessageDialog(null,"Connection successful!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Connection failed");
        } 
    return connection;
    }
    
    
    public void closeConnection(){
        try {
            if(connection != null && !connection.isClosed()){
            
                connection.close();
             //   JOptionPane.showMessageDialog(null,"Connection closed!");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Failed connection closed!");
        }
    
    
    
    }
    
    
    
    
    
    
    
    
    
}
