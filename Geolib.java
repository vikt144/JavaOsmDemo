 
public class Geolib {

  public static  double [] calcDxMetres( double  lat,double  lon,double  nlat,double  nlon) {   
    double [] ret = new double[2];
    double dxGr=nlat-lat;    //расст между широтами в гр (север юг
    double dxKm= dxGr*111.111f;  //111.111 это км в градусе 
    ret[0]= (dxKm*1000 ); // смещение в метрах

     double dyGr=nlon-lon;       //расст между долготами в гр  (запад восток
     double kLon=(float ) (40000*Math.cos(Math.toRadians(  lat))/360 );  //40000 дл окруж земли  //Math.toDegrees()    
     double dyKm=dyGr*kLon; 
     ret[1] = (dyKm*1000 );              
//  System.out.println("shirota "+ ret[0] +"dolgo  "+ret[1]);
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
//  System.out.println("ii "+ ret[0] +" "+ret[1]);
return ret;
} 

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

  public  static  double calcMetrsPix(double lat,int zoom) {
       return (156543.03*Math.cos(Math.toRadians(lat)))/Math.pow(2,zoom);///!!!! 2 * (1<<(zoom-1) better
    }

  public static int pw(int x, int y) { // x в степени y
  return  x * (1<<(y-1) );
  }  

  public static int[] calcTile(int x, int y, int z, char ch) { // left right up down -- l r u d
    int [] r = new int[3];
    int tx=x;
    int ty=y;

    switch (ch) { 
        case 'l' : tx=x-1; 
                 if (tx<0) tx=pw(2,z-1);
                 break;
        case 'r' : tx=x+1; 
               if (tx==pw(2,z) ) tx=0;
                 break;
        case 'd' : ty=y+1;
            if (ty==pw(2,z) ) ty=0;
               break; 
        case 'u' : ty=ty-1; 
           if (ty<0) ty=pw(2,z-1);
              break;
    }// endswitch
   r[0]=tx;
   r[1]=ty;
   r[2]=z; 
  return r;
 }

}//all
