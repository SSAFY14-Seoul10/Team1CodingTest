#include <iostream>
#include <queue>

using namespace std;

int T, M, x;

int main() {
	scanf("%d", &T);
	while (T--) {
		priority_queue<int> maxHeap;
		priority_queue<int, vector<int>, greater<int>> minHeap;
		maxHeap.push(INT32_MIN);
		minHeap.push(INT32_MAX);
		scanf("%d", &M);
		printf("%d\n", M / 2 + 1);
		for (int i = 0; i < M; ++i) {
			scanf("%d", &x);

            // 일단 maxHeap에 넣음
			maxHeap.push(x);

            // minHeap의 top이 maxHeap의 top보다 작으면 swap
			while (maxHeap.top() > minHeap.top()) {
				maxHeap.push(minHeap.top());
				minHeap.push(maxHeap.top());
				minHeap.pop(); maxHeap.pop();
			}

            // 힙 사이즈 조정
			while (maxHeap.size() < minHeap.size()) {
				maxHeap.push(minHeap.top());
				minHeap.pop();
			}
			while (maxHeap.size() > minHeap.size() + 1) {
				minHeap.push(maxHeap.top());
				maxHeap.pop();
			}

            // 출력
			if (i % 2 == 0) {
				if (i > 0 && i % 20 == 0) puts("");
				printf("%d ", maxHeap.top());
			}
		}
		printf("\n");
	}
}