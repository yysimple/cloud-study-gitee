## springcloud

### maven的聚合工程(cloud2020)
dependencyManagement:
+ 一般用在聚合工程的父工程中，用于同一版本号管理
+ 如在父工程中定义了mysql的版本，在其他子工程中就不需要定义版本，对以后的版本升级就很方便，
如果哪个子项目需要自己的版本，可以自行定义版本
+ 这个标签只是声明依赖，并不会实现引入，因此在子项目里需要自行引入，但可以不加上版本号 ga即可
+ 且在子模块中不需要写gv，只需要写a即可

创建好父项目后 clean之后 install 中间跳过test 便可统一管理子模块的版本


### cloud-api-commons 公共代码、模块等工程
将实体类提取出来，还有一些相关的公用依赖放在这个工程里面，然后install一下，在其他模块中就可以使用：
```xml
<dependency>
    <groupId>com.jxkj.springcloud</groupId>
    <artifactId>cloud-api-commons</artifactId>
    <version>${project.version}</version>
</dependency>
```
这样其他模块中就不需要重复写这些代码

### cloud-eureka-server7001 开启eureka注册中心
主要是开启注册管理：
```yaml
server:
  port: 7001

eureka:
  instance:
    hostname: eureka7001.com
  client:
    #false表示不向注册中心注册自己
    register-with-eureka: false
    #false 表示自己就是注册中心，职责就是维护服务实例，并不需要去检索服务
    fetch-registry: false
    service-url:
      defaultZone: http://eureka7002.com:7002/eureka/
```
这里的hostname：eureka7001.com 是修改了hosts文件 在里面做了域名映射，模拟不同的服务进行集群

然后在springboot启动类上加上@EnableEurekaServer这个注解即可，eureka的注册管理服务就开启了

在H版的cloud中，eureka的pom依赖也进行了修改,对应的注册中心是server：
```xml
<!-- euraka的服务端 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

eureka的自我保护机制：在我们初次进入服务列表页面时，不会出现，但是过了一段时间再次刷新页面，会有红色的警告字体出现，
这是因为eureka的自我保护机制，eureka是通过客户端发送心跳来维持连接的，如果过了一段时间，客户端未给出任何反应，eureka不会
将该服务直接注销，而是会选择保留该微服务的信息。所以会给出警告，可以通过关闭自我保护机制实现消除警告（不推荐），这种机制属于
微服务CAP里面的Ap机制，保证其高可用。

在注册中心需要加入(eureka.instance:)：
```yaml
server:
  # eureka的自我保护机制，默认为true
  enable-self-preservation: false
  # 过了客户端的等待时间后两秒后就关闭
  eviction-interval-timer-in-ms: 2000
```
在客户端需要加入(eureka.instance:)：
```yaml
#客户端向注册中心间隔多久发送一次心跳，默认为30s
lease-renewal-interval-in-seconds: 30
# 客户端在不发送心跳后，注册中心需要等待的时间
lease-expiration-duration-in-seconds: 90
```

### cloud-eureka-server7002 开启eureka注册中心
作为集群用，与7001几乎相同，yml文件里面的配置有点不同：
```yaml
server:
  port: 7002

eureka:
  instance:
    hostname: eureka7002.com
  client:
    #false表示不向注册中心注册自己
    register-with-eureka: false
    #false 表示自己就是注册中心，职责就是维护服务实例，并不需要去检索服务
    fetch-registry: false
    # 将7001注册到7002中
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka/
```
这样做，就相当于开启了两个注册中心，之后服务就可以像这两个注册中心进行注册


### cloud-paovider-payment8001 支付微服务
+ 创建典型的mvc工程
+ 两个方法
    - 创建流水账
    - 通过id查询
将单个项目跑通后，将该服务注册进eureka的集群只需加入依赖，加上注解，然后修改yml文件就可以完成服务注册：

这里是服务注册者，所以是客户端：
```xml
        <!-- euraka的客户端 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```

引入依赖之后，只需要在主启动类上加上@EnableEurekaClient这个注解就可以了

然后在yml文件里面加上eureka的一些配置即可完成服务注册：
```yaml
server:
  port: 8001

# 服务名称
spring:
  application:
    name: cloud-payment-service
  datasource:
    url: jdbc:mysql://localhost:3306/db_cloud_202001?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&verifyServerCertificate=false&useSSL=false
    username: root
    password: 123456
    driver-class-name: org.gjt.mm.mysql.Driver
    type: com.alibaba.druid.pool.DruidDataSource

eureka:
  instance:
    # 修改eureka服务的status名称
    instance-id: payment-8001
    # 访问路劲可以显示当前服务的ip地址
    prefer-ip-address: true
  client:
    # 入驻eureka
    register-with-eureka: true
    # 去获取已注册的服务，在集群的时候，每个微服务都需要设置为true，配合ribbon进行负载均衡
    fetch-registry: true
    # 将自己注册进的 eureka服务管理中心 的url
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka

