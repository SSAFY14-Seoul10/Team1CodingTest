import java.io.*;
import java.util.*;

class UserSolution {

	Map<Integer, News> newsInfo; // key : newsID | value : News Info
	Map<Integer, Set<Integer>> subscribeInfo; // key : channelID | value : userID
	PriorityQueue<ReceivedNotification> totalNotifications; // 전체 알림 (시간 오름차순 정렬)
	Map<Integer, List<UserAlarm>> userNotifications; // key : userID | value : 유저 알림 (시간 내림차순 정렬)

	/**
	 * 초기화 함수
	 * @param N
	 * @param K
	 */
	void init(int N, int K) {
		newsInfo = new HashMap<>();
		subscribeInfo = new HashMap<>();
		totalNotifications = new PriorityQueue<>();
		userNotifications = new HashMap<>();
	}

	/**
	 * 해당하는 시간에 알림 보내기
	 * @param currentTime
	 */
	void processPendingNotifications(int currentTime) {
		while (!totalNotifications.isEmpty() && totalNotifications.peek().deliveredTime <= currentTime) {
			ReceivedNotification rn = totalNotifications.poll();
			News news = newsInfo.get(rn.newsID);
			
			if (news.isCanceled) //취소된 알람은 무시
				continue;
			
			Set<Integer> users = subscribeInfo.get(news.channelID);
			if (users == null || users.size() == 0) //유저가 등록되어 있지 않다면 무시
				continue;
			
			for (int id : users) { // 유저에 해당하는 알림 정보 등록
				userNotifications.computeIfAbsent(id, k -> new ArrayList<>())
						.add(new UserAlarm(news.newsID, news.deliveredTime));
			}
		}
	}

	/**
	 * 유저 등록
	 * @param mTime
	 * @param mUID
	 * @param mNum
	 * @param mChannelIDs
	 */
	void registerUser(int mTime, int mUID, int mNum, int mChannelIDs[]) {
		// 먼저 mTime 알림 보내기
		processPendingNotifications(mTime);

		//유저 등록
		for (int i = 0; i < mNum; i++) {
			subscribeInfo.computeIfAbsent(mChannelIDs[i], k -> new HashSet<>()).add(mUID);
		}
	}

	/**
	 * 뉴스 제공
	 * @param mTime
	 * @param mNewsID
	 * @param mDelay
	 * @param mChannelID
	 * @return
	 */
	int offerNews(int mTime, int mNewsID, int mDelay, int mChannelID) {
		//뉴스 정보 등록
		newsInfo.put(mNewsID, new News(mNewsID, mChannelID, mTime + mDelay, false));

		//알림 정보 등록
		totalNotifications.add(new ReceivedNotification(mNewsID, mTime + mDelay));

		return subscribeInfo.get(mChannelID).size();

	}

	/**
	 * 뉴스 취소
	 * @param mTime
	 * @param mNewsID
	 */
	void cancelNews(int mTime, int mNewsID) {
		//먼저 해당하는 시간의 알림 보내기
		processPendingNotifications(mTime);
		
		newsInfo.get(mNewsID).isCanceled = true;
	}

	/**
	 * 유저가 받은 뉴스 알림의 개수와 뉴스 내용 확인
	 * @param mTime
	 * @param mUID
	 * @param mRetIDs
	 * @return
	 */
	int checkUser(int mTime, int mUID, int mRetIDs[]) {

		//해당하는 시간의 뉴스 알림 보내기
		processPendingNotifications(mTime);

		//유저가 받은 모든 알람 받아내기
		List<UserAlarm> notifications = userNotifications.get(mUID);

		if (notifications == null || notifications.size() == 0)
			return 0;

		List<UserAlarm> validationNotifications = new ArrayList<>();
		for (UserAlarm rn : notifications) {
			if (!newsInfo.get(rn.newsID).isCanceled) //취소되지 않은 알림 걸러내기
				validationNotifications.add(rn);
		}

		Collections.sort(validationNotifications);

		//반환하는 뉴스 알림은 최대 3
		int size = Math.min(validationNotifications.size(), 3);

		for (int i = 0; i < size; i++) {
			mRetIDs[i] = validationNotifications.get(i).newsID;
		}

		//유저의 뉴스 알림 모두 삭제
		userNotifications.get(mUID).clear();

		return validationNotifications.size();
	}

}

class News {
	int newsID;
	int channelID;
	int deliveredTime;
	boolean isCanceled;

	public News(int newsID, int channelID, int deliveredTime, boolean isCanceled) {
		super();
		this.newsID = newsID;
		this.channelID = channelID;
		this.deliveredTime = deliveredTime;
		this.isCanceled = isCanceled;
	}

}

class ReceivedNotification implements Comparable<ReceivedNotification> {

	int newsID;
	int deliveredTime;

	public ReceivedNotification(int newsID, int deliveredTime) {
		super();
		this.newsID = newsID;
		this.deliveredTime = deliveredTime;
	}

	@Override
	public int compareTo(ReceivedNotification o) {
		if (o.deliveredTime == this.deliveredTime)
			return Integer.compare(o.newsID, this.newsID); 
		return Integer.compare(this.deliveredTime, o.deliveredTime);

	}
}

class UserAlarm implements Comparable<UserAlarm> {

	int newsID;
	int deliveredTime;

	public UserAlarm(int newsID, int deliveredTime) {
		this.newsID = newsID;
		this.deliveredTime = deliveredTime;
	}

	@Override
	public int compareTo(UserAlarm o) {
		if (o.deliveredTime == this.deliveredTime)
			return Integer.compare(o.newsID, this.newsID); 
		return Integer.compare(o.deliveredTime, this.deliveredTime);

	}
}