import java.io.*;
import java.util.*;

public class Main {
    static int N, Q;
    static char[][] lake;
    static int[][][] distance;
    static boolean[][][] visited;
    final static int INF = Integer.MAX_VALUE;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        N = Integer.parseInt(br.readLine());
        lake = new char[N+1][N+1];
        for(int i=1; i<=N; i++) {
            String data = br.readLine()``;
            for(int j=1; j<=N; j++) {
                lake[i][j] = data.charAt(j-1);
            }
        }
        Q = Integer.parseInt(br.readLine());
        while (Q-->0) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int r1 = Integer.parseInt(st.nextToken());
            int c1 = Integer.parseInt(st.nextToken());
            int r2 = Integer.parseInt(st.nextToken());
            int c2 = Integer.parseInt(st.nextToken());

            // distance, visited 배열 초기화
            distance = new int[N+1][N+1][6];
            visited = new boolean[N+1][N+1][6];
            for(int i=1; i<=N; i++)
                for(int j=1; j<=N; j++)
                    Arrays.fill(distance[i][j], INF);

            int minTime = dijkstra(r1, c1, r2, c2);
            System.out.println(minTime);
        }
    }

    static int dijkstra(int r1, int c1, int r2, int c2) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((o1, o2) -> o1[3]-o2[3]);
        // (x, y) / 점프력 k /시작위치부터 현재위치까지 걸린 시간
        pq.add(new int[]{r1, c1, 1, 0});
        distance[r1][c1][1] = 0;
        // visited[r1][c1][1] = true;

        int[] dx = new int[]{-1, 1, 0, 0};
        int[] dy = new int[]{0, 0, -1, 1};

        while(!pq.isEmpty()) {
            int[] cur = pq.poll();
            int curX = cur[0];
            int curY = cur[1];
            int curK = cur[2];
            int curTime = cur[3];
            if(curX == r2 && curY == c2) return curTime;
            if(visited[curX][curY][curK]) continue;
            visited[curX][curY][curK] = true;


            // 1. 상하좌우 점프력 k 만큼 이동
            for(int i=0; i<4; i++) {
                int nx = curX + dx[i]*curK;
                int ny = curY + dy[i]*curK;
                if(nx<=0 || ny<=0 || nx>N || ny>N) continue;
                if(visited[nx][ny][curK]) continue;
                // 점프 위치가 미끄러운 돌이면 Pass
                if(lake[nx][ny] == 'S') continue;
                if(lake[nx][ny] == '#') continue;
                // (curX, curY) ~ (nx, ny) 사이에 '#' 천적이 사는 돌이 있으면 Pass
                boolean enemy = false;
                if(dx[i]==0) for(int j=0; j<=curK; j++) if(lake[curX][curY+dy[i]*j]=='#') enemy = true;
                if(dy[i]==0) for(int j=0; j<=curK; j++) if(lake[curX+dx[i]*j][curY]=='#') enemy = true;
                if(enemy) continue;

                // visited[nx][ny][curK] = true;
                if(curTime + 1 < distance[nx][ny][curK]) {
                    distance[nx][ny][curK] = curTime + 1;
                    pq.add(new int[]{nx, ny, curK, distance[nx][ny][curK]});
                }
            }

            // 2. 점프력 1 상승
            if(curK < 5 && !visited[curX][curY][curK+1]) {
                if(curTime + (curK+1)*(curK+1) < distance[curX][curY][curK+1]) {
                    distance[curX][curY][curK+1] = curTime + (curK+1)*(curK+1);
                    pq.add(new int[]{curX, curY, curK+1, distance[curX][curY][curK+1]});
                }
            }

            // 3. 점프력 1 감소
            if(1 < curK && !visited[curX][curY][curK-1]) {
                if(curTime + 1 < distance[curX][curY][curK-1]) {
                    distance[curX][curY][curK-1] = curTime + 1;
                    pq.add(new int[]{curX, curY, curK-1, distance[curX][curY][curK-1]});
                }
            }

        }
        return -1;
    }
}