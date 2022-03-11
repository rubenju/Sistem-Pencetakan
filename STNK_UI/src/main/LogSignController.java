/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author LATITUDE
 */
public class LogSignController implements Initializable {

    @FXML
    private BorderPane Win_Login;
    @FXML
    private TextField Log_Uname;
    @FXML
    private PasswordField Log_Pass;
    @FXML
    private Button Login;
    @FXML
    private BorderPane Win_Signup;
    @FXML
    private TextField Sign_Name;
    @FXML
    private TextField Sign_Uname;
    @FXML
    private PasswordField Sign_Pass1;
    @FXML
    private PasswordField Sign_Pass2;

    UIController parent;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void btn_Log_click() {
        boolean succeed = false;
        if(!Log_Uname.getText().equals("")&&!Log_Pass.getText().equals("")){
            succeed = parent.LogIn(Log_Uname.getText(), Log_Pass.getText());
            
        if (succeed) {
                //parent.showAlert("Login Berhasil!");
                Stage stage = (Stage) Login.getScene().getWindow();
                stage.close();
            }
        }else parent.showAlert("Silahkan isi data dengan benar!");
        Log_Pass.setText("");
        Log_Uname.setText("");
    }

    @FXML
    private void btn_Sign_click() {
        String Name, Uname, Pass1, Pass2;
        Name = Sign_Name.getText();
        Uname = Sign_Uname.getText();
        Pass1 = Sign_Pass1.getText();
        Pass2 = Sign_Pass2.getText();
        if(!Uname.equals("")&&!Name.equals("")&&!Pass1.equals("")&&!Pass2.equals("")
                &&Pass1.equals(Pass2) ){
        
            boolean succeed = parent.SignUp(Sign_Uname.getText(), Sign_Pass1.getText(), Sign_Pass2.getText());
            if (succeed) {
                //parent.showAlert("Sign Up Berhasil!");
                Sign_Name.setText("");
            }
        }else parent.showAlert("Silahkan isi data dengan benar!");
        Sign_Uname.setText("");
        Sign_Pass1.setText("");
        Sign_Pass2.setText("");
    }

    @FXML
    private void Show_Signup(MouseEvent event) {
        Win_Login.setVisible(false);
        Win_Signup.setVisible(true);
    }

    @FXML
    private void Show_Login(MouseEvent event) {
        Win_Login.setVisible(true);
        Win_Signup.setVisible(false);
    }

    private void Log_enter(KeyEvent event) {
        switch (event.getCode()) {
            case ENTER:
                btn_Log_click();
                break;
        }
    }

    public void getRef(UIController parent) {
        this.parent = parent;
    }

    @FXML
    private void close_Win(MouseEvent event) {
        Stage stage = (Stage) Login.getScene().getWindow();
        stage.close();
    }
}
