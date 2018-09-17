@title SVN工具
rem 使用时，需要将改文件和com、lib同目录
cd /d %~dp0
java -Djava.ext.dirs=lib com.svn.util.SvnMain
pause