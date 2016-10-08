# 这是什么?
这是一个用于将个人QQ空间内所有说说收集起来的工具. 最终目标是可以将其整理为html或者pdf文件, 留做纪念. 目前将内容整理成了JSON格式, 有兴趣的可以在些基础上进行进一步的处理.

# 为什么写这个？
就是想写代码了！

# 使用方法
由于在从QQ空间内下载所有说说, 因此需要登录. 但考虑到登录复杂度较高, 且不是这个项目的主要目的. 因以, 我使用了一种简洁的方法, 即, 直接在浏览器登录, 然后自己手动复制cookie, 并作为命令行参数传入.

## 获取可执行文件
可以直接在github的release中下载jar包, 也可以自己手动编译. 编译方法很简单, 下载项目, 运行`mvn package -Dmaven.test.skip=true`.

## 下载说说
准备好了cookie及jar包后, 运行`java -jar wx-qzone.jar "p_uin=o0373490201; p_skey=GYatlPmdAIAz9uEvWPA8basA0JrkyCy7hn6jpbwc23U_"`.

一段时间后, 会生成一个以你的QQ命名的json文件.

## Cookie需要哪些项?
QZone登录流程变动很快, 所以我实在懒得去写一个自动登录的程序, 维护成本太高. 直接复制Cookie简单易行. 目前看来, 有两个项是必须复制的, 即`p_uin`及`p_skey`.

示例如下:

```
p_uin=o0373490201; p_skey=GYatlPmdAIAz9uEvWPA8basA0JrkyCy7hn6jpbwc23U_
```

# FAQ
+ 有时说说下载到一半, 忽然失败, 可能是因为QZone做了限流, 所以重试即可.
