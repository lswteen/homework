# With Springboot framework For Order prompt
스프링 프레임워크 3.1.0 최신버전으로 작업된 멀티모듈 레이어드 아키텍처 구조 입니다.
DDD(Domain Driven Design) OOP Solid 를 기반으로 구성된 스켈레톤 입니다.

https://start.spring.io/
기본 구조는 spring initializr 을 이용하여 구성 되었습니다.

프로젝트 생성은 아래 블로그에 정리하였습니다.  
https://angryfullstack.tistory.com/95

## 기술스택
```java
springboot 3.1.0
java 17
jpa
h2
lombok
retry
jline 3.20.0
mapstruct 1.5.5
junit 5.7.0
Gradle
```

## Intellij 설정
Settings > Build, Execution, Deployment > Gradle 
Gradle JVM : 17 버전 
<img width="902" alt="스크린샷 2023-06-02 오후 10 56 23" src="https://github.com/lswteen/homework/assets/3292892/67c9c691-9d14-4455-ab84-049ca4a33275">
File > Project Structure > Project
<img width="1276" alt="스크린샷 2023-06-02 오후 10 58 54" src="https://github.com/lswteen/homework/assets/3292892/018c2520-d232-4165-b1b3-fd7f934fe7b2">
File > Project Structure > SDKs
<img width="1279" alt="스크린샷 2023-06-02 오후 10 58 59" src="https://github.com/lswteen/homework/assets/3292892/1c626e84-88fb-49f8-9fa7-61ef84dfd18e">


자바 버전에 다양한 업데이트가 있었지만 17로 사용한 이유는 스프링 최신버전을 사용 GC 성능 개선된 부분을 최대한 활용할수 있다는 장점 Stop-the-world 상황을 최적화 목적을 두었습니다.  
과제 특성상 기술부채 최소화에 목적을 두고 업데이트버전 사용시 삭제된 내부 클레스, Syntax변경, Entity import변경, 낙관적락 Exception 변경 등 
다양한 사이드 임팩트를 경험 하게 되었습니다.

## 요구사항
> 터미널 프롬프트를 이용한 상품 주문

RestAPI를 주로 사용하다 터미널에 프롬프트라는 요구사항으로
springboot shell, CommandLineRunner 2가지 라이브러리 모두 사용하였습니다.
일정에 압박이 있었기때문에 모든것을 파악하고 접근하기 보다는 기능에 필요한 메소드 구현하면서 
필요한것만 사용한다는 방향으로 삽질하였습니다.

## Springboot shell 
장점 : 간소화되고 commandLineRunner 보다 직관적이고 심플한 형태코드가능  
단점 : 정적 텍스트는 메소드를 미리구성해서 사용가능하지만 상품번호, 수량등 동적문자열 처리 하려면 라이브러리를 커스텀해야하는 러닝커브발생
사용후기 : 하루 진행해보고 빠른 손절

## CommandLineRunner
장점 : springboot shell 보다 다양한 방법으로 구현가능 정적,동적 모두 가능  
단점 : 자유도가 있어서 구조화가 필요하며 자칫 냄새 날수있는 코드가 될수있는 라이브러리 개발자 역량필요

> 상품 CSV 파일에 데이터 사용 (상품번호, 주문수량, 가격, 수량) 
 
상품 데이터는 JPA, H2 를 이용하여 Data.sql로 등록하였습니다.
테이블 구조는 product(상품), stock(상품재고) 2개 테이블로 구성하였습니다.

```h2
DROP TABLE IF EXISTS `product` CASCADE;
DROP TABLE IF EXISTS `stock` CASCADE;

CREATE TABLE product (
     product_id BIGINT NOT NULL,
     name VARCHAR(255),
     price DOUBLE,
     PRIMARY KEY (product_id)
);

CREATE TABLE stock (
   product_id BIGINT NOT NULL,
   quantity INT,
   PRIMARY KEY (product_id),
   FOREIGN KEY (product_id) REFERENCES product (product_id)
);
```
상품, 재고 데이터 구성을 2개로 분리한 이유는 데이터 증가와 사용자 증가로 
query, command 용도를 분리 하였습니다.
재고 특성상 빈번한 재고수량 변화가 일어날수있어
상품 정보 테이블과 함께 구성되어있다면 상품리스트 조회시 트랜잭션으로인해서 성능저하가 발생할수 있기때문입니다.

JPA를 이용하여 1차, 2차 캐쉬 
product 정보만 ehcache, caffeine, redis 등으로 처리하면 더많은 트래픽을 효과적으로 처리가능합니다.

