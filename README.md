# java-memshell-generator


## 0x01 简介

jMG (Java Memshell Generator) 是一款支持高度自定义的 Java 内存马生成工具，提供常见中间件的内存马注入支持。不仅可作为 [woodpecker](https://github.com/woodpecker-framework/woodpecker-framework-release) 的插件使用，也可以作为独立的 GUI 工具进行使用，文档详见[公众号文章](https://mp.weixin.qq.com/s/oAiGWY9ABhn2o148snA_sg)。


## 0x02 两种工作模式

#### Woodpecker 插件工作模式

<img width="877" alt="image" src="https://github.com/pen4uin/java-memshell-generator/assets/55024146/1b07d338-5cd9-4035-8566-be2d4ae914d4">

#### GUI 图形化工作模式

<img width="877" alt="image-20230610110205993" src="https://github.com/pen4uin/java-memshell-generator/assets/55024146/4b79a767-9327-4d2a-bd89-7f1386560a9d">


## 0x03 中间件/框架覆盖情况

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


|                 | interceptor       |  action           |    
| --------------- | ----------------- | -----------------  | 
| spring mvc      | ✅                |                    | 
| strust2          |                   |                    | 



## 0x04 致谢

SGLAB of Legendsec at Qi'anxin Group

<img src="./images/sglab.svg" width=300 alt="SgLab">


## 0x05 最后

免责声明
```
该工具仅适用于在授权环境/测试环境进行使用，请勿用于生产环境。
```

技术交流
```
如果遇到 bug / 建议 / 实战场景需求，欢迎提 issue 交流。
```

引用项目
```
https://github.com/woodpecker-framework/
https://github.com/woodpecker-appstore/jexpr-encoder-utils
```

参考项目
```
https://github.com/feihong-cs/memShell
https://github.com/su18/MemoryShell
https://github.com/BeichenDream/GodzillaMemoryShellProject
```



