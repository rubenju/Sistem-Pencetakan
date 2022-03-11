/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.StageStyle;

/**
 *
 * @author LATITUDE
 */
public class UIController implements Initializable {

    myThread_Client my;

    @FXML
    private TextField Search_NIK;
    @FXML
    private TableView<dataSTNK> Output;
    @FXML
    private TableColumn<dataSTNK, String> Table_VarName;
    @FXML
    private TableColumn<dataSTNK, String> Table_VarValue;
    @FXML
    private AnchorPane Pane_Main;
    @FXML
    private Label Win_Message;
    @FXML
    private TableView<dataSTNK> Output2;
    @FXML
    private TableColumn<dataSTNK, String> Table_VarName2;
    @FXML
    private TableColumn<dataSTNK, String> Table_VarValue2;

    private boolean LoggedIn = false;

    Stage AlertWindow;
    AlertBoxController Alert;
    @FXML
    private Button Btn_Search;
    @FXML
    private TextField Search_Nopol;
    @FXML
    private Button Btn_LogSign;
    
    Socket sc = new Socket();
    @FXML
    private Button btn_Print;

    String crnt_NIK;
    String crnt_Nopol;
    String[] temp_data;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //initialize alert window
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AlertBox.fxml"));
            Parent alertParent = (Parent) loader.load();
            Alert = loader.getController();
            Scene AlertScene = new Scene(alertParent);