현재 프로젝트는 추가적인 캐쉬는 적용하지 않았습니다.
```h2
INSERT INTO product (product_id, name, price)
VALUES
    (768848, '[STANLEY] GO CERAMIVAC 진공 텀블러/보틀 3종', 21000),
    (748943, '디오디너리 데일리 세트 (Daily set)', 19000),
    (779989, '버드와이저 HOME DJing 굿즈 세트', 35000),
    (779943, 'Fabrik Pottery Flat Cup & Saucer - Mint', 24900),
    (768110, '네페라 손 세정제 대용량 500ml 이더블유지', 7000),
    (517643, '에어팟프로 AirPods PRO 블루투스 이어폰(MWP22KH/A)', 260800),
    (706803, 'ZEROVITY™ Flip Flop Cream 2.0 (Z-FF-CRAJ-)', 38000),
    (759928, '마스크 스트랩 분실방지 오염방지 목걸이', 2800),
    (213341, '20SS 오픈 카라/투 버튼 피케 티셔츠 (6color)', 33250),
    (377169, '[29Edition.]_[스페셜구성] 뉴코튼베이직 브라렛 세트 (브라1+팬티2)', 24900),
    (744775, 'SHUT UP [TK00112]', 28000),
    (779049, '[리퍼브/키친마켓] Fabrik Pottery Cup, Saucer (단품)', 10000),
    (611019, '플루크 new 피그먼트 오버핏 반팔티셔츠 FST701 / 7color M', 19800),
    (628066, '무설탕 프로틴 초콜릿 틴볼스', 12900),
    (502480, '[29Edition.]_[스페셜구성] 렉시 브라렛 세트(브라1+팬티2)', 24900),
    (782858, '폴로 랄프로렌 남성 수영복반바지 컬렉션 (51color)', 39500),
    (760709, '파버카스텔 연필1자루', 200),
    (778422, '캠핑덕 우드롤테이블', 45000),
    (648418, 'BS 02-2A DAYPACK 26 (BLACK)', 238000);

INSERT INTO stock (product_id, quantity)
VALUES
    (768848, 45),
    (748943, 89),
    (779989, 43),
    (779943, 89),
    (768110, 79),
    (517643, 26),
    (706803, 81),
    (759928, 85),
    (213341, 99),
    (377169, 60),
    (744775, 35),
    (779049, 64),
    (611019, 7),
    (628066, 8),
    (502480, 41),
    (782858, 50),
    (760709, 70),
    (778422, 7),
    (648418, 5);
```

## 재고처리 비관적락, 낙관적락 동시성 문제해결
상품 재고특성상 스레드가 동시에 접근하여 상품 재고를 변경하게되는 동시성에 문제가 있어
JPA, Springboot Transactional 을 이용하였습니다.  
보통 상품재고는 동시성이 많이 발생할수 있어 비관적락을 사용한다는 블로그, 구글검색, 주변지인 들에 말을 듣고 해당내용을 구성한뒤 낙관적을 사용해서 정상적으로 처리될수 있는
2가지의 Locking 기법을 사용하였습니다.

Locking 선택은 프로젝트 요구사항, EndUser MAU, 트래픽, 가용자원을 기반으로 처리가능한 수준에 Lock기법을 사용할수 있을것 같습니다.
블로그 설명, 책에대한 기술 보다 직접 멀티스레드로 정상처리가능한 재고수량을 테스트해보는것은 쉬운일이 아닌것 같습니다.  
기술부채 최소화 목적으로 Springboot 3.x.x버전도 삽질에 기여했던것 같습니다.
중간에 2.7.12 다운그레이드 하며 정상처리되는것 확인후 버전올리고 다시 정상동작 테스트도 진행하여 완성하였습니다.

과제 진행중 60-70프로정도를 멀티스레드 동시성 재고수량 처리에 사용한것 같습니다.

## git main branch : 비관적락 (Pessimisitc)
```java
@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final EntityManager entityManager;

    @Transactional
    public void decreaseStock(Map<Long, Integer> productQuantities) {
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            var productId = entry.getKey();
            var quantity = entry.getValue();

            var stockEntity = entityManager
                    .find(StockEntity.class, productId, LockModeType.PESSIMISTIC_WRITE);

            if (stockEntity == null) {
                throw new StockNotFoundException(productId);
            }

            if (stockEntity.getQuantity() < quantity) {
                throw new SoldOutException();
            }

            stockEntity.decreaseQuantity(quantity);
            stockRepository.save(stockEntity);
        }
    }
}
```
```java
private static final int INITIAL_QUANTITY = 10;

    private ProductEntity createProductEntity() {
        ProductEntity productEntity = new ProductEntity(
            10800L,
            "BS 02-2A DAYPACK 26 (BLACK)",
            238000D,
            null
        );
        productRepository.save(productEntity);

        StockEntity stockEntity = new StockEntity(10800L, INITIAL_QUANTITY);
        stockRepository.save(stockEntity);

        productEntity = new ProductEntity(
                10800L,
                "BS 02-2A DAYPACK 26 (BLACK)",
                238000D,
                stockEntity
        );
        return productEntity;
    }

    /**
     * 10 개의 스레드를 생성하고
     * 미리 Mock으로 등록된 상품의 재고 10개를 차감하는 로직입니다.
     *
     * 한번에 3개의 상품재고를 소진하기때문에 동시에 10개를 진행하면 3개만성공하고
     * 7번의 SoldOutException 이 발생하고 filedCount 를 증가 시키게 됩니다.
     *
     * 요구사항에서 멀티스레드 동시 재고차감에서 SoldOutException 이 목적이기에 해당 Rock을
     * LockModeType.PESSIMISTIC_WRITE 으로 변경 하였습니다.
     *
     * 낙관적 락으로 처리할수있는 재고도 고민해보고
     * branch -> feature/object-optmistic-locking
     * retry, optmistic 이용한 재고차감 기능 및 테스트 코드까지 성공한 케이스 추가 하였습니다.
     *
     * 처음 테이블구조는 product 에 상품번호, 상품명, 가격, 상품재고 같이 있는 구조였지만
     * LockModeType.PESSIMISTIC_WRITE 으로 인하여 상품 테이블 트랜잭션락으로 인하여
     *
     * 추후 데이터가 증가하게되면 상품조회 와 재고 변경 이라는 Read/Write
     * 사용빈도가 다른 2개의 테이블을 용도에 맞게 분리 하였습니다.
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    void multithread_throws_SoldOutException_when_productinventory_exhaustion() throws ExecutionException, InterruptedException {
        ProductEntity productEntity = createProductEntity();

        var numberOfThreads = 10;
        var executorService = Executors.newFixedThreadPool(numberOfThreads);
        var soldOutCount = new AtomicInteger(0);

        List<Future<?>> tasks = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            Future<?> task = executorService.submit(() -> {
                try {
                    stockService.decreaseStock(Collections.singletonMap(productEntity.getProductId(), 3));
                }catch (SoldOutException e) {
                    soldOutCount.incrementAndGet();
                }
            });
            tasks.add(task);
        }
        for (Future<?> task : tasks) {
            task.get();
        }
        System.out.println("SoldOutException count: " + soldOutCount.get());
        assertThat(soldOutCount.get()).isEqualTo(7);

    }
```


