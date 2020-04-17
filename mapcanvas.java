package  navi;

 import CON.*; // import forth.*;  import myutils.*;
import javax.microedition.lcdui.*; import javax.microedition.midlet.*; 
import javax.microedition.rms.*;

import java.util.*;

import com.emath.*;

public class mapcanvas extends Canvas {

  private MIDlet midlet;
  public mapcanvas(MIDlet midlet) {this.midlet=midlet;}

    void delay(int i) {
     try {Thread.currentThread().sleep(i) ; }
      catch (InterruptedException  e) {};
     }

 
      int displaywidth = 0; 
      int displayheight = 0; 

      static double  PixelSizeMetres=10;

public void set_Point(Graphics g, int x,int y) { // курсор paint
  g.setColor(255,255,255);
  g.drawLine(x,y-5,x,y+5);
  g.drawLine(x-5,y,x+5,y);
  g.setColor(0,0,0);
  g.drawLine(x+1,y-5,x+1,y+5);
  g.drawLine(x,y+1,x+5,y+1);
} //end void

Graphics canvas;

 
 
   public void setpix(int x , int y, int c, Graphics g) {
 //     p.setColor(c);
 //     p.setStrokeWidth(s);
 //     canvas.drawPoint(x, y, p);
   } 

 public void setObjectOnScreen(int x, int y, int sizee, int color, int type, String name, int [] num) {
   canvas.setColor(color);
   canvas.fillArc( x,  y, sizee, sizee, 0 , 360 ) ;
   if (name !=null) canvas.drawString(name,x+sizee+1,y,0);
 } 

////////////////////////////////////////////////////////////waypoint////////////////////////////////
public   class WayPoint  {
      public float lat;
    public   float lon;
    public int color;
    public String name;
 };  

 public WayPoint  setWP(/* WayPoint wp,*/ double la,double  lo, int c,String s) {
   WayPoint wp = new WayPoint();   
   wp.lat=(float)la;wp.lon=(float)lo;wp.color=c;wp.name=s;  
   return wp;
 }  

 public WayPoint  setWPstring(String la, String  lo, int c,String s) {
    WayPoint wp = new WayPoint();
    try {
    double lat = Double.parseDouble(la);
    double lon = Double.parseDouble(lo);  
    wp=setWP(lat,lon,c,s);
    }  
    catch (NumberFormatException ne) { wp=null;}    
    return wp;
 }

// public WayPoint w0 = setWP( 55.75248, 37.62749  ,0x00666666,"апхуй0");// new WayPoint(); 
// WayPoint w1 = setWP(  55.75170 , 037.63006,0x00666666,"апхуй1"); 
// WayPoint w2 = setWP( 55.75166 , 037.62766,0x00666666,"апхуй3");
 

public Vector WP = new Vector();

public void setWayPoint(WayPoint wp) {
  int size=4;
  int[] crd=calcDisplayCoordinat(dispCorner[0],dispCorner[1], wp.lat,wp.lon, PixelSizeMetres); 
  setObjectOnScreen(crd[0],crd[1],size ,wp.color,0,wp.name,null);
}
/////////////////////////////////////////////////////end  waypoint//////////////////////////////////////////////////////


/////////////////////////network 
boolean needMapView= false;

inetLoader httpl=new inetLoader();
Image[][] defaultImage= new Image[2][2]; int[][][] dimgXY = new int[2][2][2];

//////////////////////////////////