#Mybatis配置
mybatis:
  mapper-locations: classpath:mybatis/mapper/*.xml
  type-aliases-package: com.jxkj.springcloud

```
这里需要注意的是：
> 1. instance-id: payment-8001 这个是在查询注册了哪些服务（http://eureka7001.com:7001）里面的那个Status的值
> 2. defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka：这个是向哪些注册中心注入自己
> 3. spring.application.name: cloud-payment-service  这个是集群服务里对外暴露的服务名

服务发现：通过在启动类上加上注解@EnableDiscoveryClient，将DiscoveryClient注入，便可以发现已注册的服务
```java

@RestController
@Slf4j
public class PaymentController {
    // 注入服务发现客户端
    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/payment/discovery")
    public Object getDiscovery(){
        // 获取到所有的已注册服务，（payment、Order）
        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            log.info("**** service: " + service);
        }
        
        // 这里通过服务名也既服务id去查询该集群里面有多少个微服务 
        List<ServiceInstance> serviceInstances =  discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance serviceInstance : serviceInstances) {
            log.info(serviceInstance.getInstanceId() + "\t" + serviceInstance.getHost() + "\t" + serviceInstance.getUri());
        }

        return this.discoveryClient;
    }
}
```

### cloud-paovider-payment8002 支付微服务2
其功能代码与8001是一样的，只需要将instance-id: payment-8001 改成8002即可
用于服务的负载均衡

### cloud-consumer-order80 订单服务
首先需要将RestTemplate注入到spring容器内，然后调用payment服务里面的controller
通过getForObject时，不能传参数，需要在后面拼接 +id  @PathVariable可以不加，默认会在请求路径上面加上
加上@PathVariable注解，可直接在后面路径上/id
```
@GetMapping("/consumer/payment/getPaymentById")
public CommentResult<Payment> getPaymentById(Long id){
    return restTemplate.getForObject(PAYMENT_URL + "/payment/getPaymentById?id=" + id, CommentResult.class);
}

@GetMapping("/consumer/payment/getPaymentById/{id}")
public CommentResult<Payment> getPaymentById(@PathVariable("id") Long id){
    return restTemplate.getForObject(PAYMENT_URL + "/payment/getPaymentById/" + id, CommentResult.class);
}

```

再调用其他服务是，如果需要传的是一个对象，在被调用（provider）里面需要加上@RequestBody注解，才能正常插入数据库，
在本服务内不需要加，默认是加上的

在进行远程调用的时候，如果是单机单服务调用的话就是只需要通过指定路径就可访问：
**public static final String PAYMENT_URL = "http://localhost:8001";**

**如果是访问多个微服务的话需要在配置类中加上@LoadBalanced，以完成负载均衡的作用，不加直接调用的时候会报错**
且访问路径需要改成**微服务的名称**：
**public static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";**
```java
@Configuration
public class ApplicationContextConfig {

    /**
     * 将 RestTemplate 注入到Spring的容器中
     * @LoadBalanced: 开启负载均衡
     * @return
     */
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}

```
这里的负载均衡，默认的是轮询的算法

### cloud-consumerzk-order80 将订单服务注册到zookeeper中心
首先需要在自己的本地一台虚拟机上安装zookeeper，并启动server端（安装很简单，然后改一下数据保存的路劲，方便自己找）
在pom中引入：
```xml
<!-- springboot整合zookeepr客户端 -->
<dependencies>
    <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zookeeper-discovery</artifactId>
            <version>2.1.1.RELEASE</version>
            <exclusions>
                <exclusion>
                    <groupId>org.apache.zookeeper</groupId>
                    <artifactId>zookeeper</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.14</version>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
</dependencies>
    
```
我的zookeeper使用的是3.4.14版本的，所以会与slf4j-log4j12这个jar冲突，排除一下依赖，而引入与springcloud整合的
zookeeper依赖需要排除一个beta的一个zookeeper的jar包

然后在启动类上加上@EnableDiscoveryClient这个注解

修改yml文件：
```yaml
server:
  port: 80
spring:
  application:
    name: cloud-comsumer-order
  cloud:
    zookeeper:
      connect-string: 192.168.109.133:2181
```
然后启动便可在zookeeper上注册了

### cloud-provider-paynebt8004 zookeeper上注册的微服务提供者
与80在zookeeper上的配置差不多没有什么大的区别

集群的话就是与eureka的差不多：
```yaml
cloud:
    zookeeper:
  # 只需要在这里多家几个zookeeper的主机就可以了
      connect-string: 192.168.109.133:2181
```

### cloud-provider-consul-payment8006 将服务注入到consul注册中心
将consul作为注册中心也很简单，与zookeeper相似，consul可以在linux，也可以在windows上，在windows上是一个exe文件，
先配置一下consul的全局环境变量，然后使用命令:
```shell script
consul agent -dev
```
在linux下：
```shell script
# 先解压
upzip consul.tar.gz
# 在启动（后面的主机ip）
./consul agent -dev -ui -node=consul-dev -client=192.168.109.133
```
然后pom文件中引入：
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
```

在启动类上加上@EnableDiscoveryClient即可，

修改yml文件：
```yaml
server:
  port: 8006

spring:
  application:
    name: consul-cloud-provider-payment
  # consul 服务注册中心
  cloud:
    consul:
      # 这里是本地，可以选择放在linux上
      host: localhost
      port: 8500
      discovery:
        # 注册的服务名
        service-name: ${spring.application.name}
```

这样便将consul作为了服务的注册中心了，启动之后，在本地（localhost:8500）就可以看到起ui了

