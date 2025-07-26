import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

public class Main {

    private static int v; // 정점의 개수
    private static int e; // 간선의 개수
    private static int k; // 시작 정점의 번호
    private static ArrayList<ArrayList<Node>> graph;
    private static final int INF = 1_000_000_000;

    public static void main(String[] args) throws IOException {
        init();
        int[] result = dijkstra();
        printResult(result);
    }

    public static void printResult(int[] result) {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i <= v; i++) {
            String dist = (result[i] >= INF) ? "INF" : String.format("%d", result[i]);
            sb.append(dist);
            sb.append("\n");
        }

        System.out.print(sb.toString());
    }

    public static int[] dijkstra() {
        int[] distances = new int[v + 1];

        // 경로 초기화
        Arrays.fill(distances, INF);
        distances[k] = 0; // 시작 지점이니까

        // 다익스트라 알고리즘을 사용하여 최단거리 찾기
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(new Node(k, 0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            for (Node neighbor : graph.get(current.node)) {
                if (distances[current.node] + neighbor.weight < distances[neighbor.node]) { // 현재까지의 거리 + 이웃까지의 거리가 더 짧은 경우
                    distances[neighbor.node] = current.weight + neighbor.weight;
                    queue.add(new Node(neighbor.node, distances[neighbor.node]));
                }
            }
        }


        return distances;
    }

    public static void init() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // 정점의 개수, 간선의 개수 초기화
        String[] s = br.readLine().split(" ");
        v = Integer.parseInt(s[0]);
        e = Integer.parseInt(s[1]);

        // 그래프 초기화
        graph = new ArrayList<>();
        for (int i = 0; i <= v; i++) {
            graph.add(i, new ArrayList<Node>());
        }

        // 시작 정점의 번호 초기화
        k = Integer.parseInt(br.readLine());

        // 그래프 간선 추가
        for (int i = 0; i < e; i++) {
            s = br.readLine().split(" ");
            int u = Integer.parseInt(s[0]);
            int v = Integer.parseInt(s[1]);
            int w = Integer.parseInt(s[2]);

            // u에서 v로 가는 가중치 w
            graph.get(u).add(new Node(v, w));
        }

        br.close();
    }
}

class Node implements Comparable<Node> {
    int node;
    int weight;

    public Node (int node, int weight) {
        this.node = node;
        this.weight = weight;
    }

    @Override
    public int compareTo(Node o) {
        return this.weight - o.weight; // 가중치가 작은 순서대로 오름차순
    }
}