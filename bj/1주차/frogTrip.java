import java.io.*;
import java.util.*;

public class Main {
    static int N;
    static String[] grid;
    static int[][][] time;
    static int[] dr = {0, 0, -1, 1};
    static int[] dc = {1, -1, 0, 0};
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        N = Integer.parseInt(br.readLine());
        grid = new String[N];
        for(int i = 0; i < N; i++){
            grid[i] = br.readLine();
        }
        time = new int[N][N][6];
        int Q = Integer.parseInt(br.readLine());
        for(int tc = 0; tc < Q; tc++){
            // 시간 배열 time 초기화
            for(int i = 0; i < N; i++){
                for(int j = 0; j < N; j++){
                    Arrays.fill(time[i][j], Integer.MAX_VALUE);
                }
            }
            StringTokenizer st = new StringTokenizer(br.readLine(), " ");
            int r1 = Integer.parseInt(st.nextToken())-1;
            int c1 = Integer.parseInt(st.nextToken())-1;
            int r2 = Integer.parseInt(st.nextToken())-1;
            int c2 = Integer.parseInt(st.nextToken())-1;
            // minheap (시간 기준)
            Queue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[3], b[3]));
            // r, c, jump, time
            pq.offer(new int[]{r1, c1, 1, 0});
            // 시작 지점 -> 점프력 1 -> 시간 0으로 초기화
            time[r1][c1][1] = 0;
            int travelTime = -1;
            while(!pq.isEmpty()){
                int[] cur = pq.poll();
                int r = cur[0], c = cur[1], jump = cur[2], t = cur[3];
                if (r == r2 && c == c2){
                    travelTime = t;
                    break;
                }
                if (time[r][c][jump] < t) continue;
                // 1. 점프: 4개 방향으로 점프 가능 판단 & 점프 실행
                for(int i = 0; i < 4; i++){
                    int nr = r;
                    int nc = c;
                    boolean canJump = true;
                    for(int j = 1; j <= jump; j++){
                        nr += dr[i];
                        nc += dc[i];
                        // 현재 점프력을 가질 때, 범위 벗어나거나 천적이 사는 돌이 있으면 점프 불가
                        if (!isValid(nr, nc) || grid[nr].charAt(nc) == '#'){
                            canJump = false;
                            break;
                        }
                    }
                    // 미끄러운 돌이면 점프 불가
                    if (canJump){
                        if (grid[nr].charAt(nc) == 'S') canJump = false;
                    }
                    // 점프가 가능하다면
                    if (canJump){
                        int newT = t + 1;
                        if (newT < time[nr][nc][jump]){
                            time[nr][nc][jump] = newT;
                            pq.offer(new int[] {nr, nc, jump, newT});
                        }
                    }
                }
                // 2. 점프력 증가
                if (1 <= jump && jump <= 4){
                    int newJ = jump + 1;
                    int newT = t + (newJ * newJ);
                    if (newT < time[r][c][newJ]){
                        time[r][c][newJ] = newT;
                        pq.offer(new int[] {r, c, newJ, newT});
                    }
                }
                // 3. 점프력 감소
                for(int newJ = 1; newJ < jump; newJ++){
                    int newT = t + 1;
                    if (newT < time[r][c][newJ]){
                        time[r][c][newJ] = newT;
                        pq.offer(new int[] {r, c, newJ, newT});
                    }
                }
            }
            bw.write(String.valueOf(travelTime)+"\n");
        }
        br.close();
        bw.flush();
        bw.close();
    }

    public static boolean isValid(int r, int c){
        return (0 <= r && r < N && 0 <= c && c < N);
    }
}