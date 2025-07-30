import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));

        int T = Integer.parseInt(br.readLine());
        for(int tc = 0; tc < T; tc++) {
            List<Integer> list = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            StringTokenizer st;
            int M = Integer.parseInt(br.readLine());
            int lineNum = (M % 10 == 0) ? M / 10 : M / 10 + 1;
            int midValCnt = 0, cnt = 0, lim = 0;
            while(lim < lineNum) {
                st = new StringTokenizer(br.readLine(), " ");
                while(st.hasMoreTokens()) {
                    list.add(Integer.parseInt(st.nextToken()));
                    if (++cnt % 2 == 1) {
                        list.sort(Integer::compare);
                        sb.append(String.valueOf(list.get(cnt / 2))+" ");
                        midValCnt++;
                        if (midValCnt % 10 == 0) sb.append("\n");
                    }
                }
                lim++;
            }
            bw.write(String.valueOf(midValCnt) + "\n" + sb.toString());
        }
        br.close();
        bw.flush();
        bw.close();
    }
}