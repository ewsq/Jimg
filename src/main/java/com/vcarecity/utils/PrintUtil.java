package com.vcarecity.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintUtil {

    private static StackTraceElement elements[];
    private static SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    public static String getDate() {
        return mFormatter.format(new Date());
    }
    public static void pritnfDebugInfo(String str)
    {
        elements = new Throwable().getStackTrace();
        String title="["+getDate()+" ("+elements[1].getClassName() + "." + elements[1].getMethodName() + " at " + elements[1].getLineNumber()+") ] :";
        System.out.println(title+str);
    }
}
