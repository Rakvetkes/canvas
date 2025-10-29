# Minecraft 原版群系参数参考

## 基础参数定义

### 温度 (Temperature) - 5个等级
| 索引 | 范围 | 描述 | 代表群系 |
|-----|------|------|---------|
| 0 | -1.0 ~ -0.45 | 冰冻 (Frozen) | 雪原、冰刺平原 |
| 1 | -0.45 ~ -0.15 | 寒冷 (Cold) | 针叶林 |
| 2 | -0.15 ~ 0.2 | 温和 (Temperate) | 平原、森林 |
| 3 | 0.2 ~ 0.55 | 温暖 (Warm) | 丛林、热带草原 |
| 4 | 0.55 ~ 1.0 | 炎热 (Hot) | 沙漠、恶地 |

### 湿度 (Humidity) - 5个等级
| 索引 | 范围 | 描述 | 特征 |
|-----|------|------|------|
| 0 | -1.0 ~ -0.35 | 干燥 (Arid) | 沙漠、恶地 |
| 1 | -0.35 ~ -0.1 | 偏干 (Dry) | 热带草原、平原 |
| 2 | -0.1 ~ 0.1 | 中等 (Neutral) | 普通森林 |
| 3 | 0.1 ~ 0.3 | 湿润 (Humid) | 桦木林、丛林 |
| 4 | 0.3 ~ 1.0 | 非常湿润 (Wet) | 深色森林、竹林 |

### 侵蚀 (Erosion) - 7个等级
| 索引 | 范围 | 描述 |
|-----|------|------|
| 0 | -1.0 ~ -0.78 | 最低侵蚀 - 高山峰顶 |
| 1 | -0.78 ~ -0.375 | 低侵蚀 - 山坡 |
| 2 | -0.375 ~ -0.2225 | 中低侵蚀 - 高原 |
| 3 | -0.2225 ~ 0.05 | 中等侵蚀 - 平地/丘陵 |
| 4 | 0.05 ~ 0.45 | 中高侵蚀 - 平原 |
| 5 | 0.45 ~ 0.55 | 高侵蚀 - 破碎地形 |
| 6 | 0.55 ~ 1.0 | 最高侵蚀 - 河流/沼泽 |

### 大陆性 (Continentalness) - 7个区域
| 名称 | 范围 | 描述 |
|-----|------|------|
| Mushroom Fields | -1.2 ~ -1.05 | 蘑菇岛 |
| Deep Ocean | -1.05 ~ -0.455 | 深海 |
| Ocean | -0.455 ~ -0.19 | 海洋 |
| Coast | -0.19 ~ -0.11 | 海岸 |
| Near Inland | -0.11 ~ 0.03 | 近内陆 |
| Mid Inland | 0.03 ~ 0.3 | 中内陆 |
| Far Inland | 0.3 ~ 1.0 | 远内陆 |

### 深度 (Depth/Weirdness)
- **Valleys（山谷）**: -0.05 ~ 0.05
- **Low**: -0.4 ~ -0.26666668 和 0.05 ~ 0.26666668
- **Mid**: -0.26666668 ~ -0.4 和 0.26666668 ~ 0.4
- **High**: -0.93333334 ~ -0.7666667, -0.7666667 ~ -0.56666666, -0.56666666 ~ -0.4 和对应正值
- **Peaks（山峰）**: -0.7666667 ~ -0.56666666 和 0.56666666 ~ 0.7666667

## 主要群系的参数组合

### MIDDLE_BIOMES（中海拔群系） - Temperature × Humidity 矩阵

|  | 湿度0 (干) | 湿度1 (偏干) | 湿度2 (中) | 湿度3 (湿) | 湿度4 (很湿) |
|--|-----------|-------------|-----------|-----------|-------------|
| **温度0 (冰冻)** | 雪原 | 雪原 | 雪原 | 积雪针叶林 | 针叶林 |
| **温度1 (寒冷)** | 平原 | 平原 | 森林 | 针叶林 | 原始云杉针叶林 |
| **温度2 (温和)** | 繁花森林 | 平原 | 森林 | 桦木林 | 深色森林 |
| **温度3 (温暖)** | 热带草原 | 热带草原 | 森林 | 丛林 | 丛林 |
| **温度4 (炎热)** | 沙漠 | 沙漠 | 沙漠 | 沙漠 | 沙漠 |

