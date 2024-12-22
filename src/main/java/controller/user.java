
package controller;

import com.toedter.calendar.JDateChooser;
import configuration.CConnection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import model.modelEmployee;
import view.FormMain;
import view.FormProduct;
import view.Login;

/**
 *
 * @author darwin
 */
public class user {
   
    
    public void createUser(JTextField user,JTextField password,JTextField customerID,JTextField customerName,JTextField lastName,JDateChooser dateBorn){
    
        model.user muser = new model.user();
        model.customer mcus= new model.customer();
        CConnection connection = new CConnection();
        
    
        muser.setUserName(user.getText());
        muser.setPassword(password.getText());
        
        mcus.setCustomerID(Integer.parseInt(customerID.getText()));
    
        try {
            String querry = "INSERT INTO dbdar2.user(userName,password,fkcustomerID,date) values (?,?,?,curdate());";
            CallableStatement cs = connection.setConnection().prepareCall(querry);
            cs.setString(1, muser.getUserName());
            cs.setString(2, muser.getPassword());
            cs.setInt(3, Integer.parseInt(customerID.getText()));
            
            cs.execute();
            
            JOptionPane.showMessageDialog(null,"Register successful!");
            
            user.setText("");
                        password.setText("");

                                    customerName.setText("");

                                                lastName.setText("");

                                                            dateBorn.setDate(null);

            
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e.toString());
            
        } finally {
            connection.closeConnection();
        }
        
    }
    
    
    public void validateUser(JTextField user,JTextField password){
    
        CConnection connection = new CConnection();
        model.user muser = new model.user();
        model.modelEmployee memployee = new modelEmployee();
        
        try {
            muser.setUserName(user.getText());
            muser.setPassword(password.getText());
            
            String querry = "Select u.userID,u.userName,u.password,c.fkroleID from dbdar2.user as u "
                    + "INNER JOIN customer as c ON u.fkcustomerID = c.customerID "
                    + "where userName=? and password =?;";
            
            PreparedStatement ps = connection.setConnection().prepareCall(querry);
            ps.setString(1, muser.getUserName());
            ps.setString(2, muser.getPassword());
            
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                JOptionPane.showMessageDialog(null,"Welcome to my app");
               int userID = rs.getInt("userID");
               int roleID = rs.getInt("fkroleID");
                FormMain fmain = new FormMain(userID,roleID);
                fmain.setVisible(true);
               
                 SwingUtilities.getWindowAncestor(user).dispose();
                
            }else{
                try {
                    String querry2 = "SELECT employee.employeeID,employee.fkroleID,employee.employeeName,employee.password FROM dbdar2.employee where employeeName = ? and password=?";
                   memployee.setEmployeeName(user.getText());
                    memployee.setPassword(password.getText());
                    CallableStatement cs = connection.setConnection().prepareCall(querry2);
                    cs.setString(1, memployee.getEmployeeName());
                    cs.setString(2, memployee.getPassword());
                    
                    ResultSet rs2 = cs.executeQuery();
                    if(rs2.next()){
                         JOptionPane.showMessageDialog(null,"Welcome admin");
               int userID = rs2.getInt("employeeID");
               int fkroleID = rs2.getInt("fkroleID");
                FormMain fmain = new FormMain(userID,fkroleID);
                fmain.setVisible(true);
                
                SwingUtilities.getWindowAncestor(user).dispose();
                    }else{
                        JOptionPane.showMessageDialog(null,"No account exists");
                    
                    }
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"Failed connection: "+e.getMessage());
                }
                
              //  JOptionPane.showMessageDialog(null,"Your account wasn't find out");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Failed connection: "+e.toString());
        } finally {
            connection.closeConnection();
            
        }
    
    
    }
    
        public void validateRol(JTextField id,JTextField roleID){
    
            configuration.CConnection connection = new CConnection();
            model.modelEmployee memployee = new modelEmployee();
            
            try {
                memployee.setEmployeeID(Integer.parseInt(id.getText()));
                memployee.setRoleID(Integer.parseInt(roleID.getText()));
                String querry = "SELECT e.employeeID,e.fkroleID FROM dbdar2.employee as e where e.employeeID = ? and e.fkroleID = ?; ";
                
                PreparedStatement ps = connection.setConnection().prepareCall(querry);
                ps.setInt(1, memployee.getEmployeeID());
                ps.setInt(2, memployee.getRoleID());
                
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                  
                    view.FormProduct product = new FormProduct(null, true);
                product.setVisible(true);
                    
                }else{
                JOptionPane.showMessageDialog(null,"Can't access here!");
                }
               
            } catch (Exception e) {
                JOptionPane.showMessageDialog( null,"Error: "+e.toString());
            } finally {
                connection.closeConnection();
            }
    
    }
        
        public void closeSession(JMenu exit){
            JOptionPane.showMessageDialog(null,"Hello");
       int result= JOptionPane.showConfirmDialog(null,"Do you want to close session?","Confirmation",JOptionPane.YES_NO_OPTION);
            
       if(result==JOptionPane.YES_OPTION){
       Login lg = new Login();
       lg.setVisible(true);
       SwingUtilities.getWindowAncestor(exit).dispose();
       }
        }
}
