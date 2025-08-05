import java.util.*;
import java.io.*;

public class Main {
	public static void main(String[] args) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
		
		StringTokenizer st = new StringTokenizer(br.readLine(), " ");
		// 서로 다른 두 정점 사이에 여러 개의 간선 존재 가능
		Map<Integer, List<int[]>> graph = new HashMap<>();
		int V = Integer.parseInt(st.nextToken());
		int E = Integer.parseInt(st.nextToken());
		int K = Integer.parseInt(br.readLine());
		for(int i = 1; i <= V; i++) {
			graph.putIfAbsent(i, new ArrayList<>());
		}
		for(int i = 0; i < E; i++) {
			st = new StringTokenizer(br.readLine(), " ");
			int u = Integer.parseInt(st.nextToken());
			int v = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			graph.get(u).add(new int[] {v, w});
		}
		
		int[] dist = new int[V+1];
		Arrays.fill(dist, Integer.MAX_VALUE);
		dist[K] = 0;
		Queue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
		pq.offer(new int[] {K, 0});
		while(!pq.isEmpty()) {
			int[] cur = pq.poll();
			if (dist[cur[0]] < cur[1]) continue;
			for(int[] adj : graph.get(cur[0])) {
				int nextW = cur[1] + adj[1];
				if (nextW < dist[adj[0]]) {
					dist[adj[0]] = nextW;
					pq.offer(new int[] {adj[0], nextW});
				}
			}
		}
		for(int i = 1; i <= V; i++) {
			if (dist[i] == Integer.MAX_VALUE) {
				bw.write("INF\n");
			}
			else {
				bw.write(dist[i] + "\n");
			}
		}
		br.close();
		bw.flush();
		bw.close();
	}
}