### MIDDLE_BIOMES_VARIANT（变种，当 weirdness ≥ 0）

|  | 湿度0 | 湿度1 | 湿度2 | 湿度3 | 湿度4 |
|--|-------|-------|-------|-------|-------|
| **温度0** | 冰刺平原 | null | 积雪针叶林 | null | null |
| **温度1** | null | null | null | null | 原始松木针叶林 |
| **温度2** | 向日葵平原 | null | null | 原始桦木林 | null |
| **温度3** | null | null | 平原 | 稀疏丛林 | 竹林丛林 |
| **温度4** | null | null | null | null | null |

### PLATEAU_BIOMES（高原群系）

|  | 湿度0 | 湿度1 | 湿度2 | 湿度3 | 湿度4 |
|--|-------|-------|-------|-------|-------|
| **温度0** | 雪原 | 雪原 | 雪原 | 积雪针叶林 | 积雪针叶林 |
| **温度1** | 草甸 | 草甸 | 森林 | 针叶林 | 原始云杉针叶林 |
| **温度2** | 草甸 | 草甸 | 草甸 | 草甸 | 深色森林 |
| **温度3** | 热带草原高原 | 热带草原高原 | 森林 | 森林 | 丛林 |
| **温度4** | 恶地 | 恶地 | 恶地 | 繁茂的恶地 | 繁茂的恶地 |

### PLATEAU_BIOMES_VARIANT（高原变种，当 weirdness ≥ 0）

|  | 湿度0 | 湿度1 | 湿度2 | 湿度3 | 湿度4 |
|--|-------|-------|-------|-------|-------|
| **温度0** | 冰刺平原 | null | null | null | null |
| **温度1** | 樱花树林 | null | 草甸 | 草甸 | 原始松木针叶林 |
| **温度2** | 樱花树林 | 樱花树林 | 森林 | 桦木林 | null |
| **温度3** | null | null | null | null | null |
| **温度4** | 风蚀恶地 | 风蚀恶地 | null | null | null |

### SHATTERED_BIOMES（破碎/风袭群系） - 高侵蚀地区

|  | 湿度0 | 湿度1 | 湿度2 | 湿度3 | 湿度4 |
|--|-------|-------|-------|-------|-------|
| **温度0-2** | 风袭沙砾丘陵 | 风袭沙砾丘陵 | 风袭丘陵 | 风袭森林 | 风袭森林 |
| **温度3-4** | null | null | null | null | null |

### 海洋群系 - OCEANS[深度][温度]

| 深度 | 温度0 (冰冻) | 温度1 (寒冷) | 温度2 (温和) | 温度3 (温暖) | 温度4 (炎热) |
|------|-------------|-------------|-------------|-------------|-------------|
| **深海** | 深冻洋 | 深冷洋 | 深海 | 深暖洋 | 暖洋 |
| **浅海** | 冻洋 | 冷洋 | 海洋 | 暖洋 | 暖洋 |

### 山峰群系（Peak Biomes）规则
根据温度和湿度：
- **温度 ≤ 2**: 
  - weirdness < 0: 尖峭山峰 (Jagged Peaks)
  - weirdness ≥ 0: 冰封山峰 (Frozen Peaks)
- **温度 = 3**: 裸岩山峰 (Stony Peaks)
- **温度 = 4**: 恶地变种（根据湿度）

### 山坡群系（Slope Biomes）规则
- **温度 ≥ 3**: 使用高原群系
- **温度 < 3**:
  - 湿度 ≤ 1: 积雪山坡 (Snowy Slopes)
  - 湿度 > 1: 雪林 (Grove)

### 海滩群系（Beach Biomes）规则
- **温度 = 0**: 积雪沙滩 (Snowy Beach)
- **温度 = 4**: 沙漠 (Desert)
- **其他**: 沙滩 (Beach)

### 恶地变种（Badlands Biomes）规则
根据湿度（仅当温度=4时）：
- **湿度 < 2**: 
  - weirdness < 0: 恶地 (Badlands)
  - weirdness ≥ 0: 风蚀恶地 (Eroded Badlands)