            //Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            AlertWindow = new Stage();
            AlertWindow.setScene(AlertScene);
            AlertWindow.initStyle(StageStyle.UNDECORATED);
            AlertWindow.setAlwaysOnTop(true);
        } catch (Exception e) {
        }

        /*try {
            Show_LogSign();
        } catch (Exception e) {
            System.err.println(e);
        }*/

        TCP_go();

        Table_VarName.setCellValueFactory(new PropertyValueFactory<dataSTNK, String>("VarName"));
        Table_VarValue.setCellValueFactory(new PropertyValueFactory<dataSTNK, String>("VarValue"));
        Table_VarName2.setCellValueFactory(new PropertyValueFactory<dataSTNK, String>("VarName"));
        Table_VarValue2.setCellValueFactory(new PropertyValueFactory<dataSTNK, String>("VarValue"));
    }

    public void showAlert(String text) {
        Alert.setAlert(text);
        AlertWindow.setX(500);
        AlertWindow.setY(250);
        AlertWindow.showAndWait();
    }

    public boolean LogIn(String Log_Uname, String Log_Pass) {
        String[] reply = new String[5];
            boolean succeed = false;
        try {

            reply = my.login(Log_Uname, Log_Pass);
            
            if(reply[3]==null){
                succeed = reply[2].equals("1");
                System.err.println("Log In " + succeed);
                setLogged(succeed, Log_Uname);
            }
            
            if (!succeed) {
                showAlert("Login Gagal");
            }
        } catch (Exception e) {
            System.err.println(e);
            reply[0] = "Alert";
            reply[1] = "Gateway Timeout";
            
            try {
                sc.close();
            } catch (Exception ex) {
                System.err.println(ex);
            }
                newSocket();
        }
        
        show_Reply(reply);
        return succeed;
    }

    public boolean LogOut() {
        String[] reply = new String[5];
        boolean succeed = false;
        try {
            reply = my.logOut();
            if(reply[3]==null)
                succeed = reply[2].equals("1");

        } catch (Exception e) {
            System.err.println(e);
            reply[0] = "Alert";
            reply[1] = "Gateway Timeout";
            
            try {
                sc.close();
            } catch (Exception ex) {
                System.err.println(ex);
            }
                newSocket();
        }
        
        show_Reply(reply);
        return succeed;
    }

    public boolean SignUp(String Sign_Uname, String Sign_Pass1, String Sign_Pass2) {
        String[] reply = new String[5];
        boolean succeed = false;
        try {
            if (Sign_Pass1.equals(Sign_Pass2)) {
                reply = my.signup(Sign_Uname, Sign_Pass1);
            }
            if(reply[3]==null)
                succeed = reply[2].equals("1");
            
            System.err.println("Sign Up " + succeed);
            
        } catch (Exception e) {
            System.err.println(e);
            reply[0] = "Alert";
            reply[1] = "Gateway Timeout";
            
            try {
                sc.close();
            } catch (Exception ex) {
                System.err.println(ex);
            }
                newSocket();
        }
        
        show_Reply(reply);
        return succeed;
    }

    @FXML
    private void btn_Search_click() {
        if(!Search_NIK.getText().equals("")&&!Search_Nopol.getText().equals("")){
            String[] data = new String[50];
            try{
                data = my.search(Search_Nopol.getText(), Search_NIK.getText());
            }catch(Exception e){
                System.err.println(e);
                data[0] = "Alert";
                data[1] = "Gateway Timeout";

                try {
                    sc.close();
                } catch (Exception ex) {
                    System.err.println(ex);
                }
                    newSocket();
            }
            
            if(!data[0].equalsIgnoreCase("alert")){
                crnt_NIK = Search_NIK.getText();
                crnt_Nopol = Search_Nopol.getText();
                Btn_Search.setDisable(true);
                btn_Print.setDisable(false);
            }
            
            show_Reply(data);
            temp_data = data;
            
        }else{
            showAlert("Silahkan isi data dengan benar!");
        }
    }
    
    @FXML
    private void Print(MouseEvent event) {
        Cetak print = new Cetak();
        if(!crnt_NIK.equals("")&&!crnt_Nopol.equals("")){
            String[] reply = new String[50];
            try{
                reply = my.print(Search_Nopol.getText(), Search_NIK.getText());
            
                if(reply[2].equalsIgnoreCase("1")){
                    print.cetak(temp_data);
                }else{
                    reply[1] += " pada tanggal "+ reply[3];
                }

                Btn_Search.setDisable(false);
                btn_Print.setDisable(true);

                crnt_NIK = "";
                crnt_Nopol = "";   

                Output.getItems().clear();
                Output2.getItems().clear();
                Search_NIK.setText("");
                Search_Nopol.setText("");
                temp_data=null;
                
            }catch(Exception e){
                System.err.println(e);
                reply[0] = "Alert";
                reply[1] = "Gateway Timeout";

                try {
                    sc.close();
                } catch (Exception ex) {
                    System.err.println(ex);
                }
                    newSocket();
            }
            show_Reply(reply);
            
            
        }else{
            showAlert("Silahkan cari data lebih dahulu");
        }
    }

    private void show_Reply(String[] reply){
        if(reply[0].equalsIgnoreCase("alert")){
            showAlert(reply[1]);
        }else{
            ObservableList<dataSTNK> Data = FXCollections.observableArrayList();
                ObservableList<dataSTNK> Data2 = FXCollections.observableArrayList();
                reply[36] = String.valueOf(Integer.valueOf(reply[24]) + Integer.valueOf(reply[30]));
                reply[37] = String.valueOf(Integer.valueOf(reply[25]) + Integer.valueOf(reply[31]));
                reply[38] = String.valueOf(Integer.valueOf(reply[26]) + Integer.valueOf(reply[32]));
                reply[39] = String.valueOf(Integer.valueOf(reply[27]) + Integer.valueOf(reply[33]));
                reply[40] = String.valueOf(Integer.valueOf(reply[28]) + Integer.valueOf(reply[34]));
                reply[41] = String.valueOf(Integer.valueOf(reply[29]) + Integer.valueOf(reply[35]));
                
                for (int i = 0; reply[i] != null; i++) {
                    System.out.println(reply[i]);
                    if (i < 21) {
                        Data.add(new dataSTNK(getVariable(i), reply[i]));
                    } else {
                        Data2.add(new dataSTNK(getVariable(i), reply[i]));
                    }
                }
            Output.setItems(Data);
            Output2.setItems(Data2);
        }
    }
    

    private String getVariable(int no) {
        String temp = null;

        switch (no) {
            case 0: temp = "NIK"; break;
            case 1: temp = "Nama"; break;
            case 2: temp = "Alamat"; break;
            case 3: temp = "Nomor_Polisi"; break;
            case 4: temp = "No_Urut"; break;
            case 5: temp = "No_Kohir"; break;
            case 6: temp = "Tgl_Penetapan"; break;
            case 7: temp = "Tgl_Berakhir"; break;
            case 8: temp = "Petugas_Penetapan"; break;
            case 9: temp = "Korektor"; break;
            case 10: temp = "Merk"; break;
            case 11: temp = "Jenis"; break;
            case 12: temp = "Th_pembuatan"; break;
            case 13: temp = "Warna_KB"; break;
            case 14: temp = "Isi_Silinder"; break;
            case 15: temp = "No_Rangka"; break;
            case 16: temp = "No_Mesin"; break;
            case 17: temp = "No_BPKB"; break;
            case 18: temp = "Bahan_Bakar"; break;
            case 19: temp = "Warna_TNKB"; break;
            case 20: temp = "No_Polisi_Lama"; break;
            case 21: temp = "Berat_KB"; break;
            case 22: temp = "Jumlah_Sumbu"; break;
            case 23: temp = "Penumpang"; break;
            
            case 24: temp = "BBN_KB"; break;
            case 25: temp = "PKB"; break;
            case 26: temp = "SWDKLLJ"; break;
            case 27: temp = "ADM_STNK"; break;
            case 28: temp = "ADM_TNKB"; break;
            case 29: temp = "JUMLAH"; break;
            
            case 30: temp = "BBN_KB2"; break;
            case 31: temp = "PKB2"; break;
            case 32: temp = "SWDKLLJ2"; break;
            case 33: temp = "ADM_STNK2"; break;
            case 34: temp = "ADM_TNKB2"; break;
            case 35: temp = "JUMLAH2"; break;
            
            case 36: temp = "BBN_KB_Total"; break;
            case 37: temp = "PKB_Total"; break;
            case 38: temp = "SWDKLLJ_Total"; break;
            case 39: temp = "ADM_STNK_Total"; break;
            case 40: temp = "ADM_TNKB_Total"; break;
            case 41: temp = "JUMLAH_Total"; break;
        }
        return temp;
    }

