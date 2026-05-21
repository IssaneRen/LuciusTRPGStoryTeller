#!/bin/bash

# CoC 7th 战斗模拟器 UI 快速启动脚本

cd "$(dirname "$0")"

echo "正在检查 Gradle Wrapper..."
if [ ! -f "./gradlew" ]; then
    echo "Gradle Wrapper 不存在，正在生成..."
    gradle wrapper --gradle-version 8.5
fi

echo "正在编译项目..."
./gradlew build

if [ $? -eq 0 ]; then
    echo "编译成功！正在启动 UI..."
    ./gradlew run
else
    echo "编译失败，请检查错误信息"
    exit 1
fi
