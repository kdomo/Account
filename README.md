# 계좌(Account) 시스템
계좌시스템의 전반적인 구조와 기능 이해를 위한 Repository

---

### 활용 기능 소개
- Spring Boot 2.7.4 (JDK11)
- Gradle
- Junit5
- H2 Database
- JPA
- Redis(Redisson)
- Mockit

--- 

<details>
<summary>API</summary>
<div markdown="1">

#### 1.계좌 생성
##### 정보
|컬럼명|데이터타입|설명
|--|--|--|
|id|pk|primary key|
|accountUser|AccountUser|소유자 정보, 사용자 테이블과 연결(n:1)|
|accountNumber|String|계좌 번호(자유도를 위해 문자로)|
|accountStatus|AccountStatus|계좌 상태(IN_USE, UNREGISTERED)|
|balance|Long|계좌 잔액|
|registeredAt|LocalDateTime|계좌 등록일시|
|unregistedAt|LocalDateTime|계좌 해지일시|
|createdAt|LocalDateTime|생성일시|
|updatedAt|LocalDateTime|최종 수정일시|

##### 요청
```json
POST /account
{
  "userId":1,
  "initBalance":100
}
```

##### 응답
```json
{
  "userId":1,
  "accountNumber":"1234567890",
  "registeredAt":"2022-06-01T23:26:14.671859"
}
```

<br>

#### 2.계좌 해지
##### 정보
- 계좌 상태 수정 : IN_USE -> UNREGISTERED
- 계좌 해지일시 수정 : NULL -> 현재시간

##### 요청
```json
DELETE /account
{
  "userId":1,
  "accountNumber":"1000000000"
}
```

##### 응답
```json
{
  "userId":1,
  "accountNumber":"1000000000",
  "unRegisteredAt":"2022-06-04T20:26:14.671859"
}
```

<br>

#### 3.계좌 확인
##### 정보
- 저장이 필요한 정보는 없음

##### 요청
```json
GET /account?user_id={userId}
```

##### 응답
```json
[
  {
    "accountNumber":"1000000000"
    "balance": 1000
  },
  {
    "accountNumber":"1000000001"
    "balance": 1000
  },
]
```

<br>

#### 4.잔액 사용
##### 정보
|컬럼명|데이터타입|설명
|--|--|--|
|id|pk|primary key|
|transactionType|TransactionType|거래의 종류 (사용, 사용취소)|
|transactionResultType|TransactionResultType|거래 결과 (성공, 실패)|
|account|Account|거래가 발생한 계좌(N:1 연결)|
|amount|Long|거래 금액|
|balanceSnapshot|Long|거래 후 계좌 잔액|
|transactionId|String|계좌 해지일시|
|transactedAt|LocalDateTime|거래일시|
|createdAt|LocalDateTime|생성일시|
|updatedAt|LocalDateTime|최종 수정일시|

##### 요청
```json
POST /transaction/use
{
  "userId":1,
  "accountNumber":"1000000000",
  "amount":1000
}
```

##### 응답
```json
{
  "accountNumber":"1234567890",
  "transactionResult":"S",
  "transactionId":"c2033bb6d82a4250aecf8e27c49b63f6",
  "amount":1000,
  "transactedAt":"2022-06-01T23:26:14.671859"
}
```

<br>

#### 5.잔액 사용 취소
##### 정보
- transaction 테이블에 잔액사용취소(CANCEL) 거래 정보 저장

##### 요청
```json
POST /transaction/cancel
{
  "transactionId":"c2033bb6d82a4250aecf8e27c49b63f6",
  "accountNumber":"1000000000",
  "amount":1000
}
```

##### 응답
```json
{
  "accountNumber":"1000000000",
  "transactionResult":"S",
  "transactionId":"5d011bb6d82cc50aecf8e27cdabb6772",
  "amount":1000,
  "transactedAt":"2022-06-01T23:26:14.671859"
}
```

<br>

#### 6.잔액 사용 확인
##### 정보
- 저장이 필요한 정보는 없음

##### 요청
```json
GET /transaction/{transactionId}
```

##### 응답
```json
{
  "accountNumber":"1000000000",
  "transactionType":"USE",
  "transactionResult":"S",
  "transactionId":"5d011bb6d82cc50aecf8e27cdabb6772",
  "amount":1000,
  "transactedAt":"2022-06-01T23:26:14.671859"
}
```

</div>
</details>

<details>
<summary>예외처리</summary>
<div markdown="1">

##### 예외 응답 형식

```json
{
  "errorCode": "USER_NOT_FOUND",
  "errorMessage": "사용자가 없습니다."
}
```

##### 예외 코드
|Code|Description|
|---|---|
|INTERNAL_SERVER_ERROR|내부 서버 오류가 발생했습니다.|
|INVALID_REUQEST|잘못된 요청입니다.|
|USER_NOT_FOUND|사용자가 없습니다.|
|ACCOUNT_NOT_FOUND|계좌가 없습니다.|
|ACCOUNT_TRANSACTION_LOCK|해당 계좌는 사용 중입니다.|
|TRANSACTION_NOT_FOUND|해당 거래가 없습니다.|
|MAX_ACCOUNT_PER_USER_10|사용자 최대 계좌는 10개입니다.|
|ACCOUNT_ALREADY_UNREGISTERED|계좌가 이미 해지되었습니다.|
|BALANCE_NOT_EMPTY|잔액이 있는 계좌는 해지할 수 없습니다.|
|AMOUNT_EXCEED_BALANCE|거래 금액이 계좌 잔액보다 큽니다.|
|USER_ACCOUNT_UN_MATCH|사용자와 계좌의 소유주가 다릅니다.|
|TRANSACTION_ACCOUNT_UN_MATCH|이 거래는 해당 계좌에서 발생한 거래가 아닙니다.|
|CANCEL_MUST_FULLY|부분 취소는 허용되지 않습니다.|
|TOO_OLD_ORDER_TO_CANCEL|1년이 지난 거래는 취소가 불가능합니다.|

</div>
</details>
