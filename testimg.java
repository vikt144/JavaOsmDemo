
import java.awt.*;//BorderLayout;Color;Dimension;EventQueue;
                 //Graphics;awt.Image;Toolkit;
import java.awt.event.*;
import javax.swing.*; // JFrame;JPanel;UIManager;UnsupportedLookAndFeelException;

import java.net.*;
import java.io.*;

import javax.imageio.*;

//import pub.*;

public class testimg {

  private JButton btn0;
  private JButton btn1;

 Geolib Geo;
 
 static    float lon , lat ; static int zoom;
 static    int xtile , ytile;
 static Image[] I;

 static   int[][] cord = new int [5][2];

    public static void main(String[] args) {
       lon = Float.parseFloat(args[1]) ; 
       lat = Float.parseFloat(args[0]) ;     
       zoom = Integer.parseInt(args[2]) ; 

      centrLat=lat ;  centrLon=lon;
 System.out.println(" "+ lat +" " + lon);
      tileArray=macroTileArray(   centrLat, centrLon,   zoom,    displaywidth  ,    displayheight);

     new testimg();
    }


    public testimg() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

//btn0= new JButton("Z+");  
//btn0= new JButton("Z-");
                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new TestPane());
//         frame.add(btn0);
//         frame.add(btn1);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);


 /*  
btn0.addActionListener(new ActionListener(){
  public void actionPerformed(ActionEvent e) {
     
//     updateDisp(" --->"); // на екран         
     
  }
});
 */


            }
        });
    }

static String cacheString = null;

static void loadCacheIndex() {
  File fl = new File("cache");
  if ( fl.exists() )
     if ( fl.isDirectory() ) {
           File ind = new File("cache/ind") ;
           if (ind.exists() ) 
               cacheString =   netWork. readText("cache/ind"); 
           else  {netWork. writeText("cache/ind"," ");   
                  cacheString = " ";
                 } 
              }       
        else ; // not dir
     else { fl.mkdir();
            netWork. writeText("cache/ind"," ");
           cacheString = " ";
          }
   }//void

public  static  Image macroGetImage(int x, int y,int zoom)  {
  String ak = ".png?apikey=02b408df1d3643e5b8002dde04810f52";
  Image I = null;
  if (cacheString==null) loadCacheIndex();
 
   if (cacheString.contains("z"+zoom+"x"+x+"y"+y)  )
          I = getCacheImage(x,y,zoom);
     else {
           // I = getImageUrl("http://tile.thunderforest.com/cycle/"+zoom+"/"+x+"/"+y+ak); 
          byte[] B = netWork. netGetBytes2("http://tile.thunderforest.com/cycle/"+zoom+"/"+x+"/"+y+ak  , -1 );
          String zxy = "z"+zoom+"x"+x+"y"+y;
          netWork. saveBinFile("cache/"+zxy+".png" , B);
          I = getImageFile("cache/"+zxy+".png");
           String s =  cacheString+zxy+'\n';
          cacheString=null;cacheString=s;
          netWork. writeText("cache/ind", cacheString);       
         }
  return I;
}

