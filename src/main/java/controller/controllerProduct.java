/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import configuration.CConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.product;

/**
 *
 * @author darwin
 */
public class controllerProduct {
    
    
    public void insertProduct(JTable ptable,JTextField pname,JSpinner stock,JTextField category,JRadioButton within,JRadioButton outside,JTextField city,JTextField price){
    
        configuration.CConnection connection = new CConnection();
        model.product mproduct = new product();
        mproduct.setName(pname.getText());
        mproduct.setStock(Integer.parseInt(stock.getModel().getValue().toString()));
      
        mproduct.setFkcategoryID(Integer.parseInt(category.getText()));
        mproduct.setPrice(Double.parseDouble(price.getText()));
       boolean isValidate = false; 
        if(within.isSelected()){
            
        mproduct.setCity(Integer.parseInt(city.getText()));
            switch (mproduct.getCity()) {
                case 1:
                     mproduct.setPlaceOrigin("Guayaquil");
                     isValidate = true;
                    break;
                      case 2:
                     mproduct.setPlaceOrigin("Quito");
                     isValidate = true;
                    break;
                      case 3:
                     mproduct.setPlaceOrigin("Cuenca");
                     isValidate = true;
                    break;
                default:
    
            }
       
        
        }else if(outside.isSelected()){
        mproduct.setPlaceOrigin("Outside");
        isValidate = true;
        }else{
            JOptionPane.showMessageDialog(null,"You must choose at least an production place!");
            isValidate = false;
        }
        
        for (int i = 0; i < ptable.getRowCount(); i++) {
            
           DefaultTableModel model = (DefaultTableModel) ptable.getModel();
           
           String validateName = model.getValueAt(i, 0).toString();
           
           if(validateName.equals(mproduct.getName())){
               JOptionPane.showMessageDialog(null,"This name already have been registered!");
               return;
           }
            
        }
        
        if(isValidate){
        try {
            String querry = """
                            INSERT INTO dbdar2.product(productName,fkcategoryID,stock,price,placeOrigin) 
                            values(?,?,?,?,?);
                            """;
            PreparedStatement ps = connection.setConnection().prepareCall(querry);
           ps.setString(1, mproduct.getName());
           ps.setInt(2, mproduct.getFkcategoryID());
           ps.setInt(3, mproduct.getStock());
           ps.setDouble(4, mproduct.getPrice());
           ps.setString(5, mproduct.getPlaceOrigin());
           
            ps.execute();
        
            JOptionPane.showMessageDialog(null,"Register successful!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Register failed!"+e.getMessage());
        } finally {
            connection.closeConnection();
        }
        }
        
    }
    
    public void selectCbxCategory(JComboBox category,JTextField txtcategory){
    
        int Category = category.getSelectedIndex();
    
        txtcategory.setText(String.valueOf(Category));
    }
       public void selectCbxCity(JComboBox city,JTextField txtcity){
    
        int City = city.getSelectedIndex();
    
        txtcity.setText(String.valueOf(City));
    }
       
       public void selectRadioBtn(JRadioButton within,JComboBox city){
       
           
          if(within.isSelected()){
             
              city.setEnabled(true);
          
          }else{
          city.setEnabled(false);
          }

       }
     
       public void showProduct(JTable tbproduct){

           configuration.CConnection connection = new CConnection();
           model.product mproduct = new product();
           DefaultTableModel model = new DefaultTableModel();
           
           model.addColumn("ProductName");
           model.addColumn("Stock");
           model.addColumn("Category");
           model.addColumn("Price");
           model.addColumn("Place_Origin");
       tbproduct.setModel(model);
       
           try {
               String querry = """
                               select p.productName,c.categoryName,p.stock,p.price,p.placeOrigin from dbdar2.product as p
                               inner join dbdar2.category as c ON c.categoryID = p.fkcategoryID
                               ;""";
               
               Statement st = connection.setConnection().createStatement();
               ResultSet rs = st.executeQuery(querry);
               
               while(rs.next()){
               mproduct.setName(rs.getString("productName"));
               mproduct.setCategoryID(rs.getString("categoryName"));
               mproduct.setStock(rs.getInt("stock"));
               mproduct.setPrice(rs.getDouble("price"));
               mproduct.setPlaceOrigin(rs.getString("placeOrigin"));
              
               model.addRow(new Object[]{mproduct.getName(),mproduct.getStock(),mproduct.getCategoryID(),mproduct.getPrice(),mproduct.getPlaceOrigin()});
               }
               tbproduct.setModel(model);
           } catch (Exception e) {
               JOptionPane.showMessageDialog(null,"Error: "+e.toString());
           } finally {
               connection.closeConnection();
           }

               tbproduct.setDefaultEditor(Object.class, null); 
           }
         
