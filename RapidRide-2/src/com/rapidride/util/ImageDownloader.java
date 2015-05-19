package com.rapidride.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageDownloader {
	
	static public Bitmap downloadImage(String url) {
          Bitmap bitmap = null;
          InputStream stream = null;
          BitmapFactory.Options bmOptions = new BitmapFactory.Options();
          bmOptions.inSampleSize = 1;

          try {
              stream = getHttpConnection(url);
              bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
              stream.close();
          } catch (IOException e1) {
              e1.printStackTrace();
          }
          return bitmap;
      }

      // Makes HttpURLConnection and returns InputStream
   private static InputStream getHttpConnection(String urlString)
              throws IOException {
          InputStream stream = null;
          URL url = new URL(urlString);
          URLConnection connection = url.openConnection();

          try {
              HttpURLConnection httpConnection = (HttpURLConnection) connection;
              httpConnection.setRequestMethod("GET");
              httpConnection.connect();

              if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                  stream = httpConnection.getInputStream();
              }
          } catch (Exception ex) {
              ex.printStackTrace();
          }
          return stream;
      }
  }	
