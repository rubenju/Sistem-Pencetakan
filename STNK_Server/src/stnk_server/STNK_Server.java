/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stnk_server;

import java.util.*;
import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Elvansen
 */
///*
class myThread_Server extends Thread {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/ppkb";
    static final String USER = "root";
    static final String PASS = "";

    static Connection conn;
    static Statement stmt;
    static ResultSet rs;
    static InputStreamReader inputStreamReader = new InputStreamReader(System.in);
    static BufferedReader input = new BufferedReader(inputStreamReader);

    Socket soc;
    DataOutputStream OutToClient;
    BufferedReader FromClient;

    public myThread_Server(Socket soc) throws IOException {
        this.soc = soc;
        OutToClient = new DataOutputStream(soc.getOutputStream());
        FromClient = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("DATABASE SERVER LOST CONNECTION");
            String pesan = bungkus("Alert|Database Server Lost Connection|6", 6);
            OutToClient.writeBytes(pesan + '\n');
        }
    }

    @Override
    public void run() {
        boolean Connected = true;
        String baca = null;
        while (Connected) {
            try {
                try {
                    baca = FromClient.readLine();
                } catch (IOException e) {
                    System.err.println(e);
                }
                if (baca != null) {
                    String[] packet = bongkar(baca);
                    System.out.println("\nRECEIVE FROM GATEWAY :");

                    log(packet, 2);
                    System.out.println("Packet : " + baca);

                    String[] message = pisah(packet[2]);
                    String[] reply = new String[25];
                    int type = 0;

                    switch (packet[1]) {
                        case "030500":
                            reply = getData(message);
                            type = 3;
                            break;
                        case "030600":
                            reply = CekStatusCetak(message);
                            type = 5;
                            break;
                    }

                    for (int i = 1; i < reply.length; i++) {
                        if (reply[i] != null) {
                            reply[0] = String.format("%s|%s", reply[0], reply[i]);
                        }
                    }
                    String outClient = bungkus(reply[0], type);
                    baca = "";

                    OutToClient.writeBytes(outClient + "\n");
                    String[] s = bongkar(outClient);
                    System.out.println("\nSEND TO GATEWAY :");
                    log(s, 1);
                    System.out.println("Packet : " + outClient);
                }
            } catch (IOException | SQLException ex) {
                Logger.getLogger(myThread_Server.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                System.out.println("DATABASE SERVER LOST CONNECTION");
                String pesan = bungkus("Alert|Database Server Lost Connection|6", 6);
                try {
                    OutToClient.writeBytes(pesan + '\n');
                } catch (IOException ex1) {
                    Logger.getLogger(myThread_Server.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }

    /*
    public boolean login_user(String[] message){
        boolean check = message[1].equals(getPassword(message));
        return check;
    }
    
    static boolean newUser(String [] message)
    {
        boolean valid=false;
        try {
            if(getPassword(message)==null){  
                String sql = "INSERT INTO user (username,password) VALUE('%s','%s')";
                sql = String.format(sql,message[0],message[1]);
                System.out.println("data berhasil Disimpan");

                stmt.execute(sql);
                return valid=true;
            }
            else{
               System.out.println("data sudah ada");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return valid;
    }
     */
    static String[] getData(String[] message) {
        String[] data = new String[50];
        String sql_user = "SELECT identitas.NIK, identitas.Nama, identitas.Alamat, "
                + "stnk.Nomor_Polisi, stnk.No_Urut, stnk.No_Kohir, stnk.Tgl_Penetapan, "
                + "stnk.Tgl_Berakhir, stnk.Petugas_Penetapan, stnk.Korektor, motor.Merk, "
                + "motor.Jenis, motor.Th_pembuatan, motor.Warna_KB, motor.Isi_Silinder, "
                + "motor.No_Rangka, motor.No_Mesin, motor.No_BPKB, motor.Bahan_Bakar, "
                + "motor.Warna_TNKB, motor.No_Polisi_Lama, motor.Berat_KB, motor.Jumlah_Sumbu, "
                + "motor.Penumpanng, pokok.BBN_KB, pokok.PKB, pokok.SWDKLLJ, pokok.ADM_STNK, "
                + "pokok.ADM_TNKB, pokok.JUMLAH, sanksi.BBN_KB2, sanksi.PKB2, sanksi.SWDKLLJ2, "
                + "sanksi.ADM_STNK2, sanksi.ADM_TNKB2, sanksi.JUMLAH2 FROM identitas "
                + "INNER JOIN stnk ON identitas.NIK = stnk.NIK "
                + "INNER JOIN motor ON stnk.Nomor_Polisi = motor.Nomor_Polisi "
                + "INNER JOIN pokok ON motor.Nomor_Polisi = pokok.Nomor_Polisi "
                + "INNER JOIN sanksi ON pokok.Nomor_Polisi = sanksi.Nomor_Polisi "
                + "WHERE (identitas.NIK = \"" + message[1] + "\" "
                + "AND stnk.Nomor_Polisi = \"" + message[0] + "\" "
                + "AND motor.Nomor_Polisi = \"" + message[0] + "\" "
                + "AND pokok.Nomor_Polisi = \"" + message[0] + "\" "
                + "AND sanksi.Nomor_Polisi = \"" + message[0] + "\");";

        try {
            rs = stmt.executeQuery(sql_user);
            rs.next();
            int i = 0;

            for (i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                data[i] = rs.getString(i + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            data[0] = "Alert|Data tidak ditemukan|0";
        }
        /*for(int i=0;data[i]!=null;i++){
            System.out.println(i+"| "+data[i]);
        }*/
        return data;
    }

    static String[] CekStatusCetak(String[] message) {
        String[] data = new String[5];

        String sql = "SELECT stnk.Tanggal_Cetak, stnk.Status_Cetak FROM stnk "
                + "WHERE (stnk.NIK = \"" + message[1] + "\" "
                + "AND stnk.Nomor_Polisi = \"" + message[0] + "\");";
        String sql_update = "UPDATE stnk SET Tanggal_Cetak = CURDATE(), Status_Cetak = 1 "
                + "WHERE (stnk.NIK = \"" + message[1] + "\" "
                + "AND stnk.Nomor_Polisi = \"" + message[0] + "\");";

        try {
            rs = stmt.executeQuery(sql);
            boolean status = false;
            while (rs.next()) {
                status = rs.getBoolean("Status_Cetak");

                if (status == true) {
                    String tgl = rs.getString("Tanggal_Cetak");
                    data[0] = "Alert|Data Sudah Dicetak|0" + tgl;
                } else {
                    data[0] = "Alert|Data Akan Dicetak|1";
                    stmt.execute(sql_update);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            data[0] = "Alert|Database unreachable|1";
        }

        return data;
    }

    /*
    static String getPassword(String [] string)
    {
        String sql_user = "SELECT * FROM user";
        boolean valid=false;
        try {
            rs = stmt.executeQuery(sql_user);
        while(rs.next())
            {
                String id_user = rs.getString("username");
                String pass_user = rs.getString("password");
                if(string[0].equalsIgnoreCase(id_user)){
                    valid=true;
                return pass_user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }*/
    private String[] bongkar(String paket) {
        String[] terbongkar = new String[5];
        if (paket != null) {
            terbongkar = paket.split("~#");
        }
        return terbongkar;
    }

    public String[] pisah(String string) {
        String[] hasil = new String[50];

        if (string != null) {
            char[] input = string.toCharArray();
            int wordStart = 0, wordEnd = 0, i = 0;
            while (wordStart < input.length) {
                wordEnd = wordStart;
                while (input[wordEnd] != '|') {
                    wordEnd++;
                    if (wordEnd == input.length) {
                        break;
                    }
                }
                hasil[i] = string.substring(wordStart, wordEnd);
                wordStart = wordEnd + 1;
                i++;
            }
            i = 0;
            while (i < hasil.length) {
                //System.out.print(hasil[i]+"\n");
                i++;
            }
        } else {
        }
        return hasil;
    }

    public String bungkus(String message, int type) {
        String result = null;
        String head = null, tail = null;
        switch (type) {
            case 3:
                head = "0210~#030500~#";
                break;
            case 5:
                head = "0210~#030600~#";
                break;
            case 6:
                head = "0210~#030700~#";
                break;
        }

        try {
            InetAddress ip;

            ip = InetAddress.getLocalHost();
            System.out.println("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            System.out.print("Current MAC address : ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
            }
            System.out.println(sb.toString());

            tail = "~#" + ip.getHostAddress() + "~#" + sb.toString() + "~#";

        } catch (UnknownHostException e) {

            e.printStackTrace();

        } catch (SocketException e) {

            e.printStackTrace();

        }
        if (head == null || tail == null) {
            result = null;
        } else {
            result = head + message + tail;
        }

        return result;
    }

    public void log(String[] pkt, int type) throws IOException, SQLException {
        java.util.Date date = new java.util.Date();
        long time = date.getTime();

        try (FileWriter fw = new FileWriter("log.txt", true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.newLine();
            bw.write("\nTime : " + date.toString());

            switch (pkt[0]) {
                case "0200":
                    bw.write(" Request");
                    break;
                case "0210":
                    bw.write(" Response");
                    break;
            }

            switch (pkt[1]) {
                case "030500":
                    bw.write(" QUERY");
                    break;
                case "030600":
                    bw.write(" PRINT");
                    break;
                case "030700":
                    bw.write(" DATABASE ERROR");
                    break;               
            }

            if (pkt[2].contentEquals("true")) {
                bw.write(" Status : Berhasil");
            } else if (pkt[2].contentEquals("false")) {
                bw.write(" Status : Gagal");
            }

            switch (type) {
                case 1: {
                    bw.write(" TO IP : 192.168.43.171");
                    bw.write(" TO MAC : 14:4F:8A:F4:32:F1");
                    bw.write(" Port : 6788");
                    bw.write(" Data  : " + pkt[2]);
                    if (pkt[2].equalsIgnoreCase("Alert|Data tidak ditemukan|0")) {
                        bw.write(" Status pencarian : Gagal ");
                    } else if (pkt[2].equalsIgnoreCase("Alert|Data Sudah Dicetak|0")) {
                        bw.write(" Status : Data Sudah Dicetak ");
                    } else if (pkt[2].equalsIgnoreCase("Alert|Data Akan Dicetak|1")) {
                        bw.write(" Status : Data Akan Dicetak ");
                    } else if (pkt[2].equalsIgnoreCase("Alert|SERVER JDBC LOST CONNECTION|0")) {
                        bw.write(" Status : JDBC LOST CONNECTION ");
                    } else {
                        bw.write(" Status : Berhasil");
                    }
                    bw.newLine();

                    System.out.println("Time : " + date.toString());
                    System.out.println("IP  : 192.168.43.171");
                    System.out.println("MAC : 14:4F:8A:F4:32:F1");
                    System.out.println("Port : 6788");
                }
                break;
                case 2: {
                    bw.write(" FROM IP : " + pkt[3]);
                    bw.write(" FROM MAC : " + pkt[4]);
                    bw.write(" Port : 6789");
                    bw.write(" Data : " + pkt[2]);
                    bw.newLine();

                    System.out.println("Time : " + date.toString());
                    System.out.println("IP : " + pkt[3]);
                    System.out.println("MAC : " + pkt[4]);
                    System.out.println("Port : 6789");
                }
                break;
            }
        }
    }

}

public class STNK_Server {

    /**
     * @param args the command line arguments
     */
    int PORT = 6789;

    public static void main(String[] args) {
        // TODO code application logic here
        STNK_Server my = new STNK_Server();
        my.TCP_go();
    }

    public void TCP_go() {
        try {
            ServerSocket sc = new ServerSocket(PORT);
            while (true) {
                Socket Connection = sc.accept();
                myThread_Server handler = new myThread_Server(Connection);
                //sc.setSoTimeout(10000);
                handler.start();
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }

    }

}
