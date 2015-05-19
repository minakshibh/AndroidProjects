package com.print;

import java.util.ArrayList;

public class Utility {
	   public static final String MyPREFERENCES = "MyPrefs" ;
	   public static final String RecieptPath = "receipt"; 
	   public static final String CashType="cashes";
	   public static final String ItemReturnBy="retrun";
	   public static final String ByReturn="retruns";
	   public static final String imagesPathSize="size";
	   
	   public static final String HeaderLine1 = "h1"; 
	   public static final String HeaderLine2="h2";
	   public static final String HeaderLine3="h3";
	   public static final String HeaderLine4="h4";
	   public static final String HeaderLine5="h5";
	   public static final String ITEMText="h6";
	   
	   public static final String FooterLine1 = "f1"; 
	   public static final String FooterLine2="f2";
	   public static final String FooterLine3="f3";
	   public static final String FooterLine4="f4";
	   public static final String ImageData="image";
	   
	   
	   private static ArrayList<String> pathLists;
	   public static ArrayList<String> pathList() 
	   {
	   	if(pathLists== null)
	   	{
	   		pathLists = new ArrayList<String>();
	   	}
	   		return pathLists;
	   	}
}
