https://github.com/newbee-ltd/newbee-mall
这个项目没有用到复杂的技术，学到的是mybatis的使用.
运行条件是：
1、电脑安装Java环境，配置项目Preference-Java-Compiler-Compiler compliance level的Java版本和电脑一致即可。
2、安装mysql并导入数据库表就可以了。
访问路径：http://localhost:28089/admin/login



首次下载下载别人的项目，可没那么简单运行起来，哪怕所有依赖库都下载/加载了，但是还是会运行后访问localhost:port/还是会出错，
因为编辑环境OK，能编译正常，但是运行环境诸如数据库表结构等，也要和项目匹配才行。
新项目一般需要知道以下操作步骤：
1、根据application.properties的server.port，知道访问端口。
2、修改application.properties的spring.datasource.username和spring.datasource.password为本机一样。
3、根据application.properties的spring.datasource.url，创建本地数据库名。
4、根据src/main/resources/mapper各文件，创建对应的数据库表结构。这个可以慢慢来，一张张表构建，比如运行代码，打开登录页，需要login表了，就创建login表，其他表用到的时候再创建即可。
5、src/main/java/controller是访问url的方式，总不可能只会http://localhost:8080/xxx而不知道xxx还能是什么吧。
6、类似商城等项目，优先看与后台管理项目的接口，因为要通过后台管理调用接口去添加数据库数据。
7、当最初数据库表建立后，不一定就一定能运行，因为数据库表为空，有可能报空指针异常，因此肯定有一个入口是自行添加数据的最开始的操作，
我一开始手动添加tb_newbee_mall_goods_category才能继续操作，因为很多数据都依赖这个表，
后来发现，左边菜单的“分类管理”里便可直接操作该数据库表了，不需要手动操作数据库表了。

mybatis是持久层，有这个一般都会出现src/main/resources/mapper，也就知道数据库表的结构了

该项目后台管理系统部分用到的数据库：
tb_newbee_mall_admin_user 后台管理系统得登录用户
tb_newbee_mall_goods_category 商品分类
tb_newbee_mall_goods_info 商品属性
tb_newbee_mall_carousel 轮播图
tb_newbee_mall_index_config 不同类型的商品
tb_newbee_mall_order 订单
tb_newbee_mall_user 注册登录的使用者


后台管理系统用到的数据库表，基本上已经包含了前端需要的数据库表了。


题外话：
springboot发布到服务器，假如服务器是CentOS，只需要把该项目打包成jar包，然后放在后台运行指令java -jar xxx即可运行，
因为springboot默认内置Apache，为什么公司还要引用Nginx呢？
首先，应当理解Tomcat Apache Nginx都是WEB服务器，也就是说只要买了一台硬件服务器，只要搭好Tomcat Apache Nginx其中之一就可以
将服务器配置成web服务器了，就可以通过链接访问了。
之所以不用springboot默认自带的Apache作为web服务器，是因为Apache稳定、开源、跨平台，不支持高并发的重量级的服务器，
在Apache上运行数以万计的并发访问，会导致服务器消耗大量内存。
而Nginx是轻量级高并发服务器。
对于小公司而言，可能是购买阿里云的几台服务器，然后每台服务器都用自带Apache运行即可，这是最简单的，或者配置外置Tomcat也行，
然后用其中一台服务器作为主服务器，部署Nginx作为负载均衡器，配置好其它服务器的访问权重，这样每次访问这个主服务器，就会反向代理到不同的
服务器了，这样比如A服务器的CPU高一点，配置权重多一点，B台服务器性能低，配置访问权重低一点，这样每台服务器都压力不大，不会因为某台服务器
高并发而不够内存而奔溃。
具体可看： https://www.cnblogs.com/Franken-Fran/p/nginx_balanceLoad.html


负载均衡，和SSO不一样，Nginx分配每台服务器的压力不一样，但是里面运行的系统是完全一样的，然后还要进行数据定时整合才行，不然不同步。
而SSO是单点登录，几个系统，只要登录了某一个系统，就能直接访问其他系统，共享autho认证。