//    private void Clear(MouseEvent event) {
//        Pane_Main.getChildren().clear();
//    }

    @FXML
    private void Log_SignOut(MouseEvent event) {
        if (!LoggedIn) {
            try {
                Show_LogSign();
            } catch (Exception e) {
                System.err.println(e);
            }
        } else {
            try {
                Show_LogSign();
            } catch (IOException ex) {
                Logger.getLogger(UIController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void Show_LogSign() throws IOException {
        if (!LoggedIn) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LogSign.fxml"));

            Parent LogSignParent = (Parent) loader.load();
            LogSignController LogSign = loader.getController();
            LogSign.getRef(this);

            Scene LogSignScene = new Scene(LogSignParent);

            //Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            Stage window = new Stage();
            window.setScene(LogSignScene);
            window.initStyle(StageStyle.UNDECORATED);
            window.setAlwaysOnTop(true);
            window.showAndWait();

        } else if (LogOut()) {
            setLogged(false, "");
        }
        Output.getItems().clear();
        Output2.getItems().clear();
        Search_NIK.setText("");
        Search_Nopol.setText("");
    }

    public void setLogged(boolean logged, String UName) {
        if (logged) {
            Win_Message.setText("Logged in as " + UName);
            Search_NIK.setDisable(false);
            Search_Nopol.setDisable(false);
            Btn_Search.setDisable(false);
            Btn_LogSign.setText("Log Out");
            LoggedIn = true;
        } else {
            Win_Message.setText("Silahkan Login Terlebih Dahulu");
            Search_NIK.setDisable(true);
            Search_Nopol.setDisable(true);
            Btn_Search.setDisable(true);
            Btn_LogSign.setText("Log In");
            LoggedIn = false;
        }
    }
    
    public void newSocket(){
        int PORT = 6788;
        
        try{
            sc = new Socket("192.168.43.171", PORT);
            sc.setSoTimeout(10000);
            myThread_Client handle = new myThread_Client(sc);
            my.connect(sc);

        } catch (SocketTimeoutException ex) {
            ex.printStackTrace();
        } catch (UnknownHostException ex) {
            Logger.getLogger(UIController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void TCP_go() {
        int PORT = 6788;
        try {
            sc = new Socket("192.168.43.171", PORT);
            //sc = new Socket("192.168.43.23", PORT);
            sc.setSoTimeout(10000);
            myThread_Client handle = new myThread_Client(sc);
            my = handle;
        
        } catch (SocketTimeoutException ex) {
            ex.printStackTrace();
            /*try {
                System.err.println("Reconnect");
                IPAddress = InetAddress.getLocalHost().getHostAddress();
                sc = new Socket(IPAddress, PORT);
                sc.setSoTimeout(10000);
                //sc.connect(new InetSocketAddress(IPAddress, PORT), 10000);                
            } catch (IOException ex1) {
                Logger.getLogger(UIController.class.getName()).log(Level.SEVERE, null, ex1);
            }*/
        } catch (UnknownHostException ex) {
            Logger.getLogger(UIController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class dataSTNK {

        private SimpleStringProperty VarName;
        private SimpleStringProperty VarValue;

        public dataSTNK(String VarName, String VarValue) {
            this.VarName = new SimpleStringProperty(VarName);
            this.VarValue = new SimpleStringProperty(VarValue);
        }

        public String getVarName() {
            return VarName.get();
        }

        public void setVarName(String VarName) {
            this.VarName = new SimpleStringProperty(VarName);
        }

        public String getVarValue() {
            return VarValue.get();
        }

        public void setVarValue(String VarValue) {
            this.VarValue = new SimpleStringProperty(VarValue);
        }
    }
}
