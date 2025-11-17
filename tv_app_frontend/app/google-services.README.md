# Google Services Configuration

This app supports Google Sign-In for TV. To enable it:

1. In Google Cloud Console, create an OAuth 2.0 Client ID (Web) and Android OAuth client as required.
2. Download the `google-services.json` file for this applicationId: `com.example.tv_app_frontend`.
3. Place the file at one of:
   - `app/google-services.json` (preferred)
   - `app/src/debug/google-services.json` and/or `app/src/release/google-services.json`

Notes:
- The Gradle build is configured to apply the Google Services plugin only when the file is present, so CI builds won't fail without it.
- Replace the placeholder Web Client ID in `SignInViewModel` (REPLACE_WITH_WEB_CLIENT_ID).