 /////////////////////////////////////    PAINT   /////////////////////////////////////////////  

protected void paint (Graphics g) {
  canvas=g;
  displaywidth = getWidth();
  displayheight = getHeight(); ///////////////////////////////////////отследить ситуацию если размеры дисплея меняются

 
  canvas.setColor(255,255,255);
  canvas.fillRect(0,0,getWidth(),getHeight() );//(int x, int y, int width, int height) 
  canvas.setColor(0,0,0);
 
//    dispCorner  = calcDispCorner(coordAr[array_ind][0],coordAr[array_ind][1],displaywidth,displayheight);  // if in inscreen???

// if (  WP != null ) for (int k=0;k<WP.length;k++) setWayPoint(WP[k]); 
  if (coordAr==null) {
      WayPoint wp = (WayPoint)WP.elementAt(0);
      centrLat=wp.lat; centrLon=wp.lon ;
     dispCorner  = calcDispCorner(wp.lat, wp.lon, getWidth(),getHeight() );
    }

 if (needMapView) {
   for (int l=1;l>=0;l--)
   for ( int k=(defaultImage[l].length-1);k>=0;k--)   {// Image ii=defaultImage[l][k] ;
          if (defaultImage[l][k]!=null) canvas.drawImage(defaultImage[l][k],dimgXY[l][k][0],dimgXY[l][k][1],0);
           else canvas.drawString( "loading",dimgXY[l][k][0],dimgXY[l][k][1],0);
	 };
/*
   if (defaultImage[1][1]!=null) canvas.drawImage(defaultImage[1][1],dimgXY[1][1][0],dimgXY[1][1][1],0);
     else canvas.drawString( "loading",dimgXY[1][1][0],dimgXY[1][1][1],0);
     
   if (defaultImage[1][0]!=null) canvas.drawImage(defaultImage[1][0],dimgXY[1][0][0],dimgXY[1][0][1],0);
     else canvas.drawString( "loading",dimgXY[1][0][0],dimgXY[1][0][1],0);
     
*/
  }//if needmap
  
  
   if (  WP != null ) 
     for (int k=0;k<WP.size();k++)  { setWayPoint(  (WayPoint)WP.elementAt(k) ); 
         WayPoint wp = (WayPoint)WP.elementAt(k);
         canvas.drawString( wp.name+"" , 3, k*15,0);  
	              } 
	  else canvas.drawString( "wp huynia ", 3,15,0);
  
  if (coordAr!=null && array_ind>=0) {
    //if ( updateDinLine==1)  обновить линию

//    dispCorner  = calcDispCorner(coordAr[array_ind][0],coordAr[array_ind][1],displaywidth,displayheight);
    
    dinamicTreckPaint( );

  // PixelSizeMetres

  canvas.drawString( ""+dispCorner[0],3 , 20 ,0);
  canvas.drawString( ""+dispCorner[1],2 , 35 ,0); 
  canvas.drawString( "X="+dimgXY[1][1][0]+" Y="+dimgXY[1][1][1],3 , 49 ,0);

/*  temp
  canvas.drawString( ""+dispCorner[0],3 , 20 ,0);
  canvas.drawString( ""+dispCorner[1],2 , 35 ,0); 
  canvas.drawString( "X="+dimgXY[1][1][0]+" Y="+dimgXY[1][1][1],3 , 49 ,0);
*/
canvas.drawString( ""+array_ind,3,90,0);
 }

 if  (debaG!=null) canvas.drawString( "->"+debaG,3 , 70 ,0);
 
  canvas.drawLine( 4, getHeight() -23, 14 , getHeight() -23 );
  canvas.drawString( " scale",20 ,getHeight() -30 ,0);
  canvas.drawString(PixelSizeMetres+" metr on pix",3 ,getHeight() -15 ,0);
  // setObjectOnScreen(100, 100, 10,  0x00666666, 0 , "Хуй", null);
// g.setClip(0, 0, dsizeX,dsizeY);
                    
// g.setClip(0, 0,  getWidth(),      getHeight());

// set_Point(g,posX,posY);

//g.drawString(load_string+pw(4,2),60,60,0);

}//endpaint 
//////////////////////////////////////////////////ENDPAINT
String debaG=null;


  
  float[] dispCorner = new float[4]; //координаты углов дисплея пр верх лев нижн

   double centrLat, centrLon;
 
  float [][] coordAr = new float [2000][2];  int array_ind=-1;
//  int currentX=0,currentY=0;

