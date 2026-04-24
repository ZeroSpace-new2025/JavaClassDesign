package common.basic;

import java.security.MessageDigest;

public class MD5Util {

        /** 
         * 计算输入字符串的 MD5 哈希值，并返回其十六进制表示。 
         * @param input 输入字符串
         * @return 输入字符串的 MD5 哈希值的十六进制表示
         */
       @SuppressWarnings("UseSpecificCatch")
       public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 加密失败", e);
        }
    }
}
