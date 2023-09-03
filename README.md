# java-memshell-generator

## 0x01 工具简介

jMG (Java Memshell Generator) 是一款支持高度自定义的 Java 内存马生成工具，提供常见中间件的内存马注入支持。


- 支持的中间件和框架 (Tomcat/Resin/Jetty/WebLogic/WebSphere/Undertow/GlassFish/SpringMVC/SpringWebFlux)
- 支持的工具 (AntSword/Behinder/Godzilla/Suo5)
- 支持的内存马类型 (Filter/Listener/Interceptor/HandlerMethod)
- 支持的输出格式 (BASE64/BCEL/CLASS/JS/JSP/JAR/BIGINTEGER)
- 支持的辅助模块 (专项漏洞封装/表达式语句封装)


![](./img/gui.png)


**免责声明**
```
该工具仅适用于在授权环境/测试环境进行使用，请勿用于生产环境。
```

## 0x02 中间件/框架覆盖情况

注：以下测试结果仅供参考

#### 中间件


|                 | listener           | filter          | 
| --------------- | -----------------  | --------------- |
| tomcat 9.0.39   | ✅                 | ✅               |                                  
| tomcat 8.5.53   | ✅                 | ✅               |                                  
| tomcat 7.0.59   | ✅                 | ✅               |                                 
| tomcat 6.0.48   | ✅                 | ✅               |                                 
| tomcat 5.5.36   | ✅                 | ✅               |                                  
| jetty 9.4.43    | ✅                 | ✅               |                              
| jetty 8.2.0     | ✅                 | ✅               |                               
| jetty 7.6.0     | ✅                 | ✅               |                               
| resin 4.0.66    | ✅                 | ✅               |                              
| resin 3.1.15    | ✅                 | ✅               |                                 
| weblogic 10.3.6 | ✅                 | ✅               |                                
| weblogic 12.1.3 | ✅                 | ✅               |                                
| weblogic 14.1.1 | ✅                 | ✅               |                                
| websphere 7.x   | ✅                 | ✅               |                                  
| websphere 8.5.5 | ✅                 | ✅               |                                  
| websphere 9.0.0 | ✅                 | ✅               |                                  
| undertow 1.4.26 | ✅                 | ✅               |                                 
| glassfish 5.0.0 | ✅                 | ✅               |                                 

#### 框架

|                | interceptor       | handlermethod |
|----------------| ----------------- |-------------|
| spring mvc     | ✅                |             |
| spring webflux |                   |✅            |


## 0x03 致谢与引用


Sponsor
```text
奇安信观星实验室(SGLAB of Legendsec at Qi'anxin Group)
```

<img src="./img/sglab.svg" width=300 alt="SgLab">

Contributors
```text
https://github.com/c0ny1
https://github.com/whwlsfb
```

References
```
https://github.com/woodpecker-framework/
https://github.com/woodpecker-appstore/jexpr-encoder-utils
https://github.com/feihong-cs/memShell
https://github.com/su18/MemoryShell
https://github.com/BeichenDream/GodzillaMemoryShellProject
https://github.com/whwlsfb/cve-2022-22947-godzilla-memshell
```

## 0x04 其他

1. 参考文档
- [jMG v1.0.4 (使用手册)](https://mp.weixin.qq.com/s/oAiGWY9ABhn2o148snA_sg)
- [jMG v1.0.5](https://mp.weixin.qq.com/s/QjoRs_J5jVANrdEiiTtVtA)
- [jMG v1.0.6](https://mp.weixin.qq.com/s/0ZzH35aRUPelq8nwilMQiA)

2. 技术交流
- 期待更多反馈，如果遇到 Bug / 建议 / 需求，欢迎提 Issue 互相交流

3. 注意
- jMG v1.0.6 暂不支持 woodpecker 插件模式