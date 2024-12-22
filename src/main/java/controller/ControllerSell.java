/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import configuration.CConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.product;

/**
 *
 * @author darwin
 */
public class ControllerSell {
     boolean initialized = false;
        
    public void showSearchProduct(JTable sproduct,JTextField searcher,JTextField pID,JLabel lblStock,JSpinner spnStock,JTextField txtStock){
    
        CConnection connection = new CConnection();
        model.customer mcustomer = new model.customer();
        model.product mproduct = new product();
        
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnCount(0);
        model.setRowCount(0);
      sproduct.setModel(model);
      model.addColumn("Product");
      model.addColumn("Category");
      model.addColumn("Stock");
      model.addColumn("Price");
      model.addColumn("Place Origin");
      
      sproduct.setModel(model);
      
      mproduct.setName(searcher.getText());
      if(mproduct.getName().isEmpty()){
      pID.setText("");
      txtStock.setText("");
      lblStock.setVisible(false);
      spnStock.setVisible(false);
      }
      else{ try {
            String querry = """
                            Select
                            p.productName,c.categoryName,p.stock,p.price,p.placeOrigin FROM dbdar2.product as p
                            INNER JOIN dbdar2.category as c ON p.fkcategoryID = c.categoryID
                            where c.categoryName like ?
                            ;""";
            
            PreparedStatement ps = connection.setConnection().prepareCall(querry);
            ps.setString(1, mproduct.getName()+"%");
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
          
                mproduct.setName(rs.getString("productName"));
                mproduct.setCategoryID(rs.getString("categoryName"));
                mproduct.setStock(rs.getInt("stock"));
                mproduct.setPrice(rs.getDouble("price"));
                mproduct.setPlaceOrigin(rs.getString("placeOrigin"));
                
                model.addRow(new Object[]{mproduct.getName(),mproduct.getCategoryID(),mproduct.getStock(),mproduct.getPrice(),mproduct.getPlaceOrigin()});
            }
            sproduct.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Error at the show product: "+e.toString());
        } finally {
            connection.closeConnection();
        }
          for (int colum = 0; colum < sproduct.getColumnCount(); colum++) {
              
              Class<?> columnClass = sproduct.getColumnClass(colum);
              sproduct.setDefaultEditor(columnClass, null);
              
          }
      }
    }
    
    public void selectRow(JTable pSelected,JTextField rowSelected,JLabel lblStock,JSpinner spnStock,JTextField txtStock){
    
        
        int row = pSelected.getSelectedRow();
        
        if(row>=0){
            
            String rowName = pSelected.getValueAt(row, 0).toString();
            rowSelected.setText( rowName);
            txtStock.setText(pSelected.getValueAt(row, 2).toString());
            lblStock.setVisible(true);
            spnStock.setVisible(true);
            
        }
       
    }
     public void addProduct(JTable pBuy,JTextField rowName,JSpinner spnStock,JTextField txtStock,JLabel Iva,JLabel Total){
        
         CConnection connection = new CConnection();
         model.product mproduct = new product();
         
         DefaultTableModel model = (DefaultTableModel) pBuy.getModel();
         mproduct.setName(rowName.getText());
         
        
         
       int validateS = Integer.parseInt(txtStock.getText());
      
      mproduct.setStock(Integer.parseInt(spnStock.getModel().getValue().toString()));
      if(mproduct.getName().isEmpty()){
      }else if(mproduct.getStock()<=0 || mproduct.getStock() > validateS ){
          JOptionPane.showMessageDialog(null,"Must choose a validate quantity!");
      }else{
       for (int row = 0; row < pBuy.getRowCount(); row++) {
             
             String pValidate = model.getValueAt(row, 0).toString();
             if(pValidate.equals(mproduct.getName())){
                 JOptionPane.showMessageDialog(null,"You already have chosen this product!");
                 return;
             }
             
             
         }
          
          
          
         try {
             String querry ="""
                            Select p.productName,c.categoryName,p.stock,p.price,p.placeOrigin FROM dbdar2.product as p
                                                        INNER JOIN dbdar2.category as c ON p.fkcategoryID = c.categoryID
                                                        where p.productName like ?""";
             
             PreparedStatement ps = connection.setConnection().prepareCall(querry);
             ps.setString(1, mproduct.getName());
             
             ResultSet rs = ps.executeQuery();
             if(rs.next()){
             
                 mproduct.setName(rs.getString("productName"));
                 mproduct.setCategoryID(rs.getString("categoryName"));
                
                mproduct.setPrice(rs.getDouble("price"));
                mproduct.setPlaceOrigin(rs.getString("placeOrigin"));
                
          double subtotal = mproduct.getPrice()*mproduct.getStock();
                model.addRow(new Object[]{mproduct.getName(),mproduct.getCategoryID(),mproduct.getStock(),mproduct.getPrice(),mproduct.getPlaceOrigin(),subtotal});     
             pBuy.setModel(model);
             }
             if(pBuy.getRowCount()==0){
             Iva.setText("0.00");
             Total.setText("0.00");
             }else{
             double totalFinal=0;
             double ivaFinal=0;
                 DecimalFormat format = new DecimalFormat("#.##");
             
                 for (int row = 0; row < pBuy.getRowCount(); row++) {
                     
                     double total = Double.parseDouble(model.getValueAt(row, 5).toString());
                     double iva = total *0.15;
                     double totalIva = total+iva;
                     totalFinal += totalIva;
                     ivaFinal +=iva; 
                     
                 }
                 Iva.setText(String.valueOf(format.format(ivaFinal)));
             Total.setText(String.valueOf(format.format(totalFinal)));
             }
             
         } catch (Exception e) {
             JOptionPane.showMessageDialog(null,"Failed add buy: "+e.getMessage());
         } finally {
             connection.closeConnection();
         }
      }
       
     }
          public void deleteSelected(JTable tbSell,JLabel Iva,JLabel Total){
     
              int row = tbSell.getSelectedRow();
             
            DefaultTableModel model =(DefaultTableModel) tbSell.getModel();
              
              if(row >=0){
              
                  model.removeRow(row);
                  tbSell.setModel(model);
                  
             double totalFinal=0;
             double ivaFinal=0;
             
                 for (int row2 = 0; row2 < tbSell.getRowCount(); row2++) {
                     
                     double total = Double.parseDouble(model.getValueAt(row2, 5).toString());
                     double iva = total *0.15;
                     double totalIva = total+iva;
                     totalFinal += totalIva;
                     ivaFinal +=iva; 
                     
                 }
                 Iva.setText(String.valueOf(ivaFinal));
             Total.setText(String.valueOf(totalFinal));
             }
              }      
      
          
          public void generateReceipt(JTable tbsell,JTextField txtCustomerID,JLabel lblReceiptID){
          CConnection connection = new CConnection();
          model.customer mcustomer = new model.customer();
       
          if(tbsell.getRowCount()==0){
              JOptionPane.showMessageDialog(null,"You have choosen anything product");
          }
          else{
                  try {
                      String querry = "INSERT INTO dbdar2.receipt(fkcustomerID,receiptDate) values(?,NOW());";
                      mcustomer.setCustomerID(Integer.parseInt(txtCustomerID.getText()));
                      
                      PreparedStatement ps = connection.setConnection().prepareCall(querry);
                      ps.setInt(1,mcustomer.getCustomerID());
                      
                      ps.execute();
                     
                      try {
                          String querry2= """
                                          SELECT r.receiptID FROM dbdar2.receipt as r 
                                          order by r.receiptID desc
                                          limit 1  
                                          ;""";
                          
                          Statement st = connection.setConnection().createStatement();
                          ResultSet rs= st.executeQuery(querry2);
                          
                          if(rs.next()){
                          lblReceiptID.setText(String.valueOf(rs.getInt("receiptID")));
                          }
                      } catch (Exception e) {
                          JOptionPane.showMessageDialog(null,"Error2: "+e.getMessage());
                      }
                      
                  } catch (Exception e) {
                      JOptionPane.showMessageDialog(null,"Error1: "+e.getMessage());
                  } finally {
                     connection.closeConnection();
                  }
             
          }
          }
          
          public void buy(JTable tbsell,JTextField cusID,JLabel lblReceiptID){
              
              CConnection connection = new CConnection();
              
              model.product mproduct = new product();
          DefaultTableModel model = (DefaultTableModel) tbsell.getModel();
              if(tbsell.getRowCount()>0){
              
                  for (int row = 0; row < tbsell.getRowCount(); row++) {
                     int receiptID = Integer.parseInt(lblReceiptID.getText());
                      mproduct.setName(model.getValueAt(row, 0).toString());
                      try {
                          String querry="SELECT p.productID FROM dbdar2.product as p where p.productName = ?";
                          
                          PreparedStatement ps = connection.setConnection().prepareCall(querry);
                          ps.setString(1, mproduct.getName());
                          
                          ResultSet rs = ps.executeQuery();
                          if(rs.next()){
                          
                              mproduct.setProductID(rs.getInt("productID"));
                          }
                              
                          try {
                      String querry2 = "INSERT INTO dbdar2.productdetail(fkreceiptID,fkproductID,quantity,priceSell,PlaceOrigin) values(?,?,?,?,?);";
                      
                      mproduct.setStock(Integer.parseInt(model.getValueAt(row, 2).toString()));
                      mproduct.setPrice(Double.parseDouble(model.getValueAt(row, 3).toString()));
                      mproduct.setPlaceOrigin(model.getValueAt(row, 4).toString());
                      
                     PreparedStatement ps2 = connection.setConnection().prepareCall(querry2);
                     ps2.setInt(1, receiptID);
                     ps2.setInt(2, mproduct.getProductID());
                     ps2.setInt(3, mproduct.getStock());
                     ps2.setDouble(4, mproduct.getPrice());
                     ps2.setString(5, mproduct.getPlaceOrigin());
                   
                     ps2.execute();
                     
                          JOptionPane.showMessageDialog(null,"Register successful!");
                          
                         
                          
                      } catch (Exception e) {
                          JOptionPane.showMessageDialog(null,"Error at register buy: "+e.toString());
                      }   
                      } catch (Exception e) {
                          
                          JOptionPane.showMessageDialog(null,"Error2 at register buy: "+e.toString());
                      }

                    finally {
                          connection.closeConnection();
                      }
                      
                  }
              
                  
                 
              }else{
                 // JOptionPane.showMessageDialog(null,"Must choose at least a product!");
              }
          }
          
          public void lessStock(JTable tbsell,JLabel sGeneral){
              
          CConnection connection = new CConnection();
          model.product mproduct = new product();
          DefaultTableModel model = (DefaultTableModel) tbsell.getModel();
          
          if(tbsell.getRowCount()==0){
           
          }
          else{
              
              for (int row = 0; row < tbsell.getRowCount(); row++) {
                  mproduct.setName(model.getValueAt(row, 0).toString());
                      mproduct.setStock(Integer.parseInt(model.getValueAt(row, 2).toString()));
                  try {
 
                      String querry = "SELECT p.productID,p.stock FROM dbdar2.product as p where p.productName=?";
                      PreparedStatement ps = connection.setConnection().prepareCall(querry);
                      ps.setString(1, mproduct.getName());
                      ResultSet rs = ps.executeQuery();
                      if(rs.next()){
                      mproduct.setProductID(rs.getInt("productID"));
                     // sGeneral.setText(String.valueOf(rs.getInt("stock"))); 
                      }
                      
                      try {
                          String querry2= "update dbdar2.product set stock = stock -? where productID=?;";
                          PreparedStatement ps2 = connection.setConnection().prepareCall(querry2);
                          ps2.setInt(1,mproduct.getStock());
                          ps2.setInt(2, mproduct.getProductID());
                          ps2.execute();
                          
                          
                      } catch (Exception e) {
                          JOptionPane.showMessageDialog(null,"Error less stock :"+e.toString());
                      }
                      
                  } catch (Exception e) {
                  } finally {
                      
                      connection.closeConnection();
                  }
              }
 
          
          model.setRowCount(0);
          tbsell.setModel(model);
          
          }
              
          }
          
}
