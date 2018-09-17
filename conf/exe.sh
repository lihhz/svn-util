#!/bin/bash
# 在Linux中请使用./exe.sh
# 在Windows中编辑后，该文件在Linux中可能报错，提示“没有那个文件或目录”。
# 这是因为文件格式不同所致。
# 请在vim中命令行模式下使用 set ff=unix解决
java -Djava.ext.dirs=lib com.svn.util.SvnMain
