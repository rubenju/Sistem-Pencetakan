/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gateway_stnk;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

class myThread_Gateway extends Thread {

    Socket soc;
    DataOutputStream Output;
    BufferedReader Input;
    static Connection con;

    public myThread_Gateway(Socket soc) throws IOException, ClassNotFoundException {
        this.soc = soc;
        Output = new DataOutputStream(this.soc.getOutputStream());
        Input = new BufferedReader(new InputStreamReader(this.soc.getInputStream()));

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/otentikasi", "root", "");
            System.out.println(con);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("DATABASE GATEWAY LOST CONNECTION");
            String pesan = bungkus("Alert|Database Gateway Lost Connection!|1", 6);
            Output.writeBytes(pesan + '\n');
            String[] msg = pesan.split("~#");
            log(msg, msg, 1);
        }
    }

    @Override
    public void run() {
        String sentence;
        String[] send;
        String[] send2;
        String[] receive;
        String[] msg_toClient = new String[50];
        String pesan = null;

        boolean connected = true;
        do {
            try {
                boolean check = false;

                sentence = Input.readLine();
                System.out.println("\nRECEIVE FROM CLIENT :");

                //kode dari client
                send = sentence.split("~#");
                String kode1 = send[0];
                String kode2 = send[1];
                String message = send[2];
                String IP = send[3];
                String MAC = send[4];

                log(send, send, 2);

                System.out.println("Packet : " + sentence);

                try {
                    check = Checking(kode1, kode2, IP, MAC);
                    if (check == true) {
                        int type = 0;

                        switch (kode2) {
                            case "030200":
                                if (login_user(pisah(message))) {
                                    msg_toClient[0] = "Alert|Log In Berhasil!|1";
                                } else {
                                    msg_toClient[0] = "Alert|Log In Gagal!|0";
                                }
                                type = 1;
                                break;
                            case "030400":
                                if (newUser(pisah(message))) {
                                    msg_toClient[0] = "Alert|Sign Up Berhasil!|1";
                                } else {
                                    msg_toClient[0] = "Alert|Sign Up Gagal!|0";
                                }
                                type = 2;
                                break;
                            case "030500":
                                System.out.println("\nSEND TO SERVER");

                                pesan = ClientServer(sentence);
                                receive = pesan.split("~#");

                                log(receive, send, 1);
                                System.out.println("Packet : " + sentence);

                                System.out.println("\nRECEIVE FROM SERVER :");
                                log(receive, receive, 2);
                                System.out.println("Packet : " + pesan);
                                type = 3;
                                break;
                            case "030300":
                                msg_toClient[0] = "Alert|Logged Out|1";
                                type = 4;
                                break;
                            case "030600":
                                System.out.println("\nSEND TO SERVER");

                                pesan = ClientServer(sentence);
                                receive = pesan.split("~#");

                                log(receive, send, 1);
                                System.out.println("Packet : " + sentence);

                                System.out.println("\nRECEIVE FROM SERVER :");
                                log(receive, receive, 2);
                                System.out.println("Packet : " + pesan);
                                type = 5;
                                break;
                            default:
                                connected = false;
                        }

                        if (type != 3 && type != 5) {
                            pesan = bungkus(msg_toClient[0], type);
                        }

                        //Send to Client                        
                        System.out.println("\nSEND TO CLIENT");
                        Output.writeBytes(pesan + '\n');//Mengirim ke client                            
                        send2 = pesan.split("~#");
                        log(send, send2, 1);
                        System.out.println("Packet : " + pesan);

                    } else {
                        System.out.println("\nSEND TO CLIENT");
                        String salah = kode1 + "~#" + kode2 + "~#Maaf alamat IP dan MAC anda tidak terdaftar~#" + IP + "~#" + MAC;
                        Output.writeBytes(salah + '\n');
                        send2 = salah.split("~#");
                    }
                } catch (ClassNotFoundException | IOException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    try {
                        Logger.getLogger(myThread_Gateway.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("JDBC LOST CONNECTION");
                        pesan = bungkus("Alert|Gateway JDBC Lost Connection!|1", 6);
                        Output.writeBytes(pesan + '\n');
                        String[] msg = pesan.split("~#");
                        log(msg, msg, 1);
                    } catch (IOException ex1) {
                        Logger.getLogger(myThread_Gateway.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Gateway_STNK.class.getName()).log(Level.SEVERE, null, ex);
                connected = false;
            }
        } while (connected);
    }

    //query to Database
    public boolean login_user(String[] message) {
        boolean check = message[1].equals(getPassword(message));
        return check;
    }

    static boolean newUser(String[] message) {
        boolean valid = false;
        try {
            Statement stmt = con.createStatement();
            if (getPassword(message) == null) {
                String sql = "INSERT INTO user (username,password) VALUE('%s','%s')";
                sql = String.format(sql, message[0], message[1]);
                System.out.println("data berhasil Disimpan");

                stmt.execute(sql);
                return valid = true;
            } else {
                System.out.println("data sudah ada");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valid;
    }

    static String getPassword(String[] string) {
        String sql_user = "SELECT * FROM user";
        boolean valid = false;
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql_user);
            while (rs.next()) {
                String id_user = rs.getString("username");
                String pass_user = rs.getString("password");
                if (string[0].equalsIgnoreCase(id_user)) {
                    valid = true;
                    return pass_user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //keperluan untuk kirim pesan
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
            case 1:
                head = "0210~#030200~#";
                break;
            case 2:
                head = "0210~#030400~#";
                break;
            case 3:
                head = "0210~#030500~#";
                break;
            case 4:
                head = "0210~#030300~#";
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

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
            }

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

    public void log(String[] pkt, String[] pkt2, int type) throws IOException {
        Date date = new Date();
        long time = date.getTime();

        try (FileWriter fw = new FileWriter("log.txt", true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.newLine();
            bw.write("\nTime : " + date.toString());

            /*String aksi = null;
            Statement stm = con.createStatement();
            String a = "SELECT aksi FROM kode_1 WHERE kode='" + pkt2[0] + "'";
            ResultSet rs = stm.executeQuery(a);
            while (rs.next()) {
                aksi = rs.getString("aksi");
            }
            String b = "SELECT aksi FROM kode_2 WHERE kode='" + pkt2[1] + "'";
            ResultSet rs2 = stm.executeQuery(b);
            while (rs2.next()) {
                aksi = " " + aksi + " " + rs2.getString("aksi");
            }
            bw.write(aksi);*/
            switch (pkt2[0]) {
                case "0200":
                    bw.write(" Request");
                    break;
                case "0210":
                    bw.write(" Response");
                    break;
            }

            switch (pkt2[1]) {
                case "030200":
                    bw.write(" LOGIN");
                    break;
                case "030400":
                    bw.write(" SIGNUP");
                    break;
                case "030500":
                    bw.write(" QUERY");
                    break;
                case "030300":
                    bw.write(" LOGOUT");
                    break;
                case "030600":
                    bw.write(" PRINT");
                    break;
                case "030700":
                    bw.write(" ERROR");
                    break;
            }

            switch (type) {
                case 1: {
                    if (pkt2[2].contentEquals("Alert|Log In Berhasil!|1")) {
                        bw.write(" Status : Berhasil");
                    } else if (pkt2[2].contentEquals("Alert|Log In Gagal!|0")) {
                        bw.write(" Status : Gagal");
                    } else if (pkt2[2].contentEquals("Alert|Sign Up Berhasil!|1")) {
                        bw.write(" Status : Berhasil");
                    } else if (pkt2[2].contentEquals("Alert|Sign Up Gagal!|0")) {
                        bw.write(" Status : Gagal");
                    } else if (pkt2[2].contentEquals("Alert|Server Timeout!")) {
                        bw.write(" Status : Server Timeout");
                    } else if (pkt2[2].contentEquals("Alert|Gateway JDBC Lost Connection!|1")) {
                        bw.write(" Status : Gateway JDBC Lost Connection");
                    }
                    bw.write(" TO IP : " + pkt[3]);
                    bw.write(" TO MAC : " + pkt[4]);
                    bw.write(" Port : 6788");
                    bw.write(" Data : " + pkt2[2]);
                    bw.newLine();

                    System.out.println("Time : " + date.toString());
                    System.out.println("IP : " + pkt[3]);
                    System.out.println("MAC : " + pkt[4]);
                    System.out.println("Port : 6788");
                }
                break;
                case 2: {
                    bw.write(" FROM IP : " + pkt[3]);
                    bw.write(" FROM MAC : " + pkt[4]);
                    bw.write(" Port : 6789");
                    bw.write(" Data : " + pkt2[2]);
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

    //sending
    public String ClientServer(String pesan) throws IOException, SQLException {
        Socket client = new Socket("127.0.0.1", 6789);        
        //Socket client = new Socket("192.168.43.23", 6789);
        String isi = null;

        int count = 0;
        boolean replied = false;
        do {
            try {
                client.setSoTimeout(3000);
                DataOutputStream outtoserver = new DataOutputStream(client.getOutputStream());
                BufferedReader infromserver = new BufferedReader(new InputStreamReader(client.getInputStream()));
                outtoserver.writeBytes(pesan + '\n'); //Mengirim ke server  
                isi = infromserver.readLine(); //Menerima dari server 
                replied = true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("CONNECTION TIMEOUT : " + (count + 1));
                replied = false;
                count++;
            }
        } while (!replied && count < 2);

        if (count == 2) {
            client.close();
            isi = bungkus("Alert|Server Timeout!", 6);
            String[] msg = pesan.split("~#");
            log(msg, msg, 1);
        }
        return isi;
    }

    //otentikasi
    public boolean cekkode1(String kode) throws ClassNotFoundException, SQLException {
        boolean kode1cek = true;
        Statement stm = con.createStatement();
        String a = "SELECT COUNT(*) FROM kode_1 WHERE kode='" + kode + "'";
        ResultSet rs = stm.executeQuery(a);
        while (rs.next()) {
            int count = rs.getInt(1);
            if (count == 0) {
                kode1cek = false;
            }
        }
        return kode1cek;
    }

    public boolean cekkode2(String kode2) throws ClassNotFoundException, SQLException {
        boolean kode2cek = true;
        Statement stm = con.createStatement();
        String a = "SELECT COUNT(*) FROM kode_2 WHERE kode='" + kode2 + "'";
        ResultSet rs = stm.executeQuery(a);
        while (rs.next()) {
            int count = rs.getInt(1);
            if (count == 0) {
                kode2cek = false;
            } else {
                kode2cek = true;
            }
        }
        return kode2cek;
    }

    public boolean cekdevice(String ip, String mac) throws ClassNotFoundException, SQLException {
        boolean devicecek = true;
        Statement stm = con.createStatement();
        String a = "SELECT COUNT(*) FROM device WHERE IP='" + ip + "' AND MAC='" + mac + "'";
        ResultSet rs = stm.executeQuery(a);
        while (rs.next()) {
            int count = rs.getInt(1);
            if (count == 0) {
                devicecek = false;
            }
        }
        return devicecek;
    }

    public boolean Checking(String code, String code2, String IP, String MAC) throws ClassNotFoundException, SQLException {
        boolean checking = false;

        boolean kode = cekkode1(code);
        boolean action = cekkode2(code2);
        boolean device = cekdevice(IP, MAC);
        if (kode == false) {
            checking = false;
        } else if (kode == true) {
            if (action == false) {
                checking = false;
            } else if (action == true) {
                if (device == false) {
                    checking = false;
                } else if (device == true) {
                    checking = true;
                }
            }
        }

        return checking;
    }
}

/**
 *
 * @author Elvansen
 */
public class Gateway_STNK {

    /**
     * @param args the command line arguments
     */
    int PORT = 6788;

    public static void main(String[] args) {
        // TODO code application logic here
        Gateway_STNK my = new Gateway_STNK();
        my.go();
    }

    public void go() {
        try {
            ServerSocket sc = new ServerSocket(PORT);
            while (true) {
                Socket Connection = sc.accept();
                System.out.println("Connection Accept");
                myThread_Gateway handler = new myThread_Gateway(Connection);
                //sc.setSoTimeout(10000);
                handler.start();
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
