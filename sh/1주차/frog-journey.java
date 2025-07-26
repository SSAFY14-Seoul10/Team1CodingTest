import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;

public class Main {

    private static int n;
    private static char[][] graph; // n * n의 정사각형 map
    private static int q;
    private static Travel[] travels; // q번의 여행 계획

    private static final char SAFE_STONE = '.'; // 안전한 돌
    private static final char SLIP_STONE = 'S'; // 미끄러운 돌
    private static final char DANGER_STONE = '#'; // 천적이 사는 돌

    private static final int[] di = new int[]{-1, 1, 0, 0}; // 상, 하, 좌, 우
    private static final int[] dj = new int[]{0, 0, -1, 1}; // 상, 하, 좌, 우

    public static void main(String[] args) throws Exception {
        init();
        String answer = getAnswer();
        printAnswer(answer);
    }

    private static void init() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // n값 초기화
        n = Integer.parseInt(br.readLine());

        // 그래프 초기화
        graph = new char[n][n];
        for (int i = 0; i < n; i++) {
            String s = br.readLine();
            for (int j = 0; j < n; j++) {
                graph[i][j] = s.charAt(j);
            }
        }

        // q값 초기화
        q = Integer.parseInt(br.readLine());

        // 여행 계획 초기화
        travels = new Travel[q];
        for (int i = 0; i < q; i++) {
            String[] s = br.readLine().split(" ");

            // 가장 왼쪽에 있는 돌의 좌표 (1, 1) / 가장 오른 쪽 아래의 돌 (N, N)
            // 각 좌표에서 -1씩 진행 (n으로 초기화 했기 때문)
            Position from = new Position(Integer.parseInt(s[0]) - 1, Integer.parseInt(s[1]) - 1);
            Position to = new Position(Integer.parseInt(s[2]) - 1, Integer.parseInt(s[3]) - 1);

            travels[i] = new Travel(from, to);
        }

        br.close();
    }

    private static String getAnswer() {
        StringBuilder sb = new StringBuilder();

        for (Travel travel : travels) {
            sb.append(getMinTime(travel.from, travel.to));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * start -> destinatino 최단 시간을 반환하는 메서드
     *
     * @param start 시작 위치
     * @param destination 도착 위치
     * @return
     */
    private static int getMinTime(Position start, Position destination) {
        boolean[][][] visited = new boolean[n][n][6]; // i, j, 점프력 (점프력은 1부터 5까지 가능)

        // 시작 위치 추가
        PriorityQueue<Info> queue = new PriorityQueue<>();
        queue.add(new Info(start));

        // 시작 위치에서 도착 지점까지 그래프 탐색
        while (!queue.isEmpty()) {
            Info current = queue.poll();

            // 만약 도착 지점에 도착했다면 -> 현재 시간 반환하고 종료
            if (current.arrived(destination)) {
                return current.time;
            }

            // 도착하지 않은 경우 -> 방문한 적이 있는지 확인
            if (visited[current.position.i][current.position.j][current.jumpingPower]) {
                continue;
            }

            visited[current.position.i][current.position.j][current.jumpingPower] = true;

            // 방문한 적이 없는 경우 -> 3가지 행위 중 한 가지 진행
            // 1. 점프하는 경우
            for (int k = 0; k < 4; k++) {
                int ni = current.position.i + di[k] * current.jumpingPower;
                int nj = current.position.j + dj[k] * current.jumpingPower;

                // 다음으로 이동할 수 없는 경우 -> 패스
                if (!isMovable(current, k)) {
                    continue;
                }

                // 이미 방문한 경우 -> 패스
                if (visited[ni][nj][current.jumpingPower]) {
                    continue;
                }

                Info i1 = new Info(new Position(ni, nj), current.time + 1, current.jumpingPower);
                queue.add(i1);
            }

            // 2. 점프력을 증가하는 경우
            if (1 <= current.jumpingPower && current.jumpingPower <= 4) {
                Info i2 = current.deepCopy();
                i2.increaseJumpingPower();
                queue.add(i2);
            }

            // 3. 점프력을 감소하는 경우
            for (int amount = 1; amount < current.jumpingPower; amount++) {
                Info i3 = current.deepCopy();
                i3.decreaseJumpingPower(amount);
                queue.add(i3);
            }

        }

        return -1;
    }

    // private static boolean isMovable(int i, int j) {
    //     return isInRange(i, j) && graph[i][j] == SAFE_STONE;
    // }

    private static boolean isMovable(Info current, int k) {
        int ni = current.position.i;
        int nj = current.position.j;

        for (int time = 0; time < current.jumpingPower; time++) {
            ni = ni + di[k];
            nj = nj + dj[k];

            // 현재 위치가 범위를 초과하는지 확인
            if (!isInRange(ni, nj)) {
                return false;
            }

            // 가는 길에 천적이 사는지 확인
            if (graph[ni][nj] == DANGER_STONE) {
                return false;
            }

        }

        // 도착한 곳이 안전한 돌인지 확인
        return graph[ni][nj] == SAFE_STONE;
    }

    private static boolean isInRange(int i, int j) {
        return i >= 0 && i < graph.length && j >= 0 && j < graph[i].length;
    }

    private static void printAnswer(String answer) {
        System.out.println(answer);
    }
}

class Position {
    int i;
    int j;

    public Position(int i, int j) {
        this.i = i;
        this.j = j;
    }
}

class Travel {
    Position from;
    Position to;

    public Travel(Position from, Position to) {
        this.from = from;
        this.to = to;
    }
}

class Info implements Comparable<Info> {
    Position position; // 현재 위치
    int time = 0; // 현재 위치까지 이동하는데 걸리는 시간
    int jumpingPower = 1; // 점프력

    public Info(Position current) {
        this(current, 0, 1);
    }

    public Info(Position position, int time, int jumpingPower) {
        this.position = position;
        this.time = time;
        this.jumpingPower = jumpingPower;
    }

    @Override
    public int compareTo(Info o) {
        return time - o.time; // 현재 위치까지 이동하는데 걸리는 시간을 기준으로 오름차순
    }

    public boolean arrived(Position destination) {
        return position.i == destination.i && position.j == destination.j;
    }

    public Info deepCopy() {
        return new Info(new Position(position.i, position.j), time, jumpingPower);
    }

    /**
     * 점프력 증가 메서드
     */
    public void increaseJumpingPower() throws IllegalStateException {
        if (1 <= jumpingPower && jumpingPower <= 4) {
            jumpingPower++;
            time += jumpingPower * jumpingPower;

        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * 점프력 감소 메서드
     *
     * @param amount 감소할 점프력
     */
    public void decreaseJumpingPower(int amount) throws IllegalStateException {
        if (amount < jumpingPower) {
            jumpingPower -= amount;
            time++;

        } else {
            throw new IllegalStateException();
        }
    }

}