### cloud-consumer-consul-order80 
跟微服务提供者（8006）中的配置几乎一样，只需要简单的修改一下yml即可，其他的部分就跟其他的微服务消费者的代码几乎相同
将**public static final String INVOKE_URL = "http://consul-cloud-provider-payment";/**改成yml文件中
对应的服务名即可

### CAP原则:

> C: Consistency(强一致性)    
> A: Availability(高可用性)
> P: Partition tolerance(分区容错性)  

在微服务中，p是必须的，而A、C不可兼得，而关系型数据库CA
CA：mysql
AP：eureka
CP：zookeeper/consul/redis/mongodb

### Ribbon
在eureka的依赖中，会自带引入ribbon的依赖，无需自己另外添加，当然也可以添加：
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

Ribbon-LB是属于进程内的LB，而相对于Nginx则属于集中式的LB（第一道门槛）
想用Ribbon实现负载均衡，只需要在RestTemplate的Bean注入上加上一个注解@LoadBalanced即可

Ribbon实现负载均衡有很多种算法：
通过IRule，可以查看到其所有实现算法，默认是轮询算法

修改其默认算法，需要在@ComponentScan扫描之外的包，要不然就不能定制自己的算法，会被所有Ribbon客户端共享
```java
@Configuration
public class MyRule {

    @Bean
    public IRule iRule(){
        // 设置成随机算法
        return new RandomRule();
    }
}
```

然后在启动类上加上：**@RibbonClient(name = "cloud-payment-service", configuration = MyRule.class)** 即可使用
这些都是ribbon自带的算法，也可以定制自己的算法

**再测试的时候发现一个bug（自己电脑上）**：两个工程8001和8002使用相同代码的DAO层时，在实现轮询算法时，第一个工程
可以找到其对应的 xml sql映射文件，但是第二个工程，就会出现找不到对应的xml的情况，这个时候只需将其中的
一个工程里面的dao层接口的名字修改一下即可。


### cloud-consumer-feign-order80 使用feign直接调用接口
feign其实就是对ribbon + RestTemplate进行封装，将微服务提供的接口（8001/8002的controller）封装到需要使用这些的接口的工程中
实现起来也很简单，首先是在pom中加入：
```xml
<!-- openfeign的依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```
因为feign也需要结合eureka使用，所以eureka的依赖也需引入

然后就是在yml里面配置，跟其他的注册差不多：
```yaml
server:
  port: 80

eureka:
  client:
    register-with-eureka: false
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka

ribbon:
  # 指建立连接后从服务器读取到资源所用的时间
  ReadTimeout: 5000
  # 指的是建立连接所用的时间，适用于网络正常的情况下，两端连接所用的时间
  ConnectTimeout: 5000

logging:
  level:
    # feign日志以什么级别监控那些借口
    com.jxkj.springcloud.service.PaymentFeignService: debug

```
上面还有延时请求和日志的配置
然后就在主启动类上加上@EnableFeignClients启用feign
然后自定义一个service接口：
```java
@Component
@FeignClient(value = "cloud-payment-service")
public interface PaymentFeignService {

    /**
     * 将url注册到feign中， 也就是封装restTemplate
     * @param id
     * @return
     */
    @GetMapping("/payment/getPaymentById")
    CommentResult<Payment> getPaymentById(@RequestParam("id") Long id);

    /**
     * 模拟延时操作
     * @return
     */
    @GetMapping("/payment/feign/timeout")
    String paymentFeignTimeout();
}

```
**这里RequestParam必须加上，如果是用路劲上直接/id则需要加上@PathVariable注解**
**@FeignClient(value = "cloud-payment-service")：**
这个是你要调用哪个微服务里面的接口,该接口里面的方法名最好与提供接口（8001/8002）里面的名字一致，
弄完这些只需要在该工程的controller里面跟调用普通的业务层一样： **return paymentFeignService.getPaymentById(id);**

日志的话除了上面的yml里面的配置，还需要定义一个配置类：
```java
@Configuration
public class FeignConfig {

    /**
     * 开启feign的详细日志
     * @return
     */
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}

```
这样在控制台就可以监控这些使用的接口，会以debug的方式打印

feign 目前已经停更维护，所以使用的依赖是 openfeign 但是功能一致

### cloud-provider-hystrix-payment8008 服务端熔断降级

**服务降级**：为了不导致因为一个服务请求时间过长，或者出现故障而一直占用资源，导致整个系统雪崩，所以需要对这些bug或者突发情况进行处理
所以引入服务降级和熔断
**服务熔断**：类比保险丝，拉闸限电，然后在调用降级方法并返回友好提示 （也是降级的一种）,有三种状态，开，关，半开。
**熔断机制概述**：熔断机制是应对雪崩效应的一种微服务链路保护机制。当某个服务出错或者不可用或者响应时间过长，会进行服务降级，
进而熔断该节点微服务的调用，快速返回错误信息，当检测该节点微服务调用响应正常后，恢复调用链路。

**服务降级：**：
也是同样步骤，pom引入(同时需要引入eureka的)：
```xml
<!--Hystrix依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```
然后yml里面的内容跟其他的服务提供差不多

然后在启动类加上@EnableCircuitBreaker

然后就是在业务类上加上对应的注解，以及对应的降级处理：
```java

@Service
public class PaymentHystrixService {

    /**
     * 正常访问的方法
     * @param id
     * @return
     */
    public String paymentInfoOk(Integer id){
        return "线程池==：" + Thread.currentThread().getName() + "paymentInfoOk_id==: " + id + "\t" + "O(∩_∩)O哈哈~";
    }

