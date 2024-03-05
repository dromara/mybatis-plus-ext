# 版本升级
version=3.5.5-EXT600-beta

echo "开始替换pom.xml的版本号：${version}"
mvn versions:set -DnewVersion=${version}

echo "开始commit到本地仓库：${version}"
git commit -am "版本升级：${version}"

echo "开始打tag：v${version}"
git tag -a v${version} -m "版本号：${version}"

echo "开始提交到远程git仓库：${version}"
git push origin main --tags

echo "开始发布新的版本到maven仓库：${version}"
mvn clean deploy -Dmaven.test.skip=true -pl mybatis-plus-ext-automapper,mybatis-plus-ext-autotable-core,mybatis-plus-ext-bind-core,mybatis-plus-ext-core-base,mybatis-plus-ext-condition-core,mybatis-plus-ext-datasource-core,mybatis-plus-ext-spring-boot-starter,mybatis-plus-ext-spring-boot3-starter -am