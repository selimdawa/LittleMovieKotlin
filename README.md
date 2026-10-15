<h1 align="center">Little Movie - Kotlin</h1>

<p align="center">
 <a><img alt="Min SDK" src="https://img.shields.io/badge/Min SDK-24-020290?logo=android&logoColor=white"/></a>
 <a><img alt="Target SDK" src="https://img.shields.io/badge/Target SDK-37-0EB265?logo=android&logoColor=0EB265"/></a>
 <a href="https://kotlinlang.org"><img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.4.20-blue?logo=kotlin&logoColor=white"/></a>
 <a href="https://opensource.org/licenses/MIT"><img alt="License" src="https://img.shields.io/badge/License-MIT-CC9900?logo=MIT&logoColor=white"/></a>
</p>

## Day Mode
Splash | Welcome | Login 
--- | --- | --- | 
![](UI/1.jpg) | ![](UI/2.jpg) | ![](UI/3.jpg) | 

Register | Forget Pass | Home 
--- | --- | --- | 
![](UI/4.jpg) | ![](UI/5.jpg) | ![](UI/6.jpg) | 

Favorites Movie | Categories | Settings 
--- | --- | --- | 
![](UI/7.jpg) | ![](UI/8.jpg) | ![](UI/9.jpg) | 

My Profile | Edit Profile | Category Movies 
--- | --- | --- | 
![](UI/10.jpg) | ![](UI/11.jpg) | ![](UI/12.jpg) | 

Details | Add Comment | Comment 
--- | --- | --- | 
![](UI/13.jpg) | ![](UI/14.jpg) | ![](UI/15.jpg) | 

My Cast | Cast Details | Cast Info
--- | --- | --- | 
![](UI/16.jpg) | ![](UI/17.jpg) | ![](UI/18.jpg) | 

My Categories | Favorites | About App
--- | --- | --- | 
![](UI/19.jpg) | ![](UI/20.jpg) | ![](UI/21.jpg) | 

Logout | Share App | Privacy Policy  
--- | --- | --- | 
![](UI/22.jpg) | ![](UI/23.jpg) | ![](UI/24.jpg) | 

## Night Mode
Splash | Welcome | Login 
--- | --- | --- | 
![](UI/25.jpg) | ![](UI/26.jpg) | ![](UI/27.jpg) | 

egister | Forget Pass  | Home 
--- | --- | --- | 
![](UI/28.jpg) | ![](UI/29.jpg) | ![](UI/30.jpg) | 

Favorites Movie | Categories | Settings 
--- | --- | --- | 
![](UI/31.jpg) | ![](UI/32.jpg) | ![](UI/33.jpg) | 

My Profile | Edit Profile | Category Movies
--- | --- | --- | 
![](UI/34.jpg) | ![](UI/35.jpg) | ![](UI/36.jpg) | 

Details | Add Comment | Comment 
--- | --- | --- | 
![](UI/37.jpg) | ![](UI/38.jpg) | ![](UI/39.jpg) | 

My Cast | Cast Details | Cast Info
--- | --- | --- | 
![](UI/40.jpg) | ![](UI/41.jpg) | ![](UI/42.jpg) | 

My Categories | Favorites | About App
--- | --- | --- | 
![](UI/43.jpg) | ![](UI/44.jpg) | ![](UI/45.jpg) | 

Logout | Share App | Privacy Policy  
--- | --- | --- | 
![](UI/46.jpg) | ![](UI/47.jpg) | ![](UI/48.jpg) | 

---

### ✨ Core Functionalities
*   **Robust Authentication Flow**: Secure identity management via **Firebase Authentication**.
*   **Rich Movie & Cast Catalog**: Explore movies, cast details, categories, and video trailers.
*   **Interactive Community**: Add comments, rate movies, and manage favorites.
*   **Context-Aware Theming**: Adaptive UI support for dark and light modes.
*   **Resilient Offline Capabilities**: "Local-first" data strategy using **Room Database**.
*   **Elastic Cloud Synchronization**: Distributed data persistence via **Firebase**.

---

## 🛠️ Built With
*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **UI Framework:** Material Design 3, ViewBinding
*   **Database:** [Room](https://developer.android.com/training/data-storage/room)
*   **Cloud Infrastructure:** [Firebase](https://firebase.google.com/) (Auth, Realtime Database)
*   **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
*   **Async Operations:** Coroutines & Flow

---

### 🏗️ Technical Architecture

#### User App
```text
app/src/main/java/com/flatcode/littlemovie/
├── db/                 # Room Database Configuration & DAOs
├── di/                 # Dependency Injection (Hilt modules)
├── model/              # Data Entities & Models
├── repository/         # Repository Pattern Implementation
├── ui/                 # Presentation Layer
│   ├── auth/           # Authentication (Login, Register)
│   ├── cast/           # Cast Profiles & Details
│   ├── category/       # Category Movies & Browsing
│   ├── main/           # Main Dashboard & Home Navigation
│   ├── movie/          # Movie Operations, Details & Comments
│   ├── profile/        # User Profile, Favorites & Edit Profile
│   └── settings/       # App Settings & Privacy Policy
└── utils/              # Extensions, Constants & Utility Classes
```

#### Admin App
```text
app/src/main/java/com/flatcode/littlemovieadmin/
├── di/                 # Dependency Injection (Hilt modules)
├── model/              # Data Entities & Models
├── repository/         # Repository Pattern Implementation
├── ui/                 # Admin Presentation Layer
│   ├── auth/           # Admin Authentication
│   ├── cast/           # Cast Management
│   ├── category/       # Category Management
│   ├── editorschoice/  # Editor's Choice Picks
│   ├── favorite/       # Featured & Favorites Management
│   ├── main/           # Admin Dashboard
│   ├── movie/          # Movie Uploads, Editing & Video Links
│   ├── privacypolicy/  # Privacy Policy Settings
│   ├── profile/        # Admin Profile Settings
│   ├── slider/         # Home Banner Sliders
│   └── users/          # User Management
└── utils/              # Extensions, Constants & Utility Classes
```

---

## 🚀 Getting Started

### Prerequisites
*   Android Studio Ladybug or newer.
*   JDK 21.
*   Android SDK Level 37 (Compile SDK).

### Installation
1.  Clone the repository:
    ```bash
    git clone https://github.com/selimdawa/LittleMovieKotlin.git
    ```
2.  Open the project in Android Studio.
3.  Sync Project with Gradle Files.
4.  Add your `google-services.json` file to the `app/` directory.
5.  Run the app on your device or emulator.

---

### 🛠️ Technology Stack
*   **Language**: Kotlin (Modern, expressive, and safe development).
*   **Dependency Injection**: **Dagger Hilt** (Simplified, standard DI for Android).
*   **Cloud Infrastructure**: **Firebase** (Scalable authentication and real-time NoSQL storage).
*   **Local Persistence**: **Room** (SQLite abstraction layer for robust offline data).
*   **Concurrency**: **Coroutines & Flow** (High-performance asynchronous data streams).
*   **View Interop**: **ViewBinding** (Safe and efficient UI component interaction).

---

## 🔗 Links & Resources
*   **Legacy Versions:**
    *   [Kotlin Old Code Version](https://github.com/selimdawa/LittleMovieKotlinOld/)
    *   [Java Old Code Version](https://github.com/selimdawa/LittleMovie/)
*   **Author:** [Selim Dawa](https://github.com/selimdawa)

---

## 📜 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