- **湿度 = 2**: 恶地
- **湿度 ≥ 3**: 繁茂的恶地 (Wooded Badlands)

### 特殊群系
- **河流 (River)**: 山谷区域（depth: -0.05~0.05），侵蚀6，非冰冻温度
- **冻河 (Frozen River)**: 山谷区域，侵蚀6，冰冻温度
- **沼泽 (Swamp)**: 温度1-2，侵蚀6，内陆区域，山谷
- **红树林沼泽 (Mangrove Swamp)**: 温度3-4，侵蚀6，内陆区域，山谷
- **蘑菇岛 (Mushroom Fields)**: 大陆性 -1.2 ~ -1.05
- **石岸 (Stony Shore)**: 海岸区域，侵蚀0-2
- **风袭热带草原 (Windswept Savanna)**: 温度>1，湿度<4，weirdness≥0

### 地下群系
- **滴水石洞穴 (Dripstone Caves)**: 大陆性 0.8~1.0
- **繁茂洞穴 (Lush Caves)**: 湿度 0.7~1.0
- **深暗之域 (Deep Dark)**: 侵蚀 ≤-0.78，深度 >0.9，底部生成

## 使用示例

### 如何创建自定义群系参数

```java
// 示例：创建一个温暖湿润的平原群系
Climate.ParameterPoint params = Climate.parameters(
    Climate.Parameter.span(0.2F, 0.55F),        // 温度：温暖
    Climate.Parameter.span(0.1F, 0.3F),         // 湿度：湿润
    Climate.Parameter.span(-0.11F, 0.3F),       // 大陆性：近-中内陆
    Climate.Parameter.span(0.05F, 0.45F),       // 侵蚀：中高侵蚀
    Climate.Parameter.point(0.0F),              // 深度：表面
    Climate.Parameter.span(-0.4F, -0.26666668F),// Weirdness: Mid slice
    0.0F                                        // 偏移
);
```

### JSON配置方式（在multi_noise_biome_source_parameter_list中）

```json
{
  "parameters": {
    "temperature": [0.2, 0.55],
    "humidity": [0.1, 0.3],
    "continentalness": [-0.11, 0.3],
    "erosion": [0.05, 0.45],
    "depth": 0.0,
    "weirdness": [-0.4, -0.26666668],
    "offset": 0.0
  },
  "biome": "your_mod:your_biome"
}
```

## 重要常量

```java
// 海岸边界
public static final float NEAR_INLAND_START = -0.11F;
public static final float MID_INLAND_START = 0.03F;
public static final float FAR_INLAND_START = 0.3F;

// 侵蚀边界
public static final float EROSION_INDEX_1_START = -0.78F;
public static final float EROSION_INDEX_2_START = -0.375F;

// 深暗之域阈值
private static final float EROSION_DEEP_DARK_DRYNESS_THRESHOLD = -0.225F;
private static final float DEPTH_DEEP_DARK_DRYNESS_THRESHOLD = 0.9F;

// 山峰和山谷
private static final float VALLEY_SIZE = 0.05F;
private static final float LOW_START = 0.26666668F;
public static final float HIGH_START = 0.4F;
private static final float HIGH_END = 0.93333334F;
private static final float PEAK_SIZE = 0.1F;
public static final float PEAK_START = 0.56666666F;
private static final float PEAK_END = 0.7666667F;
```

## 注意事项

1. **参数组合**: 所有6个参数（温度、湿度、大陆性、侵蚀、深度、怪异度）共同决定群系
2. **优先级**: 后添加的群系参数会覆盖先前的（如果范围重叠）
3. **深度参数**: 
   - 0.0 = 地表
   - 0.2-0.9 = 地下洞穴
   - 1.1 = 深暗之域层
4. **Weirdness**: 控制群系变种的生成，0为分界线
5. **Offset**: 用于细微调整群系边界，通常为0.0

## 参考文件位置
- Java源码: `net.minecraft.world.level.biome.OverworldBiomeBuilder`
- 数据文件: `data/minecraft/worldgen/multi_noise_biome_source_parameter_list/overworld.json`
