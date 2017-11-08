package com.sumscope.optimus.moneymarket.commons.util;


import com.sumscope.optimus.commons.log.LogManager;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.regex.Pattern;

public final class Pinyin4jUtil {
    private Pinyin4jUtil() {
    }

    private static Log logger = LogFactory.getLog(Pinyin4jUtil.class);

    private static final String ILLEAGAL_CHARS_PATTERN_STR = "(-|@|_)";
    
    private static Pattern ILLEAGAL_CHARS_PATTERN = null;


    static{
        ILLEAGAL_CHARS_PATTERN = Pattern.compile(ILLEAGAL_CHARS_PATTERN_STR);
    }
    
    
    
    
    private static String filterString(String str){
        return ILLEAGAL_CHARS_PATTERN.matcher(str).replaceAll("");
    }

    /**
     * 将汉字转换为全拼
     * 
     * @param src
     * @return String
     */
    public static String getPinYin(String src) {
        if(StringUtils.isBlank(src)){
            return "";
        }
        char[] charArray = src.toCharArray();
        // 设置汉字拼音输出的格式
        HanyuPinyinOutputFormat pyFormat = new HanyuPinyinOutputFormat();
        pyFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        pyFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        pyFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        StringBuilder strBld = new StringBuilder();
        try {
            for (int i = 0; i < charArray.length; i++) {
                // 判断能否为汉字字符
                if (Character.toString(charArray[i]).matches(
                        "[\\u4E00-\\u9FA5]+")) {
                    // 将汉字的几种全拼都存到t2数组中
                    String[] result = PinyinHelper.toHanyuPinyinStringArray(
                            charArray[i], pyFormat);
                    if(result == null || result.length == 0){
                        LogManager.error("the char["+ charArray[i]+"] can not to be pinyin");
                        continue;
                    }
                    strBld.append(result[0]);
                    
                } else {
                    // 如果不是汉字字符，间接取出字符并连接到字符串t4后
                    strBld.append(charArray[i]);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            LogManager.error(logger,"change string[" + src + "] to yinyin error.");
        }
        return filterString(strBld.toString());
    }

    /**
     * 提取每个汉字的首字母
     * 
     * @param str
     * @return String
     */
    public static String getPinYinHeadChar(String str) {
        if(StringUtils.isBlank(str)){
            return "";
        }
        StringBuilder strBld = new StringBuilder();
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            // 提取汉字的首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                strBld.append(pinyinArray[0].charAt(0));
            } else {
                strBld.append(word);
            }
        }
        return filterString(strBld.toString());
    }
    
//    public static void main(String[] args) {
//
//        System.out.println(filterString("asdf-asdf"));
//    }

}