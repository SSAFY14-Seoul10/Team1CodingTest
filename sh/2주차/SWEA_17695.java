import java.util.*;

/**
 * 뉴스 알림 시스템의 핵심 로직을 관리하는 클래스
 * <br/>
 * 유저 등록, 뉴스 발행, 취소, 알림 확인 기능을 제공합니다.
 *
 * <h3>주요 설계 전략:</h3>
 * <ul>
 *   <li><b>HashMap 중심의 데이터 관리</b>: 유저 ID, 채널 ID, 뉴스 ID의 범위가 매우 크기 때문에 배열 대신 HashMap을 사용하여 메모리를 효율적으로 사용합니다.</li>
 *   <li><b>이벤트 기반 시간 처리</b>: PriorityQueue를 사용하여 뉴스 발행과 같은 시간 기반 이벤트를 관리합니다. 이를 통해 현재 시간까지 처리해야 할 모든 알림을 효율적으로 처리할 수 있습니다.</li>
 *   <li><b>상태 관리의 명확성</b>: 각 데이터의 상태(예: 채널별 구독자, 유저별 알림, 취소된 뉴스)를 명확하게 분리된 자료구조로 관리하여 코드의 복잡성을 낮춥니다.</li>
 * </ul>
 */
public class UserSolution {

    // --- 상수 정의 ---
    /**
     * checkUser에서 반환할 최신 뉴스 ID의 최대 개수
     */
    private static final int MAX_RETURN_NEWS_IDS = 3;

    // --- 핵심 자료구조 ---

    /**
     * 채널별 구독자 목록을 관리하는 맵
     * <ul>
     *   <li><b>Key</b>: 채널 ID (channelId)</li>
     *   <li><b>Value</b>: 해당 채널을 구독하는 유저 ID의 Set (userIds)</li>
     * </ul>
     */
    private Map<Integer, Set<Integer>> channelSubscribers;

    /**
     * 각 뉴스가 어떤 채널에서 발행되었는지 관리하는 맵
     * <ul>
     *   <li><b>Key</b>: 뉴스 ID (newsId)</li>
     *   <li><b>Value</b>: 채널 ID (channelId)</li>
     * </ul>
     */
    private Map<Integer, Integer> newsToChannelMap;

    /**
     * 유저별로 수신한 알림을 저장하는 우선순위 큐
     * <ul>
     *   <li><b>Key</b>: 유저 ID (userId)</li>
     *   <li><b>Value</b>: 해당 유저가 받은 알림들을 담은 우선순위 큐 (최신순 정렬)</li>
     * </ul>
     */
    private Map<Integer, PriorityQueue<ReceivedNotification>> userNotificationQueues;

    /**
     * 발행될 모든 뉴스 알림을 시간순으로 관리하는 마스터 우선순위 큐
     * <br/>
     * 이 큐를 통해 시스템은 시간을 효율적으로 앞으로 흐르게 할 수 있습니다.
     */
    private PriorityQueue<Alarm> masterAlarmQueue;

    /**
     * 발행이 취소된 뉴스의 ID를 저장하는 Set
     * <br/>
     * 여기에 포함된 뉴스 ID는 알림 전송 및 확인 과정에서 필터링됩니다.
     */
    private Set<Integer> cancelledNewsIdSet;

    /**
     * 각 테스트 케이스의 시작 시 호출되어 시스템을 초기화하는 메서드
     * <br/>
     * 모든 자료구조를 새로 생성하여 이전 테스트 케이스의 데이터로부터 독립성을 보장합니다.
     *
     * @param N 뉴스 알림을 받는 유저의 수 (현재 로직에서는 직접 사용되지 않음)
     * @param K 뉴스 알림을 보내는 뉴스 채널의 수 (현재 로직에서는 직접 사용되지 않음)
     */
    void init(int N, int K) {
        channelSubscribers = new HashMap<>();
        newsToChannelMap = new HashMap<>();
        userNotificationQueues = new HashMap<>();
        masterAlarmQueue = new PriorityQueue<>();
        cancelledNewsIdSet = new HashSet<>();
    }

