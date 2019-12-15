package com.vcarecity.utils;

import org.junit.Test;

import java.io.IOException;

public class MD5UtilTest {

    @Test
    public void test01() throws IOException {
        String md5=MD5Util.getMD5String(FileUtil.readFileByBytes("E:\\Temp\\8.jpg"));
        System.out.println("md5:"+md5);

        md5=StringUtils.leftAppendZero("2",4);
        System.out.println("md5:"+md5);
    }
}
