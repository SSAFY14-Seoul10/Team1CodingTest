#include <iostream>
#include <vector>
#include <queue>
using namespace std;

int V, E, S, u, v, w, d[20001];
vector<pair<int, int>> A[20001];

int main()
{
    ios::sync_with_stdio(false);
    cin.tie(0);

    fill(d, d + 20001, 1e9);

    cin >> V >> E >> S;
    while (E--) {
        cin >> u >> v >> w;
        A[u].push_back({ v, w });
    }

    priority_queue<pair<int, int>> pq;
    d[S] = 0;
    pq.push({ 0, S });
    while (!pq.empty()) {
        auto[tmp, u] = pq.top(); pq.pop();
        int dist = -tmp;
        if (dist > d[u]) continue;
        for (auto [v,w] : A[u]) {
            if (dist + w < d[v]) {
                d[v] = dist + w;
                pq.push({ -dist - w, v });
            }
        }
    }

    for (int i = 1; i <= V; ++i)
        if (d[i] == 1e9) cout << "INF\n";
        else cout << d[i] << '\n';
}