    /**
     * 模拟延时
     * @HystrixCommand: 用来做服务请求超时的降级，出现问题通过fallbackMethod 来指定后续处理的方法
     * commandProperties： 用来限制请求时间，超过就降级
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "paymentTimeoutHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    public String paymentTimeout(Integer id){
        // 程序暂停3s, 模拟大的业务，超过3s以上的业务
        int timeout = 5;
        try {
            TimeUnit.SECONDS.sleep(timeout);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return "线程池==：" + Thread.currentThread().getName() + "paymentTimeout_id==: " + id + "\t" + "O(∩_∩)O哈哈~ == 耗时" + timeout +"秒";
    }

    public String paymentTimeoutHandler(Integer id){
        return "线程池==：" + Thread.currentThread().getName() + "paymentTimeoutHandler==: " + id + "┭┮﹏┭┮ == 耗时";
    }


}

```

@HystrixCommand(fallbackMethod = "paymentTimeoutHandler", commandProperties = {
@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"):
这个的意思是：该服务最长请求时间只能是3s，超过就会自动降级，会交由指定的方法进行处理
这里是交由:**paymentTimeoutHandler**这个方法进行处理，然后去测试，成功降级
出现什么异常：10/0的这种异常，也会熔断降级

**服务熔断：**
```java
@Service
public class PaymentHystrixService {
    /**
     * 服务熔断：====
     */
    @HystrixCommand(fallbackMethod = "paymentCircuitBreakerFallBack", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"), // 是否开启短路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"), // 请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"), // 时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60") // 失败率达到什么概率跳闸
    })
    public String paymentCircuitBreaker(@RequestParam("id") Integer id){
        if (id < 0) {
            throw new RuntimeException("******* id不能为负数 ******");
        }
        String serialNumber = IdUtil.simpleUUID();
        return Thread.currentThread().getName() + "\t" + "调用成功，流水号： " + serialNumber;
    }

    public String paymentCircuitBreakerFallBack(@RequestParam("id") Integer id){
        return "id 不能为负数，请稍后再试， o(╥﹏╥)o ~~ id: " + id;
    }
}
```
在访问的时候，id大于0的表示正确，小于0的表示错误，当小于0的次数也既失败次数大于6次（总共测试10次，要在10s内完成，代码里面的配置），
那服务会进行熔断，在下次访问正确的请求，也还是会提示报错信息，当多次成功请求之后，又会恢复正常。
可查阅官网，还有很多关于熔断的配置。

### cloud-consumer-feign-hystrix-order80 客户端的服务降级
客户端的降级跟服务端差不多（该项目已经引入过hystrix的相关依赖）
只需要在yaml中开启降级熔断支持：
```yaml
# 开启客户端的服务降级功能
feign:
  hystrix:
    enabled: true
```
然后在启动类上加上注解@EnableHystrix

在代码里面配置降级处理：
```java

@RestController
@Slf4j
@DefaultProperties(defaultFallback = "paymentGlobalTimeoutHandler")
public class OrderHystrixController {

    @Autowired
    private PaymentHystrixService paymentHystrixService;

    @GetMapping("/consumer/payment/hystrix/paymentInfoOk")
    public String paymentInfoOk(@RequestParam("id") Integer id){
        return paymentHystrixService.paymentInfoOk(id);
    }

    @GetMapping("/consumer/payment/hystrix/paymentTimeout")
    @HystrixCommand(fallbackMethod = "paymentTimeoutHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    })
    public String paymentTimeout(@RequestParam("id") Integer id){
        return paymentHystrixService.paymentTimeout(id);
    }

    public String paymentTimeoutHandler(@RequestParam("id") Integer id){
        return "我是消费端的80接口，对方的支付系统繁忙，请稍后再试...┭┮﹏┭┮...";
    }

    /**
     * 全局降级处理
     * @return
     */
    public String paymentGlobalTimeoutHandler(){
        return "Global全局降级处理，请稍后再试.../(⊙︿⊙)/";
    }
}

```
> 首先是：**@HystrixCommand(fallbackMethod = "paymentTimeoutHandler", commandProperties = {
>                  @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
>          })**:
> 这个跟服务端的意思是一样的，只需要指定其处理方法即可做到降级操作，但是这样会有个问题，一个方法一个处理，会造成代码耦合度过高
>
> 所以开启全局配置
> **@DefaultProperties(defaultFallback = "paymentGlobalTimeoutHandler")**：设置这个注解，然后在需要进行降级处理的方法上加上 
> @HystrixCommand 这个注解，如不指定具体处理方法，就会走默认的全局降级处理，这样还是会存在 处理代码和业务代码放在一块
>
> 所以可以找到这些方法的源头（即调用的微服务）：
> @FeignClient(value = "hystrix-cloud-payment-service", fallback = PaymentHystrixServiceImpl.class)
> 
 
只需要实现**public class PaymentHystrixServiceImpl implements PaymentHystrixService**,然后在feign的注解里面：
加上fallback = PaymentHystrixServiceImpl.class 即可指定处理的类在哪里，即可做到一对一处理，也可以将处理的代码抽离
```java
@Component
public class PaymentHystrixServiceImpl implements PaymentHystrixService {

    @Override
    public String paymentInfoOk(Integer id) {
        return "PaymentHystrixServiceImpl -- fallback -- paymentInfoOk";
    }

    @Override
    public String paymentTimeout(Integer id) {
        return "PaymentHystrixServiceImpl -- fallback -- paymentTimeout";
    }
}
```

### cloud-consumer-hystrix-dashboard9001 监控面板
这个监控面板可以监控每个微服务里面的每个请求，可以监控请求的状态 成功与失败的次数统计和 曲线图等
pom引入：
```xml
<!--Hystrix依赖-->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```
注意的是，actuator这个依赖是图形界面化的依赖，必须引入，才可以在界面上看到该服务
然后yml里面只需要指定端口号就行 这里是 9001，之后再启动类上加上@EnableHystrixDashboard注解 开启监控配置

我们这里是对8008 服务进行监控，所以需要对8008进行部分修改（因为springcloud的坑）：
```java
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
public class PaymentHystrixApplication8008 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentHystrixApplication8008.class, args);
    }

    /**
     * 此配置是为了监控服务而配置，与服务本身无关，springcloud升级后的坑
     * ServletRegistrationBean是因为springboot里面默认路径不是“/hystrix.stream”
     * @return
     */
    @Bean
    public ServletRegistrationBean getServlet(){
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }
}
```
然后访问 localhost:9001/hystrix 可以看到网站 ，然后在输入 http://localhost:8008/hystrix.stream 即可对8008服务进行监控

### cloud-gateway-gateway9527 网关配置
zuul 1.x 已经停更， zuul 2.x 因为Netflix公司自身的原因，一直没有出，所以springcloud自己出了一套 gateway
网关的作用是在请求前进行一次路由，也既请求转发，还可以附带负载均衡作用
首先引入pom：
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```
启动类上需要加上Eureka的注解，因为网关也需要注册到注册中心，可以选择consul和zookeeper

