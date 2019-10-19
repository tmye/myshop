/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import Utils.Constants;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.TabSettings;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import modele.ResultatsReq;
import service.IContenirVente;
import service.IHistoriqueVente;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

/**
 * FXML Controller class
 *
 * @author Christ
 */
public class DetailsVenteController implements Initializable {

    @FXML
    private TableView<ResultatsReq> venteDetailsTable;
    @FXML
    private TableColumn<ResultatsReq, String> produitCol;
    @FXML
    private TableColumn<ResultatsReq, String> prixUniCol;
    @FXML
    private TableColumn<ResultatsReq, String> qteVendueCol;
    @FXML
    private TableColumn<ResultatsReq, String> totalCol;
    @FXML
    private Label totLabel;
    @FXML
    private JFXButton btnPrint;

    IContenirVente contenirVenteService = MainViewController.contenirVenteService;
    IHistoriqueVente historiqueVenteService = MainViewController.historiqueVenteService;

    ObservableList<ResultatsReq> detailsVenteList = FXCollections.observableArrayList();
    ObservableList<ResultatsReq> venteListImp = FXCollections.observableArrayList();
    
    
    int tot = 0;

    public List<Object[]> detailsVente(int idVente) {
        return contenirVenteService.findDetailsVte(idVente) ;
    }

