package com.example.drysister;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.LogRecord;
//线程将输入流转成字节数组，转换完毕 handler 里 decodeByteArray 将字节数组解码成bitmap，然后显示~

public class PictureLoader{
    private ImageView loadImg;
    private String imgUrl;
    private byte[] picByte;

   Handler handler = new Handler(){
       @Override
       public void handleMessage(@NonNull Message msg) {
           super.handleMessage(msg);
           if (msg.what== 0x123){
//               判断msg.what==0x123是用来判断这是来自哪个线程的handler
               if (picByte != null){
                   Bitmap bitmap = BitmapFactory.decodeByteArray(picByte,0,picByte.length);
//                   把网络图片的数据流读入到内存中然后用方法来将图片流传化为bitmap类型 这样才能用到
                   loadImg.setImageBitmap(bitmap);
               }
           }
       }
   };

   public void load(ImageView loadImg,String imgUrl){
       this.loadImg = loadImg;
       this.imgUrl = imgUrl;
       Drawable drawable = loadImg.getDrawable();
//       直接强转 Drawable 为 BitmapDrawable, 然后调用getBitmap() 函数返回Bitmap
       if (drawable !=null && drawable instanceof BitmapDrawable){
           Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
           if(bitmap != null && !bitmap.isRecycled()) {
               bitmap.recycle();
           }
       }
       new Thread(runnable).start();
   }
   Runnable runnable = new Runnable() {
       @Override
       public void run() {
           try {
               URL url= new URL(imgUrl);
               HttpURLConnection conn =(HttpURLConnection)url.openConnection();
               conn.setRequestMethod("GET");
               conn.setReadTimeout(10000);
               if (conn.getResponseCode()==200){
                   InputStream in =conn.getInputStream();
                   ByteArrayOutputStream out=new ByteArrayOutputStream();
//  实现了将数据写入字节数组的输出流。 当数据写入缓冲区时，缓冲区会自动增长。
//  数据可以使用toByteArray()和toString() 。
                   byte[] bytes = new byte[1024];
                   int length =-1;
                   while ((length= in.read(bytes))!=-1){
//                        将指定字节数组中从偏移量 off 开始的 len 个字节写入此字节数组输出流。
                       out.write(bytes,0,length);
                   }
                   picByte = out.toByteArray();
                   in.close();
                   out.close();
                   handler.sendEmptyMessage(0x123);
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   };


}