然后就是在yml中加入相关的配置：
```yaml
server:
  port: 9527

# 服务名称
spring:
  application:
    name: cloud-gateway
  cloud:
    gateway:
      discovery:
        locator:
          # 开启从注册注册中心动态创建路由的功能，利用微服务名进行路由
          enabled: true
      routes:
        # 路由的id，没有固定要求，但要唯一，建议配合服务名
        - id: payment_routh01
          # 匹配后提供服务的路由地址
          uri: http://localhost:8001
          # 断言，路径匹配的进行路由，如果路径在对应的微服务里真的存在，返回就是true、就可以进行路由转发
          predicates:
            - Path=/payment/getPaymentById/**

        - id: payment_routh02
          # uri: http://localhost:8001
          # 开启动态路由
          uri: lb://cloud-payment-service
          predicates:
            - Path=/payment/lb/**
            # 配置在什么时候可以访问这个接口 还有 before between
            #- After=2020-03-19T18:02:38.387+08:00[Asia/Shanghai]
            # 配置Cookie curl http://localhost:9527/payment/lb --cookie "username=wcx" 需要带上cookie访问
            #- Cookie=username,wcx
            # 请求头要有 Header=X-Request-Id 属性并且值为整数的正则表达式
            # curl http://localhost:9527/payment/lb --cookie "username=wcx" -H "X-Request-Id:123"
            #- Header=X-Request-Id,\d+
eureka:
  instance:
    # 修改eureka服务的status名称
    instance-id: cloud-gateway-9527
    # 访问路劲可以显示当前服务的ip地址
    prefer-ip-address: true
    hostname: cloud-gateway-service
  client:
    # 入驻eureka
    register-with-eureka: true
    # 去获取已注册的服务，在集群的时候，每个微服务都需要设置为true，配合ribbon进行负载均衡
    fetch-registry: true
    # 将自己注册进的 eureka服务管理中心 的url
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka

```
配置文件里面很详细的介绍了各个配置的作用，最主要的就是gateway里面的相关配置，其他相关配置可以查阅官网
再测试断言的cookie和请求头的时候，可以选择在cmd里面使用curl进行测试

这是使用配置文件实现路由转发，还有一种就是使用编码的方式，这里是对百度的新闻网页进行转发：
```java
@Configuration
public class GatewayRouterConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder){
        RouteLocatorBuilder.Builder routes = builder.routes();
        // 进行路由转发
        routes.route("path-baidu-guonei", r -> r.path("/guonei").uri("http://news.baidu.com/guonei")).build();
        routes.route("path-baidu-guoji", r -> r.path("/guoji").uri("http://news.baidu.com/guoji")).build();
        return routes.build();
    }
}

```
这样做跟nginx做反向代理很像，访问9527端口便可以跳转指定的网站

最后便是网关的过滤功能，只需要实现两个接口：GlobalFilter, Ordered 实现他们的方法便可以进行拦截，当然也可以在yml中配置
可以参阅官网
```java
@Component
@Slf4j
public class MyLogGatewayFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("***** come in MyLogGatewayFilter: " + new Date().toString());
        String username = exchange.getRequest().getQueryParams().getFirst("username");
        if (username == null) {
            log.info("=========== 用户名不合法 o(╥﹏╥)o =========");
            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

```

这样在访问9527这个端口的请求的时候就需要在请求路径后面加上username，才能进行正常的访问

**zuul 1.x 和 gateway**： zuul使用的阻塞的io进行网关配置，而gateway则是非阻塞io，参考netty的思想做的，所以在现在非阻塞io几乎
是必备的东西，所以zuul不是老项目几乎可以不用了。

### cloud-config-center-3344 微服务配置中心
配置中心的目的是为了解决，在多个微服务里面的.yml文件内容存在相同的情况，使得其能够更好的维护
这里我选择的是 gitee 为放置配置文件的远程仓库，也可以使用githup（不过很卡，有时候）
在idea中需要下载插件 gitee 然后在settings里面 Version Control里面的 gitee 中登录即可
这个很重要，如果不配置需要在yml中加入你的用户名和密码

首先需要引入pom依赖：
```xml
<!-- springcloud 的配置中心依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```
这里引入的是服务端，再配置yml文件：
```yaml
server:
  port: 3344
spring:
  application:
    name: cloud-config-cneter
  cloud:
    config:
      server:
        git:
          uri: https://gitee.com/yysimple/springcloud-config-atguigu
          # 搜索目录
          search-paths:
            - springcloud-config
      # 读取什么分支
      label: master

eureka:
  client:
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka

```
这个配置中心也需要注册进注册中心，这里用的是eureka的注册中心
然后在主启动类上加上 @EnableConfigServer注解，开启配置中心管理，启动项目
**http://config3344.com:3344/master/config-dev.yml** (这里我在hosts里面做了域名映射)
 这样便可以从gitee上访问到对应的文件内容，并且是以yml里面格式打印
 这里采用的是 /label/{name}-{xxx}.yml的格式 
+ name一般就叫config
+ xxx一般就分为 dev prod test等

还有其他三种配置方法可以进行访问，而且返回值也不太相同，可参阅官网

### cloud-config-client 配置中心客户端的配置
在客户端里面，不会直接去访问gitee里面的文件，而是通过配置中心去获取内容
这里的pom依赖有点改变
```xml
<!-- springcloud 的客户端配置依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```
这里引入的是客户端的依赖
然后就是yml的配置也有所不同：
**配置文件bootstrap.yml**：这里使用的是这个文件，这是系统级别的配置文件 ，application.yml是用户级别的配置，
为了让其更好的去加载配置中心里面的配置，所以采用这种文件
```yaml
server:
  port: 3355
spring:
  application:
    name: config-client
  cloud:
    config:
      # 分支名称
      label: master
      # 配置文件名字
      name: config
      # 读取后缀名
      profile: dev
      # 配置中心服务端地址
      uri: http://localhost:3344

eureka:
  client:
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka
#暴露监控点
management:
  endpoints:
    web:
      exposure:
        include: "*"
```
客户端采用的启动类的注解还是@EnableEurekaClient
为了方便测试，需要一个业务类：
```java
@RestController
@Slf4j
@RefreshScope
public class ConfigClientController {

    @Value("${config.info}")
    private String configInfo;

    @GetMapping("/getConfigInfo")
    public String getConfigInfo(){
        return configInfo;
    }
}

```
这样便可以通过 **http://localhost:3355//getConfigInfo** 便可以得到对应分支上的对应文件里面的内容

