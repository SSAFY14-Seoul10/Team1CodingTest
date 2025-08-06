#include <iostream>
#include <queue>
#include <vector>
#include <set>

using namespace std;

int n;
multiset<int> s;
vector<int> answer;

void reset(){
	answer.clear();
	s.clear();
}

void solve(){
	cin>>n;
	for(int i=1;i<=n;i++){
		int temp; cin>>temp;
		s.insert(temp);
		if(i%2!=0) {
			auto it=s.begin();
			// cout<<i<<" "<<i/2<<" "<<s.size()<<"\n";
			for(int j=0;j<i/2;j++){
				it++;
			}
			answer.push_back(*it);
		}
	}
}

void output(){
	cout<<answer.size()<<"\n";
	for(int i=0;i<answer.size();i++){
		if(i!=0&&i%10==0) cout<<"\n";
		cout<<answer[i]<<" ";
	}
	cout<<"\n";
}

int main(){
	ios_base::sync_with_stdio(0);
	cin.tie(0); 
	int t; cin>>t;
	for(int i=0;i<t;i++){
		reset();
		solve();
		output();
	}
}