public  static  Image getCacheImage(int x,int y,int zoom) {
   Image I = null; 
   String zxy = "z"+zoom+"x"+x+"y"+y;
   I = getImageFile("cache/"+zxy+ ".png");
    System.out.println("load from cache " + zxy+"\n");
 return I;

}
public  static  Image[][] macroTileArray( double centrLat, double centrLon, int zoom, int  displaywidth  ,  int  displayheight) {
     String ak = ".png?apikey=02b408df1d3643e5b8002dde04810f52";
     Image[][] I = null; 
     PixelSizeMetres = Geolib.calcMetrsPix(centrLat, zoom);
     dispCorner = Geolib.calcDispCorner(centrLat,centrLon,PixelSizeMetres, displaywidth , displayheight);
     int[] ii =   Geolib.getTileNumber(dispCorner[0],dispCorner[1],zoom);
     float[] tc = Geolib.getTileCoordinats(ii[0],ii[1],zoom);
     dxdyTileCoordinat = Geolib.calcDxMetres( dispCorner[0],dispCorner[1],tc[0],tc[1] );
 //  System.out.println("PixelSizeMetres,"+ dispCorner[0]);
     dxdyPix[0]=-(int) (dxdyTileCoordinat[0]/PixelSizeMetres);
     dxdyPix[1]=(int) (dxdyTileCoordinat[1]/PixelSizeMetres); 
//     int xx = (dxdyPix[0] + displaywidth)/imggetWidth +2;
//     int yy = (dxdyPix[1] + displayheight)/imggetHeight +2;
     int xx = ( displaywidth+Math.abs(dxdyPix[0] ) )/imggetWidth +2;
     int yy = (displayheight+Math.abs(dxdyPix[1] ) )/imggetHeight +2; 

  System.out.println("image[][] xx= "+ xx +"  yy="+yy); 
     I = new Image[xx][yy];

     int[] jj = new int [2];
 jj = ii.clone();  //         jj[0]=ii[0]; jj[1]=ii[1];

//int l=0; //////////!!!
     for (int l=0;l<yy;l++) {   
        for (int k=0;k<xx;k++) {
//            I[k][l] = getImageUrl("http://tile.thunderforest.com/cycle/"+zoom+"/"+ii[0]+"/"+ii[1]+ak);  
            I[k][l] = macroGetImage(ii[0],ii[1],zoom);
            ii = Geolib.calcTile(ii[0],ii[1],zoom,'r');             
           }//for
     jj=Geolib.calcTile(jj[0],jj[1],zoom,'d');
     ii=jj;
    }//for
   return I;
  }


///////////////////////////////////////////////////////////////////

 static  int  displaywidth =800 ,   displayheight =600;  ///displaySize

 public static   double PixelSizeMetres;

 static  Image[][] tileArray;

 static  double [] dispCorner;// = new float[4]; //координаты углов дисплея пр верх лев нижн

 static   double centrLat, centrLon;  //центр

 static   int imggetWidth=255, imggetHeight=255;  //размер изображений

 static  double[] dxdyTileCoordinat;
 static   int[] dxdyPix = new int[2];

   int ifInDisplay( double X , double Y, float[] dispCoor) {
       int ret = -1000;
       if ( X >=dispCoor[0] && X < dispCoor[2] && Y <=dispCoor[1] &&  Y >dispCoor[3] ) ret= 0; 
 
      return ret;
    }  


//////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\  


///////////////////////////////geo/////////////////////////////////////////// from https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
 

  public static int pw(int x, int y) { // x в степени y
  return  x * (1<<(y-1) );
  }  





     public static Image getImageFile(String filename) {
        Image image = null;
        try {
        image = ImageIO.read(new File(filename));
        }  catch (Exception e) {} 
     return image;  
    }  
  
     public static Image getImageUrl(String url) {
        Image image = null;
        try {
        image = ImageIO.read(new URL(url));
        }  catch (Exception e) {} 
     return image;  
    }      
 



  public class TestPane extends JPanel {


       @Override
        public Dimension getPreferredSize() {
            return new Dimension(displaywidth,displayheight);//800, 600);  //window size
        }

        @Override
        protected void paintComponent(Graphics g) {
        
            super.paintComponent(g);
         int  displaywidth = getWidth();
         int  displayheight = getHeight();

            g.setColor(Color.GREEN);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.BLACK);

    for (int l=0;l< tileArray[0].length;l++) 
       for (int k=0;k< tileArray.length;k++) g.drawImage(tileArray[k][l], dxdyPix[0]+256*k, dxdyPix[1]+256*l,this);

    int cx = displaywidth/2;
    int cy = displayheight/2;
    g.fillOval(cx, cy, 10, 10);     

  g.drawString("leftup- "+dispCorner[3] + " " + dispCorner[2],10,60);
  g.drawString("cent- "+centrLat + " " +  centrLon,30,100);
   g.drawString("rghdwn- "+dispCorner[1] + " " + dispCorner[0],10,150);
     }
    }
}//all


