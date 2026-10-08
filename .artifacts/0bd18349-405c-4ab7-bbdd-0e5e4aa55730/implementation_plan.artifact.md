# 头像显示修复方案 (User Profile & Chat Avatars)

本计划旨在解决 `UserProfileScreen`（个人资料页面）和 `ChatScreen`（聊天页面）中未显示真实用户头像的问题，统一使用 Coil 的 `AsyncImage` 和统一的 `buildAvatarUrl` 逻辑进行加载。

## 用户审阅要求

> [!IMPORTANT]
> 确保后端服务正常运行且图片上传接口返回了正确的相对/绝对路径（例如 `/uploads/avatars/xxx.jpg`）。

## 拟定修改文件

### 1. [UserProfileScreen.kt](file:///C:/Users/YJU/AndroidStudioProjects/soul/app/src/main/java/com/example/soul_android/ui/screens/UserProfileScreen.kt)
- 引入 Coil 的 `AsyncImage`。
- 实现 `buildAvatarUrl` 函数。
- 在 `ProfileContent` 的头像 Box 中，当 `profile.avatar` 不为空时使用 `AsyncImage` 加载头像图片，否则回退显示默认的 `Icons.Default.Person`。

### 2. [ChatViewModel.kt](file:///C:/Users/YJU/AndroidStudioProjects/soul/app/src/main/java/com/example/soul_android/ui/viewmodels/ChatViewModel.kt)
- 在 `ChatUiState.Success` 中增加 `partnerAvatar: String?` 字段。
- 在 `fetchMessages` 时顺便调用 `apiService.getUserProfile(otherUserId)` 获取对方的用户资料并提取其 avatar，传递给 UI 状态。

### 3. [ChatScreen.kt](file:///C:/Users/YJU/AndroidStudioProjects/soul/app/src/main/java/com/example/soul_android/ui/screens/ChatScreen.kt)
- 接收 `partnerAvatar`。
- 在 `ChatBubbleComponent` 中使用 Coil `AsyncImage` 加载对方头像（通过 `buildAvatarUrl(partnerAvatar)`）。若头像不存在，则回退显示名字首字母。

## 验证计划

### 自动/手动验证
1. 运行 App 并进入个人资料页面 (`UserProfileScreen`)，验证是否能正确加载用户上传的头像。
2. 进入聊天页面 (`ChatScreen`)，验证聊天气泡左侧是否能正确显示聊天对象的头像。