    /**
     * 특정 시각에 한 유저가 여러 뉴스 채널을 구독(등록)히는 메서드
     *
     * @param timestamp   현재 시각 ( 1 ≤ timestamp ≤ 1,000,000,000 )
     * @param userId      유저의 고유 ID ( 1 ≤ userId ≤ 1,000,000,000 )
     * @param numChannels 유저가 구독할 채널의 수 ( 1 ≤ numChannels ≤ 30 )
     * @param channelIds  유저가 구독할 채널 ID 배열 ( 1 ≤ channelIds[] ≤ 1,000,000,000 )
     */
    void registerUser(int timestamp, int userId, int numChannels, int[] channelIds) {
        // 1. `timestamp`까지 도착했어야 할 모든 알림을 먼저 처리합니다.
        processAlarms(timestamp);

        // 2. 주어진 `userId`가 `channelIds` 배열에 있는 각 채널을 구독 처리합니다.
        for (int i = 0; i < numChannels; i++) {
            int channelId = channelIds[i];
            channelSubscribers.computeIfAbsent(channelId, k -> new HashSet<>()).add(userId);
        }
    }

    /**
     * 특정 채널에 새로운 뉴스를 발행하고, 알림을 예약하는 메서드
     *
     * @param timestamp 현재 시각 ( 1 ≤ timestamp ≤ 1,000,000,000 )
     * @param newsId    새 뉴스의 고유 ID (1 ≤ newsId ≤ 1,000,000,000 )
     * @param delay     뉴스가 유저에게 전달되기까지의 지연 시간 ( 1 ≤ delay ≤ 10,000 )
     * @param channelId 뉴스가 발행되는 채널의 ID ( 1 ≤ channelId ≤ 1,000,000,000 )
     * @return 해당 채널(`channelId`)을 구독하고 있는 유저의 수
     */
    int offerNews(int timestamp, int newsId, int delay, int channelId) {
        // 1. `timestamp`까지 도착했어야 할 모든 알림을 먼저 처리합니다.
        processAlarms(timestamp);

        // 2. 새 뉴스를 `newsToChannelMap`에 등록하여 뉴스 ID와 채널 ID를 매핑합니다.
        newsToChannelMap.put(newsId, channelId);

        // 3. 알림이 울릴 시간(`timestamp + delay`)과 뉴스 ID를 담은 `Alarm` 객체를 `masterAlarmQueue`에 추가합니다.
        masterAlarmQueue.add(new Alarm(timestamp + delay, newsId));

        // 4. 해당 채널을 구독하는 유저의 수를 반환합니다.
        return channelSubscribers.getOrDefault(channelId, Collections.emptySet()).size(); // 구독자가 없을 경우 빈 Set을 반환하여 NullPointerException을 방지합니다.
    }

    /**
     * 특정 뉴스의 발행을 취소하는 메서드
     *
     * @param timestamp 현재 시각 ( 1 ≤ timestamp ≤ 1,000,000,000 )
     * @param newsId    취소할 뉴스의 고유 ID (1 ≤ newsId ≤ 1,000,000,000 )
     */
    void cancelNews(int timestamp, int newsId) {
        // 1. `timestamp`까지 도착했어야 할 모든 알림을 먼저 처리합니다.
        processAlarms(timestamp);

        // 2. `newsId`를 `cancelledNewsIdSet`에 추가합니다.
        cancelledNewsIdSet.add(newsId);
    }

    /**
     * 특정 유저가 받은 유효한 알림의 개수와 최신 3개의 뉴스 ID를 확인하는 메서드
     *
     * @param timestamp 현재 시각 ( 1 ≤ timestamp ≤ 1,000,000,000 )
     * @param userId    알림을 확인할 유저의 ID ( 1 ≤ userId ≤ 1,000,000,000 )
     * @param mRetIDs   받은 뉴스 ID를 최신순으로 저장할 배열 (크기는 3으로 보장됨)
     * @return 유저가 받은 유효한(취소되지 않은) 뉴스 알림의 총 개수
     */
    int checkUser(int timestamp, int userId, int[] mRetIDs) {
        // 1. `timestamp`까지 도착했어야 할 모든 알림을 먼저 처리합니다.
        processAlarms(timestamp);

        // 2. 해당 유저의 알림 큐(`userNotificationQueues`)를 가져옵니다.
        PriorityQueue<ReceivedNotification> userMailbox = userNotificationQueues.get(userId);
        if (userMailbox == null || userMailbox.isEmpty()) {
            return 0;
        }

        // 3. 큐에 있는 모든 알림을 확인하며, 취소되지 않은(유효한) 알림만 `validNotifications` 리스트에 추가합니다.
        List<Integer> validNotifications = new ArrayList<>();
        while (!userMailbox.isEmpty()) {
            ReceivedNotification notification = userMailbox.poll();

            if (!cancelledNewsIdSet.contains(notification.newsId)) {
                validNotifications.add(notification.newsId);
            }
        }

        // 4. 유효한 알림의 총 개수를 저장합니다.
        int validNotificationCount = validNotifications.size();

        // 5. `mRetIDs` 배열에 최신순으로 정렬된 뉴스 ID를 최대 3개까지 저장합니다.
        for (int i = 0; i < Math.min(validNotificationCount, MAX_RETURN_NEWS_IDS); i++) {
            mRetIDs[i] = validNotifications.get(i);
        }

        // 6. 확인이 끝난 유저의 알림 큐는 완전히 비웁니다.
        userNotificationQueues.remove(userId);

        return validNotificationCount;
    }

