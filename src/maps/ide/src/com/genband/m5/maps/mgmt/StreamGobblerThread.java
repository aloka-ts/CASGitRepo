package com.genband.m5.maps.mgmt;
import java.io.*;
import java.util.*;

public class StreamGobblerThread extends Thread
{
  InputStream oInputStream;
  String type;
  public StreamGobblerThread(InputStream oInputStream, String type)
  {
    this.oInputStream = oInputStream;
    this.type = type;
  }
  public void run()
  {
    try
    {
      BufferedReader oBufferedReader = new BufferedReader(new InputStreamReader(oInputStream));
      String line = null;
      while((line = oBufferedReader.readLine()) != null)
      {
        System.out.println(type + " > " + line);
      }
    }catch(IOException oIOException){
      oIOException.printStackTrace();
    }
  }

}

