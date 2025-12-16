package com.base.auth.utils;

public class ConvertUtils {

    private ConvertUtils(){

    }

    public static Long convertStringToLong(String input){
        try {
            return  Long.parseLong(input);
        }catch (Exception e){
            return  Long.valueOf(0);
        }
    }

    public static int convertToCent(double b){
        int i=(int)(b);
        double k = b-(double)i;
        if(k>0.5 && k<1){
            i+=1;
        }
        return i;
    }

    public static Double convertStringToDouble(String input){
        try {
            return  Double.parseDouble(input);
        }catch (Exception e){
            return  Double.valueOf(0);
        }
    }

    public static Float convertStringToFloat(String input){
        try {
            return  Float.parseFloat(input);
        }catch (Exception e){
            return  Float.valueOf(0);
        }
    }

    public static Integer convertStringToInteger(String input){
        try {
            return  Integer.valueOf(input);
        }catch (Exception e){
            return  Integer.valueOf(0);
        }
    }
}
