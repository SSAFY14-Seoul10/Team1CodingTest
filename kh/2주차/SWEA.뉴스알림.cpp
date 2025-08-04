#include <iostream>
#include <vector>
#include <unordered_map>
#include <algorithm>

using namespace std;

int N, K;

unordered_map<int, int> subs;
unordered_map<int, vector<pair<int, int>>> subChs;
unordered_map<int, vector<pair<int, int>>> newsList;
unordered_map<int, bool> canceld;
unordered_map<int, int> lastChk;

void init(int N, int K)
{
	subs.clear();
	subChs.clear();
	newsList.clear();
	canceld.clear();
	lastChk.clear();
	::N = N;
	::K = K;
}

void registerUser(int mTime, int mUID, int mNum, int mChannelIDs[])
{
	for (int i = 0; i < mNum; ++i) {
		subChs[mUID].push_back({ mChannelIDs[i], mTime });
		subs[mChannelIDs[i]]++;
	}
}

int offerNews(int mTime, int mNewsID, int mDelay, int mChannelID)
{
	newsList[mChannelID].push_back({ mNewsID, mTime + mDelay });
	return subs[mChannelID];
}

void cancelNews(int mTime, int mNewsID)
{
	canceld[mNewsID] = true;
}

int checkUser(int mTime, int mUID, int mRetIDs[])
{
	vector<pair<int, int>> A;
	int ret = 0;
	for (auto pi1 : subChs[mUID]) {
		int ch = pi1.first;
		int tRegister = pi1.second;
		for (auto pi : newsList[ch]) {
			int newsId = pi.first;
			int t = pi.second;
			if (t > mTime) continue;
			if (canceld[newsId]) continue;
			if (t <= lastChk[mUID]) continue;
			if (tRegister >= t) continue;
			A.push_back({ -t, -newsId });
			ret++;
		}
	}

	sort(A.begin(), A.end());
	for (int i = 0; i < min(3, (int)A.size()); ++i) {
		mRetIDs[i] = -A[i].second;
	}

	lastChk[mUID] = mTime;

	return ret;
}