但是这样存在一个问题，当在gitee上修改文件内容是，配置中心可以拿到最新的数据（3344），但是客户端（3355）
则需要重启才能得到最新的数据，所以我们需要在进行配置：
+ 在yml里面加入暴露监控点的配置
+ 在controller里面加上@RefreshScope这个注解
+ 修改之后，通过命令的方式刷新（不需要重启微服务）（curl -X POST http://localhost:3355/actuator/refresh）
这样便可以成功访问到最新的数据，但还是有问题，如果文件过多，则需要输入的命令也过多，所以要引入 bus



### cloud-config-client-3366 微服务配置客户端
为了更好的演示，一次刷新，其他都刷新，所以新建 3366 工程
跟3355 配置几乎一样，只需要修改一点点内容，加入bus和mq的依赖，3344总配置中心 3355都需要加上：
```xml
<!-- 添加总线、RabbitMq的支持 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```
然后就是修改yml里面的内容：
3344里面需要加入：
```yaml
# rabbitmq的相关配置
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
# 暴露bus刷新的端点,跟rabbitmq的刷新有关
management:
  endpoints:
    web:
      exposure:
        include: 'bus-refresh'
```

在3355和3366下要加上：
```yaml
# rabbitmq的相关配置
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```
这里有点需要注意的是：在本机上访问 rabbitmq时 端口是 15672 ，在程序里的配置是 5672
因为web里面的ui和java中的端口连接不一样

然后修改gitee上的版本号，通过：
curl -X POST http://localhost:3344/actuator/bus-refresh
然后就可以全局刷新，还有一个局部的刷新：
curl -X POST http://localhost:3344/actuator/bus-refresh/{微服务名 + 端口号}（config-client）刷新

### cloud-stream-rabbitmq-provider 整合中间件的提供方（发送消息方）
由于市面上存在很多中间件，而springcloud推出stream作为中间件的统一处理，有四个概念：
+ 首先是发送消息
+ Source、Sink 消息发送者和消息接受者
+ channel管道，在代码里就是MessageChannel
+ 中间件

还是那几个步骤，首先是pom引入：
```xml
<!-- rabbitMq的依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>
```
这是是使用rabbitmq作为中间件，若需要使用其他的，则只需要修改rabbit 比如 kafka

然后就是yml配置，很多：
```yaml
server:
  port: 8801

spring:
  application:
    name: cloud-stream-provider

  cloud:
    stream:
      binders: #在此处配置要绑定的rabbitmq的服务信息
        defaultRabbit: #表示定义的命名，用于binding整合
          type: rabbit #消息组件类型
          environment: #设置rabbitmq的相关环境配置
            spring:
              tabbitmq:
                host: localhost
                port: 5672
                username: guest
                password: guest
      bindings: #服务的整合处理
        output: #这个名字是一个通用的名称
          destination: studyExchange #表示要使用的Exchange名称定义
          content-type: application/json #设置消息类型，本次为json，文本则为 text/plain
          binder: defaultRabbit #设置要绑定的消息服务的具体设置

eureka:
  client:
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka
  instance:
    lease-renewal-interval-in-seconds: 2 #心跳时间间隔（默认30秒）
    lease-expiration-duration-in-seconds: 5 #超时间隔（默认90秒）
    instance-id: send-8801.com #在信息列表时显示主机名称
    prefer-ip-address: true #访问路径变为ip地址
```
上面都有很详细的注解

写了一个业务接口，作为发送消息业务：
```java
package com.jxkj.springcloud.service;

public interface IMessageProviderService {

    /**
     * 消息提供者
     * @return
     */
    String send();
}

```
实现类：
```java
package com.jxkj.springcloud.service.impl;
import org.springframework.cloud.stream.messaging.Source;

@EnableBinding(Source.class)
public class IMessageProviderServiceImpl implements IMessageProviderService {

    /**
     * 消息发送管道 ------zhuzhzuzhuzhzuz:  这里只能写 output 要不然会报错
     */
    @Resource
    private MessageChannel output;

    @Override
    public String send() {
        String serial = UUID.randomUUID().toString();
        output.send(MessageBuilder.withPayload(serial).build());
        System.out.println("**** serial ****: " + serial);
        return serial;
    }
}
```
**output：** 这里需要注意的是，MessageChannel 后面的名字 只能写 output（我试了很多，但好像只有这个才行，汇报只要一个bean 却有三个的错误）
还有就是 Source包不能导错， 这里是发送者

然后就是controller：
```java

@RestController
public class SendMessageController {

    @Resource
    private IMessageProviderService iMessageProviderService;

    @GetMapping("/sendMessage")
    public String sendMessage(){
        return iMessageProviderService.send();
    }
}
```
然后登陆rabbitmq（rabbitmq不说了，自行下载安装包，配置好环境，一直下一步就行），便可以查看到你自己定义的Exchange，就是在yml中配置的名字
然后这就成功整合了

