package common.basic;

/**
 * 时间转换工具类，提供时间的解析、格式化、天数计算等功能。
 * 该类为工具类，不可实例化，所有方法均为静态方法。
 * 支持的时间格式为 "yyyyMMdd"，例如 "20240101"。
 */
public class TimeTransport {

    /**
     * 获取当前系统时间的毫秒表示。
     * @return 当前系统时间的毫秒表示
     */
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * 将输入的时间字符串解析为毫秒表示，假设输入的时间格式为 "yyyyMMdd"。
     * @param time 输入的时间字符串，格式为 "yyyyMMdd"
     * @return 输入时间的毫秒表示
     * @throws IllegalArgumentException 如果输入的时间格式错误
     */
    public static long parseTime(String time)throws IllegalArgumentException {
        // 这里假设输入的时间格式为 "yyyyMMdd"，例如 "20240101"
        if (time == null || time.length() != 8) {
            throw new IllegalArgumentException("时间格式错误，应该为 yyyyMMdd");
        }
        try {
            int year = Integer.parseInt(time.substring(0, 4));
            int month = Integer.parseInt(time.substring(4, 6)) - 1; // 月份从0开始
            int day = Integer.parseInt(time.substring(6, 8));
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(year, month, day, 0, 0, 0);
            calendar.set(java.util.Calendar.MILLISECOND, 0);
            return calendar.getTimeInMillis();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("时间格式错误，应该为 yyyyMMdd", e);
        }
    }

    /**
     * 将输入的时间毫秒表示格式化为字符串，格式为 "yyyyMMdd"。
     * @param time 输入的时间毫秒表示
     * @return 输入时间的字符串表示，格式为 "yyyyMMdd"
     */
    public static String formatTime(long time) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH) + 1; // 月份从0开始
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return String.format("%04d%02d%02d", year, month, day);
    }

    /**
     * 将输入的时间字符串转换为天数表示，假设输入的时间格式为 "yyyyMMdd"。
     * @param time 输入的时间字符串，格式为 "yyyyMMdd"
     * @return 输入时间的天数表示
     * @throws IllegalArgumentException 如果输入的时间格式错误
     */
    public static long toDays(String time) {
        long Time = parseTime(time);
        return  (Time / (1000 * 60 * 60 * 24));
    }

    /**
     * 将输入的时间毫秒表示转换为天数表示。
     * @param time 输入的时间毫秒表示
     * @return 输入时间的天数表示
     */
    public static long toDays(long time) {
        return  (time / (1000 * 60 * 60 * 24));
    }

    /**
     * 计算两个时间字符串之间的天数差，假设输入的时间格式为 "yyyyMMdd"。
     * @param start 输入的开始时间字符串，格式为 "yyyyMMdd"
     * @param end 输入的结束时间字符串，格式为 "yyyyMMdd"
     * @return 两个时间字符串之间的天数差
     * @throws IllegalArgumentException 如果输入的时间格式错误或结束时间早于开始时间
     */
    public static int getDaysBetween(String start, String end) {
        long startTime = parseTime(start);
        long endTime = parseTime(end);
        if(endTime < startTime) {
            throw new IllegalArgumentException("结束时间必须晚于开始时间");
        }
        return (int) ((endTime - startTime) / (1000 * 60 * 60 * 24))+1;
    }

    /**
     * 计算两个时间毫秒表示之间的天数差。
     * @param start 输入的开始时间毫秒表示
     * @param end 输入的结束时间毫秒表示
     * @return 两个时间毫秒表示之间的天数差
     * @throws IllegalArgumentException 如果结束时间早于开始时间
     */
    public static int getDaysBetween(long start, long end) {
        if(end < start) {
            throw new IllegalArgumentException("结束时间必须晚于开始时间");
        }
        return (int) ((end - start) / (1000 * 60 * 60 * 24))+1;
    }

    private TimeTransport() {
        // 私有构造函数，防止实例化
    }
}