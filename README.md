<h1 align="center">Translator (KMM)</h1></br>
<p align="center">  
A translator app built with Kotlin Mobile multiplatform and MVI based on Philipp Lackner's <a href="https://pl-coding.com/building-industry-level-multiplatform-apps-with-kmm">KMM course</a>. After completing the course I made several improvements by adding navigation library, common resource library, localization for turkish language and more test cases. You can find more detailed information about the overall app structure and my improvements.
</p>
</br>

<p align="center">
  <a href="https://android-arsenal.com/api?level=24"><img alt="API" src="https://img.shields.io/badge/API-24%2B-yellow"/></a>
  <a href="https://developer.apple.com/documentation/ios-ipados-release-notes/ios-ipados-15-release-notes"><img alt="IOS" src="https://img.shields.io/badge/IOS-15%2B-yellow"/></a>
  <a href="https://github.com/canonall"><img alt="Profile" src="https://img.shields.io/badge/git-canonall-yellow"/></a> 
</p>


## Screeshots

### Android
<p align="center">
<img src="Preview/Translate_android.gif" width="20%"/>
<img src="Preview/VoiceToText_android.gif" width="20%"/>
</p>

### iOS
<p align="center">
<img src="Preview/Translate_IOS.gif" width="20%"/>
<img src="Preview/VoiceToText_IOS.gif" width="20%"/>
</p>

## Tech stack & libraries
- [Kotlin Mobile Multiplatform](https://kotlinlang.org/lp/mobile/) - create navite apps by sharing common code between iOS and Android apps.
  - [Kotlin](https://kotlinlang.org)
  - [Swift](https://developer.apple.com/swift/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - is the modern toolkit recommended by Android for building native user UI. It streamlines and speeds up the development process by allowing for the creation of powerful and intuitive UI with less code and easy-to-use Kotlin APIs.
- [SwiftUI](https://developer.apple.com/xcode/swiftui/) - is a declarative UI toolkit created by Apple for designing responsive user interfaces for iOS and macOS. With its concise and expressive syntax, it makes it easy to create powerful and engaging UI with minimal code.
- [Navigation Destinations](https://github.com/raamcosta/compose-destinations) - Easy to use navigation library for Jetpack Compose
- [Android Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Dependency Injection Library for Android
- [Coroutines](https://developer.android.com/kotlin/coroutines) - is a concurrency design pattern that you can use on Android to simplify code that executes asynchronously.
- [Flow](https://developer.android.com/kotlin/flow) - is a type that can emit multiple values sequentially. They are built on top of coroutines and can provide multiple values
- [Ktor](https://ktor.io) - Create asynchronous client and server applications. Anything from microservices to multiplatform HTTP client apps in a simple way. Ktor is built from using Kotlin and Coroutines.
- [SQLDelight](https://cashapp.github.io/sqldelight/2.0.0-alpha05/multiplatform_sqlite/) - Database from multiplatform. SQLDelight generates typesafe kotlin APIs from your SQL statements.
- [Coil Compose](https://coil-kt.github.io/coil/compose/) - is a library for loading images on Android, utilizing Kotlin Coroutines for efficient and responsive performance.
- [Detekt](https://github.com/detekt/detekt) - Static code analysis for Kotlin
- [moko-resources](https://github.com/icerockdev/moko-resources) - is a multiplatform library that provides access to the resources on macOS, iOS, Android the JVM and JS/Browser with the support of the default system localization.
- Testing
  - [Turbine](https://github.com/cashapp/turbine) - A small testing library for kotlinx.coroutines Flow
  - [AssertK](https://github.com/willowtreeapps/assertk) - assertk is a fluent assertion library. Since assertJ is written in Java, it cannot be used to test common code. Thus, we need the Kotlin version of assetJ which is assertk.
  - [Test runner](https://developer.android.com/jetpack/androidx/releases/test)
  - [Test rules](https://developer.android.com/jetpack/androidx/releases/test)

In the shared module, only the libraries written in Kotlin are allowed to use. For example Retrofit is not compatible for shared module. 
