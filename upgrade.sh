# 版本升级
version=3.5.9-EXT812

echo "开始替换pom.xml的版本号：${version}"
mvn versions:set -DnewVersion=${version}

echo "开始commit到本地仓库：${version}"
git commit -am "版本升级：${version}"

tagName=v${version}
echo "开始打tag：${tagName}"
git rev-parse --verify ${tagName} >/dev/null 2>&1
if [ $? -eq 0 ]; then
    git tag -d ${tagName}
    echo "本地标签${tagName}已删除"
fi
if git ls-remote --tags | grep -q "refs/tags/${tagName}"; then
    git push origin --delete ${tagName}
    echo "远程标签${tagName}已删除"
fi
echo "新建标签：${tagName}"
git tag -a ${tagName} -m "版本号：${version}"

echo "开始提交到远程git仓库：${version}"
git push origin main --tags

echo "开始发布新的版本到maven仓库：${version}"
mvn clean deploy -pl mybatis-plus-ext-autotable,mybatis-plus-ext-bind,mybatis-plus-ext-core,mybatis-plus-ext-condition,mybatis-plus-ext-datasource,mybatis-plus-ext-processor,mybatis-plus-ext-spring-boot-starter,mybatis-plus-ext-spring-boot3-starter -am
