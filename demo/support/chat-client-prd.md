`ApiController`의 `postChats()` 메서드가 제공하는 REST API를 사용하는 SPA 방식의 채팅 클라이언트를 만들어 줘.

요구사항:

* HTML, CSS, Vanilla JavaScript 사용
* Spring Boot 정적 리소스 경로인 `src/main/resources/static`에 생성
* 화면 상단에 데모용 `고객명` 입력란과 `새 대화` 버튼 표시
* API 호출 시 고객명은 `username`, 채팅 식별값은 `conversationId`로 각각 전달
* 사용자 메시지는 오른쪽, AI 메시지는 왼쪽 말풍선으로 표시
* AI 응답은 하나의 말풍선에 실시간으로 누적
* Enter는 전송, Shift+Enter는 줄바꿈
* 응답 중에는 전송 버튼을 중지 버튼으로 변경하고 스트리밍 중지
* 모바일과 데스크톱에서 사용할 수 있는 간단한 채팅 UI로 구현