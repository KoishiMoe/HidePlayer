# Hide Player

从Minecraft服务器中隐藏任意玩家

## 支持的版本

目前仅支持1.21.4+，因为我出于自用而编写了它，不确定是否有其他人需要。如果你需要其他版本的支持，欢迎提出issue或pr。

## 功能

可以避免玩家不出现在：
- 系统消息（如加入、离开、死亡、获得成就）
- 游戏内可见性（玩家模型、聊天、标签列表、社交界面）
- 服务器状态和查询响应
- 计分板条目

可能的应用场景：
- 服务账户/机器人管理
- 小游戏开发
- 服务器活动
- 管理员活动

## 权限

### 隐藏玩家权限
- `hideplayer.hide.*` - 从所有位置隐藏玩家
- `hideplayer.hide.systemmessage` - 隐藏系统消息
- `hideplayer.hide.ingame` - 隐藏游戏内存在和聊天消息
- `hideplayer.hide.statusandquery` - 在服务器状态/查询中隐藏
- `hideplayer.hide.scoreboard` - 隐藏计分板条目

### 管理权限
- `hideplayer.privilege.seehiddenplayer` - 查看被隐藏的玩家（所有op默认拥有）

**注意：** 玩家重新登录时会自动刷新权限，不提供手动刷新。

## 安装

1. 安装权限管理器（如LuckPerms）
2. 将此模组添加到你的服务器
3. 为需要隐藏的玩家配置权限

## 高级配置

如果你遇到任何问题，如模组冲突或严重的性能下降，你可以编辑 `config/hideplayer.mixin.conf`
并选择禁用该模组提供的任意mixin以进行测试。请注意，这会破坏部分模组功能。

## 许可证

AGPL-3.0-or-later