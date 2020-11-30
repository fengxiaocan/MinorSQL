## 1、说明：

`1.根据JavaBean创建操作数据库`

`2.实现增删改查功能`

`3.支持重新创建数据库表、清理数据库、重命名数据库等方法`

`4.支持根据同一个JavaBean创建多个不同名称的数据库表`

`5.支持临时根据JavaBean创建多个数据库表`

`6.支持升级数据库能升级所有相关联的JavaBean的数据库表`

`7.升级不会清空数据库表，会保留已有字段的数据`

`8.支持批量操作增、删、改，优化系统自带的数据库增、删、改的效率，性能提升几十倍`

`9.支持异步操作数据库，在异步线程or主线程中回调结果`

`10.支持Insert Or Replace的插入或替换数据的操作方法:数据已存在,则更新;数据不存在,则插入`

`11.支持链式调用方法操作查询条件语句,只需要传入数据表的行名,不需要自己手动写 where 查询语句`

`12.暂时只支持基本数据类型的数据库表映射，其他类型不会映射成数据表名称`


## 2、依赖:

|  module  |  msql-api  |  msql-compiler  |
|:---|:---|:---|
|  version  |  [![](https://jitpack.io/v/fengxiaocan/MinorSQL.svg)](https://jitpack.io/#fengxiaocan/MinorSQL)  |  [![](https://jitpack.io/v/fengxiaocan/MinorSQL.svg)](https://jitpack.io/#fengxiaocan/MinorSQL)  |

**步骤 1. 将其添加到项目的根目录下的 build.gradle**

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

**步骤 2. 在项目的Module下添加依赖关系**

	dependencies {
	        implementation 'com.github.fengxiaocan.MinorSQL:msql-api:+'
            annotationProcessor 'com.github.fengxiaocan.MinorSQL:msql-compiler:+'
	}


## 3、使用

`创建一个JavaBean,并使用@com.app.annotation.SQLite来注解,并在成员变量上使用@com.app.annotation.Column注解来标注你想要的类型`

    @SQLite(table = {"tableName1","tableName2","tableName3"})
    public class JavaTest{
          @Column(id = true)
          long id;
          
          @Column(unique = true,name = "Obj")    
          String name;
          
          long time;  
    }

`在Application里或者使用之前使用MSQLHelper来注册`

        //初始化,设置数据库名(可不填),设置数据库的版本
        MSQLHelper.init(context.getApplication(),默认数据库名, 1);
        //注册数据库,参数为需要注册的数据库表的JavaBean,调用后会生成数据库,JavaBean必须被@SQLite注解    
        MSQLHelper.registerDatabase(JavaTest.class,...);
        //或者使用如下,第一个参数为添加新的数据库的名称,第二个参数为需要注册的数据库表的JavaBean
        MSQLHelper.initDatabase(new String[]{"数据库名1","数据库名2},JavaTest.class,...);


**1.@SQLite**

  1).注解是标记是否映射成数据库表的关键,如果没有该注解,不会生成映射文件

  2).注解的值为该映射数据库表的名称,可以为空,值只能为 字母 和 '_',不分大小写

  3).注解的值为空则默认为该JavaBean的名称,如果有冲突则会自动添加其他字符

  4).注解的值只有一个则默认数据表名为该值,@SQLite(table = "tableName1")

  5).注解的值有多个则默认数据表名不变,数值为新增的书库表名

  6).注解处理器会生成当前Bean的数据库的一些字段供开发者使用,例如默认的数据库表名为:
  同一包名.JavaBeanSQL.DEFAULT_TABLE_NAME;

  其他的数据库字段也在同包名下的JavaBeanSQL中

**2.@Column**

  1).注解暂时只对基本数据类型生效,其他的数据类型略过

  2).name 为数据表的字段名,没有则为该成员变量的名称

  3).id 表示是否设置为数据库的ID,只能是long或int类型

  4).nullable 表示该字段是否可以为null值,默认为true可以为null

  5).unique 表示是否为独一无二,只能有一个字段生效,默认为false否

  6).defaultValue 字符的默认值,默认没有默认值

  7).ignore 忽略该字段不作映射

**3.SQLSupport**

  1).JavaBean继承后可以对当前bean的数据进行增、删、改的操作。

  2).只继承SQLSupport而不使用@SQLite来注解，不会生效。

  3).不继承只会在编译前失去Bean自身的增删改的方法的操作，不影响数据库的使用和MSQLHelper的方法

  4).JavaBean可以继承其他的类

