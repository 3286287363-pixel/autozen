# AutoZen 🚗

> An open-source Android Automotive OS (AAOS) smart dashboard & driving assistant

![Platform](https://img.shields.io/badge/platform-Android%20Automotive%20OS-brightgreen)
![Language](https://img.shields.io/badge/language-Kotlin-blue)
![License](https://img.shields.io/badge/license-MIT-orange)

---

## 简介 / Introduction

AutoZen 是一个专为 **Android Automotive OS (AAOS)** 设计的开源智能仪表盘应用，内置模拟数据模式，无需真实车辆即可运行体验。

AutoZen is an open-source smart dashboard app designed natively for **Android Automotive OS**, with a built-in simulation mode — no real car needed.

---

## 功能模块 / Features

| 模块 | 描述 |
|------|------|
| 智能仪表盘 | 实时车速/转速/油量/水温表盘，支持驾驶模式切换 |
| 行程记录 | 引擎启动自动开始，停车自动保存，Room 本地持久化 |
| 车载天气 | 基于位置的实时天气，极简车载 UI |
| 驾驶专注模式 | 全屏大字显示车速，隐藏导航栏，防分心设计 |
| 安全建议横幅 | 结合车辆状态与天气，实时推送驾驶安全提示 |
| 模拟数据模式 | 无需真实 OBD 设备即可完整体验所有功能 |
| 多语言支持 | 中文 / English 系统语言自动切换 |

---

## 技术栈 / Tech Stack

- **语言**: Kotlin
- **架构**: MVVM + Clean Architecture + 多模块
- **UI**: Jetpack Compose for Automotive
- **DI**: Hilt
- **数据库**: Room
- **网络**: Retrofit + OkHttp
- **CI/CD**: GitHub Actions

---

## 快速开始 / Quick Start

### 1. 创建 AAOS 模拟器

1. 打开 Android Studio → Device Manager
2. Create Virtual Device → Automotive → **Polestar 2**
3. 选择 API 33 系统镜像
4. 启动模拟器

### 2. 配置 API Key（可选）

在 `local.properties` 中添加：
```
WEATHER_API_KEY=your_openweathermap_key
```

> 不配置 API Key 时，天气模块显示错误提示，其他模块正常运行。

### 3. 运行项目

```bash
git clone https://github.com/3286287363-pixel/autozen.git
cd autozen
# 用 Android Studio 打开，选择 AAOS 模拟器运行
```

---

## 项目结构 / Project Structure

```
autozen/
├── app/                    # 主应用入口 + 导航
├── feature-dashboard/      # 仪表盘（Canvas 动效）
├── feature-trip/           # 行程记录（Room）
├── feature-weather/        # 天气（Retrofit）
├── core-data/              # Room Database + DI
├── core-network/           # Retrofit 网络层
├── core-obd/               # OBD 数据源（模拟/真实）
└── core-ui/                # 公共 Compose 组件 + 主题
```

---

## 开发路线 / Roadmap

- [x] 多模块项目初始化
- [x] 仪表盘 UI（Canvas Arc 动效）
- [x] 模拟 OBD 数据源
- [x] 行程记录（Room）
- [x] 车载天气（Retrofit）
- [ ] 真实 OBD-II 蓝牙接入
- [x] 驾驶专注模式
- [x] GitHub Actions CI
- [x] 多语言支持
- [x] 自动行程记录（引擎启动/停车触发）
- [x] 驾驶安全建议（车辆状态 + 天气联动）

---

## 贡献 / Contributing

PR 和 Issue 欢迎！请遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范。

---

## 许可 / License

MIT License © 2025 AutoZen Contributors
