import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Discover {

    /** 탐지할 식별자 목록 */
    List<Identifier> idfs;

    /** 식별자 별 compile 해둔 regex pattern 목록 */
    List<Pattern> pts = new ArrayList<>();

    Discover(List<Identifier> idfs) {
        this.idfs = idfs;

        // Regex를 미리 compile 해둔다.
        for (Identifier idf : idfs) {
            pts.add(Pattern.compile(idf.pttn));
        }
    }

    int scan(String txt, IncidentConsumer cnsm) {
    	
        for (int i = 0; i < pts.size(); i++) {
            // 각각의 i번째 식별자를 검색한다.
            Identifier idf = idfs.get(i);
            Matcher mt = pts.get(i).matcher(txt); // txt 검증 (주어진 패턴과 일치하는지)
            while (mt.find()) {
                // Captured groups를 array로
            	// g[0] 전체, g[1] 첫번째 (), g[2] 두번째 () ...
                String[] grps = IntStream.range(0, mt.groupCount() + 1).mapToObj(mt::group).toArray(String[]::new);

                // 유효성 검증
                if (idf.valid.check(grps)) {
                	
                	// number of each detection (0:SSN, 1:MPH, 2:PHN, 3:HIN)                	
                	if(idf.name == "주민등록번호") Validation.checkNum[0]++;
                	else if(idf.name == "휴대전화번호") Validation.checkNum[1]++;
                	else if(idf.name == "집전화번호") Validation.checkNum[2]++;
                	else if(idf.name == "건강보험증번호") Validation.checkNum[3]++;
                	
                    // 매칭되는 문자열 시작위치와 끝(다음)위치 이용하여 Validator class에서 람다 함수로 정의
                    //cnsm.accept(idf, mt.start(), mt.end());
                }
            }
        }
        
        System.out.println("\n\nInspect personal information version JAVA regex");
		System.out.println("SSN " + Validation.checkNum[0]);
		System.out.println("MPH " + Validation.checkNum[1]);
		System.out.println("PHN " + Validation.checkNum[2]);
		System.out.println("HIN " + Validation.checkNum[3]);
		
		// 통제 정책
		if(Validation.checkNum[0] > 5 || Validation.checkNum[1] >5
				|| Validation.checkNum[2] > 5 || Validation.checkNum[3] > 5
				|| (Validation.checkNum[0] + Validation.checkNum[1]
						+ Validation.checkNum[2] + Validation.checkNum[3] > 5))
			return 1;
		else
			return 0;
    }

    @FunctionalInterface
    public interface IncidentConsumer {
        void accept(Identifier idf, int start, int end);
    }
}