  void dinamicTreckPaint( ) { 
    if (array_ind==0) {   //случай array_ind<0  тестируется в ondraw
           int x = displaywidth/2;
           int y = displayheight/2;
//	   currentX=x; currentY=y;
           setObjectOnScreen(x, y, 10,  0x00FF00FF, 0 , "Хуй", null);
          }
	  else
	  
	  for (int k=array_ind;k>0;k--) {
	  // int[] crd=calcDxMetres(coordAr[k-1][0],coordAr[k-1][1],coordAr[k][0],coordAr[k][1] );
	  int[] crd=calcDisplayCoordinat(dispCorner[0],dispCorner[1], coordAr[k-1][0],coordAr[k-1][1], PixelSizeMetres);
   //       setObjectOnScreen(  crd[0]  , crd[1], 10 , 0x0099999, 0 , ""+(k-1), null);
	  int[] crd1=calcDisplayCoordinat(dispCorner[0],dispCorner[1], coordAr[k][0],coordAr[k][1], PixelSizeMetres);
 //         setObjectOnScreen(  crd1[0]  , crd1[1], 10 , 0x0099999, 0 , ""+k, null);

         canvas.setColor(0x00FF00FF);      ///    for android 
         canvas.drawLine( crd[0], crd[1] ,crd1[0] ,crd1[1]);
	   }//for
/*
    if (array_ind==1) { 
//        int ii=array_ind-2; 
//	 int iii = array_ind-1; 
         int[] crd=calcDxMetres(coordAr[0][0],coordAr[0][1],coordAr[1][0],coordAr[1][1] );
         int dx=-crd[0]/PixelSizeMetres;
	 int dy=crd[1]/PixelSizeMetres;

          dx= currentX-dx;dy=dy+currentY;
            setObjectOnScreen(dx, dy, 10,  0x00666666, 0 , "Хуй2", null);       

	 drawingLine(coordAr[0][0],coordAr[0][1],coordAr[1][0],coordAr[1][1] , 0x00660000, dispCorner);
         
crd=calcDisplayCoordinat(dispCorner[0],dispCorner[1], coordAr[1][0],coordAr[1][1], PixelSizeMetres);
setObjectOnScreen(  crd[0]  , crd[1], 10 , 0x0099999, 0 , "Хуй3", null);
//	 int dxx=currentX+dx;
//	 int dyy=currentY+dy;


      }//if
      */
  }//void


  int [] calcDisplayCoordinat(double LftUpLat ,double LftUpLon ,double pLat,double pLon,double MetresOnPix) {//левыйверхугол коорд точки метры на пикс
    int[] ret = new int[2];
    int[] crd=calcDxMetres( (float)LftUpLat , (float)LftUpLon ,(float) pLat, (float)pLon);

    ret[1]= /*displayheight*/(int)(-crd[0]/MetresOnPix); //y

    ret[0]=(int)(crd[1]/MetresOnPix);// по х
        
    //ret=    
    return ret;
  } 

 void drawingLine(float X,float Y, float XX,float YY , int col, float[] dispCoor) {

   int[] crd=calcDisplayCoordinat(dispCoor[0],dispCoor[1], X,Y,PixelSizeMetres);
 
   int[] crd2=calcDisplayCoordinat(dispCoor[0],dispCoor[1], XX,YY,PixelSizeMetres);

   canvas.setColor(col);      ///    for android 
   canvas.drawLine( crd[0], crd[1] ,crd2[0] ,crd2[1]);

 }//void

 final int max_ar=128;
 byte[] z_ar = new byte [max_ar]; 
 int [] x_ar = new int [max_ar]; int [] y_ar = new int [max_ar];   
 int [] rms_ar = new int [max_ar];
 byte [][] scache  = new byte [max_ar][];  // temp 

 
     
    
int pw(int x , int n) {int i=1; for (int k=1;k<=n;k++) i=i*x; return i;}// степень

int t0z, t0x, t0y;

int tls[][] = new int [4][3]; 

// public STACK St = new STACK();
// St.initStack(10);

int ifInDisplay( double X , double Y, float[] dispCoor) {
 int ret = -1000;
 if ( X >=dispCoor[0] && X < dispCoor[2] && Y <=dispCoor[1] &&  Y >dispCoor[3] ) ret= 0; 
 
 return ret;
}  

////////////////////////////////////////////////////////////РАЗИЕР ИЗОБРАЖЕНИЯ////////////////
int imggetWidth=255, imggetHeight=255; // стр 181 переписать автоматическит определять размеры
/////////////////////////////////////////////////////////////////////////////////////
 
double [][] fsrc ={ { 53.56677, 32.4555}, {53.545,32.45} , {53.565,32.45}  }; //{ { 53.56677, 32.4555}, {53.565,32.45}   };


/////////////////////////////////////////////////////////////////////////////////////////////////////math////////// 
//http://www.java2s.com/Code/Java/Data-Type/ClassforfloatpointcalculationsinJ2MEapplicationsCLDC.htm
 
