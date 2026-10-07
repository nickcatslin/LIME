# <img src="app/src/main/ic_launcher-playstore.png" width="60px"> LIME：LINE 廣告清除工具（台灣 fork）

[![Latest Release](https://img.shields.io/github/v/release/nickcatslin/LIME?label=latest)](https://github.com/nickcatslin/LIME/releases/latest)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 概要

LIME 是一個用來清理 [**LINE**](https://line.me) 的 Xposed 模組，可以移除廣告、推薦內容與多餘的圖示，並提供多項隱私相關功能。

本專案是 [Chipppppppppp/LIME](https://github.com/Chipppppppppp/LIME) 的 fork。原專案最後發布的版本是 v1.12.1（對應 LINE 15.0.0），之後沒有再發布新版。本 fork 接手讓 LIME 支援新版 LINE，並針對**台灣版 LINE** 的介面調整，例如主頁的廣告卡片、「逛逛」分頁等。

> [!NOTE]
> 本 fork 的 APK 以 repo 內附的 `android.jks` 簽章，與原專案正式版的簽章不同。
> 若手機上已安裝原專案的 LIME，請先移除後再安裝本 fork 的版本。

## 支援的 LINE 版本

LIME 依賴 LINE 經混淆後的類別名稱，LINE 每次改版幾乎都需要重新對應。請務必使用與 LIME 版本相符的 LINE。

| LIME | 對應的 LINE | 分支 |
|---|---|---|
| [v1.13.3](https://github.com/nickcatslin/LIME/releases/tag/v1.13.3) | 26.15.1 | `support-line-26.15.1` |
| [v1.13.2](https://github.com/nickcatslin/LIME/releases/tag/v1.13.2) | 26.15.0 | `support-line-26.15.0` |
| [v1.13.1](https://github.com/nickcatslin/LIME/releases/tag/v1.13.1) | 26.15.0 | `support-line-26.15.0` |
| [v1.13.0](https://github.com/nickcatslin/LIME/releases/tag/v1.13.0) | 26.14.0 | `support-line-26.14.0` |

LINE 版本與 LIME 不符時，開啟 LINE 會跳出提示訊息。可以在設定中開啟「停止檢查 LINE 版本」關閉這個提示，但不相符的版本可能有部分功能失效。

## 使用方法

從 LINE 的 <kbd>主頁</kbd> > <kbd>⚙</kbd> 進入「**設定**」，點右上角的「**LIME**」按鈕即可開啟設定。Root 使用者也可以從 LI**M**E App 進行設定。分身 App 等環境可能只能從 LI**M**E 那一側設定。

<details><summary>查看圖片</summary>

<a href="#"><img src="https://github.com/Chipppppppppp/LIME/assets/78024852/2f344ce7-1329-4564-b500-1dd79e586ea9" width="400px" alt="Sample screenshot"></a>

</details>

在聊天畫面右上角的 <kbd>⁝</kbd> 開啟開關後，就能**以未讀狀態閱讀訊息**（這個開關可以在設定中移除）。

※回覆訊息後未讀狀態就會解除，請留意。

<details><summary>查看圖片</summary>

<a href="#"><img src="https://github.com/Chipppppppppp/LIME/assets/78024852/bd391a83-b041-4282-9eec-fe71b3b19aa0" width="400px" alt="Sample screenshot"></a>

</details>

## 功能

- 刪除底部列多餘的圖示（逛逛、錢包、新聞或通話）
- 刪除底部列圖示的文字標籤
- 刪除廣告與推薦內容
  - 包含主頁的廣告卡片、主頁與聊天列表頂端的 Google 廣告橫幅，以及相簿、行事曆、記事本、社群等處的廣告
- 移除服務標籤
- 將導覽列設為黑色
- 刪除通知中的「靜音聊天」操作
- 用預設瀏覽器開啟 WebView
- 總是不標記為已讀
- 查看群組中的已讀者
  - 點群組聊天上方的「R」按鈕即可查看
- 以未讀狀態閱讀
  - 從聊天畫面右上角選單的開關設定（開關可移除）
- 防止訊息撤回
  - 會保存被撤回訊息的內容與時間
- 總是以靜音訊息傳送
  - 傳送時選擇「一般訊息」就會照常通知對方
- 讓隱藏的聊天不再重新顯示
- 自動備份聊天記錄（參考：https://github.com/areteruhiro/LIMEs/issues/10）
- 阻擋追蹤通訊
  - 阻擋 `noop`、`reportAbuseEx`、`reportDeviceState`、`reportLocation`、`reportNetworkStatus`、`reportProfile`、`reportPushRecvReports`、`reportSetting`
- 將通訊內容輸出到日誌
- 修改通訊內容
  - 可以用 JavaScript 修改通訊內容（見下文）

### 用 JavaScript 修改通訊內容

<details>

在設定的「修改請求」與「修改回應」中撰寫 Rhino 的 JavaScript 程式碼，就能自由修改通訊內容。原專案已確認可以用這個方式實作新功能（見 `communication_modification_sample.md`）。

程式中預先準備了變數 `data`，包含以下屬性：

- `type`：`Enum` 型別，值為 `REQUEST` 或 `RESPONSE`
- `name`：通訊的名稱
- `value`：通訊內容

※`data` 是[這個類別](app/src/main/java/io/github/chipppppppppp/lime/hooks/Communication.java)的實例，可以用「將通訊內容輸出到日誌」確認內容。

另外預先準備了 `getMember` 與 `setMember` 兩個函式，用來讀取與設定成員變數。直接用 `.` 存取時可能會存取到同名的方法而不是成員變數，因此建議使用這兩個函式。

```js
console.log(getMember(data.value, "a")); // 取得成員變數 a 的值
setMember(data.value, "a", false); // 將成員變數 a 設為 false
```

`console.log` 會輸出到 `XposedBridge` 的日誌，發生錯誤時也會輸出到這裡。
不論請求或回應，JavaScript 都會比其他處理先執行，而「將通訊內容輸出到日誌」最後執行。
請留意 Rhino 的特性，尤其是**和 Java 字串比較時必須使用 `equals`**。

</details>

## 安裝

先下載 **LINE** 與 **LIME** 的 APK。LINE 的版本請參考上方「支援的 LINE 版本」表格。

> [!IMPORTANT]
> 請不要使用分割 APK（split APK）。
> 不要硬把分割檔合併，請務必使用完整的原始 APK。

LI**N**E
- [APKMirror](https://www.apkmirror.com/uploads/?appcategory=line)
- [APKPure](https://apkpure.net/jp/line-calls-messages/jp.naver.line.android/versions)
- [APKCombo](https://apkcombo.com/ja/line/jp.naver.line.android/old-versions/)
- [Uptodown](https://line.jp.uptodown.com/android/versions)

LI**M**E
- [本 fork 的 Releases](https://github.com/nickcatslin/LIME/releases/latest)

### Root 裝置（Magisk）

1. 安裝 [**LSPosed（JingMatrix 版）**](https://github.com/JingMatrix/LSPosed/releases)
2. 安裝 LI**N**E 與 LI**M**E 兩個 App
3. 為了避免 Google Play 商店自動更新 LINE，請用 [**Update Locker**](https://github.com/Xposed-Modules-Repo/ru.mike.updatelocker) 或 [**Hide My Applist**](https://github.com/Dr-TSNG/Hide-My-Applist) 指定 LINE
   使用 [Aurora Store](https://auroraoss.com) 時請用黑名單功能
4. 在 LSPosed 的模組列表中進入 LIME，勾選 <kbd>啟用模組</kbd> 並勾選 LINE

### 非 Root 裝置

> [!WARNING]
> 非 Root 裝置有以下問題：
> - 無法用 Google 帳號（雲端硬碟）還原聊天記錄
>   （用[這個方法](https://github.com/Chipppppppppp/LIME/issues/50#issuecomment-2174842592)登入則可以）
> - 來電與撥出時不會響鈴
>   LIME 有模擬鈴聲的功能
> - 有來電時會當機
> - 無法購買代幣
> - LINE Pay 的部分功能無法使用
> - 無法在 Wear OS（智慧手錶）上使用

1. 安裝 [**LSPatch**](https://github.com/LSPosed/LSPatch)
   ※由 fork 開發的 [**NPatch**](https://github.com/HSSkyBoy/NPatch) 可能會有問題。
   如果用 **LSPosed 官方**的 LSPatch 修補後 App 會當機，改用 fork 版的 [**JingMatrix LSPatch**](https://github.com/JingMatrix/LSPatch/) 修補，有可能就能正常運作。
2. 開啟 **LSPatch** App，依序點 <kbd>管理</kbd> > 右下角的 <kbd>＋</kbd> > <kbd>從儲存空間選擇 apk</kbd> > 選擇剛才下載的 LI**N**E APK > <kbd>整合</kbd> → <kbd>嵌入模組</kbd> > <kbd>選擇已安裝的應用程式</kbd> > 勾選 LI**M**E 並按 <kbd>＋</kbd> > <kbd>開始修補</kbd>

※用[這個方法](https://github.com/Chipppppppppp/LIME/issues/50#issuecomment-2174842592)似乎可以還原聊天記錄。

> [!TIP]
> 出現 <kbd>選擇目錄</kbd> 時，按 <kbd>OK</kbd> 開啟檔案選擇器，在任意目錄下建立資料夾，再按 <kbd>使用這個資料夾</kbd> > <kbd>允許</kbd>。

3. 有使用 [**Shizuku**](https://github.com/RikkaApps/Shizuku) 的話，按 <kbd>安裝</kbd> 繼續。
   沒有使用的話，請改用檔案管理員等其他 App 安裝。

> [!IMPORTANT]
> 如果已經安裝了從 Play 商店下載的 LINE，簽章會衝突，請先解除安裝。

## 多裝置登入

### 1. 以電腦版身分登入

> [!WARNING]
> 這個方法目前無法使用。

<details><summary>查看方法</summary>

偽裝成電腦版（Windows）LINE。這樣電腦版 LINE 會被強制登出，但可以把功能受限的電腦版改成在另一台 Android 上使用。

※若其中一台裝置是 iOS，可能會因為 Letter Sealing 失敗而收不到訊息。請依照[這個方法](https://github.com/Chipppppppppp/LIME/issues/88#issuecomment-2012001059)重新產生 Letter Sealing 金鑰（金鑰可以在任一聊天右上角的 <kbd>☰</kbd> > <kbd>設定</kbd> > <kbd>加密金鑰</kbd> 查看）。

- 優點：訊息同步沒有問題、LIME 只需裝在其中一台、非 Root 也可以使用
- 缺點：無法在 3 台以上的裝置登入、第 2 台裝置不會顯示服務圖示

#### 步驟

1. 在另一台裝置安裝 LINE 與 LIME
2. 在 LINE 的登入畫面勾選「偽裝成 PC（DESKTOPWIN）」
3. 從 <kbd>設定</kbd> > <kbd>應用程式</kbd> > <kbd>LINE</kbd> 進入 LINE 的應用程式資訊，點「強制停止」，再到「儲存空間和快取」點「清除快取」
4. 再次開啟 LINE，點「Log in as secondary device」登入
5. 登入後，從 LINE 設定的「聊天記錄備份與復原」還原 2 週以前的聊天記錄

</details>

### 2. 偽裝 Android ID

這個方法只有在**兩台裝置都已 Root** 時才能使用。
如同 <https://jesuscorona.hatenablog.com/entry/2019/02/10/010920> 所說，訊息同步會有些許延遲，請留意。

<details>

- 優點：可以在 3 台以上的裝置登入，所有服務都能使用
- 缺點：訊息同步會延遲、僅限 Root

#### 步驟

1. 安裝 LINE 與 LIME
2. 在 LINE 的登入畫面勾選「多裝置登入（偽裝 Android ID）」
3. 從 <kbd>設定</kbd> > <kbd>應用程式</kbd> > <kbd>LINE</kbd> 進入 LINE 的應用程式資訊，點「強制停止」，再到「儲存空間和快取」點「清除快取」
4. 再次開啟 LINE 並登入
5. 登入後，用 [Swift Backup](https://play.google.com/store/apps/details?id=org.swiftapps.swiftbackup) 備份 LINE（詳見[這篇](https://blog.hogehoge.com/2022/01/android-swift-backup.html)）
6. 把 Swift Backup 的備份資料夾移到另一台裝置，安裝備份的 LINE（詳見[這篇](https://blog.hogehoge.com/2022/05/SwiftBackup2.html)）
7. **不要開啟** LINE，先安裝 LIME

</details>

## 與原專案的差異

- 支援新版 LINE（26.14.0 以後），所有混淆後的 hook 目標都已重新對應。
- 修正開啟「阻擋追蹤通訊」後 LINE 當機的問題（原專案 [#239](https://github.com/Chipppppppppp/LIME/issues/239)）。
- 擋下新版 LINE 的廣告：主頁廣告卡片、Google 廣告橫幅，以及 LINE Ads SDK v2 的廣告。
- 「刪除推薦」也會隱藏台灣版主頁的推薦內容，例如「推薦貼圖」。
- 底部列的「刪除 VOOM 圖示」改為「刪除逛逛圖示」。台灣版 LINE 已經用「逛逛」取代了 VOOM 分頁。
- 某一個功能的 hook 失效時，不再連帶讓其他功能一起失效，並會在 LSPosed 日誌留下 `LIME: … failed to hook` 方便追查。

## 本 fork 歷程

- **2026-09（v1.13.0）**：從原專案 v1.12.1 接手，支援 LINE 26.14.0。修正 #239 的當機問題，新增主頁與聊天列表的廣告清除。
- **2026-09（v1.13.1）**：支援 LINE 26.15.0。hook 安裝改為逐一容錯，避免單一 hook 失效時拖累其他功能。
- **2026-10（v1.13.2）**：「刪除 VOOM 圖示」改為「刪除逛逛圖示」，對應台灣版 LINE 底部列的改版。
- **2026-10（v1.13.3）**：支援 LINE 26.15.1，修正主頁廣告卡片在新版中重新出現的問題。

各版本的詳細內容請見 [Releases](https://github.com/nickcatslin/LIME/releases)。

## 問題回報

本 fork 目前沒有開放 Issues。與 LINE 版本無關、原專案也有的問題，可以到[原專案的 Issues](https://github.com/Chipppppppppp/LIME/issues/new/choose) 回報。原專案希望以日文撰寫，看不懂日文也可以用英文。

## 致謝

LIME 由 [Chipppppppppp](https://github.com/Chipppppppppp) 與原專案的貢獻者開發，本 fork 在其基礎上維護。授權條款沿用原專案的 [MIT License](LICENSE)。
