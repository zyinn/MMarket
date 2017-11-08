package com.sumscope.optimus.moneymarket.service;

import java.io.*;
import java.util.Base64;

/**
 * Created by Administrator on 2016/7/13.
 */
public class readExcel {
    public static void main(String[] args) {
        try {
            byte[] decode = Base64.getDecoder().decode(getContent("E:\\下载\\a95e2cf71778949652d9ad16eb9b5796.jpg"));
            File fi = new File("D:\\风景.jpg");
            FileOutputStream fs = new FileOutputStream(fi);
            fs.write(decode);
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getContent(String filePath) throws IOException {
        InputStream in = null;
        try {
            System.out.println("以字节为单位读取文件内容，一次读多个字节：");
            //一次读多个字节
            int byteread = 0;
            in = new FileInputStream(filePath);
            byte[] tempbytes = new byte[in.available()];
            //读入多个字节到字节数组中，byteread为一次读入的字节数
            //in.read(tempbytes);
            while((byteread = in.read(tempbytes,0,tempbytes.length))!=-1){
                System.out.println(byteread);
            }
           // in.read(tempbytes);
            in.close();
           String encode = Base64.getEncoder().encodeToString(tempbytes);
            System.out.println("当前加密的字节数"+tempbytes.length);
            return encode;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * 显示输入流中还剩的字节数
     * @param in
     */
    private static void showAvailableBytes(InputStream in){
        try {
            System.out.println("当前字节输入流中的字节数为:" + in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
