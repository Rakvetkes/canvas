# 翻转草方块 (Flipped Grass Block) 实现说明

## 概述
成功创建了一个翻转草方块，具有与原版草方块相同的扩散和生物群系变色行为。

## 创建的文件

### 1. Java 代码
- **CFlippedGrassBlock.java** (`src/main/java/org/aki/helvetti/block/`)
  - 继承自 `SpreadingSnowyDirtBlock`
  - 实现草方块的扩散机制
  - 在光照不足时退化为泥土
  - 支持雪层覆盖（snowy 属性）

### 2. 方块注册 (CCanvasMain.java)
- 注册了 `FLIPPED_GRASS_BLOCK` 方块
- 注册了 `FLIPPED_GRASS_BLOCK_ITEM` 物品
- 添加到创造模式物品栏
- 配置了方块属性：
  - 颜色映射：GRASS
  - 随机刻（用于扩散）
  - 硬度：0.6F
  - 声音：草方块音效

### 3. 颜色提供者
在 `ClientModEvents` 中注册：
- **BlockColor**: 根据生物群系动态改变方块颜色
- **ItemColor**: 物品栏中显示默认草地颜色

### 4. 资源文件

#### 方块状态 (blockstates/)
- `flipped_grass_block.json` - 定义 snowy 状态的模型切换

#### 模型 (models/)
- `block/flipped_grass_block.json` - 普通状态模型
  - 底部：翻转的草方块顶部纹理
  - 顶部：泥土纹理（带生物群系染色）
  - 侧面：基础纹理 + 草地覆层（带生物群系染色）
- `block/flipped_grass_block_snow.json` - 雪覆盖状态模型
- `item/flipped_grass_block.json` - 物品模型

#### 战利品表 (loot_tables/)
- `blocks/flipped_grass_block.json` - 破坏时掉落泥土

#### 纹理 (textures/block/) - 占位符
- `flipped_grass_block_top.png` - 底面纹理（棕色）
- `flipped_grass_block_side.png` - 侧面基础纹理（泥土色）
- `flipped_grass_block_overlay.png` - 草地覆层纹理（半透明绿色）
- `flipped_grass_block_snow.png` - 雪覆盖纹理（白色）

#### 语言文件
- `en_us.json`: "Flipped Grass Block"
- `zh_cn.json`: "翻转草方块"

## 功能特性

### 1. 草方块扩散
- 在光照充足时（亮度 ≥9），会向周围的泥土方块扩散
- 每次随机刻尝试扩散到 4 个随机位置
- 扩散范围：水平 ±1，垂直 -3~+1

### 2. 草方块退化
- 当上方光照不足时，会退化为泥土
- 允许单层雪覆盖

### 3. 生物群系变色
- 方块顶部和侧面覆层会根据所在生物群系改变颜色
- 使用 `BiomeColors.getAverageGrassColor()` 获取生物群系草地颜色
- 物品栏中显示默认草地颜色

### 4. 雪覆盖支持
- 当上方有雪层时，`snowy` 属性为 true
- 切换到雪覆盖模型，侧面显示雪纹理

## 下一步

### 替换占位符纹理
当前纹理是简单的单色占位符，你需要创建实际的纹理：

1. **flipped_grass_block_top.png** (16x16)
   - 应该类似草方块的底部纹理（泥土）
   - 建议参考原版 `grass_block_top.png` 并翻转或修改

2. **flipped_grass_block_side.png** (16x16)
   - 基础侧面纹理（泥土部分）
   - 建议参考原版 `grass_block_side.png` 的泥土部分

3. **flipped_grass_block_overlay.png** (16x16)
   - 草地覆层，应该是半透明的
   - 建议参考原版 `grass_block_side_overlay.png`
   - 这层会被染色以实现生物群系变色

4. **flipped_grass_block_snow.png** (16x16)
   - 雪覆盖时的侧面纹理
   - 建议参考原版 `grass_block_snow.png`

### 测试
运行游戏后：
1. 在创造模式物品栏中找到翻转草方块
2. 测试放置和破坏
3. 测试草方块扩散（将其放在泥土旁边）
4. 测试生物群系变色（在不同生物群系放置）
5. 测试雪覆盖（在上方放置雪层）

## 技术细节

### 为什么是"翻转"？
模型配置中：
- `bottom`: 使用草方块顶部纹理
- `top`: 使用泥土纹理
- 这创建了一个顶部是泥土、底部是草的"翻转"效果

### tintindex 说明
- `tintindex: 0` 应用于需要生物群系染色的面
- 模型中，顶部（泥土面）和侧面覆层使用了 tintindex
- 颜色提供者会根据 tintindex 返回相应颜色

### 扩散机制
与原版草方块相同：
1. 检查当前方块是否有足够光照
2. 如果光照不足，退化为泥土
3. 如果光照充足，尝试扩散到附近泥土方块
4. 扩散目标也需要满足光照条件
