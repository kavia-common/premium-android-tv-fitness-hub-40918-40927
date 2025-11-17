# TV App Frontend Environment and Auth Configuration

This app is configured to use build-time constants via BuildConfig. Do not hardcode secrets in code.

API Base URL
- Default: BuildConfig.API_BASE_URL is set to https://api.example.com/ for both debug and release.
- Override: CI/local builds can override with a Gradle property:
  - Example (assemble): ./gradlew :app:assembleDebug -PapiBaseUrl=https://dev.api.yourdomain.com/
  - Example (install/run): ./gradlew :app:installDebug -PapiBaseUrl=https://staging.api.yourdomain.com/
- Implementation:
  - In app/build.gradle.kts, API_BASE_URL is resolved from project.findProperty("apiBaseUrl") if provided, otherwise defaults to https://api.example.com/.
  - Both buildTypes.debug and buildTypes.release set buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"").

Google Sign-In
- Web Client ID: BuildConfig.GOOGLE_OAUTH_WEB_CLIENT_ID is configured with:
  554965754520-27borongeqi1a3j06tacjolcbea2e91o.apps.googleusercontent.com
- Sign-in flow references this ID in SignInViewModel via:
  GoogleSignInOptions.Builder(...).requestIdToken(BuildConfig.GOOGLE_OAUTH_WEB_CLIENT_ID)
- Google Services setup:
  1) In Google Cloud Console, create an OAuth 2.0 Client ID (Web) for backend token exchange and configure Android OAuth client as needed.
  2) Download google-services.json for applicationId com.example.tv_app_frontend.
  3) Place at:
     - app/google-services.json (preferred), or
     - app/src/debug/google-services.json and/or app/src/release/google-services.json
  Notes:
  - The build applies the Google Services plugin only if the file exists (CI-safe).
  - Ensure SHA-1 fingerprints are configured in your Google Cloud Console.

Feature Flags
- BuildConfig.FEATURE_TTS and BuildConfig.FEATURE_RECOMMENDATIONS gate optional features.

Summary of Key BuildConfig Fields
- API_BASE_URL: https://api.example.com/ (overridable via -PapiBaseUrl)
- GOOGLE_OAUTH_WEB_CLIENT_ID: 554965754520-27borongeqi1a3j06tacjolcbea2e91o.apps.googleusercontent.com
