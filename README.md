# Test Result — native Android

A single-activity Kotlin application that opens directly onto the supplied test result design. The UI is hosted in Jetpack Compose with Material 3. Every visible surface, icon and letter is drawn with native Android graphics; no web content, screenshot background, external UI framework, XML layout, navigation flow or backend is included.

## Open and build

Open this directory in Android Studio, install Android SDK Platform 35 and Build Tools 35.0.0, and allow Gradle sync to complete. Use JDK 17 or 21. Android Studio supplies the machine-specific SDK location in `local.properties`.

```sh
./gradlew assembleDebug
```

Install `app/build/outputs/apk/debug/app-debug.apk` on Android 9 (API 28) or newer. The app needs no network access or permissions. No test suites are included.

## Drawing

`ResultArtwork.kt` defines the design in the supplied image's 1122 × 1402 coordinate space: custom score-panel contours, separate accuracy surface, concentric percentage surfaces, panel edges, directional shadows, gradients, custom icons and bundled fonts. `MainActivity.kt` hosts the artwork in a Compose Canvas. It renders native paths into an offscreen bitmap at the current viewport resolution once per size change, so blurred path shadows work consistently without repeated per-frame work. The bitmap is generated from code, not from the supplied image.

The entire composition scales uniformly to fit the available screen and centers on taller or wider viewports; its geometry is never stretched. System bars are hidden to match the supplied artwork and can be revealed by a system gesture. A semantic description exposes all result values to accessibility services.

The logo and icons are hand-traced from the supplied raster image. Roboto and Noto Sans Devanagari are bundled under the licenses in `licenses/`. The original font files and vector assets were not provided, so their identity with the source cannot be guaranteed. Visual fidelity must be judged against the supplied image rather than inferred from a successful build.

## Validation in this workspace

- The included Gradle wrapper completed `assembleDebug` successfully using JDK 21, SDK 35 and Build Tools 35.0.0. A fresh compilation and a repeat wrapper build passed.
- No test suites or UI tests were created or run.
- Native screenshot validation is **not completed**. An API 35 software emulator was installed and booted without KVM. Android's system process was terminated by its watchdog, then crashed again after losing its network stack. APK installation could not complete reliably, so application startup and rendered appearance remain unverified.
- There is no claim of a pixel-perfect match. The supplied raster was used to construct the native geometry; original fonts and SVGs were unavailable. Open the project on a working Android emulator or device to complete native visual comparison and refinement.

## Downloads

- [APK-only ZIP](downloads/TestResult-APK-only.zip) — contains only `TestResult.apk`.
- [Android Studio project ZIP](downloads/TestResult-Android.zip) — complete source project.
- [APK](downloads/TestResult.apk) — direct debug APK download.

These downloads are the compiled delivery described above; native visual validation remains incomplete.
