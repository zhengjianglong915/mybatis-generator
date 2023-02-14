MyBatis Generator (MBG)
=======================
[![Build Status](https://github.com/mybatis/generator/workflows/Java%20CI/badge.svg?branch=master)](https://github.com/mybatis/generator/actions?query=workflow%3A%22Java+CI%22)
[![Coverage Status](https://coveralls.io/repos/mybatis/generator/badge.svg?branch=master&service=github)](https://coveralls.io/github/mybatis/generator?branch=master)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/org.mybatis.generator/mybatis-generator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.mybatis.generator/mybatis-generator)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/org.mybatis.generator/mybatis-generator.svg)](https://oss.sonatype.org/content/repositories/snapshots/org/mybatis/generator/mybatis-generator/)
[![License](https://img.shields.io/:license-apache-brightgreen.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mybatis_generator&metric=alert_status)](https://sonarcloud.io/dashboard?id=mybatis_generator)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=mybatis_generator&metric=security_rating)](https://sonarcloud.io/dashboard?id=mybatis_generator)

![mybatis-generator](https://mybatis.org/images/mybatis-logo.png)

This is a code generator for MyBatis.

This library will generate code for use with MyBatis. It will introspect a database table (or many tables) and will generate artifacts that can be used to access the table(s). This lessens the initial nuisance of setting up objects and configuration files to interact with database tables. MBG seeks to make a major impact on the large percentage of database operations that are simple CRUD (Create, Retrieve, Update, Delete).

MBG can generate code in multiple styles (or "runtimes"). MBG can generate code for Java based projects, or for Kotlin based projects.

## 代码介绍
代码从 Mybatis-generator 仓库的 1.4.2 版本fork 出来，并对代码做了改造，适配自己的使用。 避免自动生成代码的性能问题.

优化并提供一下能力:
1. xml sql 格式化。 不同 element 支持换行；去除条件里的 jdbcType.
2. 保留根据主键的操作
3. Mapper 中的参数名字优化
4. 支持java 代码合并，新增代码不会被覆盖