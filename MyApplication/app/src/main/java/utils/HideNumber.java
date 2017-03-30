package utils;

/**
 * Created by wAngxIn on 2017/3/8.
 */

public class HideNumber {
    /**
     * 18233185724
     * 如果小于等于3个字符，全部隐藏
     * 大于3个字符。中间隐藏60%
     *
     * @param s
     * @return
     */
    public static String hideInfo(String s) {
        StringBuffer sb = new StringBuffer();
        int i = s.length();
        if (i < 4) {
            return "***";
        }
        for (int j = 0; j < i; j++) {
            int hideLengh = (int) Math.floor(i * 0.6);
            int start = (i - hideLengh) / 2;
            int end = start + hideLengh;
            if (j >start && j <= end) {
                sb.append("*");
            } else {
                sb.append(s.charAt(j));
            }
        }
        return sb.toString();
    }

}
