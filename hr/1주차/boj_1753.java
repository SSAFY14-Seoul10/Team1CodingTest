import java.io.*;
import java.util.*;

public class boj_1753 {
	final static int INF = 100_000_000;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine(), " ");

		int V = Integer.parseInt(st.nextToken());
		int E = Integer.parseInt(st.nextToken());

		int K = Integer.parseInt(br.readLine());

		int[] dist = new int[V + 1];
		boolean[] visited = new boolean[V + 1];
		List<Node>[] list = new List[V + 1];

		for (int i = 1; i <= V; i++) {
			list[i] = new ArrayList<>();
		}

		Arrays.fill(dist, INF);

		for (int i = 0; i < E; i++) {
			st = new StringTokenizer(br.readLine(), " ");
			int u = Integer.parseInt(st.nextToken());
			int v = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());

			list[u].add(new Node(v, w));
		}

		PriorityQueue<Node> q = new PriorityQueue<>();
		dist[K] = 0;
		q.add(new Node(K, 0));

		while (!q.isEmpty()) {
			Node cur = q.poll();

			if (!visited[cur.end])
				visited[cur.end] = true;
			for (int i = 0; i < list[cur.end].size(); i++) {
				Node next = list[cur.end].get(i);

				if (!visited[next.end] && cur.weight + next.weight < dist[next.end]) {
					dist[next.end] = cur.weight + next.weight;

					q.add(new Node(next.end, dist[next.end]));
				}
			}
		}

		for (int i = 1; i <= V; i++) {
			if (dist[i] == INF) {
				System.out.println("INF");
			} else {
				System.out.println(dist[i]);
			}
		}

	}

}

class Node implements Comparable<Node> {
	int end;
	int weight;

	public Node(int end, int weight) {
		this.end = end;
		this.weight = weight;
	}

	@Override
	public int compareTo(Node o) {
		return Integer.compare(this.weight, o.weight);
	}

}
