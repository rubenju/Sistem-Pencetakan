/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 *
 * @author LATITUDE
 */
public class Cetak extends Thread {

    private String[] dt;

    public Cetak() {

    }

    public void cetak(String[] vdt) {
        this.dt = vdt;
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(new BillPrintable(), getPageFormat(pj));
        try {
            pj.print();

        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }

    public PageFormat getPageFormat(PrinterJob pj) {

        PageFormat pf = pj.defaultPage();
        Paper paper = pf.getPaper();
   
        double middleHeight = 8.0;
        double headerHeight = 1.0;
        double footerHeight = 1.0;
        double width = convert_CM_To_PPI(10);      //printer know only point per inch.default value is 72ppi
        double height = convert_CM_To_PPI(24);
        paper.setSize(width, height);
        paper.setImageableArea(
                0,
                0,
                width,
                height - convert_CM_To_PPI(1)
        );   //define boarder size    after that print area width is about 180 points

        pf.setOrientation(PageFormat.LANDSCAPE);           //select orientation portrait or landscape but for this time portrait
        pf.setPaper(paper);

        return pf;
    }

    protected static double convert_CM_To_PPI(double cm) {
        return toPPI(cm * 0.393600787);
    }

    protected static double toPPI(double inch) {
        return inch * 72d;
    }

    public class BillPrintable implements Printable {

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
                throws PrinterException {
            int result = NO_SUCH_PAGE;
            if (pageIndex == 0) {
                Graphics2D g2d = (Graphics2D) graphics;
                double width = pageFormat.getImageableWidth();
                g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

                try {
                    /*Draw Header*/
                    int x = 2;
                    int y = 20;
                    int y1 = 10;
                    int headerRectHeight = 10;
                    int headerRectHeighta = 40;

                    /*for (int i = 0; i < dt.length; i++) {
                        if (i==1) {
                            g2d.setFont(new Font("Courier New", Font.BOLD, 9));
                        } else if (i==2||i==3) {
                            g2d.setFont(new Font("Courier New", Font.BOLD, 8));
                        } else if ((dt.length - i) <= 2) {
                            g2d.setFont(new Font("Courier New", Font.BOLD, 8));
                        } else {
                            g2d.setFont(new Font("Courier New", Font.PLAIN, 9));
                        }
                        if (dt[i].equalsIgnoreCase("[space]")) {
                            y += 10;
                            i += 1;
                        }
                        g2d.drawString(dt[i], x, y);
                        y += y1;

                    }*/
                    g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
                    
                    for (int i = 0; i <42; i++){
                        //sentence = getFormat(i);
                        switch(i){
                            case 0 : x = 20; y = 15; break;
                            case 1 : x = 0; y = 36;   break;
                            case 2 : x = 0; y = 48;   break;
                            case 3 : x = 0; y = 24;   break;
                            case 4 : x = 375; y = 12;   break;
                            case 5 : x = 375; y = 36;   break;
                            case 6 : x = 210; y = 144;   break;
                            case 7 : x = 20; y = 180;   break;
                            case 8 : x = 300; y = 144;   break;
                            case 9 : x = 375; y = 144;   break;
                            case 10 : x = 15; y = 72;   break;
                            case 11 : x = 15; y = 84;   break;
                            case 12 : x = 15; y = 96;   break;
                            case 13 : x = 15; y = 108;   break;
                            case 14 : x = 15; y = 120;   break;
                            case 15 : x = 15; y = 132;   break;
                            case 16 : x = 15; y = 144; break;
                            case 17 : x = 15; y = 156;   break;
                            case 18 : x = 170; y = 96;   break;
                            case 19 : x = 170; y = 108;   break;
                            case 20 : x = 170; y = 120;   break;
                            case 21 : x = 170; y = 132;   break;
                            case 22 : x = 170; y = 144;   break;
                            case 23 : x = 170; y = 156;   break;
                            case 24 : x = 230; y = 60;   break;
                            case 25 : x = 230; y = 72;   break;
                            case 26 : x = 230; y = 84;   break;
                            case 27 : x = 230; y = 96;   break;
                            case 28 : x = 230; y = 108;   break;
                            case 29 : x = 230; y = 120;   break;
                            case 30 : x = 305; y = 60;   break;
                            case 31 : x = 305; y = 72;   break;
                            case 32 : x = 305; y = 84;   break;
                            case 33 : x = 305; y = 96;   break;
                            case 34 : x = 305; y = 108;   break;
                            case 35 : x = 305; y = 120;   break;
                            
                            case 36 : x = 380; y = 60;   break;
                            case 37 : x = 380; y = 72;   break;
                            case 38 : x = 380; y = 84;   break;
                            case 39 : x = 380; y = 96;   break;
                            case 40 : x = 380; y = 108;   break;
                            case 41 : x = 380; y = 120;   break;
                           }
                        g2d.drawString(dt[i], x, y);
                    }

//            g2d.setFont(new Font("Monospaced",Font.BOLD,10));
//            g2d.drawString("Customer Shopping Invoice", 30,y);y+=yShift; 
                } catch (Exception r) {
                    r.printStackTrace();
                }

                result = PAGE_EXISTS;
            }
            return result;
        }
        
        
    }

}