### cloud-stream-rabbitmq-consumer8802/8803 中间件消息接收方，两个工程一样 除端口等一些配置外
pom的引入跟提供方是一样的
yml文件里存在一些修改：
```yaml
server:
  port: 8802

spring:
  application:
    name: cloud-stream-consumer

  cloud:
    stream:
      binders: #在此处配置要绑定的rabbitmq的服务信息
        defaultRabbit: #表示定义的命名，用于binding整合
          type: rabbit #消息组件类型
          environment: #设置rabbitmq的相关环境配置
            spring:
              tabbitmq:
                host: localhost
                port: 5672
                username: guest
                password: guest
      bindings: #服务的整合处理
        input: #这个名字是一个通用的名称
          destination: studyExchange #表示要使用的Exchange名称定义
          content-type: application/json #设置消息类型，本次为json，文本则为 text/plain
          binder: defaultRabbit #设置要绑定的消息服务的具体设置
          group: rabbit-a # 分组，持久化都是在这里配置

eureka:
  client:
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka
  instance:
    lease-renewal-interval-in-seconds: 2 #心跳时间间隔（默认30秒）
    lease-expiration-duration-in-seconds: 5 #超时间隔（默认90秒）
    instance-id: receive-8802.com #在信息列表时显示主机名称
    prefer-ip-address: true #访问路径变为ip地址
```
这里修改的就是服务名，端口号，然后就是把output改成input，这就是提供方和接收方在配置上的一点点不同

如果后面不配置 **group: rabbit-a** 那么默认会将两个相同的功能服务分成不同的组，这样会造成重复消费，就是 提供方值给相同功能的两个接收方发送了
四条消息，但是在两个服务端内确各自都接收到了四条，这样就重复了，都是相同功能的微服务， 所以需要帮他们设置成同一个组，这样就会每个服务只会接收到
两条消息，这个也将持久化做了。

持久化： 比如8001发了4条消息（这个时候把8002的group配置去掉，就是8002 8003是两个不用的组），然后8002、8003这段时间突然宕机了，然后
8002、8003都重启了一下，发现8002没有接收到消息，而8003接收到了4条消息，这就是持久化，也只需要加上group即可。

消费端的代码，需要加上Sink 和 @StreamListener这个注解：
```java
@Component
@EnableBinding(Sink.class)
public class ReceiveMessageListenerController {

    @Value("${server.port}")
    private String serverPort;

    /**
     * 前面提供的是String类型的数据所以 <String>
     * @param message
     */
    @StreamListener(Sink.INPUT)
    public void input(Message<String> message){
        // 消息提供方使用的是withPayload，所以这里使用getPayload
        System.out.println("消费者1号，------>接收到消息：" + message.getPayload() + "\t port:" + serverPort);
    }
}

```
### sleuth 和 zinpkin的使用
使用这两个可以追踪到服务之间的调用
这里采用的是eureka的8001 8002 80 7001 7002 作为测试，7001 7002不需要修改
8001 8002 80里面修改pom：
```xml
<!-- 引入zipkin、sleuth依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
``` 
然后是yml：
```yaml
spring:
    zipkin:
        base-url: http://localhost:9411
      sleuth:
        sampler:
          # 采样率介于0 到 1之间， 1表示全部采集
          probability: 1
```
然后进入 http://localhost:9411 查看最近的服务间的调用

