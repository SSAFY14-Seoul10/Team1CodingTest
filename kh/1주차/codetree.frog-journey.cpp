#include <iostream>
#include <vector>
#include <queue>
#include <string>
#include <cstring>
#include <tuple>
using namespace std;

const int dr[] = { 0,0,-1,1 };
const int dc[] = { 1,-1,0,0 };
const int INF = 0x7f7f7f7f;

int N, Q, r1, c1, r2, c2, d[51][51][6];
string s, A[51];

bool issafe(int r1, int c1, int r2, int c2) {
	if (r1 == r2) {
		for (int c = min(c1, c2); c <= max(c1, c2); ++c)
			if (A[r1][c] == '#') return false;
		return true;
	}
	if (c1 == c2) {
		for (int r = min(r1, r2); r <= max(r1, r2); ++r)
			if (A[r][c1] == '#') return false;
		return true;
	}
}

int main()
{
	ios::sync_with_stdio(false);
	cin.tie(0);

	cin >> N;
	for (int i = 1; i <= N; ++i) {
		cin >> s;
		A[i] = ' ' + s;
	}

	cin >> Q;
	while (Q--) {
		cin >> r1 >> c1 >> r2 >> c2;

		memset(d, 0x7f, sizeof(d));
		priority_queue<tuple<int, int, int, int>> pq;
		d[r1][c1][1] = 0;
		pq.push(make_tuple( 0, r1, c1, 1 ));

		while (!pq.empty()) {
			auto[tmp, r, c, j] = pq.top();
			int dist = -tmp;
			pq.pop();
			if (dist > d[r][c][j]) continue;
			
			for (int k = 0; k < 4; ++k) {
				int nr = r + dr[k] * j;
				int nc = c + dc[k] * j;
				int ndist = dist + 1;
				if (!(1 <= nr && nr <= N && 1 <= nc && nc <= N)) continue;
				if (A[nr][nc] != '.') continue;
				if (!issafe(r, c, nr, nc)) continue;
				if (ndist < d[nr][nc][j]) {
					d[nr][nc][j] = ndist;
					pq.push(make_tuple(-ndist,nr,nc,j ));
				}
			}

			if (j < 5) {
				int nj = j + 1;
				int ndist = dist + nj * nj;
				if (ndist < d[r][c][nj]) {
					d[r][c][nj] = ndist;
					pq.push(make_tuple(-ndist,r,c,nj ));
				}
			}

			for (int nj = 1; nj < j; ++nj) {
				int ndist = dist + 1;
				if (ndist < d[r][c][nj]) {
					d[r][c][nj] = ndist;
					pq.push(make_tuple(-ndist,r,c,nj ));
				}
			}
		}

		int ans = INF;
		for (int j = 1; j <= 5; ++j)
			ans = min(ans, d[r2][c2][j]);

		if (ans == INF) ans = -1;

		cout << ans << '\n';
	}
}