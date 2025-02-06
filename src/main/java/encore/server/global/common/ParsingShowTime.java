package encore.server.global.common;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ParsingShowTime {
    /**
     * 요일을 영어로 변환하는 맵
     */
    private static Map<String, String> dayMap = new HashMap<>();

    static {
        dayMap.put("월", "MON");
        dayMap.put("화", "TUE");
        dayMap.put("수", "WED");
        dayMap.put("목", "THU");
        dayMap.put("금", "FRI");
        dayMap.put("토", "SAT");
        dayMap.put("일", "SUN");
    }

    /**
     * 공연 시간을 파싱하는 메서드
     * @param showTime
     * @return List<String> 공연 시간 목록
     */
    public List<String> parseShowTimes(String showTime) {
        List<String> result = new ArrayList<>();

        // 월, 화, 수, 목, 금, 토, 일 순서대로 반복문을 돌며 처리
        String[] daysOfWeek = {"월", "화", "수", "목", "금", "토", "일"};

        for (String d : daysOfWeek) {
            // 해당 요일에 맞는 시간 패턴을 찾아서 추출
            String pattern = d + "(?!,\\s*공휴일)\\s*[^0-9]*(\\d+시(,\\s*\\d+시)*)?";
            Matcher matcher = Pattern.compile(pattern).matcher(showTime);
            // 공연 시간이 있는 요일 및 시간 처리
            while (matcher.find()) {
                String times = matcher.group(1); // 시간 정보

                // 공연 시간이 있는 요일에 대해서만 처리
                if (times != null && !times.contains("공연없음")) {
                    // 쉼표로 구분된 시간 처리
                    String[] timeArray = times.split(",\\s*");

                    // 해당 요일들에 대해 동일한 시간을 적용
                    for (String time : timeArray) {
                        String formattedTime = formatTime(time);
                        result.add(dayMap.get(d) + ": " + formattedTime);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 시간을 24시간 형식으로 변환하는 메서드
     * @param time
     * @return String 24시간 형식의 시간
     */
    private static String formatTime(String time) {
        // "시"를 제거하고 분리
        String[] timeParts = time.replace("시", "").split("분");
        String hour = timeParts[0].trim();
        String minute = timeParts.length > 1 ? timeParts[1].trim() : "00";

        int hourInt = Integer.parseInt(hour);

        if (hourInt < 12) {
            hourInt += 12;
        }

        return String.format("%02d:%s", hourInt, minute);
    }
}
