import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.*;

public class netWork {

     public static String netGetText(String url,int port ) { // port = -1 не исполь  
        String ret = null;
        int c;
        try { 
        URL myUrl = new URL(url);
//        myUrl.setRequestProperty("Content-Type",
        URLConnection myUrlCon = myUrl.openConnection();       
        long length = myUrlCon.getContentLengthLong();

        if(length == -1) System.out.println(" ret -1 ");
        if(length != 0) {
            System.out.println("===  ==="+length );
 
            InputStream input = myUrlCon.getInputStream();
            while(((c = input.read()) != -1)) {
                System.out.print((char) c);
            }//while

            input.close();
            }//if 
            else {  System.out.println(" .");}
            }catch(Exception e){}        
  
    return ret; 
    }


public static int RC =0;

     public static byte[] netGetBytes(String url,int port ) { // port = -1 не исполь  
        byte[] ret = null; byte[] B=null;
        int c;
        try { 
        URL myUrl = new URL(url);

        HttpURLConnection myUrlCon = (HttpURLConnection) myUrl.openConnection();

        myUrlCon.setRequestProperty("Content-Type", "image/png");         
        myUrlCon.setRequestProperty("User-Agent", "java test");
 //       myUrlCon.setRequestMethod("GET");
          RC = myUrlCon.getResponseCode();

        long length = myUrlCon.getContentLengthLong();

        if(length == -1) System.out.println(" ret -1 ");
        if(length != 0) {
            System.out.println("===  ==="+length );
            B = new byte[(int)length];

            InputStream input = myUrlCon.getInputStream();    
            input.read(B);
           input.close();
            }//if 
            else {  System.out.println(" .");}
            }catch(Exception e){ System.out.println("  catch ") ; }        
    ret = B;
    return ret;   
   }


public static  byte[] netGetBytes2(String url , int prt) {

       ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

       try {
       URL toDownload = new URL(url);
           byte[] chunk = new byte[4096];
           int bytesRead;
           InputStream stream = toDownload.openStream();

           while ((bytesRead = stream.read(chunk)) > 0) {
               outputStream.write(chunk, 0, bytesRead);
           }

       } catch (IOException e) {
           e.printStackTrace();
           return null;
       }

       return outputStream.toByteArray();
}



     //// from https://stackoverrun.com/ru/q/476813

    public static int saveBinFile(String pathAndName, byte[] data) {
        int ret = 0;
             try {
             final File file = new File(pathAndName);
                   FileOutputStream fos = new FileOutputStream(file);
                   fos.write(data,0,data.length);
                   fos.close();
                   } catch (Exception e) { ret = -1;}

     return ret;
    }


    public static String readText(String f)  {  // улучшить -- гарантировать закрытие
            String ret = null;
            Reader in = null ;
            int  chr; 
            StringBuilder sb = new StringBuilder();
            try {
               in = new FileReader(f);
               chr=in.read();
               while (chr != -1 ) {
                        sb.append( (char)chr );
                        chr=in.read();
                     }
               ret = new String (sb); 
               in.close();
           } catch (Exception e) {ret = null;   }

   return ret;
    }


    public static void writeText(String f, String txt)  {  
// http://proglang.su/java/io-and-files-and-directories#navigaciya-po-faylovoy-sisteme-i-vvodu-vyvodu
       try {
        // char c[] = {'a','b','c'};
         OutputStream output = new FileOutputStream(f); // Создание текстового файла
         for(int i = 0; i < txt.length(); i++) {
            output.write(txt.charAt(i) ); // Запись каждого символа в текстовый файл
         }
         output.close();     
       }catch (Exception e) {}
   }
}//all

 

