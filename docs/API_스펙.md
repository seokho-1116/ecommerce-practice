# API Spec

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
  "resultCode": "00",
  "message": "success",
  "data": {
    "products": [
      {
        "id": 1001,
        "name": "스마트폰 A",
        "description": "최신 스마트폰 A 모델",
        "basePrice": 800000,
        "options": [
          {
            "id": 10011,
            "name": "블랙/128GB",
            "description": "블랙 색상, 128GB 저장공간",
            "additionalPrice": 0,
            "inventory": 150
          },
          {
            "id": 10012,
            "name": "화이트/256GB",
            "description": "화이트 색상, 256GB 저장공간",
            "additionalPrice": 200000,
            "inventory": 75
          }
        ]
      },
      {
        "id": 1002,
        "name": "노트북 B",
        "description": "고성능 노트북 B 모델",
        "basePrice": 1200000,
        "options": [
          {
            "id": 10021,
            "name": "i5/8GB/256GB",
            "description": "인텔 i5, 8GB RAM, 256GB SSD",
            "additionalPrice": 0,
            "inventory": 50
          },
          {
            "id": 10022,
            "name": "i7/16GB/512GB",
            "description": "인텔 i7, 16GB RAM, 512GB SSD",
            "additionalPrice": 400000,
            "inventory": 30
          }
        ]
      }
    ]
  }
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
  "resultCode": "00",
  "message": "success",
  "data": {
    "couponId": 1230,
    "userId": 1234,
    "userCouponId": 9876,
    "name": "봄맞이 할인 쿠폰",
    "discountRate": 10,
    "discountAmount": null,
    "couponType": "PERCENTAGE",
    "fromTs": "2025-04-01T00:00:00Z",
    "toTs": "2025-04-30T23:59:59Z",
    "createdAt": "2025-04-04T15:10:22Z"
  }
}

```

## 보유 쿠폰 목록 조회

- GET `/api/v1/coupons`

응답

```json
{
  "resultCode": "00",
  "message": "success",
  "data": {
    "coupons": [
      {
        "userCouponId": 9876,
        "couponId": 1230,
        "name": "봄맞이 할인 쿠폰",
        "discountRate": 10,
        "discountAmount": null,
        "couponType": "PERCENTAGE",
        "fromTs": "2025-04-01T00:00:00Z",
        "toTs": "2025-04-30T23:59:59Z",
        "isUsed": false,
        "createdAt": "2025-04-04T15:10:22Z"
      },
      {
        "userCouponId": 9875,
        "couponId": 1229,
        "name": "신규 회원 할인",
        "discountRate": null,
        "discountAmount": 10000,
        "couponType": "FIXED",
        "fromTs": "2025-03-15T00:00:00Z",
        "toTs": "2025-05-15T23:59:59Z",
        "isUsed": false,
        "createdAt": "2025-03-20T10:45:12Z"
      }
    ]
  }
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
  "resultCode": "00",
  "message": "success",
  "data": {
    "orderId": 5001,
    "userId": 1234,
    "status": "CREATED",
    "totalPrice": 1400000,
    "discountPrice": 140000,
    "finalPrice": 1260000,
    "items": [
      {
        "orderItemId": 7001,
        "productId": 1001,
        "productName": "스마트폰 A",
        "productDescription": "최신 스마트폰 A 모델",
        "productOptionId": 10012,
        "productOptionName": "화이트/256GB",
        "productOptionDescription": "화이트 색상, 256GB 저장공간",
        "basePrice": 800000,
        "additionalPrice": 200000,
        "quantity": 1,
        "itemTotalPrice": 1000000,
        "couponInfo": {
          "couponId": 1230,
          "name": "봄맞이 할인 쿠폰",
          "discountRate": 10,
          "discountAmount": null
        }
      },
      {
        "orderItemId": 7002,
        "productId": 1002,
        "productName": "노트북 B",
        "productDescription": "고성능 노트북 B 모델",
        "productOptionId": 10021,
        "productOptionName": "i5/8GB/256GB",
        "productOptionDescription": "인텔 i5, 8GB RAM, 256GB SSD",
        "basePrice": 1200000,
        "additionalPrice": 0,
        "quantity": 1,
        "itemTotalPrice": 400000,
        "couponInfo": null
      }
    ],
    "createdAt": "2025-04-04T15:30:45Z"
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
  "resultCode": "00",
  "message": "success",
  "data": {
    "paymentId": 6001,
    "orderId": 5001,
    "userId": 1234,
    "amount": 1260000,
    "status": "COMPLETED",
    "remainingPoint": 3970,
    "createdAt": "2025-04-04T15:35:22Z"
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
  "resultCode": "00",
  "message": "success",
  "data": {
    "dateRange": {
      "from": "2025-04-01",
      "to": "2025-04-03"
    },
    "topProducts": [
      {
        "rank": 1,
        "productId": 1001,
        "name": "스마트폰 A",
        "description": "최신 스마트폰 A 모델",
        "basePrice": 800000,
        "salesCount": 145,
        "salesAmount": 152500000
      },
      {
        "rank": 2,
        "productId": 1005,
        "name": "스마트워치 C",
        "description": "건강 모니터링 스마트워치",
        "basePrice": 250000,
        "salesCount": 120,
        "salesAmount": 30000000
      },
      {
        "rank": 3,
        "productId": 1002,
        "name": "노트북 B",
        "description": "고성능 노트북 B 모델",
        "basePrice": 1200000,
        "salesCount": 78,
        "salesAmount": 109200000
      },
      {
        "rank": 4,
        "productId": 1010,
        "name": "무선이어폰 D",
        "description": "노이즈 캔슬링 무선이어폰",
        "basePrice": 150000,
        "salesCount": 65,
        "salesAmount": 9750000
      },
      {
        "rank": 5,
        "productId": 1008,
        "name": "태블릿 E",
        "description": "10인치 태블릿",
        "basePrice": 500000,
        "salesCount": 52,
        "salesAmount": 26000000
      }
    ]
  }
}

```