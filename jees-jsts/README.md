# jees-jsts

## 内容介绍
本工具基于Spring和Netty实现Socket服务器，目前支持Socket和WebSocket这2种格式。

## 基本用法
> pom.xml
```
<dependency>
	<groupId>com.github.aiyoyoyo</groupId>
	<artifactId>jees-jsts</artifactId>
	<version>${jees.jsts.version}</version>
</dependency>
```

## 配置文件说明
> application.yml
```
spring:
 application:
  name: jees-jsts

jees:
 jsts:
  message: #消息代理机制
   request:
    enable: false #日志开启
    exception: false
    clazz: com.jees.interf.IRequest,com.jees.interf.IResponse #请求命令注册类
   handler:
    enable: false #日志开启
    clazz: com.jees.interf.IResponse #应答命令注册类
   error:
    enable: false #日志开启
    clazz: com.jees.interf.IError #错误命令注册类
   proxy: false #启用Message消息类代理
   type: json #消息内容为json/proto
   jsonFile: false #启用消息写入文件
   jsonFormat: yyyy-MM-dd_HH-mm-ss-SSS
   jsonPath: C:/jsons/
   jsonLogs: true #消息日志
   monitor: false #输出消息处理时长
  socket:
   enable: true #启用socket服务
   trigger: true #使用心跳
   standMax: 1 #挂起多少次后断开
   standTime: 60000 #挂起时长，毫秒
   port: 8000 #端口
   bom: false #是否使用消息小端字节序
  websocket:
   enable: true #启用websocket服务
   url: / #WebSocket地址头， 默认 ws:// 
   trigger: true
   standMax: 1
   standTime: 60000
   port: 8001
   ssl: 
    enable: false #启用ssl，访问由ws://变更为wss://
    file: config/cert/jees.pfx #加密相关文件位置
    keyfile: config/cert/jees.key #加密相关文件位置
    type: PKCS12 #加密类型
    pass: PASSWORD #密钥
  connector: #连接器，可以与指定服务器连接
   enable: true 
   retry:
    max: 3
    rate: 10000
    delay: 300000
   hosts: 127.0.0.1:8000 #要连接的服务器，多个服务器用“,”分割
```

## 版本说明
### 1.2.1-SNAPSHOT <font color='red'>新版本</font>
1. 优化日志文件
2. 优化打包内容
### 1.2.0
1. 重写了服务器结构，去掉了一些多余的类。
2. 消息代理机制可以参考演示程序中CustomMessage
3. 在socket服务器可以使用proto形式，解码过程可以参考MessageCrypto类
```
// 消息处理代码，见JstsModel类
@MessageRequest( CMD_PROXY )
public void CMD_PROXY( ChannelHandlerContext _ctx, Message _msg ){
    System.out.println( "CMD_PROXY-->" + _msg.toString() );
    handler.response( _msg, _ctx );
}

@MessageRequest( CMD_PROTO )
public void CMD_PROTO( ChannelHandlerContext _ctx, CustomMessage _msg ){
    System.out.println( "CMD_PROTO-->" + _msg.toString() );
    handler.response( _msg, _ctx );
}
```
4. 版本不稳定，需要更多测试，欢迎各位反馈。
5. 提供现有支持的多种协议方式的演示程序，将包括protobuff和json的2种方式的Message代理模式和非代理模式。
## 1.2.0-SNAPSHOT
1. 简化Socket和WebSocket实现类结构，现在统一通过SocketServer类的onload方法来区分。
2. Message消息分为2种代理方式，一种是Message类，一种是AbsMessage类。
3. AbsMessage适用于Protobuff类型定义，可以参考CustomMessage方式定义。
4. 优化了通讯消息、错误命令注解方式，提供了更详细的打印，包括收发消息的文件输出日志。
### 1.1.7-SNAPSHOT
1. 完善协议细节支持，提供了非Message代理的模式（JSON/PROTO）
2. MessageDecoder变更为MessageCrypto
3. 移除了AbsResponse，统一由AbsRequestHanler处理消息内容
4. 相关Protobuf类，需要在类上加上@MessageProxy标识
5. Connector连接器优化
6. 优化Session管理器
### 1.1.6-SNAPSHOT
1. 优化消息协议，为1.1.7做铺垫
2. 修正SessionService网络切换的问题
### 1.1.5-SNAPSHOT
1. 使用Json代替原有WebSocket下的消息解析形式
2. 优化了部分相关内容。
3. 细节优化
### 1.1.4-SNAPSHOT
1. 优化MessageException细节
2. 增加连接管理容器SessionService
3. 提供了服务器套件，以便于各类项目直接应用。
4. 提供了服务器之间通讯方案，详细使用方法，请参考Demo。
### 1.1.3-SNAPSHOT
1. 偷偷修改了配置参数命名，统一结构。
2. 暂时不会增加更多内容，但可能会做细节优化。
