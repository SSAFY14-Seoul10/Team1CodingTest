import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int T = Integer.parseInt(br.readLine());
        StringBuilder sb = new StringBuilder();
        while (T-- > 0) {
            int N = Integer.parseInt(br.readLine());
            PriorityQueue<Integer> maxHeap = new PriorityQueue<>((o1, o2) -> o2-o1);
            PriorityQueue<Integer> minHeap = new PriorityQueue<>((o1, o2) -> o1-o2);
            sb.append(N/2+1).append("\n");

            int cnt = 1;
            StringTokenizer st = null;
            for(int i=0; i<N; i++) {
                if(i%10==0) st = new StringTokenizer(br.readLine());
                int num = 0;
                if(st.hasMoreTokens()) num = Integer.parseInt(st.nextToken());
                if(maxHeap.size() == minHeap.size()) maxHeap.offer(num);
                else minHeap.offer(num);
                if(!minHeap.isEmpty()) {
                    if(minHeap.peek() < maxHeap.peek()) {
                        int min = minHeap.poll();
                        int max = maxHeap.poll();
                        minHeap.offer(max);
                        maxHeap.offer(min);
                    }
                }
                if(i % 2 == 0) {
                    int value = maxHeap.peek();
                    sb.append(value).append(" ");
                    if(cnt % 10 == 0) sb.append("\n");
                    cnt++;
                }
            }
            sb.append("\n");
        }
        System.out.println(sb);
        br.close();
    }
}
