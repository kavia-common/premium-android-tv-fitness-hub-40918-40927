# TV App Frontend Environment

Do not hardcode secrets in code. Configure at build time:

- API_BASE_URL: Set via Gradle BuildConfig fields (debug/release). CI can override with:
  - -PapiBaseUrl=https://your.backend/

Google Sign-In:
- Replace "REPLACE_WITH_WEB_CLIENT_ID" in SignInViewModel with the OAuth 2.0 Web client ID.
- Ensure SHA-1 fingerprints are configured in Google Cloud Console.

Feature Flags:
- BuildConfig.FEATURE_TTS and BuildConfig.FEATURE_RECOMMENDATIONS gate optional features.
