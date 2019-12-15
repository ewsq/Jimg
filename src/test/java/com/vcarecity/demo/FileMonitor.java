package com.vcarecity.demo;

import java.awt.*;
import java.text.SimpleDateFormat;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.text.SimpleDateFormat;

public class FileMonitor
{

    public static void main(String[] args) throws AWTException
    {
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        Robot robot=new Robot();
        File file=new File("config/jimg.yaml");
        //获取修改时间
        long lastModifitime = file.lastModified();
        while(true)
        {
//			一秒钟后，再次获取修改时间
            robot.delay(1000);
            long lastModifitime2 = file.lastModified();
            if(lastModifitime!=lastModifitime2)
            {
                System.out.println(file.getName()+"在"+format.format(lastModifitime2)+"被修改过");
                lastModifitime=lastModifitime2;


            }
        }
    }
}


