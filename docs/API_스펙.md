# API Spec
작성자 : 홍석호

작성일 : 2024.04.04

1. [공통 성공 응답](#공통-성공-응답)
2. [공통 실패 응답](#공통-실패-응답)
3. [잔액 충전](#잔액-충전)
4. [잔액 조회](#잔액-조회)
5. [상품 목록 조회](#상품-목록-조회)
6. [선착순 쿠폰 발급](#선착순-쿠폰-발급)
7. [보유 쿠폰 목록 조회](#보유-쿠폰-목록-조회)
8. [주문](#주문)
9. [결제](#결제)
10. [상위 상품 조회](#상위-상품-조회)

## 공통 성공 응답

```json
{
	"resultCode": "00",
  "message": "success",
  "data": {}
}

```

## 공통 실패 응답

```json
{
	"errorCode": "01",
  "message": "fail",
  "errorDescription": "결제 오류가 발생했습니다."
}

```

## 잔액 충전

엔드포인트

- POST `/api/v1/point/123/charge`

요청

- request body

```json
{
	"userId": 1234,
	"amount": 1230
}

```

응답

```json
{
  "resultCode": "00",
  "message": "success",
  "data": {
    "userId": 1234,
    "amount": 5230
  }
}

```

## 잔액 조회

- GET `/api/v1/points`

요청

- request param
- `/api/v1/point/123`

응답

```json
{
  "resultCode": "00",
  "message": "success",
  "data": {
    "userId": 123,
    "amount": 5230
  }
}

```

## 상품 목록 조회

- GET `/api/v1/products`

응답

```json
{
  "resultCode": "SUCCESS",
  "message": "상품 목록 조회 성공",
  "data": [
    {
      "id": 1,
      "name": "아메리카노",
      "description": "깔끔하고 깊은 맛의 에스프레소에 물을 더한 커피",
      "basePrice": 4500,
      "options": [
        {
          "id": 1,
          "name": "ICE",
          "description": "차갑게 즐기는 아메리카노",
          "additionalPrice": 500,
          "inventory": 100
        },
        {
          "id": 2,
          "name": "HOT",
          "description": "뜨겁게 즐기는 아메리카노",
          "additionalPrice": 0,
          "inventory": 100
        }
      ]
    },
    {
      "id": 2,
      "name": "카페라떼",
      "description": "에스프레소와 스팀 밀크가 어우러진 부드러운 커피",
      "basePrice": 5000,
      "options": [
        {
          "id": 3,
          "name": "ICE",
          "description": "차갑게 즐기는 카페라떼",
          "additionalPrice": 500,
          "inventory": 80
        },
        {
          "id": 4,
          "name": "HOT",
          "description": "뜨겁게 즐기는 카페라떼",
          "additionalPrice": 0,
          "inventory": 80
        }
      ]
    },
    {
      "id": 3,
      "name": "바닐라 라떼",
      "description": "바닐라 시럽이 첨가된 달콤한 라떼",
      "basePrice": 5500,
      "options": [
        {
          "id": 5,
          "name": "ICE",
          "description": "차갑게 즐기는 바닐라 라떼",
          "additionalPrice": 500,
          "inventory": 70
        },
        {
          "id": 6,
          "name": "HOT",
          "description": "뜨겁게 즐기는 바닐라 라떼",
          "additionalPrice": 0,
          "inventory": 70
        }
      ]
    }
  ]
}

```

## 선착순 쿠폰 발급

- POST `/api/v1/coupons/issue`

요청

- request body

```json
{
	"userId": 1234,
	"couponId": 1230
}

```

응답

```json
{
  "resultCode": "SUCCESS",
  "message": "쿠폰 발급 성공",
  "data": {
    "userId": 1001,
    "couponId": 10,
    "userCouponId": 504,
    "couponName": "봄맞이 이벤트 쿠폰",
    "discountRate": 0.0,
    "discountAmount": 2000,
    "couponType": "FIXED",
    "from": "2025-04-04T00:00:00Z",
    "to": "2025-05-04T23:59:59Z",
    "createdAt": "2025-04-04T15:20:30Z"
  }
}

```

## 보유 쿠폰 목록 조회

- GET `/api/v1/coupons`

응답

```json
{
  "resultCode": "SUCCESS",
  "message": "쿠폰 목록 조회 성공",
  "data": [
    {
      "userId": 1001,
      "userCouponId": 501,
      "couponName": "신규 가입 할인",
      "discountRate": 10.0,
      "discountAmount": 0,
      "couponType": "PERCENTAGE",
      "from": "2025-01-01T00:00:00Z",
      "to": "2025-12-31T23:59:59Z",
      "createdAt": "2025-03-15T10:30:00Z"
    },
    {
      "userId": 1001,
      "userCouponId": 502,
      "couponName": "생일 축하 쿠폰",
      "discountRate": 0.0,
      "discountAmount": 3000,
      "couponType": "FIXED",
      "from": "2025-04-01T00:00:00Z",
      "to": "2025-04-30T23:59:59Z",
      "createdAt": "2025-04-01T00:00:00Z"
    },
    {
      "userId": 1001,
      "userCouponId": 503,
      "couponName": "VIP 회원 특별 할인",
      "discountRate": 15.0,
      "discountAmount": 0,
      "couponType": "PERCENTAGE",
      "from": "2025-04-01T00:00:00Z",
      "to": "2025-04-30T23:59:59Z",
      "createdAt": "2025-04-01T00:00:00Z"
    }
  ]
}

```

## 주문

- POST `/api/v1/orders`

요청

- request body

```json
{
	"userId": 1234,
	"amountProductOptions": [
		{
            "couponId": 1230,
			"productOptionId": 1,
			"amount": 5
		}
	]
}

```

응답

```json
{
  "resultCode": "SUCCESS",
  "message": "주문 생성 성공",
  "data": {
    "orderId": 2001,
    "userId": 1001,
    "status": "CREATED",
    "totalPrice": 16000,
    "discountPrice": 1000,
    "finalPrice": 15000,
    "itemInfos": [
      {
        "orderItemId": 3001,
        "productId": 1,
        "productName": "아메리카노",
        "productDescription": "깔끔하고 깊은 맛의 에스프레소에 물을 더한 커피",
        "productOptionId": 1,
        "productOptionName": "ICE",
        "productOptionDescription": "차갑게 즐기는 아메리카노",
        "basePrice": 4500,
        "additionalPrice": 500,
        "totalPrice": 10000,
        "discountPrice": 1000,
        "finalPrice": 9000,
        "quantity": 2,
        "couponInfo": {
          "couponId": 501,
          "couponName": "신규 가입 할인",
          "discountRate": 10.0,
          "discountAmount": 0,
          "couponType": "PERCENTAGE"
        }
      },
      {
        "orderItemId": 3002,
        "productId": 2,
        "productName": "카페라떼",
        "productDescription": "에스프레소와 스팀 밀크가 어우러진 부드러운 커피",
        "productOptionId": 3,
        "productOptionName": "ICE",
        "productOptionDescription": "차갑게 즐기는 카페라떼",
        "basePrice": 5000,
        "additionalPrice": 500,
        "totalPrice": 5500,
        "discountPrice": 0,
        "finalPrice": 5500,
        "quantity": 1,
        "couponInfo": null
      }
    ]
  }
}

```

## 결제

- GET `/api/v1/orders/{orderId}/payment`

요청

- request body

```json
{
	"userId": 1234
}

```

응답

```json
{
  "resultCode": "SUCCESS",
  "message": "주문 결제 성공",
  "data": {
    "orderId": 2001,
    "userId": 1001,
    "amount": 15000,
    "status": "PAID",
    "remainingPoint": 20000,
    "createdAt": "2025-04-04T15:30:45Z"
  }
}


```

## 상위 상품 조회

- GET `/api/v1/point`

요청

- request param
- `/api/v1/point?userId=123`

응답

```json
{
  "resultCode": "SUCCESS",
  "message": "상위 판매 상품 조회 성공",
  "data": {
    "from": "2025-03-01T00:00:00Z",
    "to": "2025-03-31T23:59:59Z",
    "topSellingProducts": [
      {
        "rank": 1,
        "productId": 1,
        "productOptionId": 1,
        "name": "아메리카노 ICE",
        "description": "차갑게 즐기는 아메리카노",
        "basePrice": 4500,
        "additionalPrice": 500,
        "quantity": 350
      },
      {
        "rank": 2,
        "productId": 2,
        "productOptionId": 3,
        "name": "카페라떼 ICE",
        "description": "차갑게 즐기는 카페라떼",
        "basePrice": 5000,
        "additionalPrice": 500,
        "quantity": 280
      },
      {
        "rank": 3,
        "productId": 3,
        "productOptionId": 5,
        "name": "바닐라 라떼 ICE",
        "description": "차갑게 즐기는 바닐라 라떼",
        "basePrice": 5500,
        "additionalPrice": 500,
        "quantity": 220
      }
    ]
  }
}

```