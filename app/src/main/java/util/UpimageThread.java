//package util;
//
//import android.util.Log;
//
//import java.io.BufferedReader;
//import java.io.DataOutput;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
///**
// * Created by hsp on 2016/12/15.
// */
//
//public class UpimageThread extends Thread {
//
//    private String url ;
//
//    private String filename;
//
//    public UpimageThread(String filename, String url) {
//        this.filename = filename;
//        this.url = url;
//    }
//
//
//    @Override
//    public void run() {
//
//        String bounday="----------7de2c25201d48";
//        String prefix="--";
//        String end="\r\n";
//        try{
//            URL httpUrl =new URL(url);
//            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            connection.setRequestProperty("Content-Type","multipart/form-data;boundary="+bounday);
//            DataOutputStream out =new DataOutputStream(connection.getOutputStream());
//            out.writeBytes(prefix+bounday+end);
//            out.writeBytes("Content-Disposition:form-data;"
//                    +"name=\"file\";filename=\""+"door.png"+"\""+end);
//            FileInputStream fileInputStream =new FileInputStream(new File(filename));
//            byte[] b =new byte[1024*4];
//            int len;
//            while((len=fileInputStream.read(b))!=-1)
//            {
//                out.write(b,0,len);
//            }
//            out.writeBytes(end);
//            out.writeBytes(prefix+bounday+prefix+end);
//            out.flush();;
//            BufferedReader reader =new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            StringBuffer sb =new StringBuffer();
//            String str;
//            while((str=reader.readLine())!=null)
//            {
//                sb.append(str);
//            }
//            Log.d("Main",str);
//            if(out!=null)
//            {
//                out.close();;
//            }
//            if(reader!=null)
//            {
//                reader.close();;
//            }
//
//        }catch(Exception e)
//        {
//
//        }
//    }
//}
