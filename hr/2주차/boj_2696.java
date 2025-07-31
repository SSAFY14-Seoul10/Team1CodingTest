import java.io.*;
import java.util.*;


public class boj_2696 {

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int T = Integer.parseInt(br.readLine());

		for (int test_case = 1; test_case <= T; test_case++) {
			int N = Integer.parseInt(br.readLine());

			List<Integer> answer = new ArrayList<>();
			PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
			PriorityQueue<Integer> minHeap = new PriorityQueue<>();

			StringTokenizer st = null;
			for (int i = 0; i < N; i++) {
				if (i % 10 == 0) {
					st = new StringTokenizer(br.readLine(), " ");
				}

				int num = Integer.parseInt(st.nextToken());

				if (maxHeap.size() == minHeap.size())
					maxHeap.add(num);
				else
					minHeap.add(num);

				if (!minHeap.isEmpty()) {
					if (minHeap.peek() < maxHeap.peek()) {
						int max = maxHeap.poll();
						int min = minHeap.poll();

						maxHeap.add(min);
						minHeap.add(max);
					}
				}

				if (i % 2 == 0)
					answer.add(maxHeap.peek());
			}
			System.out.print(answer.size());
			for (int i = 0; i < answer.size(); i++) {
				if (i % 10 == 0)
					System.out.println();
				System.out.print(answer.get(i) + " ");
			}

		}

	}

}
