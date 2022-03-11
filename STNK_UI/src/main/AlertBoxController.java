/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author LATITUDE
 */
public class AlertBoxController implements Initializable {

    @FXML
    private Label Alert_Window;
    @FXML
    private Button btn_Ok;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void setAlert(String text){
        Alert_Window.setText(text);
    }

    @FXML
    private void btn_Alert(MouseEvent event) {// get a handle to the stage
        Stage stage = (Stage) btn_Ok.getScene().getWindow();
        // do what you have to do
        stage.close();
    }
    
}
