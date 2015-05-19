package com.pos.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Waiter extends Thread
{
    private static final String TAG=Waiter.class.getName();
    private long lastUsed;
    private long period;
    private boolean stop;
    Context mcontext;
    public Waiter(long period,Context context)
    {
        this.period=period;
        mcontext = context;
        stop=false;
    }

    @SuppressWarnings("deprecation")
	public void run()
    {
        long idle=0;
        this.touch();
        do
        {
            idle=System.currentTimeMillis()-lastUsed;
            Log.d(TAG, "Application is idle for "+idle +" ms");
            try
            {
                Thread.sleep(5000); //check every 5 seconds
            }
            catch (InterruptedException e)
            {
                Log.d(TAG, "Waiter interrupted!");
            }
            if(idle > period)
            {
                idle=0;
               this.forceInterrupt();
              
               SharedPreferences  pref = mcontext.getSharedPreferences("mypref",Context. MODE_PRIVATE);
          		Editor editor = pref.edit();
          		editor.putString("ReceiptGenerate", "hello");
                 editor.commit(); 
               System.exit(0);
               
             
                //do something here - e.g. call popup or so
            }
        }
        while(!stop);
        Log.d(TAG, "Finishing Waiter thread");
    }

    public synchronized void touch()
    {
        lastUsed=System.currentTimeMillis();
        stop = false;
        
       
    }

    @SuppressWarnings("deprecation")
	public synchronized void forceInterrupt()
    {
        this.interrupt();
       
     
        stop = true;
       
    }

    //soft stopping of thread
   

    public synchronized void setPeriod(long period)
    {
        this.period=period;
    }

}