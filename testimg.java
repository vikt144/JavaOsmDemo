
import java.awt.*;//BorderLayout;Color;Dimension;EventQueue;
                 //Graphics;awt.Image;Toolkit;
import javax.swing.*; // JFrame;JPanel;UIManager;UnsupportedLookAndFeelException;

import java.net.*;
import java.io.*;

import javax.imageio.*;

public class testimg {

 
 
 static    float lon , lat ; static int zoom;
 static    int xtile , ytile;
 static Image[] I;

 static   int[][] cord = new int [5][2];

    public static void main(String[] args) {
       lon = Float.parseFloat(args[1]) ; 
       lat = Float.parseFloat(args[0]) ;     
       zoom = Integer.parseInt(args[2]) ; 

      centrLat=lat ;  centrLon=lon;

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

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
  

public  static  Image[][] macroTileArray( double centrLat, double centrLon, int zoom, int  displaywidth  ,  int  displayheight) {
     String ak = ".png?apikey=02b408df1d3643e5b8002dde04810f52";
     Image[][] I = null; 
     PixelSizeMetres = calcMetrsPix(centrLat, zoom);
     dispCorner = calcDispCorner(centrLat,centrLon,PixelSizeMetres, displaywidth , displayheight);
     int[] ii = getTileNumber(dispCorner[0],dispCorner[1],zoom);
     float[] tc = getTileCoordinats(ii[0],ii[1],zoom);
     dxdyTileCoordinat = calcDxMetres( dispCorner[0],dispCorner[1],tc[0],tc[1] );
 //  System.out.println("PixelSizeMetres,"+ dispCorner[0]);
     dxdyPix[0]=-(int) (dxdyTileCoordinat[0]/PixelSizeMetres);
     dxdyPix[1]=(int) (dxdyTileCoordinat[1]/PixelSizeMetres); 
     int xx = (dxdyPix[0] + displaywidth)/imggetWidth +2;
     int yy = (dxdyPix[1] + displayheight)/imggetHeight +2;
     I = new Image[xx][yy];
     int[] jj = new int [2];
           jj[0]=ii[0]; jj[1]=ii[1];
     for (int l=0;l<yy;l++) {   
        for (int k=0;k<xx;k++) {
            I[k][l] = getImageUrl("http://tile.thunderforest.com/cycle/"+zoom+"/"+ii[0]+"/"+ii[1]+ak);  
            ii = calcTile(ii[0],ii[1],zoom,"r");             
           }//for
     jj=calcTile(jj[0],jj[1],zoom,"d");
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

  public static  double [] calcDxMetres( double  lat,double  lon,double  nlat,double  nlon) {  //~~~111!!!!!!!!!!!!!!!!!!!!!
    double [] ret = new double[2];
    double dxGr=nlat-lat;    //расст между широтами в гр (север юг
    double dxKm= dxGr*111.111f;  //111.111 это км в градусе 
    ret[0]= (dxKm*1000 ); // смещение в метрах

     double dyGr=nlon-lon;       //расст между долготами в гр  (запад восток
     double kLon=(float ) (40000*Math.cos(Math.toRadians(  lat))/360 );  //40000 дл окруж земли  //Math.toDegrees()    
     double dyKm=dyGr*kLon; 
     ret[1] = (dyKm*1000 );              
  return ret;
  }
  static double[] calcDispCorner(double lat /*lon*/ , double lon /*lat*/ , double PixelSizeMetres, int dwidth, int dheight) {//коор центр дисплей разм
   double[] ret  = new double[4];
  // 111111.0f;  //111.111 это км в градусе по  широте
  float klat=111111.0f;
  float kLon=(float ) (40000*Math.cos( Math.toRadians(  lat) )/360 ) *1000;  //40000 дл окруж земли
  float dgh = (float )PixelSizeMetres*dheight/2/klat; // nG=w
  float dgw = (float )PixelSizeMetres*dwidth/2/kLon; 

  ret[0]= (float)(lat+dgh);
  ret[1]=(float)(lon-dgw);
  ret[2]=(float)(lat-dgh);
  ret[3]=(float)(lon+dgw);
  System.out.println("ii "+ ret[0] +" "+ret[1]);
return ret;
} 

//////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\  


///////////////////////////////geo/////////////////////////////////////////// from https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames

 public static int[] getTileNumber( double lat,  double lon,  int zoom) {
   int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) ) ;
   int ytile = (int)Math.floor((1-Math.log(Math.tan(Math.toRadians(lat))+1/Math.cos(Math.toRadians(lat)))/Math.PI)/2*(1<<zoom));
    if (xtile < 0)
     xtile=0;
    if (xtile >= (1<<zoom))
     xtile=((1<<zoom)-1);
    if (ytile < 0)
     ytile=0;
    if (ytile >= (1<<zoom))
     ytile=((1<<zoom)-1);
    
    int[] ret=new int[2];
    ret[0]=xtile;ret[1]=ytile;
    return ret; 
   }


 public static float[]  getTileCoordinats(int xtile, int ytile, int zoom) {
   float[] ret = new float[4];
   ret[0]=(float)tile2lat(ytile, zoom); //north
   ret[1]=(float)tile2lon(xtile, zoom);
   ret[2]=(float)tile2lat(ytile+1, zoom); //north
   ret[3]=(float)tile2lon(xtile+1, zoom);    
  return ret;
 }

  public static int pw(int x, int y) { // x в степени y
  return  x * (1<<(y-1) );
  }  

  public static int[] calcTile(int x, int y, int z, String s) { // left right up down -- l r u d
    int [] r = new int[3];
    int tx=x;
    if  (s=="l") {tx=x-1; 
               if (tx<0) tx=pw(2,z-1);
    }  
    if  (s=="r") {tx=x+1; 
               if (tx==pw(2,z) ) tx=0;
   }

    int ty=y;
    if  (s=="d") {ty=y+1;
            if (ty==pw(2,z) ) ty=0;} 
   if  (s=="u") {ty=ty-1; 
           if (ty<0) ty=pw(2,z-1);}
   r[0]=tx;
   r[1]=ty;
   r[2]=z; 
  return r;
 }

    static  double calcMetrsPix(double lat,int zoom) {
       return (156543.03*Math.cos(Math.toRadians(lat)))/Math.pow(2,zoom);///!!!! 2 * (1<<(zoom-1) better
    }

  class BoundingBox {
    double north;
    double south;
    double east;
    double west;   
  }
  BoundingBox tile2boundingBox(final int x, final int y, final int zoom) {
    BoundingBox bb = new BoundingBox();
    bb.north = tile2lat(y, zoom);
    bb.south = tile2lat(y + 1, zoom);
    bb.west   = tile2lon(x, zoom);
    bb.east = tile2lon(x + 1, zoom);
    return bb;
  }


  static double tile2lon(int x, int z) {
     return x / Math.pow(2.0, z) * 360.0 - 180;
  }

  static double tile2lat(int y, int z) {
    double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z); // 2 * (1<<(zoom-1) 
    return Math.toDegrees(Math.atan(Math.sinh(n)));
  }

/////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\



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
            g.fillOval(100, 100, 30, 30);
           g.drawString("This is gona be awesome "+Math.pow(2.0,10.0)+ "anoth "+  2 * (1<<9)  , 200, 200);
 

    g.drawString("" + dxdyPix[0] + " " + dxdyPix[1] + "x= " + tileArray.length + "y= " +tileArray[0].length , 0,580);

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