## git object-optmistic-locking : 낙관적락 (Optimistic)
```java

@Configuration
@EnableRetry
public class RetryConfig {
}

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 5)
    public void objectOptimisticLockingDecreaseStock(Map<Long, Integer> productQuantities) {
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            var productId = entry.getKey();
            var quantity = entry.getValue();

            // 낙관적 락을 사용합니다.
            var stockEntity = stockRepository.findById(productId)
                    .orElseThrow(() -> new StockNotFoundException(productId));

            if (stockEntity == null) {
                throw new StockNotFoundException(productId);
            }

            if (stockEntity.getQuantity() < quantity) {
                throw new SoldOutException();
            }

            stockEntity.decreaseQuantity(quantity);
            //stockRepository.save(stockEntity);
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Entity(name="stock")
    public class StockEntity {
        @Id
        @Column(name = "product_id")
        private Long productId;

        private Integer quantity;

        @Version
        private Long version;
        
        (생략)..
}
```

```sql
CREATE TABLE stock (
   product_id BIGINT NOT NULL,
   quantity INT,
   version BIGINT DEFAULT 0,
   PRIMARY KEY (product_id),
   FOREIGN KEY (product_id) REFERENCES product (product_id)
);
```

```java

@Slf4j
@SpringBootTest
public class OptimisticLockingTest {
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final StockService stockService;

    public OptimisticLockingTest(@Autowired ProductRepository productRepository,
                                 @Autowired StockRepository stockRepository,
                                 @Autowired StockService stockService) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.stockService = stockService;
    }

    private static final int INITIAL_QUANTITY = 10;

    private ProductEntity createProductEntity() {
        ProductEntity productEntity = new ProductEntity(
            10800L,
            "BS 02-2A DAYPACK 26 (BLACK)",
            238000D,
            null
        );
        productRepository.save(productEntity);

        StockEntity stockEntity = new StockEntity(10800L, INITIAL_QUANTITY);
        stockRepository.save(stockEntity);

        productEntity = new ProductEntity(
                10800L,
                "BS 02-2A DAYPACK 26 (BLACK)",
                238000D,
                stockEntity
        );
        return productEntity;
    }

    @Test
    void multithread_throws_OptimisticLockException_when_productinventory_exhaustion() throws ExecutionException, InterruptedException {
        ProductEntity productEntity = createProductEntity();

        var numberOfThreads = 10;
        var executorService = Executors.newFixedThreadPool(numberOfThreads);
        var optimisticLockCount = new AtomicInteger(0);
        var soldOutCount = new AtomicInteger(0);
        List<Future<?>> tasks = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            Future<?> task = executorService.submit(() -> {
                try {
                    stockService.objectOptimisticLockingDecreaseStock(Collections.singletonMap(productEntity.getProductId(), 3));
                } catch (ObjectOptimisticLockingFailureException e) {
                    optimisticLockCount.incrementAndGet();
                } catch(SoldOutException e){
                    soldOutCount.incrementAndGet();
                }

            });
            tasks.add(task);
        }
        for (Future<?> task : tasks) {
            task.get();
        }

        assertThat(optimisticLockCount.get()).isEqualTo(0);
        assertThat(soldOutCount.get()).isEqualTo(7);
    }


}
```

<img width="2074" alt="스크린샷 2023-06-02 오후 11 31 58" src="https://github.com/lswteen/homework/assets/3292892/e7fdea55-edb0-450a-b001-42ade51ac4d3">
