package com.aftonbladet.tfilter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Hello world!
 *
 */
public class AppTest 
{
    public static void main( String[] args )
    {
        //System.out.println( "Hello World!" );
    	
    	//String dat = DateFormat.getInstance().format( new Date());
    	
    	//String S = new SimpleDateFormat("MM/dd/yyyy").format(myTimestamp);
    	
    	//SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
    	//dateFormat.format(new Date() );
    	//System.out.println(dateFormat);
    	
    	 /*
    	  String s;
    	  Format formatter;
    	  Date date = new Date();
    	  formatter = new SimpleDateFormat("MM/dd/yy");
    	  s = formatter.format(date);
    	  System.out.println(s);
    	  */
    		String s = new SimpleDateFormat("dd/M/yy").format(new Date());
    		System.out.println(s);
    	
    	
    }
}