       public void selectRow(JTable tbproduct,JComboBox Category,JTextField pname,JSpinner stock,JTextField category,JTextField price,JRadioButton within,JRadioButton outside,JTextField city,JComboBox City){
       
           int row = tbproduct.getSelectedRow();
           if(row >=0){
              
               pname.setText(tbproduct.getValueAt(row, 0).toString());
               stock.setValue(tbproduct.getValueAt(row, 1));
               Category.getModel().setSelectedItem(tbproduct.getValueAt(row, 2));
               price.setText(tbproduct.getValueAt(row, 3).toString());
               
               if(tbproduct.getValueAt(row, 4).equals("Guayaquil") ||tbproduct.getValueAt(row, 4).equals("Quito")|| tbproduct.getValueAt(row, 4).equals("Cuenca") ){
               
                   within.setSelected(true);
                   City.getModel().setSelectedItem(tbproduct.getValueAt(row, 4));
               }
               if(tbproduct.getValueAt(row, 4).equals("Outside")){
               
                   outside.setSelected(true);
               }
               
           
           }
           else{
               JOptionPane.showMessageDialog(null,"Must choose at least a product to continue with the process");
           }
       
       }
       
       public void editProduct(JTextField pname,JSpinner stock,JTextField category,JTextField price,JRadioButton within,JRadioButton outside,JComboBox city){ 
           CConnection connection = new CConnection();
           model.product mproduct = new product();
          
           mproduct.setName(pname.getText());
           mproduct.setStock(Integer.parseInt(stock.getModel().getValue().toString()));
           mproduct.setFkcategoryID(Integer.parseInt(category.getText()));
           mproduct.setPrice(Double.parseDouble(price.getText()));
           
           if(within.isSelected()){
           
               mproduct.setCCity(city.getModel().getSelectedItem().toString());
               
           }else if(outside.isSelected()){
           
               mproduct.setCCity("Outside");
           }else{
               JOptionPane.showMessageDialog(null,"You must choose at least an origin place");
           return;
           }
           
           
           try {
               String querry="UPDATE dbdar2.product as p set p.productName = ?,fkcategoryID =?,stock=?,price=?,placeOrigin=? WHERE p.productName = ?;";
               
               PreparedStatement ps = connection.setConnection().prepareCall(querry);
               ps.setString(1, mproduct.getName());
               ps.setInt(2, mproduct.getFkcategoryID());
               ps.setInt(3, mproduct.getStock());
               ps.setDouble(4, mproduct.getPrice());
               ps.setString(5,  mproduct.getCCity());
               ps.setString(6, mproduct.getName());
               
               ps.execute();
               
               JOptionPane.showMessageDialog(null,"Edit successful!");
               
           } catch (Exception e) {
               
               JOptionPane.showMessageDialog(null,"Failed edit: "+e.getMessage());
           } finally {
               connection.closeConnection();
           }
       }
       
       public void deleteProduct(JTextField pname){
       
           CConnection connection = new CConnection();
           model.product mproduct = new product();
           mproduct.setName(pname.getText());
           try {
               String querry = "DELETE FROM dbdar2.product as p WHERE p.productName=? ";
               
               PreparedStatement ps = connection.setConnection().prepareCall(querry);
               ps.setString(1, mproduct.getName());
              ps.execute();
               
               JOptionPane.showMessageDialog(null,"Delete successful!");
               
           } catch (Exception e) {
               JOptionPane.showMessageDialog(null,"Failed delete: "+e.toString());
           } finally {
               connection.closeConnection();
           }
       
       }
       
       
       
       }
    
    

