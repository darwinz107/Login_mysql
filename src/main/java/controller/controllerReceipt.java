
package controller;




import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Table;
import configuration.CConnection;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.product;


/**
 *
 * @author darwin
 */
public class controllerReceipt {
   
    
    public void AssingInfo(JTable receipt,JTextField receiptID,JLabel customerName,JLabel gender,JLabel dateReceipt,JLabel iva,JLabel Total){
    
        CConnection connection = new CConnection();
        model.customer mcustomer = new model.customer();
        model.product mproduct = new product();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Product");
        model.addColumn("Price");
        model.addColumn("Quantity");
        model.addColumn("Subtotal");
        model.addColumn("Place_Origin");
        receipt.setModel(model);
      
        
        
        if(receiptID.getText().isEmpty()){
        model.setRowCount(0);
        receipt.setModel(model);
        customerName.setText("");
        gender.setText("");
        dateReceipt.setText("");
        iva.setText("0.00");
        Total.setText("0.00");
        }else{
            int recptID = Integer.parseInt(receiptID.getText());
            try {
                 String querry = """
                        SELECT c.customerName,c.lastName,s.sexName as Gender,p.productName,r.receiptDate,pd.priceSell,pd.quantity,pd.priceSell * pd.quantity as Subtotal,pd.placeOrigin FROM dbdar2.productdetail as pd
                        INNER JOIN dbdar2.receipt as r ON pd.fkreceiptID = r.receiptID
                        INNER JOIN dbdar2.customer as c ON r.fkcustomerID = c.customerID
                        INNER JOIN dbdar2.product as p ON pd.fkproductID = p.productID
                        INNER JOIN dbdar2.sex as s ON c.fksexID = s.sexID
                        where r.receiptID = ?
                        ;""";
        
                  
                DecimalFormat format = new DecimalFormat("#.##");
        PreparedStatement ps = connection.setConnection().prepareCall(querry);   
        ps.setInt(1, recptID);      
        
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
        mcustomer.setCustomerName(rs.getString("customerName"));
        mcustomer.setLastName(rs.getString("lastName"));
        mcustomer.setSexName(rs.getString("Gender"));
        mproduct.setName(rs.getString("productName"));
      mcustomer.setDateBorn(rs.getDate("receiptDate")); 
        mproduct.setPrice(rs.getDouble("priceSell"));
        mproduct.setStock(rs.getInt("quantity"));
        mproduct.setPlaceOrigin(rs.getString("placeOrigin"));
        
        double subtotal = Double.parseDouble(format.format(rs.getDouble("Subtotal"))) ;
        
        model.addRow(new Object[]{mproduct.getName(),mproduct.getPrice(),mproduct.getStock(),subtotal,mproduct.getPlaceOrigin()});
        
        }
        receipt.setModel(model);
        customerName.setText(mcustomer.getCustomerName()+" "+mcustomer.getLastName());
        gender.setText(mcustomer.getSexName());
        dateReceipt.setText( mcustomer.getDateBorn().toString());
        double totalFinal=0;
             double ivaFinal=0;
                for (int row = 0; row < receipt.getRowCount(); row++) {
                 double total = Double.parseDouble(model.getValueAt(row, 3).toString());
                     double iva2 = total *0.15;
                     double totalIva = total+iva2;
                     totalFinal += totalIva;
                     ivaFinal +=iva2; 
                     
                 }
                 iva.setText(String.valueOf(format.format(ivaFinal) ));
             Total.setText(String.valueOf(format.format(totalFinal)));       
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,"Failed at show the receipt: "+e.toString());
            } finally {
                connection.closeConnection();
            }
   
        
        
        
        }
        
    }
    
    
   public void showReceipt(JTextField cusID,JComboBox receipt){
   
       CConnection connection = new CConnection();
       model.customer mcustomer = new model.customer();
      
       try {
           mcustomer.setCustomerID(Integer.parseInt(cusID.getText()));
           String querry = "SELECT receiptID FROM dbdar2.receipt  where fkcustomerID =?; ";
           PreparedStatement ps = connection.setConnection().prepareCall(querry);
           ps.setInt(1, mcustomer.getCustomerID());
           ResultSet rs = ps.executeQuery();
           receipt.addItem("....");
           while(rs.next()){          
           receipt.addItem(rs.getInt("receiptID"));
           }
           
           
       } catch (Exception e) {
           JOptionPane.showMessageDialog(null,"Error show receipt: "+e.getMessage());
       } finally {
           connection.closeConnection();
       }
      
   } 
    public void selectRow( JComboBox receipt,JTextField receiptID){
       
        DefaultComboBoxModel cmodel = (DefaultComboBoxModel) receipt.getModel();
        int row = receipt.getSelectedIndex();
        if(row>0){            
        receiptID.setText(cmodel.getElementAt(row).toString());
       
                }else{
        receiptID.setText("");
        }
  
       }  
    
public void printCustom(JTable proReceipt, JTextField numReceipt, JLabel cusName, JLabel gender, JLabel dateBuy, JLabel iva, JLabel total) {
    try {
    String path = cusName.getText()+"_receipt#"+numReceipt.getText()+".pdf";
 
 PdfWriter writer = new PdfWriter(path); 
 PdfDocument pdfdocument = new PdfDocument(writer);
 pdfdocument.setDefaultPageSize(PageSize.A4);
 Document document = new Document(pdfdocument);
 
 document.add(new Paragraph("Receipt# " +numReceipt.getText()));
 document.add(new Paragraph("Date of buy: "+dateBuy.getText()));
        SolidLine border = new SolidLine(1f/2f);
        border.setColor(ColorConstants.BLACK);
        LineSeparator line = new LineSeparator(border);
        
 document.add(line);
 
 document.add(new Paragraph("Name: "+cusName.getText()));
 document.add(new Paragraph("Gender: "+gender.getText()));
 document.add(new Paragraph("\n\n"));

        Table pdfReceipt = new Table(proReceipt.getColumnCount());
 
        for (int columns = 0; columns < proReceipt.getColumnCount(); columns++) {
            pdfReceipt.addCell(new Cell().add(new Paragraph(proReceipt.getColumnName(columns))).setBackgroundColor(ColorConstants.LIGHT_GRAY));
           
        }
        DefaultTableModel model = (DefaultTableModel) proReceipt.getModel();
 
        for (int rows = 0; rows < model.getRowCount(); rows++) {
            for (int columns = 0; columns < model.getColumnCount(); columns++) {
                pdfReceipt.addCell(new Cell().add(new Paragraph(proReceipt.getValueAt(rows, columns).toString())));
                
            }
        }
        document.add(pdfReceipt);
 document.add(new Paragraph("IVA: "+iva.getText()));
 document.add(new Paragraph("TOTAL: "+total.getText()));
 document.close();
        JOptionPane.showMessageDialog(null,"Pdf generated: "+path);
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null,e.toString());
    }

    
    }



}
    
    