### cloud-alibaba-config-nacos-client3377 nocos的配置中心
nacos是阿里推出的一款注册中心，属于AP，但是也可以切换成CP
首先还是一样pom文件：
```xml
<!-- 配置中心的依赖 -->
<dependencies>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>

    <!-- nacos依赖的引入 -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
</dependencies>
```
然后yaml文件：
```yaml
server:
  port: 3377

spring:
  application:
    name: nacos-config-client
  cloud:
    nacos:
      discovery:
        # 配置nacos地址
        server-addr: localhost:8848
      config:
        # 配置中心的地址
        server-addr: localhost:8848
        # 指定配置文件格式，相当于gitee上的那个文件
        file-extension: yaml
        # 分组
        group: DEV_GROUP
        # 命名空间
        namespace: 4aca4479-cf8e-4ce2-b00d-9742e225a386

# 消费者将去访问微服务的名称
service-url:
  nacos-user-service: http://nacos-cloud-payment-provider

# ${prefix}-${spring.profile.active}.${file-extension}
```
这里的配置都不难，官网上都很详细
这里需要去下载一个nacos的windows版，zip文件，下载下来直接进入bin目录下，双击startup.cmd便可以启动配置中心
很简单，然后去访问(http://localhost:8848/nacos)便可以进入ui界面，可以看到自己的微服务名已经注册进入了
**@GetMapping("/config/info")**：访问这个可以获取到对应的配置文件信息（这里注意的是需要.yaml），这里相较于使用springcloud-config和bus总线
作为加载配置文件，nacos是可以自动的刷新文件里面的内容，而bus和中间件需要去手动刷新，本地启动或者linux服务器上启动nacos，然后在yaml里面加点配置
便可以完成注册，不需要任何新建工程

其他配置之后跟后面的工程是联系的

### cloud-alibaba-provider-payment9001/9002 服务提供者
很简单，跟eureka差不多，只是配置中心变了
这里只需要引入nacos的依赖便可：
```xml
<!-- nacos依赖的引入 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

然后就是yaml的配置：
```yaml
server:
  port: 9001

spring:
  application:
    name: nacos-cloud-payment-provider
  cloud:
    # 加入nacos的配置 
    nacos:
      discovery:
        # 配置nacos地址
        server-addr: localhost:8848
# 暴露服务信息
management:
  endpoints:
    web:
      exposure:
        include: "*"
```
然后启动两个服务，在nacos的ui界面中就可以找到这两个服务名
这样两个服务就已经注册到nacos中了

### cloud-alibaba-consumer-nacos-order93 服务消费方
跟eureka的服务差不多，也只需要引入上面一样的依赖便可
然后就是yaml（这里用yaml是因为nacos里面的文件后缀只能是yaml，如果写成yml就会报错）：
```yaml
server:
  port: 93
spring:
  application:
    name: nacos-cloud-order-consumer
  cloud:
    nacos:
      discovery:
        # 配置nacos地址
        server-addr: localhost:8848
# 消费者将去访问微服务的名称
service-url:
  nacos-user-service: http://nacos-cloud-payment-provider
```
这里一定要记得加上负载均衡的注解@LoadBalanced，跟eureka是一样的，要不然会报错：找不到对应的服务，在引入nacos时候，也会把ribbon引入

### nacos集群的配置
首先需要说的是nacos：
**${prefix}-${spring.profile.active}.${file-extension}** ：
分别是：服务名，或者自定义prefix前缀，然后就是 dev test prod等环境，然后是文件后缀
+ prefix 默认为 spring.application.name 的值，也可以通过配置项 spring.cloud.nacos.config.prefix来配置。
+ spring.profile.active 即为当前环境对应的 profile，详情可以参考 Spring Boot文档。 注意：当 spring.profile.active 为空时，
对应的连接符 - 也将不存在，dataId 的拼接格式变成 ${prefix}.${file-extension}
+ file-exetension 为配置内容的数据格式，可以通过配置项 spring.cloud.nacos.config.file-extension 来配置。
目前只支持 properties 和 yaml 类型。

然后就是每一个文件有唯一的坐标：namespace + group + data ID

还有就是这些配置文件的数据信息，每次重新登录nacos，都活存在，是因为其自身带有一个嵌入式的数据库
而我们实际生产中需要放入高可用的mysql集群中（nacos目前只支持mysql），很简单，只需要在本地建好数据库nacos
然后导入nacos目录下的conf里面的nacos-mysql.sql文件即可（这个是nacos官方提供的），然后就是修改application.properties
里面的信息:
```properties
spring.datasource.platform=mysql

### Count of DB:
db.num=1

### Connect URL of DB:
db.url.0=jdbc:mysql://127.0.0.1:3306/nacos_config?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
db.user=root
db.password=123456
```
然后就可以在自己本地数据库里面看到数据，这样就完成了数据的迁移

这是在本地的操作，但是大多数生产环境下都是linux上的集群操作
首先有条件的可以选择使用多台服务器，如果没有的话这里可以使用nacos的一种方法：
+ 在linux上复制一个文件cluster.conf，他自己文件不动cluster.conf.example，修改里面的配置：
> 192.168.109.134:3333
> 192.168.109.134:4444
> 192.168.109.134:5555
+ application.properties里面跟本地数据库一样的配置
+ 然后修改startup.sh: 
```shell script
while getopts ":m:f:s:p:" opt
do
    case $opt in
        m)
            MODE=$OPTARG;;
        f)
            FUNCTION_MODE=$OPTARG;;
        s)
            SERVER=$OPTARG;;
        p)
            PORT=$OPTARG;;
        ?)
        echo "Unknown parameter"
        exit 1;;
    esac
done

....

nohup $JAVA -Dserver.port=${PORT} ${JAVA_OPT} nacos.nacos >> ${BASE_DIR}/logs/start.out 2>&1 &
```
这里就是需要修改两个配置，使其运行的时候可以加上端口号运行，这样就可以启动三台nacos，但是有条件的话可以启动三台不同的服务器

然后就是使用nginx做负载均衡，做请求分发：
```
upstream cluster{
	server 127.0.0.1:3333;
	server 127.0.0.1:4444;
	server 127.0.0.1:5555;
}

server
    {
        listen 1111;
        server_name localhost;
        
        location / {
        	proxy_pass http://cluster;
        }

        access_log  /www/wwwlogs/access.log;
    }
```
然后在浏览器上输入（192.168.109.134:1111/nacos/#/login）便可以进入nacos注册中心，然后在9002项目中修改yaml文件
将**server-addr: 192.168.109.134:1111**进行修改，就做成了nacos集群的搭建

### cloud-alibaba-sentinel-service8401 sentinel的限流、降级等
首先需要去官网或者其他地方下载sentinel的jar包，然后java -jar就可以跑起来，我这里是在本地安装，没有放在服务器上
下载运行之后，在网站上打开 http://localhost:8080就可以访问到了，但是什么都没有，需要先创建工程，然后跑起来


在新建项目，pom、加入：
```xml
<dependencies>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
</dependencies>
```
然后就是yaml的配置：
```yaml
server:
  port: 8401
spring:
  application:
    name: cloud-alibaba-sentinel-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
    sentinel:
      transport:
        # 控制面板中心
        dashboard: localhost:8080
        # 默认端口号，如果存在则默认+1
        port: 8719
management:
  endpoints:
    web:
      exposure:
        include: "*"
```
启动项目，访问/testA或者/testB
再次打开sentinel的网站（用户名和密码都是sentinel）
就会发现有这个服务注册到了sentinel里面，可以对这个服务里面的资源进行限流、降级等，类似于工具的使用

在sentinel的持久化中：
- resource：代表的是服务名称
- limitApp：来源应用
- grade：阈值类型，0表示线程数，1表示QPS
- count：单机阈值
- strategy：流控模式，0表示直接，1表示关联，2表示链路
- controlBehavior：流控效果，0表示快速失败，1表示Warm up，2表示排队等待
- clusterMode：是否集群

具体看官网：
sentinel：（https://github.com/alibaba/Sentinel/wiki/）

seata：https://seata.io/zh-cn/docs/overview/what-is-seata.html

TC - 事务协调者
维护全局和分支事务的状态，驱动全局事务提交或回滚。

TM - 事务管理器
定义全局事务的范围：开始全局事务、提交或回滚全局事务。

RM - 资源管理器
管理分支事务处理的资源，与TC交谈以注册分支事务和报告分支事务的状态，并驱动分支事务提交或回滚。









