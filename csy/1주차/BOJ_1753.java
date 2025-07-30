package algorithm.ShortestPath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class BOJ_1753 {
    static int V, E, K;
    static List<List<int[]>> graph = new ArrayList<>();
    static int[] distance;
    static boolean[] visited;
    static final int INF = 3000000;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] data = br.readLine().split(" ");
        V = Integer.parseInt(data[0]);
        E = Integer.parseInt(data[1]);
        K = Integer.parseInt(br.readLine());
        for (int i=0; i<=V; i++) graph.add(new ArrayList<>());
        for (int i=0; i<E; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int u = Integer.parseInt(st.nextToken());
            int v = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            graph.get(u).add(new int[]{v, w});
        }

        distance = new int[V+1];
        visited = new boolean[V+1];
        Arrays.fill(distance, INF);

        dijkstra(K);
        for(int i=1; i<=V; i++) {
            if (distance[i] >= INF) {
                System.out.println("INF");
                continue;
            }
            System.out.println(distance[i]);
        }
    }

    static void dijkstra(int start) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((o1, o2) -> o1[1]-o2[1]);
        distance[start] = 0;
        pq.add(new int[]{start, 0});

        while(!pq.isEmpty()){
            int[] cur = pq.poll();
            if(visited[cur[0]]) continue;
            visited[cur[0]] = true;

            for(int[] naver: graph.get(cur[0])) {
                int naverIdx = naver[0];
                int naverCost = naver[1];
                if(visited[naverIdx]) continue;
                if(distance[cur[0]] + naverCost < distance[naverIdx]) {
                    distance[naverIdx] = distance[cur[0]] + naverCost;
                    pq.add(new int[]{naverIdx, distance[naverIdx]});
                }
            }
        }
    }
}