  static public double log(double x) {   return Float11.log(x);   }

 static public double atan(double x) {
 
    return Float11.atan(x);
  }

static public double exp(double x) {
   
       return Float11.exp(x);
  }

// myaadd
 static double sinH(double x) {
   return (exp(x) - exp(-x) )/2 ;
 
 } 
///////////////////////////////////////////////math//////////////////////////////////////////////



 public static int[] getTileNumber( double lat,  double lon,  int zoom) {
   int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) ) ;
   int ytile = (int)Math.floor( (1 - /*Math.log*/log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom) ) ;
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
    return ret;//("" + zoom + "/" + xtile + "/" + ytile);
   }


 public static float[]  getTileCoordinats(int xtile, int ytile, int zoom) {
  float[] ret = new float[2];
  double  n=Float11.pow(2,zoom);//(1<<zoom); //2^zoom
  double lon_deg = xtile  / n * 360.0 - 180.0;
  double lat_rad = atan(sinH(Math.PI * (1 - 2 * ytile / n) )  );
  
  ret[0]=(float)(Math.toDegrees(lat_rad) ); 
  ret[1]=(float)lon_deg;
  return ret;
 }

/*
local n = 2 ^ z
    local lon_deg = x / n * 360.0 - 180.0
    local lat_rad = math.atan(math.sinh(math.pi * (1 - 2 * y / n)))
    local lat_deg = lat_rad * 180.0 / math.pi
*/

 void dodo() {

  
     array_ind++; 

     coordAr[array_ind][0]=(float)fsrc[array_ind][0];
     coordAr[array_ind][1]=(float)fsrc[array_ind][1];    

     if (array_ind == 0) {
         dispCorner=calcDispCorner(coordAr[0][0],coordAr[0][1],getWidth(),getHeight()  );//displaywidth,displayheight);
         centrLat=coordAr[0][0];
	 centrLon=coordAr[0][1];
        }
 	                       
	 
 
         
     if (   ifInDisplay(coordAr[array_ind][0] , coordAr[array_ind][1],dispCorner) !=0 ) {// удалить переменную updateDinLine=1;
                  dispCorner=calcDispCorner(coordAr[array_ind][0],coordAr[array_ind][1],displaywidth,displayheight);
                  centrLat=coordAr[array_ind][0];
	          centrLon=coordAr[array_ind][1];
                  
		 // if (httpl.buffer!=null) {defaultImage =Image.createImage(httpl.buffer,0,httpl.buffer.length); }
	   }
 }
 

/* https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
Exact length of the equator (according to wikipedia) is 40075.016686 km in WGS-84. At zoom 0, one pixel would equal 156543.03 meters 
(assuming a tile size of 256 px):

40075.016686 * 1000 / 256 ≈ 6378137.0 * 2 * pi / 256 ≈ 156543.03

resolution = 156543.03 meters/pixel * cos(latitude) / (2 ^ zoomlevel)

Some applications need to know a map scale, that is, how 1 cm on a screen translates to 1 cm of a map.

scale = 1 : (screen_dpi * 39.37 in/m * resolution)

*/

 
Vector cacheInd=null ;  Vector cacheTiles = new Vector();

void appendCache(int x, int y, int z , byte[] tl) {
 if (cacheInd == null) cacheInd = new Vector();
 int[] I = new int[3]; 
 I[0]=x; 
 I[1]=y;
 I[2]=z;
 cacheInd.addElement(I); 
 byte[] CC = new byte[tl.length];
 System.arraycopy( tl , 0, CC , 0, CC.length );
// byte[] CC = tl.clone();//new byte(tl);
 cacheTiles.addElement(CC);
}
// /*
byte[] findCache(int x, int y, int z) { 
  byte[] ret = null;

  if (  cacheInd != null) {
  
     //     debaG=null;debaG="fcache??";
  
   int k=0;
   boolean b=false;
   while (k<cacheInd.size() && b == false)  {
    int[] I = (int[])cacheInd.elementAt(k);
    if (I[0]==x && I[1]==y && I[2]==z) b=true;
      else k++;      
   }//while
  if (b) { 
    byte[] B = (byte[]) cacheTiles.elementAt(k); 
    ret = new byte[B.length];
    System.arraycopy( B , 0, ret , 0, B.length );   
    }
  }//if
 // */
return ret;  
}

  double calcMetrsPix(double lat,int zoom) {
    return (156543.03*Math.cos(Math.toRadians(lat)))/(Float11.pow(2,(zoom  )) );///!!!!
  }
 
