# DamagerRewards

[![Version](https://img.shields.io/badge/version-1.0--SNAPSHOT-blue.svg)](https://github.com/i7mc/DamagerRewards)
[![Minecraft](https://img.shields.io/badge/minecraft-1.20.1-green.svg)](https://papermc.io/)
[![Java](https://img.shields.io/badge/java-17+-orange.svg)](https://adoptium.net/)

**DamagerRewards** 是一个基于 MythicMobs 的伤害排名奖励系统插件，为 Minecraft 服务器提供公平、灵活的 Boss 战奖励机制。

## 🎯 核心功能

### 伤害统计系统
- **实时伤害记录** - 自动监听并记录玩家对 MythicMobs 怪物造成的伤害
- **智能排名计算** - 根据总伤害量自动生成实时排行榜
- **多怪物支持** - 可同时为多种不同的 MythicMobs 怪物配置独立的奖励系统

### 奖励发放机制
- **排名奖励** - 根据伤害排名给予不同等级的奖励（如第1名获得传说装备，第2名获得稀有装备等）
- **参与奖励** - 所有参与攻击的玩家都能获得基础参与奖励，鼓励团队合作
- **自动发放** - 怪物死亡时自动执行奖励命令，无需手动干预

### 灵活配置系统
- **范围限制** - 可设置玩家与怪物的距离限制，防止远程"蹭伤害"
- **智能排名** - 系统根据TopRewards配置自动确定排名数量，无需手动设置
- **世界限制** - 可指定在哪些世界启用奖励系统
- **自定义奖励** - 支持执行任意控制台命令作为奖励
- **实时排名显示** - 怪物死亡后自动向范围内玩家显示完整伤害排行榜

### 占位符支持
- **PlaceholderAPI 集成** - 提供丰富的占位符用于其他插件调用
- **实时数据** - 占位符数据实时更新，可用于记分板、聊天等场景

## 📁 项目结构

```
src/
├── main/
│   ├── java/cn/i7mc/damagerRewards/
│   │   ├── DamagerRewards.java              # 主插件类，负责插件初始化和生命周期管理
│   │   ├── command/
│   │   │   └── DamagerRewardsCommand.java   # 命令处理器，处理 /dr 相关命令
│   │   ├── config/
│   │   │   ├── ConfigLoader.java            # 配置文件加载器，管理所有怪物配置
│   │   │   └── MobConfig.java               # 单个怪物配置类，封装怪物奖励规则
│   │   ├── data/
│   │   │   ├── PlayerDamageData.java        # 玩家伤害数据类，存储单个玩家的伤害信息
│   │   │   └── RankingData.java             # 排名数据类，管理整个怪物的排行榜
│   │   ├── listener/
│   │   │   ├── DamageListener.java          # 伤害监听器，监听玩家攻击事件
│   │   │   └── MobDeathListener.java        # 怪物死亡监听器，触发奖励发放
│   │   ├── manager/
│   │   │   ├── ConfigManager.java           # 配置管理抽象类，提供配置文件操作基础功能
│   │   │   ├── DamageManager.java           # 伤害管理抽象类，定义伤害统计接口
│   │   │   ├── DamageManagerImpl.java       # 伤害管理实现类，具体的伤害统计逻辑
│   │   │   └── RewardManager.java           # 奖励管理器，负责奖励命令的执行
│   │   ├── placeholder/
│   │   │   └── DamagerPlaceholder.java      # PlaceholderAPI 扩展，提供占位符功能
│   │   └── util/
│   │       ├── LocationUtil.java            # 位置工具类，处理距离计算和世界检查
│   │       ├── MessageUtil.java             # 消息工具类，统一管理插件消息输出
│   │       └── MythicMobsUtil.java          # MythicMobs 工具类，处理怪物识别和信息获取
│   └── resources/
│       ├── plugin.yml                       # 插件描述文件
│       ├── config.yml                       # 主配置文件
│       ├── message.yml                      # 消息配置文件
│       └── mobs/                            # 怪物配置目录
│           ├── mob1.yml                     # 示例怪物配置1（King）
│           └── mob2.yml                     # 示例怪物配置2（Dragon）
```

## 🚀 安装与使用

### 前置要求
- **Minecraft 服务端**: Paper 1.20.1 或更高版本
- **Java**: 17 或更高版本
- **必需插件**: MythicMobs 5.5+
- **可选插件**: PlaceholderAPI（用于占位符功能）

### 安装步骤
1. 下载 `DamagerRewards.jar` 文件
2. 将文件放入服务器的 `plugins` 目录
3. 重启服务器或使用 `/reload` 命令
4. 插件会自动生成配置文件和示例配置

### 基础配置

#### 主配置文件 (`config.yml`)
```yaml
# 是否启用调试模式
debug: false

# 语言设置
language: zh_CN

# 插件设置
settings:
  # 是否在控制台显示详细日志
  verbose-logging: false
  
  # 是否自动保存数据
  auto-save: true
  
  # 自动保存间隔(分钟)
  save-interval: 5
```

#### 消息配置 (`message.yml`)
所有插件输出的消息都可以在此文件中自定义，支持颜色代码和占位符：

```yaml
prefix: "&6[DamagerRewards] &f"

plugin:
  reloaded: "${prefix}&a配置文件已重新加载!"
  no-permission: "${prefix}&c你没有权限执行此命令!"

reward:
  top-reward: "${prefix}&6恭喜 &a{player} &6获得第{rank}名奖励!"
  participation-reward: "${prefix}&6感谢 &a{player} &6的参与，获得参与奖励!"
```

## ⚙️ 怪物配置详解

每个怪物都需要在 `mobs` 目录下创建独立的配置文件。文件名可以自定义，但必须以 `.yml` 结尾。

### 🎯 简化设计理念

本插件采用简化的配置设计：

- **无伤害门槛**: 所有造成伤害的玩家都参与排名，无需达到特定伤害值
- **动态排名数**: 系统根据TopRewards中配置的奖励数量自动确定排名数
- **智能奖励**: 前N名获得排名奖励，其余获得安慰奖励，避免重复发放
- **透明显示**: 自动向在场玩家显示完整排行榜，增加游戏体验

### 配置文件示例 (`mobs/king.yml`)

```yaml
# 统计的MythicMobs插件中配置的怪物名(注意不是DISPLAY)
MythicMobs: King

# 排名奖励配置[控制台身份执行]
# 系统会自动根据配置的Top奖励数量确定排名数
# 由于兼容了PlaceholderAPI插件注册按排名识别玩家名的占位符,我们直接用占位符代替玩家名
TopRewards:
  Top1: mm item 传说圣剑 give %dr_top_1% 1
  Top2: mm item 优良圣剑 give %dr_top_2% 1
  Top3: mm item 普通圣剑 give %dr_top_3% 1

# 安慰奖励(给未获得排名奖励的参与者)
# 只要对boss造成过伤害且在自定义范围内就算参与
AllRewards:
  all: mm item 安慰奖品 give <player> 1

# 自定义生效的世界名限制
world:
  world1: false        # 在world1禁用
  world2: true         # 在world2启用

# 自定义玩家范围限制(方块)
Range: 50
```

### 配置参数说明

| 参数 | 说明 | 示例 |
|------|------|------|
| `MythicMobs` | MythicMobs 中配置的怪物内部名称 | `King` |
| `TopRewards` | 排名奖励命令配置，系统自动根据配置数量确定排名数 | `Top1: give %dr_top_1% diamond 1` |
| `AllRewards` | 安慰奖励命令配置，给未获得排名奖励的参与者 | `all: give <player> bread 1` |
| `world` | 世界启用/禁用配置 | `world: true` |
| `Range` | 玩家与怪物的最大距离限制，也是排名显示范围 | `50` |

### 奖励命令说明

#### 排名奖励占位符
- `%dr_top_1%` - 第1名玩家名称
- `%dr_top_2%` - 第2名玩家名称  
- `%dr_top_3%` - 第3名玩家名称
- 以此类推...

#### 参与奖励占位符
- `<player>` - 当前参与玩家名称

#### 命令示例
```yaml
TopRewards:
  Top1: "give %dr_top_1% diamond 10"           # 给第1名10个钻石
  Top2: "give %dr_top_2% gold_ingot 5"         # 给第2名5个金锭
  Top3: "eco give %dr_top_3% 1000"             # 给第3名1000金币

AllRewards:
  all: "give <player> bread 3"                 # 给每个参与者3个面包
```

## 📊 排名显示系统

当怪物死亡后，系统会自动向范围内的所有玩家显示完整的伤害排行榜：

### 显示效果
```
=== King 伤害排行榜 ===
1. PlayerA 造成了 150.50 伤害
2. PlayerB 造成了 120.30 伤害
3. PlayerC 造成了 80.20 伤害
4. PlayerD 造成了 45.10 伤害
========================
```

### 显示规则
- **范围限制**: 只有在配置的Range范围内的玩家才能看到排名
- **世界限制**: 遵循配置文件中的世界启用/禁用设置
- **实时显示**: 怪物死亡后立即显示给在场玩家
- **完整排名**: 显示所有参与伤害统计的玩家排名

### 奖励分发逻辑
- **前N名**: 根据TopRewards中配置的数量，前N名玩家获得对应的排名奖励
- **其余玩家**: 未获得排名奖励的参与者获得AllRewards安慰奖励
- **排除重复**: 已获得排名奖励的玩家不会再获得安慰奖励

## 🎮 游戏内使用

### 管理员命令
- `/dr` 或 `/damagerrewards` - 显示帮助信息
- `/dr reload` - 重新加载所有配置文件

### 权限节点
- `damagerrewards.admin` - 管理员权限（默认：OP）
- `damagerrewards.reload` - 重载配置权限（默认：OP）

### 游戏流程
1. **配置怪物** - 在 `mobs` 目录下为每种 MythicMobs 怪物创建配置文件
2. **玩家攻击** - 玩家攻击配置的怪物时，系统自动记录伤害
3. **实时排名** - 系统实时计算并更新伤害排行榜
4. **怪物死亡** - 怪物死亡时：
   - 向范围内玩家显示完整伤害排行榜
   - 根据TopRewards配置给前N名发放排名奖励
   - 给其余参与者发放安慰奖励
   - 重置排名数据

## 🔌 PlaceholderAPI 占位符

如果安装了 PlaceholderAPI，可以使用以下占位符：

### 全局排名占位符
- `%dr_top_1%` - 全局第1名玩家名称
- `%dr_top_2%` - 全局第2名玩家名称
- `%dr_top_3%` - 全局第3名玩家名称

### 特定怪物排名占位符
- `%dr_top_1_king%` - King 怪物第1名玩家名称
- `%dr_top_2_dragon%` - Dragon 怪物第2名玩家名称
- `%dr_top_3_boss%` - Boss 怪物第3名玩家名称

这些占位符可以用在记分板、聊天格式、全息显示等支持 PlaceholderAPI 的地方。

## 🛠️ 故障排除

### 常见问题

**Q: 插件无法加载？**
A: 检查是否安装了 MythicMobs 插件，确保服务器版本为 Paper 1.20.1+

**Q: 怪物死亡后没有发放奖励？**
A: 检查以下几点：
- 怪物配置文件中的 `MythicMobs` 名称是否正确
- 玩家是否在配置的距离范围内
- 当前世界是否在 `world` 配置中启用
- `TopRewards` 和 `AllRewards` 配置是否正确

**Q: 占位符不工作？**
A: 确保安装了 PlaceholderAPI 插件，并使用 `/papi reload` 重载占位符

**Q: 奖励命令执行失败？**
A: 检查控制台错误日志，确保奖励命令语法正确，相关插件已安装

### 调试模式
在 `config.yml` 中设置 `debug: true` 可以启用调试模式，查看详细的运行日志。

## 📞 支持与反馈

- **官网**: https://github.com/linghun91/DamagerRewards
- **作者**: Saga
- **版本**: 1.0-SNAPSHOT

如有问题或建议，请联系插件作者或在相关论坛发帖求助。
