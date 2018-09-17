# svn增量发布工具

在实际项目中，因为用的是svn。在项目上线后必须增量发布补丁包。项目的结构比较简单，是在eclipse中新建的maven java web 项目。根据eclipse新建maven java web的文件系统结构，写了这个工具。目前该工具在windows和linux下均可使用。

因为是针对正在使用的项目，所以在其它项目使用时，可能有“水土不服”，还需要做一定的修改。


## 使用方法

1. 根据实际环境修改exe.bat（windows环境）或者exe.sh（linux环境）
2. 执行exe.bat或者exe.sh
3. 按照提示输入相关信息并回车
4. 详细信息在各个配置文件中都有详细描述，详见svn-util/conf下各个配置文件的说明注释
