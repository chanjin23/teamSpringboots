# Spring-boots : 신발 판매 쇼핑몰

## 프로젝트 소개
Spring-boots는 사용자가 신발을 편리하게 구매할 수 있도록 다양한 기능을 제공하는 웹 기반 쇼핑몰입니다. 관리자 기능, 상품 카테고리 분류, 장바구니 시스템, 주문 기능을 지원하며, 소셜 로그인 및 JWT 기반 보안 인증을 제공합니다.

## 팀원 소개
| 이름   | 역할     | 담당 기능                        |
|--------|---------|----------------------------------|
| 이서율 | Order    | 주문 시스템 |
| 윤호준 | Cart     | 장바구니 도메인 |
| 고범석 | Category | 카테고리 도메인   |
| 이찬진 | User     | 회원 도메인   |
| 차현승 | Product  | 상품 도메인   |

<aside>
<img src="![image](/uploads/5ca2093e2e0d072fc764d88d1693523a/image.png)" alt="![image](/uploads/5ca2093e2e0d072fc764d88d1693523a/image.png)" width="40px" /> **필수 요구사항**

- **회원 도메인 (`User`) - 찬진**
- **카테고리 도메인 (`Category`) - 범석**
- **상품 도메인 (`Product`) - 현승**
- **장바구니 도메인 (`orderItem`) - 호준**
- **주문 도메인 (`Order`) - 서율**
</aside>