    public void loadInventairegrid(int idVente) {
        detailsVenteList.clear();
        List<Object[]> lv = detailsVente(idVente);
        
        if(lv!=null){
            for (Object[] o : lv) {
                detailsVenteList.add(new ResultatsReq(
                        new SimpleStringProperty(o[0]+""),
                        new SimpleStringProperty(o[1]+""),
                        new SimpleStringProperty(o[2]+""),
                        new SimpleStringProperty(o[3]+"")
                    )
                );
                
                tot+= Double.valueOf(o[3]+"");
            }
            produitCol.setCellValueFactory(cellData -> cellData.getValue().getResultString());
            prixUniCol.setCellValueFactory(cellData -> cellData.getValue().getResultInt());
            qteVendueCol.setCellValueFactory(cellData -> cellData.getValue().getPrixProd());
            totalCol.setCellValueFactory(cellData -> cellData.getValue().getMtVte());
            
            venteDetailsTable.setItems(detailsVenteList);
            totLabel.setText("TOTAL: "+tot+" F");
        }        
        
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        Font.loadFont(MainViewController.class.getResource("/css/Heebo-Bold.ttf").toExternalForm(), 10);
        Font.loadFont(MainViewController.class.getResource("/css/Bearskin DEMO.otf").toExternalForm(), 10);
        Font.loadFont(MainViewController.class.getResource("/css/Heebo-ExtraBold.ttf").toExternalForm(), 10);
        Font.loadFont(MainViewController.class.getResource("/css/Heebo-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(MainViewController.class.getResource("/css/Jurassic Park.ttf").toExternalForm(), 10);
        
        int selectedVteId = Integer.parseInt(RechercheVentePaneController.vtTableRecVte.getSelectionModel().getSelectedItem().getResultString().get());
        
        loadInventairegrid(selectedVteId);
        
    }
    
    @FXML
    public void imprimerFacture(){
        venteListImp.clear();
        //detailsVenteList.clear();
        int idVteImp = Integer.parseInt(RechercheVentePaneController.vtTableRecVte.getSelectionModel().getSelectedItem().getResultString().get()) ;
        
        List<Object[]> lv1 = historiqueVenteService.findByIdVen(idVteImp);
        //List<Object[]> lv2 = venteDetailsTable.getItems() ;
        
        for(Object[] o: lv1){
            String s = o[3]+" ("+o[4]+") ";
            venteListImp.add( new ResultatsReq(
                    new SimpleStringProperty(o[0]+""), 
                    new SimpleStringProperty(o[1]+"") , 
                    new SimpleStringProperty(o[2]+""), 
                    new SimpleStringProperty(s), 
                    new SimpleStringProperty(o[5]+""), 
                    new SimpleStringProperty(o[6]+"")
            ));
        }
        /*float tot = 0;
        for (Object[] o : lv2) {
                detailsVenteList.add(new ResultatsReq(
                        new SimpleStringProperty(o[0]+""),
                        new SimpleStringProperty(o[1]+""),
                        new SimpleStringProperty(o[2]+""),
                        new SimpleStringProperty(o[3]+"")
                    )
                );
                
                tot+= Double.valueOf(o[3]+"");
        }*/
        
        
        Rectangle pageSize =  new Rectangle(320, 380);
        
        Document doc = new Document(pageSize);
                
        if(venteListImp != null){
            try{
                    
                    PdfWriter.getInstance(doc, new FileOutputStream("factureReim.pdf"));
                    doc.open();
                    //document.add(new Paragraph(excerptsFromDavidCopperfield[0], new Font(Font.TIMES_ROMAN)));
                    com.itextpdf.text.Font font=new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER);
                    
                    Paragraph p = new Paragraph();
                    p.setFont(font);
                    p.setTabSettings(new TabSettings(100f));
                    p.add(Chunk.TABBING);
                    
                    //com.itextpdf.text.Font font = new com.itextpdf.text.Font("Courrier Niew", 12);
                    p.add(new Chunk("MYSHOP"));
                   
                    doc.add(p);

                    

                    p = new Paragraph();
                    p.setFont(font);
                    p.setTabSettings(new TabSettings(72f));
                    p.add(Chunk.TABBING);
                    p.add(new Chunk("**************"));
                    doc.add(p); 
                    
                    
                    
                    p = new Paragraph();
                     p.setFont(font);
                    p.setTabSettings(new TabSettings(82f));
                    p.add(Chunk.TABBING);
                    p.add(new Chunk("Vente N."+venteListImp.get(0).getResultString().get()));
                    p.setFont(font);
                    doc.add(p); 

                    p = new Paragraph();
                     p.setFont(font);
                    p.setTabSettings(new TabSettings(120f));
                    //p.add(Chunk.TABBING);
                    int caiPseudoLength =  venteListImp.get(0).getResultInt().get().length();
                    String recompCaiName = "";
                    if(caiPseudoLength > 7){
                        for(int i=0;i<7;i++){
                            recompCaiName = recompCaiName+venteListImp.get(0).getResultInt().get().charAt(i);
                        }
                        recompCaiName = recompCaiName+".";
                    }else{
                        recompCaiName = venteListImp.get(0).getResultInt().get();
                    }
                    
                    p.add(new Chunk("==================================")) ;
                    p.add(new Chunk("Cais: "+recompCaiName)) ;
                    p.add(Chunk.TABBING);
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = new Date(); 
                    df.format( new Date(date.getTime()));
                    p.add(new Chunk("Date: "+df.format( new Date(date.getTime()))+" \n"));
                    doc.add(p);

                    p = new Paragraph("");
                     p.setFont(font);
                    p.add(new Chunk("Client: "+venteListImp.get(0).getMtVte().get()+" \n"));
                    p.add(new Chunk("=================================="));
                    doc.add(p);

                    PdfPTable table = new PdfPTable(4);
                    table.setWidths(new float []{2f, 1f, 1f,1.5f});
                     table.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.setWidthPercentage(100);
                    PdfPCell cell = new PdfPCell();
                    //cell.setColspan(5);
                    table.getDefaultCell().setBorder(cell.NO_BORDER);
                    //cv.idVen, cv.montVente, p.libProd, cv.prixProd, cv.qteVen, (cv.prixProd * cv.qteVen), c.nomClt,
        //c.numClt, cp.pseudoComp, cv.dtVente, hv.smeRecue, hv.smeRendue
                    
                    table.addCell( new Phrase("Produits ",font));
                    table.addCell(new Phrase("PU ",font));
                    table.addCell(new Phrase("Qte ",font));
                    table.addCell(new Phrase("Total ",font));
                    
                    //ObservableList<ProduitR> pdt =  produitCaisseTable.getItems();
                    //System.out.println("length: "+pdt.size());
                    
                    for( ResultatsReq prod : detailsVenteList ){
                        
                        String prodName = prod.getResultString().get() +"";
                      
                        String tabNameSplitted[] = prodName.split(" ");
                        String recomposedName = "";
                        for(int i=0;i<2;i++ ){
                            //System.out.println(""+tabNameSplitted[i]);
                            if(tabNameSplitted[i].length()>3){
                                 recomposedName = recomposedName+tabNameSplitted[i].substring(0,3)+". ";
                            }else {
                                if( !recomposedName.equalsIgnoreCase("") || !recomposedName.equalsIgnoreCase(" ")){
                                    recomposedName = recomposedName+tabNameSplitted[i].substring(0,tabNameSplitted[i].length())+". ";
                                }
                            }
                       
                        }
                        //p.add(new Chunk(""+recomposedName));
                        table.addCell( new Phrase(""+recomposedName,font));
                        table.addCell( new Phrase(""+prod.getResultInt().get(),font));
                        table.addCell(new Phrase(""+prod.getPrixProd().get(),font));
                        table.addCell(new Phrase(""+prod.getMtVte().get(),font));
                        
                       
                      
                    }
                    table.addCell(" ");
                    table.addCell(" ");
                    table.addCell(" ");
                    table.addCell(" ");
                    
                    table.addCell(new Phrase("Reg: Espèce",font));
                    table.addCell(" ");
                    table.addCell(" ");
                    table.addCell(new Phrase(tot+" F",font) );
                    
                    table.addCell(" ");
                    table.addCell(" ");
                    table.addCell(" ");
                    table.addCell(" ");
                    
                    table.addCell(new Phrase("Reçu: "+venteListImp.get(0).getDateVen().get(),font) );
                    table.addCell(" ");
                    table.addCell(new Phrase("Rend: ",font) );
                    table.addCell(new Phrase(""+venteListImp.get(0).getNomClt().get(),font));
                    doc.add(table);
                    
                    p = new Paragraph();
                    p.setFont(font);
                    p.add(new Chunk("=================================="));
                    //p.setTabSettings(new TabSettings(20f));
                   // p.add(Chunk.TABBING);
                    p.add(new Chunk(" Merci de votre visite et à Bientôt "));
                    p.add(new Chunk("=================================="));
                    
                    doc.add(p);
                    
                    //impression de la facture
                    if (Desktop.isDesktopSupported()){  
                        if(Desktop.getDesktop().isSupported(java.awt.Desktop.Action.PRINT)){  
                            try {  
                                        java.awt.Desktop.getDesktop().print(new File("factureReim.pdf"));  
                                } catch (IOException ex) {  
                                    System.out.println("ex "+ex);
                                        //Traitement de l'exception  
                                }  
                        } else {  
                              System.out.println("La fonction n'est pas supportée par votre système d'exploitation");  //  
                        }  
                    } else {  
                        System.out.println("Desktop pas supporté par votre système d'exploitation ");
                            // 
                    }
                    
                     TrayNotification notification = new TrayNotification();
                    notification.setAnimationType(AnimationType.POPUP);
                    notification.setTray("MyShop", "Opération Effectuée", NotificationType.SUCCESS);
                    notification.showAndDismiss(Duration.seconds(1.5));
                    Stage stage = (Stage) btnPrint.getScene().getWindow();
                    stage.close();
                    switchPane(Constants.RechercheVente);


                }catch(Exception e){
                    System.out.println(""+e);
                    TrayNotification notification = new TrayNotification();
                    notification.setAnimationType(AnimationType.POPUP);
                    notification.setTray("MyShop", "Opération Non Effectuée! "+e, NotificationType.ERROR);
                    notification.showAndDismiss(Duration.seconds(1.5));
                    
                   // e.printStackTrace();
                }
                doc.close();
            }
                
                
    }
    
     private void switchPane(String pane) {
        try {
            MainViewController.temporaryPane.getChildren().clear();
            StackPane stackPane = FXMLLoader.load(getClass().getResource(pane));
            ObservableList<Node> elements = stackPane.getChildren();

            MainViewController.temporaryPane.getChildren().setAll(elements);

            MainViewController.drawerTmp.setVisible(true);
            // MainViewController.hamburgerTmp = new JFXHamburger();
        } catch (IOException ex) {
            Logger.getLogger(MenuLateraleController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