public int[] calcTile(int x, int y, int z, String s) { // stack
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

 double dd1,dd2;
 
 int xx,yy,zz;
 long LastConnections;
 
 byte[] getImageFile(int x, int y ,int zoom) {
  byte[] ret=findCache( x, y, zoom);
  if (ret==null) 
      if (httpl.buffer!=null && httpl.Error==0 && httpl.buzy==0) {
                               try{
                  Image I =Image.createImage(httpl.buffer,0,httpl.buffer.length); //чтобы фальшивый img не записать в каш.
                  appendCache(xx,yy,zz,httpl.buffer);
                               httpl.buffer=null;
		  
		  } catch(Exception e){httpl.buffer=null;}
                            //   appendCache(xx,yy,zz,httpl.buffer);
                            //   httpl.buffer=null;
			       } else {
			       
			         if (httpl.buzy==0){
			         
			         long time=System.currentTimeMillis();
                                 if ( (time - LastConnections) >300 ) {
                                LastConnections=time; 
				xx=x;yy=y;zz=zoom;
                                httpl.test("http://tile.openstreetmap.org/"+zoom+"/"+x+"/"+y+".png");
                               }
			       }
 
                           }
debaG=null;debaG="rc "+httpl.Error;
  return ret;
 }

  void showimage(double lat,  double lon,  int zoom) {
   
       needMapView=true;
       int TileSize=256;
       PixelSizeMetres = calcMetrsPix(lat,zoom);	  
	  
       dispCorner=calcDispCorner(lat,lon, getWidth(),getHeight());//displaywidth,displayheight);
       centrLat=lat;
       centrLon=lon;

       int[] tn=getTileNumber( dispCorner[2], dispCorner[3] , zoom);//тайл, где правый нижний угол дисплея    
       float[] IC = getTileCoordinats(tn[0],tn[1],zoom);  
       dd1=IC[0];dd2=IC[1];
       int[] crd=calcDisplayCoordinat(dispCorner[0],dispCorner[1],IC[0],IC[1],PixelSizeMetres);

       dimgXY[1][1][0]= crd[0];dimgXY[1][1][1]= crd[1];			      

       byte[] B = getImageFile(tn[0],tn[1],zoom);
       
       try{
       if (B!= null) defaultImage[1][1] =Image.createImage(B,0,B.length); //!!! через try  !!!!!!!!!!!!!!!
       } catch(Exception e){}
       
       int [] prevTileX=null;  //warning crd[0] - перепрверить calcDisplayCoordinat( возвр парам
       if (crd[0] > 0) prevTileX=calcTile( tn[0],tn[1],zoom,"l");  ////если нижн прав угол в экране   оформить через while
        dimgXY[1][0][1]=dimgXY[1][1][1];// y=y
        dimgXY[1][0][0]=dimgXY[1][1][0]-TileSize; //x=x-256
	
         B=null; B = getImageFile(prevTileX[0],prevTileX[1],zoom);
 try{
       if (B!= null) defaultImage[1][0] =Image.createImage(B,0,B.length); //!!! через try  !!!!!!!!!!!!!!!
} catch(Exception e){}
//
//
//   00    01
//   10    11

final  int x_ = 0,y_=1,X_= 0,Y_=1; 
      prevTileX=null;
       if (crd[1] > 0) prevTileX=calcTile( tn[0],tn[1],zoom,"u");  ////если нижн прав угол в экране   оформить через while   

       dimgXY[0][1][0]=dimgXY[1][1][0];// x=x
       dimgXY[0][1][1]=dimgXY[1][1][1]-TileSize; //y=y-256 
 
         B=null; B = getImageFile(prevTileX[0],prevTileX[1],zoom);
try{
       if (B!= null) defaultImage[0][1] =Image.createImage(B,0,B.length); //!!! через try  !!!!!!!!!!!!!!!
} catch(Exception e){}
       int[] prevTileXX=null;
       if (crd[0] > 0) prevTileXX=calcTile( prevTileX[0],prevTileX[1],zoom,"l");
       dimgXY[0][0][0]=dimgXY[0][1][0]-TileSize;// x=x-256
       dimgXY[0][0][1]=dimgXY[0][1][1];//-TileSize; //y=y 
  
        B=null;  B = getImageFile(prevTileXX[0],prevTileXX[1],zoom);
 try{
       if (B!= null) defaultImage[0][0] =Image.createImage(B,0,B.length); //!!! через try  !!!!!!!!!!!!!!!
} catch(Exception e){}  
 
 
  }
  
 
 public void upd(double lat,double lon,double alt){
      array_ind++; 
     coordAr[array_ind][0]=(float)lat;
     coordAr[array_ind][1]=(float)lon;      
     if (array_ind == 0) dispCorner=calcDispCorner(coordAr[0][0],coordAr[0][1],getWidth(),getHeight()  );//displaywidth,displayheight);
         
     if (  ifInDisplay(coordAr[array_ind][0] , coordAr[array_ind][1],dispCorner) !=0 ) {// удалить переменную updateDinLine=1;
                                      dispCorner=calcDispCorner(coordAr[array_ind][0],coordAr[array_ind][1],displaywidth,displayheight);
                  centrLat=coordAr[array_ind][0];
	          centrLon=coordAr[array_ind][1];
	 }	    
   repaint();
 }
 
//lat Широту принято отсчитывать от экватора на север.

  public int [] calcDxMetres( float lat,float lon,float nlat,float nlon) {  //~~~111!!!!!!!!!!!!!!!!!!!!!
    int[] ret = new int[2];
    float dxGr=nlat-lat;    //расст между широтами в гр (север юг
    float dxKm= dxGr*111.111f;  //111.111 это км в градусе 
    ret[0]=(int) (dxKm*1000 ); // смещение в метрах

     float dyGr=nlon-lon;       //расст между долготами в гр  (запад восток
     float kLon=(float ) (40000*Math.cos(Math.toRadians(  lat))/360 );  //40000 дл окруж земли  //Math.toDegrees()    
     float dyKm=dyGr*kLon; 
     ret[1]=(int) (dyKm*1000 );              
  return ret;
  }

float[] calcDispCorner(double lat /*lon*/ , double lon /*lat*/ , int dwidth, int dheight) { // коор центр дисплей разм
  float ret [] = new float[4];
  // 111111.0f;  //111.111 это км в градусе по  широте
  float klat=111111.0f;
  float kLon=(float ) (40000*Math.cos( Math.toRadians(  lat) )/360 ) *1000;  //40000 дл окруж земли
  float dgh = (float )PixelSizeMetres*dheight/2/klat; // nG=w
  float dgw = (float )PixelSizeMetres*dwidth/2/kLon;//~~~111!!!!!!!!!!!!!!!!!!!!!PixelSizeMetres В ПАРАМЕТРЫ
  ret[0]=(float)(lat+dgh);
  ret[1]=(float)(lon-dgw);
  ret[2]=(float)(lat-dgh);
  ret[3]=(float)(lon+dgw);
return ret;
} 


 
public float[] moveDisplayMetres(int x_metrs,int y_metrs, double oldLat, double oldLon) {
 float[] ret = new float[2];
 ret[0]=(float)(  oldLat+(y_metrs/111111.0f)  );
 float kLon=(float ) (40000*Math.cos( Math.toRadians(  oldLat) )/360 ) *1000; 
 ret[1]=(float)(  oldLon+(x_metrs/kLon) );
return ret;
} 

public void mvScr(int x, int y) {
   float [] dk = moveDisplayMetres(x,y, centrLat,centrLon);
   centrLat=dk[0];
   centrLon=dk[1];
   dispCorner=calcDispCorner(centrLat,centrLon, displaywidth,displayheight);

 //  dimgXY[1][1][0]=dimgXY[1][1][0]-x;dimgXY[1][1][1]=dimgXY[1][1][1]-y;
 if (  needMapView ) showimage(centrLat,centrLon,15);

}
///////////////////////////////////////////////////// keyPressed/////////////////////////////////////////
public int shift_key =0 ;

protected void keyPressed(int key) { // keyPressed(int key) {
// boolean ifrepaint=true;
 if (shift_key==0)
   switch (key) {
     case KEY_NUM1 : PixelSizeMetres=PixelSizeMetres*2;
                     dispCorner=calcDispCorner(centrLat,centrLon, displaywidth,displayheight);break; //  shift_key=1 ;break;  
     case KEY_NUM5 : dodo(); break; // mmove_Point(0,-stepXY); break ;  //go up
     case KEY_NUM6 : mvScr((int)PixelSizeMetres*30,0);break;// PixelSizeMetres=PixelSizeMetres*2;break; //mmove_Point(stepXY,0); break ;
     case KEY_NUM4 : mvScr(-(int)PixelSizeMetres*30,0);break;//mmove_Point(-stepXY,0); break ;
     case KEY_NUM2 : mvScr(0,(int)PixelSizeMetres*30);break;// mmove_Point(0,stepXY); break ;
     case KEY_NUM3 :  if (PixelSizeMetres>1) {PixelSizeMetres=PixelSizeMetres/2;
                                             dispCorner=calcDispCorner(centrLat,centrLon, displaywidth,displayheight);}
					      break; 
     case KEY_NUM8 : mvScr(0,-(int)PixelSizeMetres*30);break;


     case KEY_NUM7 :  if (  needMapView ) showimage(55.75111,37.6172,15 );break;//kreml 55.75111,37.6172,15  // crims most 55.735,37.598,15
     default : break; 
        } 
  else shift_keyPressed(shift_key , key);

repaint();
}

/*
2x, 2y 	       2x + 1, 2y 
2x, 2y + 1     2x + 1, 2y + 1
*/
/*
protected void keyPressed(int key) { // keyPressed(int key) {
// boolean ifrepaint=true;
 if (shift_key==0)
   switch (key) {
     case KEY_NUM6 : moves_Point(stepXY,0); break ;
     case KEY_NUM4 : moves_Point(-stepXY,0); break ;
     case KEY_NUM2 : moves_Point(0,-stepXY); break ;
     case KEY_NUM8 : moves_Point(0,stepXY); break ;
     case KEY_NUM3 : stepXY=stepXY/2; break; 
     case KEY_NUM9 : stepXY=stepXY*2; if (stepXY>=dsizeX-1) stepXY=stepXY/2;break; //stepXY=dsizeX-1; break; 
     case KEY_NUM5 : zoomer(0) ;break;
     case KEY_NUM0 : shift_key=1 ;break;                    //jk
     default : break; 
        } 
  else shift_keyPressed(shift_key , key);
repaint();
}
*/
////////////////////  end paint  ////////////////////////////////

protected void shift_keyPressed(int shi_key, int key) {
   switch (key) {
     case KEY_NUM1 : //load_set(yes_load,"loading_now"); shift_key=0; break ;
     case KEY_NUM2 :  //load_set(no_load,"not_loading"); shift_key=0; break ;
/*     case KEY_NUM2 : move_Point(0,-stepXY); break ;
     case KEY_NUM8 : move_Point(0,stepXY); break ;
     case KEY_NUM3 : stepXY=stepXY/2; break; */
     case KEY_NUM5 :  //zoomer(-1); break; 
     default : break; 
        } 
shift_key=0;  
}
///////////////////////////////////POINTER/////////////////////////////////////

protected void pointerDragged(int x,int y) {

repaint();
}
protected void pointerPressed(int x, int y) {
//posX=x; posY=y;
repaint();
}

protected void pointerReleased(int x, int y) {

repaint();
}
/////////////////////////////////endpointer//////////////////////////////////

 

 } 
//endCanvas6
/*
Имя	Код	Имя	Код	Имя	Код	Имя	Код
aqua	#00FFFF	green	#008000	navy	#000080	silver	#C0C0C0
black	#000000	gray	#808080	olive	#808000	teal	#008080
blue	#0000FF	lime	#00FF00	purple	#800080	white	#FFFFFF
fuchsia	#FF00FF	maroon	#800000	red	#FF0000	yellow	#FFFF00 */