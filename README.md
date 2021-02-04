# MusiCloud
( [Intensive Coursework : Winter School] Cloud App. Azure 2차수 Final 개별 과제 by 김범진 )

MusiCloud 는 Creator 가 만드는 새로운 음악에 대한 저작권 승인과 음원 등록 절차 등을 쉽게 연결해줍니다.
또한 새로운 음악이 아니라 Cover 및 2차 창작 Content 를 등록된 원곡 정보와 쉽게 연결하여,
원작자의 허가 절차를 간소화함과 동시에 원작자와 Creator 들이 쉽게 함께 Content 를 느낄 수 있는 환경을 제공합니다.



# Table of contents

- [서비스 시나리오](#서비스-시나리오)
- [체크포인트](#체크포인트)
- [분석/설계](#분석설계)
- [구현](#구현)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [Gateway 적용](#gateway-적용)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-fallback-처리)
    - [비동기식 호출 / 시간적 디커플링 / 장애격리](#비동기식-호출--시간적-디커플링--장애격리)
- [운영](#운영)
    - [Deploy / Pipeline](#Deploy--Pipeline)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출--서킷-브레이킹--장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
    - [Config Map](#config-map)
    - [Self-healing (Liveness Probe)](#self-healing-liveness-probe)

# 서비스 시나리오
- 기능적 요구사항
 1. Creator 가 새로운 Content 를 Upload 한다.
 2. 저작권 서비스에서 저작권 승인 절차를 진행한다.
 3. 저작권 승인이 완료되면 음원 등록 내역이 음원 서비스에 전달된다.
 4. 음원 서비스에 음원 등록 내역이 도착하면 음원을 등록한다.
 5. 음원 등록이 완료되면 Creator 는 음원 등록 상태를 조회할 수 있다.
 6. Creator 는 Content 를 삭제할 수 있다.
 7. Content 가 삭제되면 저작권 서비스에서는 저작권을 해제한다.
 8. 저작권 해제 상태를 Creator 가 조회할 수 있다.
 9. Creator 는 Content 들의 모든 진행 상태를 조회 할 수 있다.
- 비기능적 요구사항
 1. 트랜잭션
    1. 저작권 승인이 되지 않은 음원은 등록되지 않아야 한다. > Sync 호출
    2. Content 가 삭제되면 저작권이 해제되고 Content 정보에 없데이트가 되어야 한다. > SAGA, 보상 트랜잭션
 2. 장애격리
    1. 음원 관리 시스템이 수행되지 않더라도 Content Upload 는 365일 24시간 받을 수 있어야 한다. > Async (event-driven), Eventual Consistency
    2. 저작권 관리 시스템이 과중되면 Content Upload 를 잠시동안 받지 않고 저작권 승인을 잠시 후에 하도록 유도한다. > Circuit Breaker, Fallback
 3. 성능
    1. Creator 가 모든 짆애 상태를 조회할 수 있도록 성능을 고려하여 별도의 View 로 구성한다. > CQRS

# 체크포인트
- Saga
- CQRS
- Correlation
- Req/Resp
- Gateway
- Deploy/ Pipeline
- Circuit Breaker
- Autoscale (HPA)
- Zero-downtime deploy (Readiness Probe)
- Config Map/ Persistence Volume
- Polyglot
- Self-healing (Liveness Probe)

# 분석설계

## AS-IS 조직 (Horizontally-Aligned)

![image](https://user-images.githubusercontent.com/6468351/106836030-bbd37300-66db-11eb-9c32-7913c6337c28.png)

## TO-BE 조직 (Vertically-Aligned)

![image](https://user-images.githubusercontent.com/6468351/106837027-9c3d4a00-66dd-11eb-89b4-53294005b1b2.png)

## 이벤트 스토밍 결과
MSAEZ로 모델링한 이벤트스토밍 결과
http://www.msaez.io/#/storming/NbHnocpkJjWAo9omQbeAD61P1TA3/share/b7b210b6bd9291be0cfbce1167617033

### 완성된 모델 구조

![image](https://user-images.githubusercontent.com/6468351/106837643-33a29d00-66de-11eb-997f-de74c92d3846.png)

### 기능적 요구사항 검증

![image](https://user-images.githubusercontent.com/6468351/106846504-5178fd80-66f0-11eb-8396-2ad20d946e7a.png)

    - Creator 가 Content 를 Update 한다. (OK)
    - Copyright 의 Approve 절차가 진행된다. (OK)
    - Copyright 가 Approved 되면 저작권 승인 정보가 음원 서비스에 전달된다. (OK)
    - 음원 서비스에 저작권 승인 정보가 도착하면 Source 를 Register 한다. (OK)
    - Registered 되면 Creator 가 등록 상태를 조회할 수 있다. (OK)
    
![image](https://user-images.githubusercontent.com/6468351/106846516-576ede80-66f0-11eb-84d2-04f648d071ad.png)

    - Creator 가 Content 를 Delete 할 수 있다. (OK)
    - Content 가 Deleted 되면 Copyright 가 해제된다. (OK)
    - Creator 가 해제 상태를 조회할 수 있다. (OK)
      
![image](https://user-images.githubusercontent.com/6468351/106846525-5b026580-66f0-11eb-8756-9c8140ecdd99.png)

    - Creator 가 모든 진행 상태를 볼 수 있어야 한다. (OK)
       
### 비기능적 요구사항 검증

![image](https://user-images.githubusercontent.com/6468351/106846531-5e95ec80-66f0-11eb-83c2-c5f90ae4a5cc.png)

    - 1) Copyright 가 Approved 되지 않은 Content 는 아예 음원 등록이 성립되지 않아야 한다. (Req/Res)
    - 2) 음원 서비스 기능이 수행되지 않더라도 Content 서비스는 365일 24시간 받을 수 있어야 한다. (Pub/Sub)
    - 3) 저작권 관리 시스템이 과중되면 저작권 신청을 잠시동안 받지 않고 승인을 잠시 후에 하도록 유도한다. (Circuit Breaker)
    - 4) Content 가 삭제되면 저작권이 해제되고 Content 정보에 업데이트가 되어야 한다. (SAGA, 보상 트랜잭션)
    - 5) Creator 가 모든 진행 상태를 조회할 수 있도록 성능을 고려하여 별도의 View 로 구성한다. (CQRS, DML/Select 분리)

## 헥사고날 아키텍처 다이어그램 도출 (Polyglot)

![image](https://user-images.githubusercontent.com/6468351/106844851-001b3f00-66ed-11eb-8cef-5cce05f055bb.png)

    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐
    - 원음 서비스의 경우 Polyglot 검증을 위해 Hsql 로 설계

# 구현
서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 8084 이다)

```
cd content
mvn spring-boot:run

cd copyright
mvn spring-boot:run 

cd source
mvn spring-boot:run  

cd mypage
mvn spring-boot:run  
```

## DDD 의 적용

각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: (예시는 content 마이크로 서비스). 
이때 가능한 현업에서 사용하는 언어(유비쿼터스 랭귀지)를 그대로 사용하려고 노력했다. 
하지만, 일부 구현 단계에 영문이 아닌 경우는 실행이 불가능한 경우가 발생하여 영문으로 구축하였다.  
(Maven pom.xml, Kafka의 topic id, FeignClient 의 서비스 ID 등은 한글로 식별자를 사용하는 경우 오류 발생)

![image](https://user-images.githubusercontent.com/6468351/106859522-855f1d80-6706-11eb-8337-e9c5551ac279.png)

Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 
데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다

![image](https://user-images.githubusercontent.com/6468351/106859632-aaec2700-6706-11eb-8ba4-f425a5a84d18.png)

## 폴리글랏 퍼시스턴스

source MSA의 경우 H2 DB인 content 와 copyright 와 달리 Hsql으로 구현하여 MSA간 서로 다른 종류의 DB간에도 문제 없이 동작하여 다형성을 만족하는지 확인하였다. 


content, copyright, mypage 의 pom.xml 설정

![image](https://user-images.githubusercontent.com/6468351/106859941-1504cc00-6707-11eb-8654-15d92dba1585.png)

source 의 pom.xml 설정

![image](https://user-images.githubusercontent.com/6468351/106860083-3fef2000-6707-11eb-9238-f3c65083b9ef.png)

## Gateway 적용

gateway > resources > applitcation.yml 설정

![image](https://user-images.githubusercontent.com/6468351/106861178-c5270480-6708-11eb-96a9-b53a3fd5cd9a.png)

gateway 테스트

```bash
http POST http://(gateway IP):8080/contents creatorName="TIKITIK" title="The Song Of Today" type="New Music" description="TIKITIK 1st Anniversary"
```
![image](https://user-images.githubusercontent.com/6468351/106902892-a9d5ec80-673c-11eb-98c8-8eb69581ff73.png)

```bash
http GET http://10.0.158.68:8080/copyrights
```
![image](https://user-images.githubusercontent.com/6468351/106903040-da1d8b00-673c-11eb-82e3-14c69e6ea10b.png)


## 동기식 호출 과 Fallback 처리

분석단계에서의 조건 중 하나로 content -> copyright 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 
호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- copyright 서비스를 호출하기 위하여 FeignClient 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 
```
# (content) external > CopyrightService.java
```
![image](https://user-images.githubusercontent.com/6468351/106904299-5fee0600-673e-11eb-962f-a35551b6f90d.png)

- upload 직후 copyright approve 를 요청하도록 처리
```
# (content) Content.java (Entity)
```
![image](https://user-images.githubusercontent.com/6468351/106904779-eefb1e00-673e-11eb-91fb-2d062a2306bb.png)

- 동기식 호출이 적용되서 copyright 시스템이 장애가 나면 upload 도 불가능한 것을 확인:
```
# copyright 서비스를 잠시 Down

# upload (Fail)
http POST http://10.0.158.68:8080/contents creatorName="TIKITIK" title="The Song Of Today" type="New Music" description="TIKITIK 1st Annyversary"
```
![image](https://user-images.githubusercontent.com/6468351/106905871-f40c9d00-673f-11eb-80c1-3ab6f56e08e0.png)

```
# copyright 서비스 재기동
cd copyright
mvn spring-boot:run

# upload (Success)
http POST http://10.0.158.68:8080/contents creatorName="TIKITIK" title="The Song Of Today" type="New Music" description="TIKITIK 1st Annyversary"
```
![image](https://user-images.githubusercontent.com/6468351/106906071-26b69580-6740-11eb-8250-d431c6264a87.png)



## 비동기식 호출 / 시간적 디커플링 / 장애격리 

copyright 가 approved 된 후에 source 서비스로 이를 알려주는 행위는 비동기식으로 처리하여 source 의 처리를 위하여 content upload 가 블로킹 되지 않도록 처리한다.

- copyright 가 approved 되었다는 도메인 이벤트를 카프카로 송출한다. (Publish)
 
![image](https://user-images.githubusercontent.com/6468351/106908143-5f576e80-6742-11eb-8a81-4b5a43e9cea2.png)

- source 서비스에서는 copyright approved 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다.
- source register 는 송출된 copyright approved 정보를 source repository 에 저장한다.
 
![image](https://user-images.githubusercontent.com/6468351/106910662-c9711300-6744-11eb-9169-8c3bbd05455b.png)


source 시스템은 content / copyright 와 완전히 분리되어있으며(sync transaction 없음), 이벤트 수신에 따라 처리되기 때문에, source 시스템이 유지보수로 인해 잠시 내려간 상태라도 content 시스템 이용에는 문제가 없다.(시간적 디커플링):
```
# source 서비스를 잠시 내려놓음 (ctrl+c)

# upload (Success)
http POST http://10.0.158.68:8080/contents creatorName="TIKITIK" title="The Song Of Today" type="New Music" description="TIKITIK 1st Annyversary"

# 상태 확인
http GET http://10.0.158.68:8080/contents    # 상태값이 'Registered' 가 아닌 'Approved' 에서 멈춤을 확인
```
![image](https://user-images.githubusercontent.com/73699193/98078301-2b577d80-1eb5-11eb-9d89-7c03a3fa27dd.png)
```
# source 서비스 기동
cd source
mvn spring-boot:run

# 상태 확인
http GET http://localhost:8081/contents     # 'Approved' 였던 상태값이 'Registered'로 변경된 것을 확인
```
![image](https://user-images.githubusercontent.com/73699193/98078837-2cd57580-1eb6-11eb-8850-a8c621410d61.png)

# 운영

## Deploy / Pipeline

- deployment.yml을 사용하여 배포 

- deployment.yml 편집
```
namespace, image 설정
env 설정 (config Map) 
readiness 설정 (무정지 배포)
liveness 설정 (self-healing)
resource 설정 (autoscaling)
```
![image](https://user-images.githubusercontent.com/73699193/98092861-8182eb80-1eca-11eb-87c5-afa22140ebad.png)

- deployment.yml로 서비스 배포
```
cd content
kubectl apply -f kubernetes/deployment.yml
```

- gateway 의 경우 deployment.yml 이 없으므로 따로 배포

## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Hystrix 옵션을 사용하여 구현함

시나리오는 단말앱(app)-->결제(pay) 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 결제 요청이 과도할 경우 CB 를 통하여 장애격리.

- Hystrix 를 설정:  요청처리 쓰레드에서 처리시간이 610 밀리가 넘어서기 시작하여 어느정도 유지되면 CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정
```
# application.yml
feign:
  hystrix:
    enabled: true
    
hystrix:
  command:
    # 전역설정
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610

```
![image](https://user-images.githubusercontent.com/73699193/98093705-a166df00-1ecb-11eb-83b5-f42e554f7ffd.png)

* siege 툴 사용법:
```
 siege가 생성되어 있지 않으면:
 kubectl run siege --image=apexacme/siege-nginx -n phone82
 siege 들어가기:
 kubectl exec -it pod/siege-5c7c46b788-4rn4r -c siege -n phone82 -- /bin/bash
 siege 종료:
 Ctrl + C -> exit
```
* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 100명
- 60초 동안 실시

```
siege -c100 -t60S -r10 -v --content-type "application/json" 'http://app:8080/orders POST {"item": "abc123", "qty":3}'
```
- 부하 발생하여 CB가 발동하여 요청 실패처리하였고, 밀린 부하가 pay에서 처리되면서 다시 order를 받기 시작 

![image](https://user-images.githubusercontent.com/73699193/98098702-07eefb80-1ed2-11eb-94bf-316df4bf682b.png)

- report

![image](https://user-images.githubusercontent.com/73699193/98099047-6e741980-1ed2-11eb-9c55-6fe603e52f8b.png)

- CB 잘 적용됨을 확인


### 오토스케일 아웃

- 대리점 시스템에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 15프로를 넘어서면 replica 를 10개까지 늘려준다:

```
# autocale out 설정
store > deployment.yml 설정
```
![image](https://user-images.githubusercontent.com/73699193/98187434-44fbd200-1f54-11eb-9859-daf26f812788.png)

```
kubectl autoscale deploy store --min=1 --max=10 --cpu-percent=15 -n phone82
```
![image](https://user-images.githubusercontent.com/73699193/98100149-ce1ef480-1ed3-11eb-908e-a75b669d611d.png)


-
- CB 에서 했던 방식대로 워크로드를 2분 동안 걸어준다.
```
kubectl exec -it pod/siege-5c7c46b788-4rn4r -c siege -n phone82 -- /bin/bash
siege -c100 -t120S -r10 -v --content-type "application/json" 'http://store:8080/storeManages POST {"orderId":"456", "process":"Payed"}'
```
![image](https://user-images.githubusercontent.com/73699193/98102543-0d9b1000-1ed7-11eb-9cb6-91d7996fc1fd.png)

- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
```
kubectl get deploy store -w -n phone82
```
- 어느정도 시간이 흐른 후 스케일 아웃이 벌어지는 것을 확인할 수 있다. max=10 
- 부하를 줄이니 늘어난 스케일이 점점 줄어들었다.

![image](https://user-images.githubusercontent.com/73699193/98102926-92862980-1ed7-11eb-8f19-a673d72da580.png)

- 다시 부하를 주고 확인하니 Availability가 높아진 것을 확인 할 수 있었다.

![image](https://user-images.githubusercontent.com/73699193/98103249-14765280-1ed8-11eb-8c7c-9ea1c67e03cf.png)


## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscale 이나 CB 설정을 제거함


- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
kubectl apply -f kubernetes/deployment_readiness.yml
```
- readiness 옵션이 없는 경우 배포 중 서비스 요청처리 실패

![image](https://user-images.githubusercontent.com/73699193/98105334-2a394700-1edb-11eb-9633-f5c33c5dee9f.png)


- deployment.yml에 readiness 옵션을 추가 

![image](https://user-images.githubusercontent.com/73699193/98107176-75ecf000-1edd-11eb-88df-617c870b49fb.png)

- readiness적용된 deployment.yml 적용

```
kubectl apply -f kubernetes/deployment.yml
```
- 새로운 버전의 이미지로 교체
```
cd acr
az acr build --registry admin02 --image admin02.azurecr.io/store:v4 .
kubectl set image deploy store store=admin02.azurecr.io/store:v4 -n phone82
```
- 기존 버전과 새 버전의 store pod 공존 중

![image](https://user-images.githubusercontent.com/73699193/98106161-65884580-1edc-11eb-9540-17a3c9bdebf3.png)

- Availability: 100.00 % 확인

![image](https://user-images.githubusercontent.com/73699193/98106524-c152ce80-1edc-11eb-8e0f-3731ca2f709d.png)



## Config Map

- apllication.yml 설정

* default쪽

![image](https://user-images.githubusercontent.com/73699193/98108335-1c85c080-1edf-11eb-9d0f-1f69e592bb1d.png)

* docker 쪽

![image](https://user-images.githubusercontent.com/73699193/98108645-ad5c9c00-1edf-11eb-8d54-487d2262e8af.png)

- Deployment.yml 설정

![image](https://user-images.githubusercontent.com/73699193/98108902-12b08d00-1ee0-11eb-8f8a-3a3ea82a635c.png)

- config map 생성 후 조회
```
kubectl create configmap apiurl --from-literal=url=http://pay:8080 --from-literal=fluentd-server-ip=10.xxx.xxx.xxx -n phone82
```
![image](https://user-images.githubusercontent.com/73699193/98107784-5bffdd00-1ede-11eb-8da6-82dbead0d64f.png)

- 설정한 url로 주문 호출
```
http POST http://app:8080/orders item=dfdf1 qty=21
```

![image](https://user-images.githubusercontent.com/73699193/98109319-b732cf00-1ee0-11eb-9e92-ad0e26e398ec.png)

- configmap 삭제 후 app 서비스 재시작
```
kubectl delete configmap apiurl -n phone82
kubectl get pod/app-56f677d458-5gqf2 -n phone82 -o yaml | kubectl replace --force -f-
```
![image](https://user-images.githubusercontent.com/73699193/98110005-cf571e00-1ee1-11eb-973f-2f4922f8833c.png)

- configmap 삭제된 상태에서 주문 호출   
```
http POST http://app:8080/orders item=dfdf2 qty=22
```
![image](https://user-images.githubusercontent.com/73699193/98110323-42f92b00-1ee2-11eb-90f3-fe8044085e9d.png)

![image](https://user-images.githubusercontent.com/73699193/98110445-720f9c80-1ee2-11eb-851e-adcd1f2f7851.png)

![image](https://user-images.githubusercontent.com/73699193/98110782-f4985c00-1ee2-11eb-97a7-1fed3c6b042c.png)



## Self-healing (Liveness Probe)

- store 서비스 정상 확인

![image](https://user-images.githubusercontent.com/27958588/98096336-fb1cd880-1ece-11eb-9b99-3d704cd55fd2.jpg)


- deployment.yml 에 Liveness Probe 옵션 추가
```
cd ~/phone82/store/kubernetes
vi deployment.yml

(아래 설정 변경)
livenessProbe:
	tcpSocket:
	  port: 8081
	initialDelaySeconds: 5
	periodSeconds: 5
```
![image](https://user-images.githubusercontent.com/27958588/98096375-0839c780-1ecf-11eb-85fb-00e8252aa84a.jpg)

- store pod에 liveness가 적용된 부분 확인

![image](https://user-images.githubusercontent.com/27958588/98096393-0a9c2180-1ecf-11eb-8ac5-f6048160961d.jpg)

- store 서비스의 liveness가 발동되어 13번 retry 시도 한 부분 확인

![image](https://user-images.githubusercontent.com/27958588/98096461-20a9e200-1ecf-11eb-8b02-364162baa355.jpg)

