![image](https://user-images.githubusercontent.com/20436113/131467647-03e923f0-3295-4b24-87d9-4ee4a09e8cb6.png)


# E-Book 대여 시스템

클라우드 네이티브 애플리케이션의 개발에 요구되는 체크포인트 확인
- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW


# Table of contents

- [E-Book 대여](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [폴리글랏 프로그래밍](#폴리글랏-프로그래밍)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
  - [운영](#운영)
    - [CI/CD 설정](#cicd설정)
    - [ConfigMap 설정](#ConfigMap-설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [무정지 재배포](#무정지-재배포)
    - [Self healing](#Liveness-Probe)


# 서비스 시나리오

E-Book  서비스

기능적 요구사항
기능적 요구사항
1. 관리자가 대여용 책을 생성할 수 있다.
2. 고객이 책을 선택하고 대여요청과 함께 결재가 진행된다. 
3. 대여/결제가 되면 내역이 관리자에게 전달된다
4. 관리자가 대여를 최종 승인/거절 한다
5. 관리자가 거절하면 예약이 취소된다.(결제도 취소)
6. 고객이 대여를 취소할수 있다. (결제도 취소)
7. 고객은 대여 현황을 조회하고, 관리자는 책/대여 상태를 확인할 수 있다.

비기능적 요구사항
1. 트랜잭션
    1. 결제 전 대여 요청 건은 아예 거래가 성립되지 않아야 한다  Sync 호출 
2. 장애격리
    1. 대여 관리 기능이 수행되지 않더라도 대여 주문은 365일 24시간 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
    1. 대여시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다  Circuit breaker, fallback
3. 성능
    1. 책에 대한 정보 및 대여 상태 등을 한 화면에서 확인 할 수 있다. CQRS


# 체크포인트

- 분석 설계


  - 이벤트스토밍: 
    - 스티커 색상별 객체의 의미를 제대로 이해하여 헥사고날 아키텍처와의 연계 설계에 적절히 반영하고 있는가?
    - 각 도메인 이벤트가 의미있는 수준으로 정의되었는가?
    - 어그리게잇: Command와 Event 들을 ACID 트랜잭션 단위의 Aggregate 로 제대로 묶었는가?
    - 기능적 요구사항과 비기능적 요구사항을 누락 없이 반영하였는가?    

  - 서브 도메인, 바운디드 컨텍스트 분리
    - 팀별 KPI 와 관심사, 상이한 배포주기 등에 따른  Sub-domain 이나 Bounded Context 를 적절히 분리하였고 그 분리 기준의 합리성이 충분히 설명되는가?
      - 적어도 3개 이상 서비스 분리
    - 폴리글랏 설계: 각 마이크로 서비스들의 구현 목표와 기능 특성에 따른 각자의 기술 Stack 과 저장소 구조를 다양하게 채택하여 설계하였는가?
    - 서비스 시나리오 중 ACID 트랜잭션이 크리티컬한 Use 케이스에 대하여 무리하게 서비스가 과다하게 조밀히 분리되지 않았는가?
  - 컨텍스트 매핑 / 이벤트 드리븐 아키텍처 
    - 업무 중요성과  도메인간 서열을 구분할 수 있는가? (Core, Supporting, General Domain)
    - Request-Response 방식과 이벤트 드리븐 방식을 구분하여 설계할 수 있는가?
    - 장애격리: 서포팅 서비스를 제거 하여도 기존 서비스에 영향이 없도록 설계하였는가?
    - 신규 서비스를 추가 하였을때 기존 서비스의 데이터베이스에 영향이 없도록 설계(열려있는 아키택처)할 수 있는가?
    - 이벤트와 폴리시를 연결하기 위한 Correlation-key 연결을 제대로 설계하였는가?

  - 헥사고날 아키텍처
    - 설계 결과에 따른 헥사고날 아키텍처 다이어그램을 제대로 그렸는가?
    
- 구현
  - [DDD] 분석단계에서의 스티커별 색상과 헥사고날 아키텍처에 따라 구현체가 매핑되게 개발되었는가?
    - Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 데이터 접근 어댑터를 개발하였는가
    - [헥사고날 아키텍처] REST Inbound adaptor 이외에 gRPC 등의 Inbound Adaptor 를 추가함에 있어서 도메인 모델의 손상을 주지 않고 새로운 프로토콜에 기존 구현체를 적응시킬 수 있는가?
    - 분석단계에서의 유비쿼터스 랭귀지 (업무현장에서 쓰는 용어) 를 사용하여 소스코드가 서술되었는가?
  - Request-Response 방식의 서비스 중심 아키텍처 구현
    - 마이크로 서비스간 Request-Response 호출에 있어 대상 서비스를 어떠한 방식으로 찾아서 호출 하였는가? (Service Discovery, REST, FeignClient)
    - 서킷브레이커를 통하여  장애를 격리시킬 수 있는가?
  - 이벤트 드리븐 아키텍처의 구현
    - 카프카를 이용하여 PubSub 으로 하나 이상의 서비스가 연동되었는가?
    - Correlation-key:  각 이벤트 건 (메시지)가 어떠한 폴리시를 처리할때 어떤 건에 연결된 처리건인지를 구별하기 위한 Correlation-key 연결을 제대로 구현 하였는가?
    - Message Consumer 마이크로서비스가 장애상황에서 수신받지 못했던 기존 이벤트들을 다시 수신받아 처리하는가?
    - Scaling-out: Message Consumer 마이크로서비스의 Replica 를 추가했을때 중복없이 이벤트를 수신할 수 있는가
    - CQRS: Materialized View 를 구현하여, 타 마이크로서비스의 데이터 원본에 접근없이(Composite 서비스나 조인SQL 등 없이) 도 내 서비스의 화면 구성과 잦은 조회가 가능한가?

  - 폴리글랏 플로그래밍
    - 각 마이크로 서비스들이 하나이상의 각자의 기술 Stack 으로 구성되었는가?
    - 각 마이크로 서비스들이 각자의 저장소 구조를 자율적으로 채택하고 각자의 저장소 유형 (RDB, NoSQL, File System 등)을 선택하여 구현하였는가?
  - API 게이트웨이
    - API GW를 통하여 마이크로 서비스들의 집입점을 통일할 수 있는가?
    - 게이트웨이와 인증서버(OAuth), JWT 토큰 인증을 통하여 마이크로서비스들을 보호할 수 있는가?
- 운영
  - SLA 준수
    - 셀프힐링: Liveness Probe 를 통하여 어떠한 서비스의 health 상태가 지속적으로 저하됨에 따라 어떠한 임계치에서 pod 가 재생되는 것을 증명할 수 있는가?
    - 서킷브레이커, 레이트리밋 등을 통한 장애격리와 성능효율을 높힐 수 있는가?
    - 오토스케일러 (HPA) 를 설정하여 확장적 운영이 가능한가?
    - 모니터링, 앨럿팅: 
  - 무정지 운영 CI/CD (10)
    - Readiness Probe 의 설정과 Rolling update을 통하여 신규 버전이 완전히 서비스를 받을 수 있는 상태일때 신규버전의 서비스로 전환됨을 siege 등으로 증명 
    - Contract Test :  자동화된 경계 테스트를 통하여 구현 오류나 API 계약위반를 미리 차단 가능한가?


# 분석/설계


## AS-IS 조직 (Horizontally-Aligned)
![분석설계0](https://user-images.githubusercontent.com/27762942/130011063-35d4610a-540a-43c8-a3b3-195e8ac0b6d4.png)

## TO-BE 조직 (Vertically-Aligned)
투비조직_2.png![투비조직_2](https://user-images.githubusercontent.com/20436113/131941801-570e1695-2a3b-4f31-a2da-a4bc8dbba831.png)

## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:  http://labs.msaez.io/#/storming/gvBKJ8asEZSiRXXDACkN51rc7D83/b91e49b9c2bccf0e41a4d77414fc5141


### 이벤트 도출
![690B2947-8F11-4E53-924E-FD3DFCA48068_4_5005_c](https://user-images.githubusercontent.com/20436113/131933491-e6a59d19-6780-478b-acac-07cbbc84aba4.jpeg)

### 부적격 이벤트 탈락
![image](https://user-images.githubusercontent.com/20436113/131933657-093c10a9-e968-4ca8-8723-a5a92a02cd95.png)

    - 과정중 도출된 잘못된 도메인 이벤트들을 걸러내는 작업을 수행함
        - 대여 승인 완료 시>RentalCompleted :  대여 승인 완료 이벤트 RequestApproved와 동작이 중복되어 제거

### 액터, 커맨드 부착 및 어그리게잇으로 묶기
![image](https://user-images.githubusercontent.com/20436113/131934002-8d8e0dcb-fa19-4178-b6c0-889d1b2cb15b.png)


    - rental의 Rental, manage의 대여책 현황관리, 대여용 결제의 결제이력은 그와 연결된 command 와 event 들에 의하여 트랜잭션이 유지되어야 하는 단위로 그들 끼리 묶어줌

### 바운디드 컨텍스트로 묶기

![image](https://user-images.githubusercontent.com/20436113/131934408-8b872575-50da-4f4d-91a3-f1528e46e2ff.png)

    - 도메인 서열 분리 
        - Core Domain:  Rental(front), Manage : 없어서는 안될 핵심 서비스이며, 연견 Up-time SLA 수준을 99.999% 목표, 배포주기는 app 의 경우 1주일 1회 미만, store 의 경우 1개월 1회 미만
        - Supporting Domain:  ViewPage(RentalStatusView) : 경쟁력을 내기위한 서비스이며, SLA 수준은 연간 60% 이상 uptime 목표, 배포주기는 각 팀의 자율이나 표준 스프린트 주기가 1주일 이므로 1주일 1회 이상을 기준으로 함.
        - General Domain:   Payment : 결제서비스로 3rd Party 외부 서비스를 사용하는 것이 경쟁력이 높음 (핑크색으로 이후 전환할 예정)

### 컨텍스트 매핑 (점선은 Pub/Sub, 실선은 Req/Resp)

![image](https://user-images.githubusercontent.com/20436113/131934511-92b186c9-806a-47ab-8716-d6fff5045d6c.png)


### 완성된 1차 모형

![DF289F25-6333-458B-94D0-11168A98259E](https://user-images.githubusercontent.com/20436113/131934812-55a9ee63-c208-4163-a49d-fba1b9e48fa2.jpeg)

    - View Model 추가

### 1차 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증

이미지엠에스_1.jpg![이미지엠에스_1](https://user-images.githubusercontent.com/20436113/131935631-a720a791-c20c-4a83-9e96-926ed67732c0.jpg)

    - 고객이 bookId를 선택하여 예약한다 (ok)
    - 고객이 결제한다 (ok)
    - 대여가 요청되고 결제가 완료되면 되면 대여 내역이 관리자에게 전달된다 (ok)
    - 관리자는 실제 Book 현황을 체크하여 최종 예약 승인 처리를 한다 (ok)
    - 관리자는 중간중간 대여 현황을 조회한다 (View-green sticker 의 추가로 ok) 
    

이미지엠에스_2.jpg![이미지엠에스_2](https://user-images.githubusercontent.com/20436113/131935736-c2bba6b2-207c-4d59-9be6-a63d2c57656a.jpg)


    - 고객이 대여를 취소할 수 있다 (ok)
    - 대여가 취소되면 대여 예약 상태가 변경되고 결제가 취소된다 (ok)    
    


### 모델 수정

![분석설계6_new](https://user-images.githubusercontent.com/27762942/130165883-b6ca8706-8189-4c9e-a529-1f4291eef6de.png)
    
    - 수정된 모델은 모든 요구사항을 커버함.

### 비기능 요구사항에 대한 검증

![분석설계6_new](https://user-images.githubusercontent.com/27762942/130165883-b6ca8706-8189-4c9e-a529-1f4291eef6de.png)

    - 마이크로 서비스를 넘나드는 시나리오에 대한 트랜잭션 처리
        - 대여 요청 시 결제처리:  결제가 완료되지 않은 대여는 절대 받지 않는다는 정책에 따라, ACID 트랜잭션 적용. 대여 요청시 결제처리에 대해서는 Request-Response 방식 처리
        - 결제 완료시 관리자연결 및 최종 대여 완료 및 Book 상태 변경 처리:  Rental(front) 에서 Manage 마이크로서비스로 주문요청이 전달되는 과정에 있어서 Manage 마이크로 서비스가 별도의 배포주기를 가지기 때문에 Eventual Consistency 방식으로 트랜잭션 처리함.
        - 나머지 모든 inter-microservice 트랜잭션: 예약상태, Book상태 등 모든 이벤트에 대해 RentalStatusView 처리 등, 데이터 일관성의 시점이 크리티컬하지 않은 모든 경우가 대부분이라 판단, Eventual Consistency 를 기본으로 채택함.
	- 대여 관리 기능이 수행되지 않더라도 대여 주문은 365일 24시간 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
        - 대여시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다  Circuit breaker, fallback




## 헥사고날 아키텍처 다이어그램 도출
    
헥사고날_2.png![헥사고날_2](https://user-images.githubusercontent.com/20436113/131936708-0fac369d-75f9-4906-b043-7e19fc57e3e9.png)


    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현:

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
   cd Rental
   mvn spring-boot:run
   
   cd Payment
   mvn spring-boot:run
   
   cd Manage
   mvn spring-boot:run
   
   cd viewPage
   mvn spring-boot:run
   
   cd gateway
   mvn spring-boot:run
   
```

## CQRS

숙소 생성 및 예약/결재 등 총 Status 에 대하여 고객(rental)/호텔매니저(manage)가 조회 할 수 있도록 CQRS 로 구현하였다.
- rental, payment, manage 개별 Aggregate Status 를 통합 조회하여 성능 Issue 를 사전에 예방할 수 있다.
- 비동기식으로 처리되어 발행된 이벤트 기반 Kafka 를 통해 수신/처리 되어 별도 Table 에 관리한다
- Table 모델링

  ![image](https://user-images.githubusercontent.com/20436113/131938148-b1dcc8e8-698d-4934-b136-d03533e52866.png)
  
- viewPage MSA PolicyHandler 를 통해 구현 
  ("BookAdded" 이벤트 발생 시, Pub/Sub 기반으로 별도 테이블에 저장)
  ![image](https://user-images.githubusercontent.com/20436113/131938120-4d4e8543-833e-4ffe-9c08-3f4a32a52c52.png)

  ("RentalReqeusted" 이벤트 발생 시, Pub/Sub 기반으로 별도 테이블에 저장)

  ![image](https://user-images.githubusercontent.com/20436113/131938065-973be539-6585-44d8-9dde-1a3b82135b78.png)
- 실제로 view 페이지를 조회해 보면 모든 에 대한 정보, 예약 상태, 결제 상태 등의 정보를 종합적으로 알 수 있다.

  ![8E1B38B6-CD21-42C2-A1D7-B19EF5620DE5](https://user-images.githubusercontent.com/20436113/131938379-6e45b13c-3ab5-4688-bec5-f0d82668f3ab.jpeg)

  
  
## API 게이트웨이

      1. gateway 스프링부트 App을 추가 후 application.yaml내에 각 마이크로 서비스의 routes 를 추가하고 gateway 서버의 포트를 8080 으로 설정함
          - application.yaml 예시
            ```
               spring:
		  profiles: docker
		  cloud:
		    gateway:
		      routes:
			- id: Rental
			  uri: http://user21-rental:8080
			  predicates:
			    - Path=/rentals/** 
			- id: Payment
			  uri: http://user21-payment:8080
			  predicates:
			    - Path=/payments/** 
			- id: Manage
			  uri: http://user21-manage:8080
			  predicates:
			    - Path=/manages/** 
			- id: ViewPage
			  uri: http://user21-viewpage:8080
			  predicates:
			    - Path= /rentalStatusViews/**
		      globalcors:
			corsConfigurations:
			  '[/**]':
			    allowedOrigins:
			      - "*"
			    allowedMethods:
			      - "*"
			    allowedHeaders:
			      - "*"
			    allowCredentials: true

		server:
		  port: 8080
            ```

         
      2. buildspec.yml 파일의 Deployment 설정 내용 
          
            ```
          apiVersion: apps/v1
          kind: Deployment
          metadata:
            name: $_PROJECT_NAME
            namespace: $_NAMESPACE
            labels:
              app: $_PROJECT_NAME
          spec:
            replicas: 1
            selector:
              matchLabels:
                app: $_PROJECT_NAME
            template:
              metadata:
                labels:
                  app: $_PROJECT_NAME
              spec:
                containers:
                  - name: $_PROJECT_NAME
                    image: $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$_PROJECT_NAME:$CODEBUILD_RESOLVED_SOURCE_VERSION
                    ports:
                      - containerPort: 8080
            ```               

      3. buildspec.yml 파일에 Service 설정 내용
          
            ```
            apiVersion: v1
		  kind: Service
		  metadata:
		    name: $_PROJECT_NAME
		    namespace: $_NAMESPACE
		    labels:
		      app: $_PROJECT_NAME
		  spec:
		    ports:
		      - port: 8080
			targetPort: 8080
		    selector:
		      app: $_PROJECT_NAME
		    type:
		      LoadBalancer   
            ```             
 
적용된 Deploy, Service 및 API Gateway 엔드포인트 확인

![5AB463FB-7700-42CE-B484-A4F190BD23DC](https://user-images.githubusercontent.com/20436113/131938513-ecaa0028-4da5-4eaf-81dc-7266cab755db.jpeg)

# Correlation

e-book rental 프로젝트에서는 PolicyHandler에서 처리 시 어떤 건에 대한 처리인지를 구별하기 위한 Correlation-key 구현을 
이벤트 클래스 안의 변수로 전달받아 서비스간 연관된 처리를 정확하게 구현하고 있습니다. 

아래의 구현 예제를 보면

대여(Rental)을 하면 동시에 연관된 대여관리(Manage), 결제(Payment) 등의 서비스의 상태가 적당하게 변경이 되고,
예약건의 취소를 수행하면 다시 연관된 대여관리(Manage), 결제(Payment) 등의 서비스의 상태값 등의 데이터가 적당한 상태로 변경되는 것을
확인할 수 있습니다.


대여요청
http POST http://ae9e4f2df26fb4609ad3506aa7d808e4-1575466458.eu-west-2.elb.amazonaws.com:8080/rentals rentalId=1 bookId=1 rentalStatus=REQUESTED

대여요청 후 - 예약 상태
http http://ae9e4f2df26fb4609ad3506aa7d808e4-1575466458.eu-west-2.elb.amazonaws.com:8080/rentals 

![343D02E0-D31B-4C34-9960-3B40C9F2DED4](https://user-images.githubusercontent.com/20436113/131938726-f1ef0cf3-6cc4-4059-bef0-a3c78081ac9e.jpeg)


대여요청 후 - 결제 상태
http http://ae9e4f2df26fb4609ad3506aa7d808e4-1575466458.eu-west-2.elb.amazonaws.com:8080/payments

![68B10EC5-D019-47D4-A92E-133F135AF186](https://user-images.githubusercontent.com/20436113/131938793-68611ad1-5ce4-458d-bfcb-5fe78d6cbc14.jpeg)


대여요청 승인
http PATCH http://a5ad5ea581539402cb2908592e29b179-597025294.eu-west-2.elb.amazonaws.com:8080/manages/1 rentalStatus=APPROVED

승인 후 - 예약 상태
http http://ae9e4f2df26fb4609ad3506aa7d808e4-1575466458.eu-west-2.elb.amazonaws.com:8080/rentals 

![E6ACB43A-60A4-469C-80AE-39FC481BE4A8](https://user-images.githubusercontent.com/20436113/131938958-e1ac7b6c-ba51-4065-b363-f74b8259f992.jpeg)



승인 후 - 대여관리 상태
http http://ae9e4f2df26fb4609ad3506aa7d808e4-1575466458.eu-west-2.elb.amazonaws.com:8080/managed

![6F2E3AD4-4A8A-4F21-9E8F-37DE03A0C255](https://user-images.githubusercontent.com/20436113/131949067-6ec0de40-66d4-4df0-af9f-690f042932f2.jpeg)

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다. (예시는 Rental 마이크로 서비스). 이때 가능한 현업에서 사용하는 언어 (유비쿼터스 랭귀지)를 그대로 사용하려고 노력했다. 현실에서 발생가는한 이벤트에 의하여 마이크로 서비스들이 상호 작용하기 좋은 모델링으로 구현을 하였다.

```
package project;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Rental_table")
public class Rental {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;                //시퀀스 넘버
    private Long rentalId;          //대여 ID
    private Long bookId;            //책 ID
    private String rentalStatus;    //대여 상태 : REQUESTED(대여요청됨), APPROVED(대여승인됨), CANCELED(대여취소됨), PAID(결제됨), REJECTED(대여거절됨)


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public String getRentalStatus() {
        return rentalStatus;
    }

    public void setRentalStatus(String rentalStatus) {
        this.rentalStatus = rentalStatus;
    }

}

```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다
```
package rentalapp;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="rentals", path="rentals")
public interface RentalRepository extends PagingAndSortingRepository<Rental, Long>{


}


```
- 적용 후 REST API 의 테스트
```
# manage 서비스의 book 등록
http POST http://ae9e4f2df26fb4609ad3506aa7d808e4-1575466458.eu-west-2.elb.amazonaws.com:8080/manages bookId=1

# rental 서비스의 대여 요청
http POST http://ae9e4f2df26fb4609ad3506aa7d808e4-1575466458.eu-west-2.elb.amazonaws.com:8080/rentals rentalId=1 bookId=1 rentalStatus=REQUESTED

# rental 서비스의 대여 상태 확인
http http://ae9e4f2df26fb4609ad3506aa7d808e4-1575466458.eu-west-2.elb.amazonaws.com:8080/rentals 

```

## 동기식 호출(Sync) 과 Fallback 처리

분석단계에서의 조건 중 하나로 대여(rental)->결제(payment) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 
호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient로 이용하여 호출하도록 한다.

- 결제 서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 

```
# PaymentService.java

package project.external;

<!--import 문 생략 -->

@FeignClient(name="Payment", url="${api.service.url}")
public interface PaymentService {
    @RequestMapping(method= RequestMethod.GET, path="/payments")
    public void paymentrequest(@RequestBody Payment payment);

}


```

- 예약 요청을 받은 직후(@PostPersist) 가능상태 확인 및 결제를 동기(Sync)로 요청하도록 처리
```
# Rental.java (Entity)

     @PostPersist
    public void onPostPersist(){
        System.out.println("######대여 요청 되었음#####");

        // 책 대여 요청
        // 결제 동기 호출(req/res) 진행
        // 결제가 진행가능한 지 확인 후 결제 진행
        rentalapp.external.Payment payment = new rentalapp.external.Payment();
        if(this.getRentalStatus().equals("REQUESTED")){
            payment.setRentalId(this.getRentalId());
            payment.setRentalStatus("PAID");
        }

        // mappings goes here
        RentalApplication.applicationContext.getBean(rentalapp.external.PaymentService.class)
            .paymentrequest(payment);

        RentalRequested rentalRequested = new RentalRequested();
        BeanUtils.copyProperties(this, rentalRequested);
        rentalRequested.publishAfterCommit();

    }
```

- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 결제 시스템이 장애가 나면 주문도 못받는다는 것을 확인

```
# 결제 (payment) 서비스를 잠시 내려놓음 (ctrl+c)

# 예약 요청  - Fail
http POST http://ae9e4f2df26fb4609ad3506aa7d808e4-1575466458.eu-west-2.elb.amazonaws.com:8080/rentals rentalId=1 bookId=1 rentalStatus=REQUESTED

# 결제서비스 재기동
cd payment
mvn spring-boot:run

# 예약 요청  - Success
http POST http://ae9e4f2df26fb4609ad3506aa7d808e4-1575466458.eu-west-2.elb.amazonaws.com:8080/rentals rentalId=1 bookId=1 rentalStatus=REQUESTED

```

- 또한 과도한 요청시에 서비스 장애가 도미노 처럼 벌어질 수 있다. (서킷브레이커 처리는 운영단계에서 설명한다.)



## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트

결제가 이루어진 후에 예약 시스템의 상태가 업데이트 되고, manage 시스템의 상태 업데이트가 비동기식으로 호출된다.
- 이를 위하여 결제가 승인되면 결제가 승인 되었다는 이벤트를 카프카로 송출한다. (Publish)
 
```
# Payment.java

package project;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Payment_table")
public class Payment {

    ....
    @PostPersist
    public void onPostPersist(){
        // 결제 완료
        PaymentCompleted paymentCompleted = new PaymentCompleted();
        BeanUtils.copyProperties(this, paymentCompleted);
        paymentCompleted.publishAfterCommit();

    }
    ....
}
```

- 대여 시스템에서는 결제 승인 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:

```
# PolicyHandler.java

package project;

@Service
public class PolicyHandler{

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCompleted_PaymentComplete(@Payload PaymentCompleted paymentCompleted){
        //결제 완료됨
        if(!paymentCompleted.validate()) return;
        System.out.println("####PolicyHandler: wheneverPaymentCompleted_PaymentComplete####" );
        System.out.println("\n\n##### listener PaymentComplete : " + paymentCompleted.toJson() + "\n\n");

        setChengedStatus(paymentCompleted.getRentalId(), "PAID");
    }
```

그 외 대여 승인/거부는 대여/결제와 완전히 분리되어있으며, 이벤트 수신에 따라 처리되기 때문에, 유지보수로 인해 잠시 내려간 상태 라도 대여을 받는데 문제가 없다.

```
# manaage 서비스 (manage) 를 잠시 내려놓음 (ctrl+c)

# 대여 요청  - Success
http POST http://localhost:8080/rentals rentalId=1 bookId=1 rentalStatus=REQUESTED
   

# 대여 상태 확인  - manage 서비스와 상관없이 예약 상태는 정상 확인
http GET http://localhost:8088/rentals
```


## 폴리글랏 퍼시스턴스

viewPage 는 RDB 계열의 데이터베이스인 Maria DB 를 사용하기로 하였다. 
별다른 작업없이 기존의 Entity Pattern 과 Repository Pattern 적용과 데이터베이스 관련 설정 (pom.xml, application.yml) 만으로 Maria DB 에 부착시켰다.

```
# RentalStatusView.java


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="RentalStatusView_table")
public class RentalStatusView {

}

# RentalStatusViewRepository.java
package rentalapp;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RentalStatusViewRepository extends CrudRepository<RentalStatusView, Long> {

    RentalStatusView findByRentalId(Long rentalId);
    RentalStatusView findByBookId(Long bookId);

}

![4FDD8866-9A65-48E2-AF42-5F516A561916_4_5005_c](https://user-images.githubusercontent.com/20436113/131949435-89dcacf9-15e0-4d5c-bae9-11a8c6c0b9f7.jpeg)

```

실제 확인 시, 데이터 확인 가능 (ex. manage에서 book 생성한 경우)

![0C2AA4CE-B558-4CDB-AD29-5E91A65E60B1_4_5005_c](https://user-images.githubusercontent.com/20436113/131939966-cb384210-8140-4fa1-b129-c98ce9ac8ab3.jpeg)

# 운영

## CI/CD 설정


각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD 플랫폼은 AWS를 사용하였으며, pipeline build script 는 각 프로젝트 폴더 이하 buildspec.yml 에 포함되었다.

AWS CodeBuild 적용 현황
![image](https://user-images.githubusercontent.com/20436113/131940006-305ed7d0-b627-4c50-a5e4-dc77e6ab032d.png)

webhook을 통한 CI 확인
![image](https://user-images.githubusercontent.com/20436113/131940075-59cc9a69-aca8-4662-a0f0-3452459da5c4.png)

AWS ECR 적용 현황
![image](https://user-images.githubusercontent.com/20436113/131940122-67a82aa5-2273-4722-92d0-ef62c53e1025.png)

EKS에 배포된 내용

![5AB463FB-7700-42CE-B484-A4F190BD23DC](https://user-images.githubusercontent.com/20436113/131940146-6740a26b-b218-4e41-9b52-892377bf5b5a.jpeg)


## ConfigMap 설정


 동기 호출 URL을 ConfigMap에 등록하여 사용


 kubectl apply -f configmap

```
 apiVersion: v1
 kind: ConfigMap
 metadata:
   name: rental-configmap
   namespace: rental
 data:
   apiurl: "http://user21-gateway:8080"

```
buildspec 수정

```
              spec:
                containers:
                  - name: $_PROJECT_NAME
                    image: $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$_PROJECT_NAME:$CODEBUILD_RESOLVED_SOURCE_VERSION
                    ports:
                      - containerPort: 8080
                    env:
                    - name: apiurl
                      valueFrom:
                        configMapKeyRef:
                          name: rental-configmap
                          key: apiurl 
                        
```            
application.yml 수정
```
api:
  service:
    url: ${apiurl}
``` 

동기 호출 URL 실행
![595E4E43-DD82-46DD-BF8D-21698E9864EE_4_5005_c](https://user-images.githubusercontent.com/20436113/131940229-bec678bd-c666-41e6-9a2f-d545a0b56e39.jpeg)


## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹 프레임워크의 선택: istio의 Destination Rule을 적용 Traffic 관리함.

시나리오는 렌탈서비스(rental)-->결제(payment) 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 결제 요청이 과도할 경우 CB 를 통하여 장애격리.

* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 10명
- 10초 동안 실시

```
# siege 서비스 생성
kubectl run siege --image=apexacme/siege-nginx -n onedayclass

# seige pod 접속
kubectl exec pod/siege-d484db9c-mr7s9 -n rental -it -- /bin/bash

# URL 호출
siege -c10 -t10S -v --content-type "application/json" 'http://user21-rental:8080/rentals POST {“rentalId”: 1, “bookId”: 2, “rentalStatus”: “REQUESTED"}'

```
![4C68F8BB-D148-4C89-BCED-9366BFE13D01](https://user-images.githubusercontent.com/20436113/131940447-436be5b7-b866-4169-96a4-8a9acdbc9fb2.jpeg)

CB가 없기 때문에 100% 성공

```
# istio-injection 활성화
kubectl label namespace rental istio-injection=enabled 

# VirtualService 적용 
kubectl apply -f destinationRule -n rental

apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: user21-rental
  namespace: rental
spec:
  host: user21-rental
  trafficPolicy:
    connectionPool:
      http:
        http1MaxPendingRequests: 1
        maxRequestsPerConnection: 1

```


![59E483F1-5FD2-4198-8CEF-0DE9CC743DD7](https://user-images.githubusercontent.com/20436113/131940536-95c22212-a512-4e43-b4a9-c16ae12301bd.jpeg)

CB적용 되어 일부 실패 확인





## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함

- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
siege -c30 -t30S -v http://user21-viewpage:8080/rentalStatusViews

```

```
# buildspec.yaml 의 readiness probe 의 설정:
![2F49D377-B2F0-4149-9A64-AC73D20069F7_4_5005_c](https://user-images.githubusercontent.com/20436113/131940605-b0377b37-0833-4746-af00-22dce4687fa4.jpeg)


Customer 서비스 신규 버전으로 배포

![AEA660A4-C9FA-48D7-835C-0D5FA119266A](https://user-images.githubusercontent.com/20436113/131940866-60caee2b-cd68-4e83-bbc6-4c9203f9ad78.jpeg)

배포기간 동안 Availability 가 변화없기 때문에 무정지 재배포가 성공한 것으로 확인됨.

## Liveness Probe

테스트를 위해 buildspec.yml을 아래와 같이 수정 후 배포

![C737694D-C337-45BE-901A-2431C267629C_4_5005_c](https://user-images.githubusercontent.com/20436113/131940891-6e9ef970-3ca5-4044-bc0b-ac97ea47a29a.jpeg)



 pod 상태 확인
 
 컨테이너 실행 후 30초 동인은 정상이나, 이후 /tmp/healthy 파일이 삭제되어 livenessProbe에서 실패를 리턴하게 됨. (이후 자동으로 재시작)

 kubectl describe ~ 로 pod에 들어가서 아래 메시지 확인
 ![9E5F7934-46B8-4BD8-B41D-D6DE08288474_4_5005_c](https://user-images.githubusercontent.com/20436113/131941036-67abcf62-b3d3-489a-adfb-ac068e3c7083.jpeg)


rental의 Restart 횟수 증가함을 확인
![D2A2AC53-18D3-4E99-8370-F1403C703FB5_4_5005_c](https://user-images.githubusercontent.com/20436113/131940942-2b58cf0d-b54d-4461-bb6d-54e504d1dcf5.jpeg)

