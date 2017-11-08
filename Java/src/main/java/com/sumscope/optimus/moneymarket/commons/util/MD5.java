package com.sumscope.optimus.moneymarket.commons.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 从ams项目复制过来，用于加密用户密码
 *
 * @author xingyue.wang
 *
 */
public final class MD5 {

    private MD5(){}
    //static Logger  logger = Logger.getLogger(MD5.class);
    public static String md5s(String plainText) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            final int mdLength=16;
            final int mdConstant=256;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += mdConstant;
                }
                if (i < mdLength) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            //logger.error(e);
        }
        return "";
    }

}