## ERD
- [ERD 링크](https://www.erdcloud.com/d/7RXXuJrNBwNyYMd7s)

## 기술 스택
- **Frontend**: Bulma, Javascript
- **Backend**: Spring Boot
- **Database**: MySQL, H2
- **Security**: Spring Security, JWT(JSON Web Token)
- **DevOps**: AWS (EC2, S3, RDS)..?

## 주요 기능
- **사용자**: 회원가입, 로그인, 로그아웃, 소셜 로그인, 관리자 계정 전환
- **카테고리**: 상품 카테고리 등록, 수정, 삭제 기능 제공
- **상품**: 상품 조회, 상품 상세 페이지, 상품 등록/수정/삭제 기능 제공
- **장바구리**: 장바구니 표시, 가격 계산
- **주문**: 결제 정보 입력, 주문 내역 조회

## 배포 환경
```
- 서버 : AWS EC2, GCP VM
    - OS: Ubuntu 24.04 LTS(EC2) / Ubuntu 20.04.6 LTS(GCP)
    - JRE: OpenJDK 22
    - 애플리케이션 서버: Spring Boot (내장 Tomcat 사용)
- 데이터베이스: AWS RDS (MySQL)
    - MySQL 버전: 8.0.35
- 파일 저장소: AWS S3
- 기타 라이브러리 및 도구
    - AWS CLI: AWS 명령줄 인터페이스 v2
```

## 개발 환경
```
- 빌드 도구: Gradle-8.10.2
- JDK 버전: JDK 17
- 버전 관리: Git / Gitlab
- 기타 툴: Postman (API 테스트), Lombok
```



##테스트##

## API 명세서

##회원  

| **MVP**        | **Method** | **URI**                               | **Description**                              | **Cookie**            | **Request Body**                                                                                                                             | **Response**                                                                                                                                                                                                 |
|----------------|------------|---------------------------------------|----------------------------------------------|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| user (비회원)   | POST       | /api/login                            | 로그인                                       |                       | JwtTokenLoginRequestDto { “userRealId” : “string”, “password” : “string” }                                                                   | 200 OK : 인증 성공, JWT 리프레쉬토큰, 엑세스 토큰 반환 <br> 401 Unauthorized : 인증 실패                                                                                                                     |
| user (비회원)   | POST       | /api/signup                           | 회원가입                                     |                       | UserSignupRequestDto { “username” : “string”, “userRealId” : “string”, “password” : “string”, “email” : “string” }                           | 201 Created : 회원 가입 완료 <br> { “message” : “성공적으로 회원가입하셨습니다.” } <br> 400 Bad Request : 잘못된 요청 데이터 <br> { “message” : “잘못된 요청입니다.” }                                          |
| user (회원)     | GET        | /api/users-info                       | 개인 정보 조회(인증된 토큰 발급 시)            | accessToken : {user_token} <br> refreshToken : {user_token} |                                                                                                                      | 200 OK : 개인정보조회 <br> { “userId” : “Long”, “username” : “String”, “userRealId” : “String”, “email” : “String”, “role” : “String”, “createdAt” : “String”, “userInfoList” : { “address” : “String”, “streetAddress” : “String”, “detailedAddress” : “String”, “phone” : “String” } } <br> 404 NOT_FOUND : 인증정보를 불러올 수 없음 <br> 400 BAD_REQUEST : 잘못된 요청 |
| user (회원)     | PATCH      | /api/users/{userInfoId}               | 개인 정보 수정 (이름, 아이디는 변경 불가능)    | accessToken : {user_token} <br> refreshToken : {user_token} | UserUpdateRequestDto { “currentPassword” : “String”, “updatePassword” : “String”, “email” : “string”, “address” : { “address” : “String”, “streetAddress” : “String”, “detailedAddress” : “String”, “phone” : “String” } } | 200 OK : 회원정보 수정 완료 <br> { “message” : “정상적으로 수정되었습니다.” } <br> 400 Bad Request : 잘못된 데이터 요청 <br> { “message” : “잘못된 데이터 요청입니다.” }                                       |
| user (회원)     | DELETE     | /api/users-soft/{id}                  | 회원 탈퇴 (soft-delete)                       | accessToken : {user_token} <br> refreshToken : {user_token} |                                                                                                                      | 200 OK : 회원 탈퇴 완료 <br> { “message” : “회원탈퇴 성공” } <br> 400 Bad Request : 탈퇴 실패 <br> { “message” : “오류 발생” }                                                                                   |
| user (회원)     | POST       | /api/users/check-password             | 비밀번호 확인 (회원 정보 삭제)                 |                       | UserPasswordRequestDto { “password” : “string” }                                                                                              | 200 OK : 비밀번호 확인 완료 <br> 401 Unauthorized : 인증 실패                                                                                                                                                 |
| user (회원)     | POST       | /api/logout                           | 로그아웃                                      |                       |                                                                                                                      | 200 OK : 로그아웃 <br> 401 Unauthorized : 권한 없음                                                                                                                                                          |
| user (비회원)   | GET        | /api/signup/checkId?user_real_id={user_real_id} | 아이디 중복확인 (회원가입 시)                  |                       |                                                                                                                      | 200 OK : 중복체크 완료 <br> { “is_available” : true, “message” : “사용할 수 있는 아이디입니다.” } <br> 409 Conflict : { “is_available” : false, “message” : “이미 사용 중인 아이디입니다.” }                 |
| admin          | GET        | /api/admin/users                      | 모든 회원 정보 조회                          | accessToken : {admin_token} <br> refreshToken : {admin_token} |                                                                                                                      | 200 OK : 모든 사용자 정보 반환 (JSON) <br> List<AllUsersInfoResponseDto> { “userId” : “Long”, “userRealId” : “String”, “created_at” : “string”, “email” : “string”, “role” : “string”, “provider” : “string”, “username” : “string” }, … <br> 403 Forbidden : 관리자 권한 필요 <br> 500 Internal Server Error : 서버 오류 발생                  |
| admin          | GET        | /api/admin/users/{user_id}            | 특정 회원 정보 조회                          | accessToken : {admin_token} <br> refreshToken : {admin_token} |                                                                                                                      | 200 OK : 특정 사용자 정보 반환 (JSON) <br> UserInfoResponseDto { “created_at” : “string”, “email” : “string”, “role” : “string”, “provider” : “string”, “username” : “string” } <br> 404 Not Found : 해당 사용자 찾을 수 없음                                                                 |
| admin          | GET        | /api/users/admin-check                | 관리자 확인 API                              | accessToken : {admin_token} <br> refreshToken : {admin_token} |                                                                                                                      | 401 Unauthorized : 인증되지 않은 사용자 <br> { “message” : “현재 엑세스 토큰이 없습니다.” } <br> 200 OK : 인증 성공 <br> { “message” : “관리자 인증 성공” } <br> 403 Forbidden : 인증 실패 <br> { “message” : “관리자 인증 실패” } |
| admin          | POST       | /api/users/grant                      | 관리자 코드 체크 API                          | accessToken : {admin_token} <br> refreshToken : {admin_token} |                                                                                                                      | 200 OK : 인증 성공 <br> { “message” : “success” } <br> 401 Unauthorized : 인증 실패 <br> { “message” : “fail” }                                                                                              |


##상품
| **MVP** | **Method** | **URI**                              | **Description**                         | **Request Body**                                                                                                                                      | **Request Params**                                                                                                                                                                           | **Response**                                                                                                                       |
|---------|------------|--------------------------------------|-----------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| Item    | POST       | /api/admin/items                     | 관리자 제품 추가                         | { “id” : “Long”, “item_name” : “string”, “category_id” : “Long”, “item_maker” : “string”, “item_price” : “int”, “item_description” : “string”, “item_color” : “string”, “item_size” : “int”, “created_at” : “datetime”, “updated_at” : “datetime”, “image_url” : “string”, “keywords” : “List<string>” } | @ModelAttribute CreateItemDto requestItemDto, @RequestParam("file") MultipartFile file                                                                  | 200 OK : { “message” : “등록이 완료되었습니다.” } <br> 400 Bad Request : { “error” : “내용이 충분하지 않습니다.” }                     |
| Item    | PUT        | /api/items/{items_id}                | 제품 수정                               | { “id” : “Long”, “item_name” : “string”, “category_id” : “Long”, “item_maker” : “string”, “item_price” : “int”, “item_description” : “string”, “item_color” : “string”, “item_size” : “int”, “created_at” : “datetime”, “updated_at” : “datetime”, “image_url” : “string”, “keywords” : “List<string>” } | @PathVariable("itemId") Long id, @ModelAttribute UpdateItemDto updateItemDto                                                                             | 200 OK : { “message” : “수정이 완료되었습니다.” } <br> 400 Bad Request : { “error” : “내용이 충분하지 않습니다.” }                     |
| Item    | GET        | /api/items/{items_id}                | 제품 상세보기                            |                                                                                                                                                       | @PathVariable("itemId") Long id                                                                                                                        | 200 OK : 제품 상세보기 완료 <br> 410 Gone : { “error” : “제품이 더이상 존재하지 않습니다.” }                                           |
| Item    | DELETE     | /api/items/{items_id}                | 제품 삭제                               |                                                                                                                                                       | @PathVariable("itemId") Long id                                                                                                                        | 200 OK : { “message” : “제품 삭제를 완료했습니다.” }                                                                                   |
| Item    | GET        | /api/items/categories/{category_id}  | 카테고리 ID별 상품 조회                  |                                                                                                                                                       | @PathVariable("category_id") Long categoryId, @RequestParam(required = false, defaultValue = "default") String sort, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int limit |                                                                                                                                   |
| Item    | GET        | /api/items/thema/{thema}             | 특정 테마에 속하는 상품 조회             |                                                                                                                                                       | @PathVariable("thema") String thema, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int limit, @RequestParam(defaultValue = "default") String sort             |                                                                                                                                   |
| Item    | GET        | /api/items/search                    | 특정 키워드를 갖는 상품 조회             |                                                                                                                                                       | @RequestParam String keyword, @RequestParam(required = false) String sort, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int limit                          |                                                                                                                                   |
| Item    | GET        | /api/items/list/search/name          | 상품 목록 페이지에서 특정 상품명을 갖는 상품 조회 |                                                                                                                                                       | @RequestParam String itemName, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size                                 |                                                                                                                                   |
| Item    | GET        | /api/items                           | 상품 전체 조회                           |                                                                                                                                                       | @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size                                                                                                        |                                                                                                                                   |


##카테고리

| **MVP**     | **Method** | **URI**                             | **Description**                    | **Request Body**                                                                                                                                                                                        | **Request Params**                        | **Response**                                                                                                                   |
|-------------|------------|-------------------------------------|------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| category    | POST       | /api/admin/categories               | 관리자 - 새 카테고리 추가           | CategoryRequestDto { "category_name": "string", "category_thema": "string", "image_url": "string", "display_order": "int" }                                                                             |                                           | 201 Created: 생성된 카테고리 정보 반환 <br> 400 Bad Request: { "errorCode": "필수_파라미터_누락", "errorMessage": "카테고리 이름은 필수 항목입니다." } <br> 401 Unauthorized: 인증 실패 |
| category    | PUT        | /api/admin/categories/{category_id}  | 관리자 - 카테고리 정보 수정         | CategoryRequestDto { "category_name": "string", "category_thema": "string", "image_url": "string", "display_order": "int" }                                                                             | category_id (PathVariable)               | 200 OK: 수정된 카테고리 정보 반환 <br> 404 Not Found: 카테고리가 존재하지 않음 <br> 400 Bad Request: { "errorCode": "파라미터_길이_초과", "errorMessage": "카테고리 이름은 50자를 초과할 수 없습니다." } <br> 401 Unauthorized: 인증 실패 |
| category    | DELETE     | /api/admin/categories/{category_id}  | 관리자 - 카테고리 삭제             |                                                                                                                                                                                                         | category_id (PathVariable)               | 204 No Content: 삭제 성공 <br> 404 Not Found: { "errorCode": "리소스_없음", "errorMessage": "삭제할 카테고리를 찾을 수 없습니다." } <br> 401 Unauthorized: { "errorCode": "권한_없음", "errorMessage": "카테고리 삭제 권한이 없습니다." } |
| category    | GET        | /api/categories/themas              | 모든 카테고리 테마 목록 조회         |                                                                                                                                                                                                         |                                           | 200 OK: 카테고리 테마 목록 반환                                                                                                 |
| category    | GET        | /api/categories/themas/{thema}      | 특정 테마의 카테고리 목록 조회      |                                                                                                                                                                                                         | category_thema (PathVariable)            | 200 OK: 카테고리 테마 목록 반환                                                                                                |
| category    | GET        | /api/categories/{category_id}       | 카테고리 상세 조회                 |                                                                                                                                                                                                         | category_id (PathVariable)               | 200 OK: 카테고리 상세 정보 반환 { “id”: “Long”, "category_name": "string", "category_content": "string", "category_thema": "string", "image_url": "string", "subcategories": [ { "id": "Long", "category_name": "string" } ] } <br> 404 Not Found: 카테고리가 존재하지 않음 |
| category    | GET        | /api/admin/categories               | 관리자 카테고리 전체 목록 조회 (페이지네이션) |                                                                                                                                                                                                         | @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit  | 200 OK: 모든 카테고리 목록 반환 { "total": "Long", "page": "int", "size": "int", "categories": [ { "id": "Long", "category_name": "string", "category_content": "string", "category_thema": "string", "image_url": "string", "display_order": "int" } ] } <br> 400 Bad Request: { "errorCode": "잘못된_파라미터_형식", "errorMessage": "페이지 번호는 1 이상의 정수여야 합니다." } <br> 401 Unauthorized: { "errorCode": "인증_실패", "errorMessage": "관리자 권한이 필요합니다." } |
| event       | GET        | /api/events                        | 종료되지 않은 이벤트 목록 조회 (페이지네이션) |                                                                                                                                                                                                         | @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit | 200 OK: 이벤트 목록 반환 { "total": "Long", "page": "int", "size": "int", "events": [ { "id": "Long", "title": "string", "thumbnail_image_url": "string", "content_image_url": "string", "start_date": "date", "end_date": "date" } ] } <br> 400 Bad Request: { "errorCode": "잘못된_파라미터_형식", "errorMessage": "페이지당 항목 수는 1에서 100 사이여야 합니다." } |
| event       | GET        | /api/events/{event_id}             | 이벤트 상세 조회                    |                                                                                                                                                                                                         | event_id (PathVariable)                 | 200 OK: 이벤트 상세 정보 반환 { “id”: “Long”, “title”: “string”, “content”: “string”, “thumbnail_image_url”: “string”, “content_image_url”: “string”, “start_date”: “date”, “end_date”: “date”, “is_active”: “boolean” } <br> 404 Not Found: 이벤트가 존재하지 않음 |
| event       | POST       | /api/events                        | 새 이벤트 생성                      | EventRequestDto { “title”: “string”, “content”: “string”, “thumbnail_image_url”: “string”, “content_image_url”: “string”, “start_date”: “date”, “end_date”: “date”, “is_active”: “boolean” }            |                                           | 201 Created: 생성된 이벤트 정보 반환 <br> 400 Bad Request: { "errorCode": "유효하지_않은_날짜", "errorMessage": "이벤트 종료일은 시작일 이후여야 합니다." } <br> 401 Unauthorized: 인증 실패 |
| event       | PUT        | /api/events/{event_id}             | 이벤트 정보 수정                    | EventRequestDto { “title”: “string”, “content”: “string”, “thumbnail_image_url”: “string”, “content_image_url”: “string”, “start_date”: “date”, “end_date”: “date” }                                    | event_id (PathVariable)                 | 200 OK: 수정된 이벤트 정보 반환 <br> 404 Not Found: { "errorCode": "리소스_없음", "errorMessage": "수정할 이벤트를 찾을 수 없습니다." } <br> 400 Bad Request: { "errorCode": "파라미터_길이_초과", "errorMessage": "이벤트 제목은 100자를 초과할 수 없습니다." } <br> 401 Unauthorized: 인증 실패 |
| event       | DELETE     | /api/events/{event_id}             | 이벤트 삭제                         |                                                                                                                                                                                                         | event_id (PathVariable)                 | 204 No Content: 삭제 성공 <br> 404 Not Found: { "errorCode": "리소스_없음", "errorMessage": "삭제할 이벤트를 찾을 수 없습니다." } <br> 401 Unauthorized: { "errorCode": "권한_없음", "errorMessage": "이벤트 삭제 권한이 없습니다." } |



### 주문 API 문서

| MVP     | Method                     | URI                           | 설명                                                                                                     | 요청 본문                                                                                                                                             | 요청 파라미터 | 응답                                                                                                                                                              |
|---------|----------------------------|-------------------------------|---------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **order** | `GET`                       | `/api/orders`                | 사용자의 주문 목록을 조회합니다.                                                                         |                                                                                                                                                      |                | 응답: 200 OK: 조회 완료.<br>401 Unauthorized: 사용자 인증이 필요함.<br>404 Not Found: 주문을 찾을 수 없는 경우.<br><br>응답 본문: <br>[ { "orders_id": "int", "created_at": "datetime", "orders_total_price": "int", "order_status": "string", "shipping_address": "string", "delivery_fee": "int", "quantity": "int", "items": [ { "item_name": "string", "orderitems_quantity": "int", "orderitems_total_price": "int" } ] } ] |
| **order** | `GET`                       | `/api/orders/{orders_id}`    | 특정 주문 번호(orders_id)에 해당하는 주문의 상세 정보를 조회합니다.                                       |                                                                                                                                                      | `orders_id`    | 응답: 200 OK: 주문 상세 조회 완료.<br>404 Not Found: 주문 ID가 존재하지 않는 경우.<br>401 Unauthorized: 사용자 인증이 필요함.<br><br>응답 본문: <br>{ "orders_id": "int", "created_at": "datetime", "orders_total_price": "int", "order_status": "string", "shipping_address": "string", "recipient_name": "string", "recipient_contact": "string", "delivery_fee": "int", "quantity": "int", "items": [ { "item_name": "string", "orderitems_quantity": "int", "orderitems_total_price": "int", "item_image": "string" } ] } |
| **order** | `POST`                      | `/api/orders`                | 사용자가 장바구니를 바탕으로 새 주문을 추가합니다.                                                     | { "user_id": "int", "shipping_address": "string", "recipient_name": "string", "recipient_contact": "string", "delivery_message": "string", "items": [ { "item_id": "int", "item_quantity": "int", "item_size": "int" } ] } |                | 응답: 201 Created: 주문 추가.<br>400 Bad Request: 요청 본문이 잘못됨 (수취인 정보, 배송 정보 누락, 주문 상품이 없음).<br>401 Unauthorized: 사용자 인증이 필요함.<br><br>응답 본문: <br>{ "orders_id": "int", "status": "주문이 성공적으로 생성되었습니다." } |
| **order** | `PUT`                       | `/api/orders/{orders_id}`    | 사용자가 주문을 수정합니다 (배송 시작 전까지 가능).                                                    | { "shipping_address": "string", "recipient_name": "string", "recipient_contact": "string" }                                                       | `orders_id`    | 응답: 200 OK: 주문 수정 완료.<br>404 Not Found: 주문 ID가 존재하지 않는 경우.<br>403 Forbidden: 사용자가 권한이 없는 경우.<br><br>응답 본문: <br>{ "orders_id": "int", "status": "주문이 성공적으로 수정되었습니다." } |
| **order** | `DELETE`                    | `/api/orders/{orders_id}`    | 사용자가 배송 전 주문을 취소합니다.                                                                     |                                                                                                                                                      | `orders_id`    | 응답: 200 OK: 주문 취소 완료.<br>404 Not Found: 주문 ID가 존재하지 않는 경우.<br>403 Forbidden: 사용자가 권한이 없는 경우.<br><br>응답 본문: <br>{ "orders_id": "int", "status": "주문이 성공적으로 취소되었습니다." } |
| **order** | `GET`                       | `/api/admin/orders`          | 관리자가 모든 주문을 조회합니다.                                                                         |                                                                                                                                                      |                | 응답: 200 OK: 조회 완료.<br>401 Unauthorized: 인증이 필요한 경우.<br>403 Forbidden: 관리자가 아닌 사용자가 접근한 경우.<br>404 Not Found: 주문을 찾을 수 없는 경우.<br><br>응답 본문: <br>[ { "orders_id": "int", "user_id": "int", "created_at": "datetime", "orders_total_price": "int", "order_status": "string", "shipping_address": "string", "recipient_name": "string", "recipient_contact": "string", "delivery_fee": "int" } ] |
| **order** | `PATCH`                     | `/api/admin/orders/{orders_id}/status` | 관리자가 주문 상태를 수정합니다.                                                                       | { "orders_status": "string" }                                                                                                                        | `orders_id`    | 응답: 200 OK: 주문 상태 수정 완료.<br>400 Bad Request: 요청 본문이 잘못된 경우.<br>404 Not Found: 주문 ID가 존재하지 않는 경우.<br>403 Forbidden: 관리자가 아닌 사용자가 접근한 경우.<br><br>응답 본문: <br>{ "orders_id": "int", "status": "주문 상태가 성공적으로 수정되었습니다." } |
| **order** | `DELETE`                    | `/api/admin/orders/{orders_id}` | 관리자가 주문을 삭제합니다.                                                                             |                                                                                                                                                      | `orders_id`    | 응답: 200 OK: 삭제 완료.<br>404 Not Found: 주문 ID가 존재하지 않는 경우.<br>403 Forbidden: 관리자가 아닌 사용자가 접근한 경우.<br><br>응답 본문: <br>{ "orders_id": "int", "status": "주문이 성공적으로 삭제되었습니다." } |