**3.MSQLHelper(带有Async都为异步线程操作)**

  1).MSQLHelper.init():初始化,设置数据库名(可不填),设置数据库的版本

  2).MSQLHelper.registerDatabase():注册数据库,参数为需要注册的数据库表的JavaBean,调用后会生成数据库,JavaBean必须被@SQLite注解

  3).MSQLHelper.registerDatabaseAsync():异步线程中注册数据库

  4).MSQLHelper.initDatabase():注册数据库,需要添加新的数据库名称的需要使用,不能跟registerDatabase方法同时存在

  5).MSQLHelper.initDatabaseAsync():异步线程中注册数据库

  6).MSQLHelper.DefaultDbName():获取默认的数据库名称

  7).MSQLHelper.query(String tableName):创建一个默认数据库的查询,用于查询出数据Bean；

  8).MSQLHelper.where(String tableName):创建一个默认数据库的查询,用于删除、更新；

  9).MSQLHelper.SQL(String databaseName):数据库操作链式调用开始,里面封装了相关的插入、更新、替换、删除、查询的方法；

 **4.MinorSQLHandler**

  1).registerTempTable 临时创建一个数据表,返回的为数据表的名称

  1).tempTable 临时创建一个数据表

  1).tempTableAsync 异步线程临时创建一个数据表

 **5.QueryHandler**

    //选择需要查询的字段,相当于SQL语句中的 SELECT columns FORM
    select(String... columns);
    
    //查询的是否唯一,相当于SQL语句的 SELECT DISTINCT * FROM
    distinct(boolean unique);
    
    //查询的组排序 
    groupBy(String groupBy, String having);
    
    //查询排序 
    orderBy(String orderBy, boolean desc);
    
    //查询多少个数据
    limit(int limit) ;
    
    //查询数据偏移量
    offset(int offset);
    
    //构建 AND WHERE SQl 语句
    whereSql(String whereClause, Object... condition);
    
    //构建 OR WHERE SQl 语句
    orWhereSql(String whereClause, Object... condition);
    
    //构建 AND column IN(?,?...?) 查询条件语句
    whereIn(String column, Object... conditions);
    
    //构建 AND column IN(?,?...?) 查询条件语句
    whereIn(String column, List<T> list);

    //构建以ID为查询条件语句
    whereId(long... ids);

    //构建 AND column LIKE ? 查询条件语句
    whereLike(String column, Object condition);

    //构建 AND column = ? 查询条件语句
    whereEqual(String column, Object condition);

    //构建 AND column != ? 查询条件语句
    whereNoEqual(String column, Object condition);
    
    //构建 AND column > ? 查询条件语句
    whereGreater(String column, Object condition);

    //构建 AND column < ? 查询条件语句
    whereLess(String column, Object condition);

    //构建 AND column < ? 查询条件语句
    whereNoGreater(String column, Object condition);

    //构建 AND column >= ? 查询条件语句
    whereNoLess(String column, Object condition);

## 4、例子

    //1.插入一个Bean数据
    JavaBean bean = new JavaBean();
    bean.setData(...);
    bean.insert(JavaBeanSQL.otherTableName);
    //JavaBeanSQL为生成同级目录下的数据库字段常量类,有数据表名以及行字段
    //2.使用其他方式插入
    MSQLHelper.SQL().table(JavaBeanSQL.otherTableName).insert(bean);
    
    //3.更新,直接调用update会以Bean的sqlId为条件来更新
    bean.setSQLId(100);
    bean.update();
    MSQLHelper.SQL().table(JavaBeanSQL.otherTableName).update(bean);
    
    //4.删除
    bean.delete();
    MSQLHelper.SQL().table(JavaBeanSQL.otherTableName).delete(bean);

    //5.插入或更新,以id或者有unique的字段来更新,没有则插入新的数据
    bean.replace();
    MSQLHelper.SQL().table(JavaBeanSQL.otherTableName).replace(bean);
    
    //6.查询所有的数据
    List<JavaBean> list = MSQLHelper.SQL(dbName).table(JavaBeanSQL.otherTableName).asQuery().find(JavaBean.class);
    //7.查询age字段为40,50,60的所有数据
    List<JavaBean> list = MSQLHelper.SQL().model(JavaBean.class).asQuery().whereIn("age",40,50,60).find(JavaBean.class);
    //8.查询age字段大于100的最后一条数据
    JavaBean bean = MSQLHelper.SQL().model(JavaBean.class).asQuery().whereGreater("age",100).findLast(JavaBean.class);
    //9.查询age字段小于5的第一条数据
    JavaBean bean = MSQLHelper.SQL().model(JavaBean.class).asQuery().whereLess("age",100).findFirst(JavaBean.class);
    //10.查询id为1,2,3,4,5的所有数据
    List<JavaBean> list = MSQLHelper.SQL().model(JavaBean.class).asQuery().whereId(1,2,3,4,5).find(JavaBean.class);
    //11.异步查询id为1,2,3,4,5的所有数据,回调在主线程
    MSQLHelper.SQL().model(JavaBean.class)
              .asQuery()
              .whereId(1,2,3,4,5)
              .findAsync(JavaBean.class)
              .mainListen(new DataCallback<List<JavaBean>>() {
                    @Override
                    public void finish(List<JavaBean> data) {
                        ...
                    }
              });
    
    //12.更新age为5的数据
    MSQLHelper.SQL().model(JavaBean.class)
              .asWhere()
              .whereEqual(JavaBeanSQL.age,5)
              .update(bean);
               
    //13.创建一个临时数据库并插入数据
    MSQLHelper.SQL()
              .tempTable(JavaBeanSQL.class, "http://***.com/api/1000")
              .insert(bean);
    
    //14.异步线程中创建一个临时数据库并更新数据
    MSQLHelper.SQL()
              .tempTableAsync(JavaBeanSQL.class, "http://***.com/api/1000")
              .listen(data -> {
                    data.insert(bean);
              });          