    /**
     * `masterAlarmQueue`에서 현재 시간(`currentTime`) 이전에 발생했어야 할 모든 알림을 처리하는 메서드
     * <br/>
     * 이 메서드는 다른 API 호출 시 가장 먼저 실행되어 시스템의 시간을 동기화합니다.
     *
     * @param currentTime 동기화할 목표 시간
     */
    private void processAlarms(int currentTime) {
        while (!masterAlarmQueue.isEmpty() && masterAlarmQueue.peek().alarmTime <= currentTime) {
            Alarm alarm = masterAlarmQueue.poll();

            // 큐에서 꺼낸 알림이 이미 취소된 뉴스에 대한 것이라면, 무시하고 다음 알림으로 넘어갑니다.
            if (cancelledNewsIdSet.contains(alarm.newsId)) {
                continue;
            }

            Integer channelId = newsToChannelMap.get(alarm.newsId);
            if (channelId == null) continue; // 방어 코드: 뉴스를 발행한 채널 정보가 없을 경우 스킵

            // 이 채널에 구독자가 있는지 확인합니다.
            if (channelSubscribers.containsKey(channelId)) {
                Set<Integer> subscribers = channelSubscribers.get(channelId);
                ReceivedNotification notification = new ReceivedNotification(alarm.alarmTime, alarm.newsId);

                // 채널을 구독하는 모든 유저에게 알림을 전송합니다.
                for (int subscriberId : subscribers) {
                    userNotificationQueues
                            .computeIfAbsent(subscriberId, k -> new PriorityQueue<>())
                            .add(notification);
                }
            }
        }
    }


    // --- 정적 중첩 클래스 (Helper Classes) ---

    /**
     * 발행될 뉴스 알림을 나타내는 클래스. `masterAlarmQueue`에서 사용됩니다.
     * `Comparable`을 구현하여 알림 시간(오름차순), 뉴스 ID(내림차순) 순으로 정렬됩니다.
     */
    private static class Alarm implements Comparable<Alarm> {
        final int alarmTime; // 알림이 울려야 할 정확한 시각
        final int newsId;    // 알림에 포함된 뉴스의 ID

        public Alarm(int alarmTime, int newsId) {
            this.alarmTime = alarmTime;
            this.newsId = newsId;
        }

        @Override
        public int compareTo(Alarm other) {
            // 1. 알림 시간은 오름차순으로 정렬 (더 빠른 시간이 먼저 처리됨)
            if (this.alarmTime != other.alarmTime) {
                return Integer.compare(this.alarmTime, other.alarmTime);
            }

            // 2. 시간이 같다면 뉴스 ID는 내림차순으로 정렬 (ID가 큰 뉴스가 우선순위가 높음)
            return Integer.compare(other.newsId, this.newsId);
        }
    }

    /**
     * 유저가 수신한 뉴스 알림을 나타내는 클래스. `userNotificationQueues`에서 사용됩니다.
     * `Comparable`을 구현하여 수신 시간(내림차순), 뉴스 ID(내림차순) 순으로 정렬되어 "최신순"을 구현합니다.
     */
    private static class ReceivedNotification implements Comparable<ReceivedNotification> {
        final int receivedTime; // 알림을 받은 시각
        final int newsId;       // 알림에 포함된 뉴스의 ID

        public ReceivedNotification(int receivedTime, int newsId) {
            this.receivedTime = receivedTime;
            this.newsId = newsId;
        }

        @Override
        public int compareTo(ReceivedNotification other) {
            // 1. 수신 시간은 내림차순으로 정렬 (더 최근에 받은 알림이 먼저 옴)
            if (this.receivedTime != other.receivedTime) {
                return Integer.compare(other.receivedTime, this.receivedTime);
            }

            // 2. 수신 시간이 같다면 뉴스 ID를 내림차순으로 정렬 (ID가 큰 뉴스가 더 최신으로 간주됨)
            return Integer.compare(other.newsId, this.newsId);
        }
    }
}