# Pure WebView App — ডকুমেন্টেশন

Android Studio-র জন্য Pure WebView অ্যাপ টেমপ্লেট। কোনো সাইডবার, ড্রয়ার বা মেনু নেই — শুধু আপনার ওয়েবসাইট পুরো স্ক্রিনে WebView-এ চলবে।

---

## সূচিপত্র

1. [প্রজেক্ট ওভারভিউ](#প্রজেক্ট-ওভারভিউ)
2. [প্রয়োজনীয় সফটওয়্যার](#প্রয়োজনীয়-সফটওয়্যার)
3. [Android Studio-তে খোলা](#android-studio-তে-খোলা)
4. [ওয়েবসাইট URL সেট করা](#ওয়েবসাইট-url-সেট-করা)
5. [অ্যাপ কাস্টমাইজেশন](#অ্যাপ-কাস্টমাইজেশন)
6. [অ্যাপ লোগো পরিবর্তন](#অ্যাপ-লোগো-পরিবর্তন)
7. [প্রজেক্ট স্ট্রাকচার](#প্রজেক্ট-স্ট্রাকচার)
8. [বৈশিষ্ট্য](#বৈশিষ্ট্য)
9. [Release APK/AAB বানানো](#release-apkaab-বানানো)
10. [সমস্যা সমাধান](#সমস্যা-সমাধান)

---

## প্রজেক্ট ওভারভিউ

| বিষয় | বিবরণ |
|-------|--------|
| **প্রজেক্ট নাম** | WebView App |
| **Package** | `com.example.webviewapp` |
| **Min SDK** | 24 (Android 7.0) |
| **Target SDK** | 35 |
| **Language** | Kotlin |
| **লোকেশন** | `android-webview-app/` |

### অ্যাপ ফ্লো

```
অ্যাপ খোলা → WebView (ওয়েবসাইট)
              ↓
      ইন্টারনেট নেই → No Internet Screen → Try Again
```

---

## প্রয়োজনীয় সফটওয়্যার

- **Android Studio** — Ladybug (2024.2) বা নতুন ভার্সন
- **JDK 17**
- **Android SDK 35**
- ইন্টারনেট (Gradle sync-এর জন্য)

---

## Android Studio-তে খোলা

1. Android Studio চালু করুন
2. **File → Open** ক্লিক করুন
3. `android-webview-app` ফোল্ডার সিলেক্ট করুন
4. **Trust Project** ক্লিক করুন
5. Gradle sync শেষ হতে দিন (নিচে progress bar দেখাবে)
6. Emulator বা real phone connect করুন
7. **Run (▶)** বাটন চাপুন

> প্রথমবার sync-এ কিছু সময় লাগতে পারে — SDK ও dependencies ডাউনলোড হবে।

---

## ওয়েবসাইট URL সেট করা

ফাইল: `app/src/main/res/values/strings.xml`

```xml
<string name="website_url">https://ecommerce2.nutunit.com</string>
```

### Live ওয়েবসাইট

```xml
<string name="website_url">https://ecommerce2.nutunit.com</string>
```

### Local XAMPP সাইট (PC + Phone একই Wi-Fi-তে)

```xml
<string name="website_url">http://192.168.0.100/</string>
```

> **গুরুত্বপূর্ণ:** `localhost` বা `127.0.0.1` ফোনে কাজ করবে না। PC-র local IP ব্যবহার করুন।

PC-র IP জানতে:
- Windows CMD: `ipconfig` → **IPv4 Address** দেখুন
- সাধারণত `192.168.x.x` ফরম্যাট

---

## অ্যাপ কাস্টমাইজেশন

### ১. অ্যাপের নাম

`app/src/main/res/values/strings.xml`:

```xml
<string name="app_name">আপনার অ্যাপের নাম</string>
```

### ২. Package Name (Play Store-এর জন্য)

`app/build.gradle.kts`:

```kotlin
namespace = "com.yourcompany.yourapp"
applicationId = "com.yourcompany.yourapp"
```

তারপর Android Studio-তে **Refactor → Rename** দিয়ে package folder rename করুন।

### ৩. App Icon — সংক্ষিপ্ত

বিস্তারিত গাইড: [অ্যাপ লোগো পরিবর্তন](#অ্যাপ-লোগো-পরিবর্তন) সেকশন দেখুন।

### ৪. Offline Screen টেক্সট (বাংলা)

`app/src/main/res/values-bn/strings.xml` — বাংলা ডিভাইসে অটো বাংলায় দেখাবে।

### ৫. Version আপডেট

`app/build.gradle.kts`:

```kotlin
versionCode = 2        // প্রতিবার release-এ +1
versionName = "1.1.0"  // ইউজার-দেখা ভার্সন
```

---

## অ্যাপ লোগো পরিবর্তন

App icon শুধু **home screen** ও **app drawer**-এ দেখায়।

| জায়গা | ফাইল |
|--------|------|
| **App Icon** | `ic_launcher_*.xml` + `AndroidManifest.xml` |

---

### Method 1 — Image Asset Studio (সবচেয়ে সহজ) ✅

Android Studio দিয়ে PNG/JPG লোগো থেকে সব size-এর icon অটো বানাবে।

#### App Icon পরিবর্তন

1. আপনার লোগো প্রস্তুত করুন — **512×512 px** PNG (transparent background ভালো)
2. Android Studio-তে project খুলুন
3. **File → New → Image Asset**
4. **Icon Type:** `Launcher Icons (Adaptive and Legacy)` সিলেক্ট করুন
5. **Foreground Layer** ট্যাবে:
   - **Asset Type:** `Image`
   - **Path:** আপনার PNG ফাইল সিলেক্ট করুন
   - **Trim:** Yes (optional)
   - **Resize:** 70–80% (লোগো বড়/ছোট দেখালে adjust করুন)
6. **Background Layer** ট্যাবে:
   - **Color** সিলেক্ট করে আপনার brand color দিন (যেমন `#2563EB`)
   - অথবা background-ও image দিতে পারেন
7. ডান পাশে **Preview** দেখুন (circle, square, rounded)
8. **Next → Finish**

Android Studio নিজে থেকে `mipmap` ও `drawable` ফোল্ডারে icon generate করবে।

9. `AndroidManifest.xml`-এ icon path আপডেট করুন (Image Asset সাধারণত `@mipmap/ic_launcher` সেট করে):

```xml
android:icon="@mipmap/ic_launcher"
android:roundIcon="@mipmap/ic_launcher_round"
```

10. **Build → Clean Project → Rebuild Project**

---

### Method 2 — PNG/Image দিয়ে ম্যানুয়ালি (দ্রুত)

আপনার লোগো PNG/JPG file দিয়ে সরাসরি replace করুন।

#### ধাপ ১ — লোগো PNG যোগ করুন

1. আপনার লোগো ফাইল (`logo.png`) কপি করুন
2. Android Studio-তে `app/src/main/res/drawable/` ফোল্ডারে paste করুন
3. ফাইলের নাম দিন: `my_logo.png` (ছোট হাতের অক্ষর, underscore, no space)

#### ধাপ ২ — App Icon সেট করুন

`app/src/main/AndroidManifest.xml` খুলুন:

```xml
android:icon="@drawable/my_logo"
android:roundIcon="@drawable/my_logo"
```

> **নোট:** PNG icon সব Android version-এ কাজ করবে। Adaptive icon (Android 8+) চাইলে Method 1 ব্যবহার করুন।

#### Rebuild

```
Build → Clean Project
Build → Rebuild Project
Run (▶)
```

---

### Method 3 — XML Drawable এডিট (advanced)

| ফাইল | কাজ |
|------|-----|
| `res/drawable/ic_launcher_foreground.xml` | App icon-এর সামনের অংশ |
| `res/drawable/ic_launcher_background.xml` | Icon background gradient |
| `res/drawable/ic_launcher_legacy.xml` | Fallback icon |

**Manifest-এ বর্তমান icon:**
```xml
android:icon="@drawable/ic_launcher_legacy"
android:roundIcon="@drawable/ic_launcher_legacy"
```

---

### Logo Change — Checklist ✅

- [ ] `AndroidManifest.xml`-এ `android:icon` সঠিক drawable/mipmap point করছে
- [ ] **Clean Project → Rebuild Project** করা হয়েছে
- [ ] ফোনে পুরনো app uninstall করে নতুন install (icon cache clear)

---

### Logo সমস্যা সমাধান

| সমস্যা | সমাধান |
|--------|--------|
| Icon দেখাচ্ছে না | Clean + Rebuild; PNG `drawable/` folder-এ আছে কিনা দেখুন |
| Icon blur/low quality | কমপক্ষে **512×512 px** PNG ব্যবহার করুন |
| Icon crop হয়ে যাচ্ছে | Image Asset-এ Resize 60–70% করুন |
| Round icon square দেখাচ্ছে | `@mipmap/ic_launcher_round` ব্যবহার করুন |

---

## প্রজেক্ট স্ট্রাকচার

```
android-webview-app/
├── app/
│   ├── src/main/
│   │   ├── java/com/purewebview/app/
│   │   │   ├── MainActivity.kt        ← WebView + Offline
│   │   │   └── NetworkUtils.kt        ← ইন্টারনেট চেক
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml
│   │   │   │   └── layout_no_internet.xml
│   │   │   ├── drawable/              ← Icon, Offline
│   │   │   ├── anim/                  ← Animation
│   │   │   ├── values/
│   │   │   │   ├── strings.xml        ← URL, নাম, টেক্সট
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   └── values-bn/             ← বাংলা টেক্সট
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── DOCUMENTATION.md                     ← এই ফাইল
```

---

## বৈশিষ্ট্য

| বৈশিষ্ট্য | বিবরণ |
|---------|--------|
| Pure WebView | কোনো menu, drawer, sidebar নেই |
| No Internet Screen | Offline হলে সুন্দর error page + Retry |
| JavaScript | সম্পূর্ণ সাপোর্ট |
| Cookies & Storage | Login/session কাজ করবে |
| File Upload | ওয়েব ফর্ম থেকে ফাইল আপলোড |
| Pull to Refresh | নিচে টেনে reload |
| Back Button | WebView history অনুযায়ী |
| Zoom | Pinch to zoom |
| tel:/mailto: | ফোন ও ইমেইল লিংক খুলবে |
| HTTP + HTTPS | Local dev-এ HTTP allowed |

---

## Release APK/AAB বানানো

### Signed APK (সরাসরি ইনস্টল)

1. **Build → Generate Signed Bundle / APK**
2. **APK** সিলেক্ট করুন
3. **Create new keystore** (প্রথমবার):
   - Key store path: নিরাপদ জায়গায় রাখুন
   - Password: মনে রাখুন (হারালে আপডেট দিতে পারবেন না)
   - Alias, validity: 25+ years
4. **release** build type
5. Finish → APK `app/release/` ফোল্ডারে পাবেন

### Play Store-এর জন্য AAB

1. একই প্রক্রিয়া, কিন্তু **Android App Bundle** সিলেক্ট করুন
2. `.aab` ফাইল Google Play Console-এ upload করুন

> **Keystore backup:** Keystore ফাইল ও password নিরাপদে backup রাখুন। হারালে Play Store-এ আপডেট দেওয়া যাবে না।

---

## সমস্যা সমাধান

### সাইট লোড হচ্ছে না (Local XAMPP)

- PC ও ফোন **একই Wi-Fi**-তে আছে কিনা দেখুন
- `localhost` ব্যবহার করবেন না — PC-র IP (`192.168.x.x`) দিন
- XAMPP **Apache** চালু আছে কিনা দেখুন
- Windows Firewall Apache block করছে কিনা দেখুন

### Gradle Sync Failed

- **File → Invalidate Caches → Restart**
- Internet connection চেক করুন
- Android Studio আপডেট করুন

### White/Blank Screen

- `website_url` সঠিক কিনা দেখুন
- HTTPS সাইটে SSL error হলে URL চেক করুন
- Logcat-এ error দেখুন: **View → Tool Windows → Logcat**

### File Upload কাজ করছে না

- Android 13+ এ storage permission লাগতে পারে
- `AndroidManifest.xml`-এ প্রয়োজনে permission যোগ করুন

### App Icon দেখাচ্ছে না

- `res/drawable/ic_launcher_legacy.xml` আছে কিনা দেখুন
- **Build → Clean Project** চালান

---

## দ্রুত রেফারেন্স

| কাজ | ফাইল |
|-----|------|
| Website URL | `res/values/strings.xml` |
| App name | `res/values/strings.xml` |
| Package name | `app/build.gradle.kts` |
| **App Icon** | `AndroidManifest.xml` → `@drawable/` বা `@mipmap/` |
| Colors | `res/values/colors.xml` |
| Version | `app/build.gradle.kts` |

---

*Pure WebView App Template — Android Studio*
