/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.toedter.calendar.JDateChooser;
import configuration.CConnection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author darwin
 */
public class customer {
    
    public void createAccount(JTextField name,JTextField lastName,JDateChooser dateBorn,JTextField sex){
    
        CConnection connection = new CConnection();
        model.customer mcustomer = new model.customer();
        
        try {
            String querry;
            querry = "INSERT INTO dbdar2.customer(customerName,lastName,fksexID,dateBorn)"
                    + "values (?,?,?,?)";
            
            mcustomer.setCustomerName(name.getText());
            mcustomer.setLastName(lastName.getText());
           mcustomer.setDateBorn(dateBorn.getDate());
            mcustomer.setSexID(Integer.parseInt(sex.getText()));
            
            CallableStatement cs = connection.setConnection().prepareCall(querry);
            cs.setString(1, mcustomer.getCustomerName());
            cs.setString(2, mcustomer.getLastName());
            cs.setInt(3, mcustomer.getSexID());
            java.sql.Date dateSql = new java.sql.Date(mcustomer.getDateBorn().getTime());
            cs.setDate(4, dateSql);
            cs.execute();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Error create Account: "+e.getMessage());
        } finally {
            connection.closeConnection();
        }
        
        
    }
    public void lastCustomerSignUp(JTextField customerID,JTextField name){
    
        CConnection connection = new CConnection();
    
        model.customer mcus = new model.customer();
        
        try {
            String querry = "Select customerID from dbdar2.customer where customerName = ?";
            mcus.setCustomerName(name.getText());
            
            CallableStatement cs = connection.setConnection().prepareCall(querry);
                    cs.setString(1, mcus.getCustomerName());
            cs.execute();
            
            ResultSet rs = cs.executeQuery();
            if(rs.next()){
            customerID.setText(String.valueOf(rs.getInt("customerID")));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Error lastCusID: "+ e.getMessage());
            
        } finally {
            connection.closeConnection();
        }
    }
    
    public void showSex(JComboBox sex,JTextField numSex){
    
        CConnection connection = new CConnection();
      
    String querry ="Select sexID from dbdar2.sex where sexName = ?";
     
        try {
            
            
            PreparedStatement ps = connection.setConnection().prepareCall(querry);
            ps.setString(1, sex.getSelectedItem().toString());
            ps.execute();
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                
            numSex.setText(rs.getString("sexID"));
            
            }else{
            numSex.setText("");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e.toString());
        } finally {
            connection.closeConnection();
        }
    
    }
    
    public void showProfiel(JTable Tprofile,JTextField userID,JTextField customerID,JTextField name,JTextField lastName,JTextField sexID,JComboBox sex,JDateChooser dateBorn){
    
    configuration.CConnection connection = new CConnection();
       model.user muser = new model.user();
       model.customer mcustomer = new model.customer();
        DefaultTableModel model = new DefaultTableModel();
        
        model.addColumn("Name");
        model.addColumn("LastName");
        model.addColumn("Sex");
        model.addColumn("DateBorn");
        
        Tprofile.setModel(model);
    
        try {
            String querry = """
                            SELECT c.customerID,c.customerName,c.lastName,s.sexName,c.fksexID,c.dateBorn FROM dbdar2.customer as c
                            INNER JOIN dbdar2.user as u ON c.customerID = u.fkcustomerID
                            inner join dbdar2.sex as s ON c.fksexID = s.sexID
                            WHERE u.userID = ?""";
          muser.setUserID(Integer.parseInt(userID.getText()));
            
            PreparedStatement ps = connection.setConnection().prepareCall(querry);
            
            ps.setInt(1, muser.getUserID());
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                mcustomer.setCustomerID(rs.getInt("customerID"));
            mcustomer.setCustomerName(rs.getString("customerName"));
            mcustomer.setLastName(rs.getString("lastName"));
            mcustomer.setSexName(rs.getString("sexName"));
            mcustomer.setSexID(rs.getInt("fksexID"));
            mcustomer.setDateBorn(rs.getDate("dateBorn"));
            
          //  model.addRow(new Object[]{mcustomer.getCustomerName(),mcustomer.getLastName(),mcustomer.getSexName(),mcustomer.getDateBorn()});
         customerID.setText(String.valueOf(mcustomer.getCustomerID()));
          name.setText(mcustomer.getCustomerName());
         lastName.setText(mcustomer.getLastName());
         sex.setSelectedIndex(mcustomer.getSexID());
         sexID.setText(String.valueOf(mcustomer.getSexID()));
         dateBorn.setDate(mcustomer.getDateBorn());
         
        
            }
            //Tprofile.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e.toString());
            
        } finally {
            connection.closeConnection();
        }
        
        
    }
    
    public void editProfile(JTextField userID,JTextField customerID,JTextField name,JTextField lastName,JComboBox sexName,JTextField sexID,JDateChooser dateBorn){
    
        name.setEnabled(true);
         lastName.setEnabled(true);
          sexName.setEnabled(true);
           dateBorn.setEnabled(true);
    CConnection connection = new CConnection();
    model.customer mcustomer = new model.customer();
    
        try {
            
            String querry = "UPDATE dbdar2.customer as c SET c.customerName=?,c.lastName=?,c.fksexID =?,c.dateBorn=?    WHERE c.customerID =?";
          
            CallableStatement cs = connection.setConnection().prepareCall(querry);
            cs.setString(1, name.getText());
            cs.setString(2, lastName.getText());
         
            cs.setInt(3,Integer.parseInt(sexID.getText()));
           // Date dateUtil = dateBorn.getDate();
            mcustomer.setDateBorn(dateBorn.getDate());
            java.sql.Date datesql = new java.sql.Date(mcustomer.getDateBorn().getTime()) ;
            
            cs.setDate(4, datesql);
            cs.setInt(5,Integer.parseInt(customerID.getText()));
            cs.execute();
            JOptionPane.showMessageDialog(null,"Edit successful!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e.toString());
        } finally {
            connection.closeConnection();
        }
    }
    

    
}
