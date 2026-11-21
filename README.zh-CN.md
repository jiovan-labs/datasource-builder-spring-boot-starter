[English](README.md) | [简体中文](README.zh-CN.md) | [繁體中文](README.zh-TW.md)

# 数据源构建器 Spring Boot Starter

让 Spring Boot 应用快速集成 `DataSource`。

## 快速开始

- 导入依赖

```xml

<dependency>
  <groupId>com.jiovan.spring.starter</groupId>
  <artifactId>datasource-builder-spring-boot-starter</artifactId>
  <version>LATEST</version>
</dependency>
```

> 默认情况下，此 starter 会自动生效，你可以通过 `jiovan.datasource-builder.enabled = false` 关闭它。

- 通过构造函数或 `@Autowired`/`@Resource` 注解注入一个 `DataSourceBuilder` Bean，然后即可用它创建如下 Bean：

| 方法返回值 | 方法名 |
|------------------------------|------------------------|
| DataSourceProperties | dataSourceProperties |
| DataSource | dataSource |
| DataSource | xaDataSource |
| DataSourceTransactionManager | jdbcTransactionManager |
| JpaTransactionManager | jpaTransactionManager |

- 此 starter 支持全球主流数据源连接池，包括
  - c3p0
  - dbcp2
  - druid
  - hikari
  - oracle ucp
  - tomcat
  - vibur

## 文档

- GitHub：https://github.com/jiovan-labs/datasource-builder-spring-boot-starter

## 环境要求

- jdk 17+

## 许可证

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可证。

请参阅 `NOTICE.txt` 文件了解所需的声明与归属信息。

## 捐赠

喜欢这个工具包吗？欢迎[向我们捐赠](https://jiovan.com/donation)以支持开发。

## 版权

北京玖漫科技有限公司

## 网站

- 玖漫：https://jiovan.com
