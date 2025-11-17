# FitFlex TV (Offline Demo Mode)

This build runs fully offline using local/mock data bundled in assets. No backend, no sign-in setup required.

What changed:
- Replaced all remote repositories with a LocalDataSource that reads `app/src/main/assets/mock/content.json`
- Stubbed Google Sign-In: calling sign-in immediately "logs in" with a local demo token (no UI, no network)
- Leanback UI tuned for Android TV with Ocean Professional colors, card sizing, and focus scaling
- Media3 playback uses a static demo stream URL; if network is unavailable, playback may not start (catalog/UI still works offline)

Key paths:
- Local content JSON: `app/src/main/assets/mock/content.json`
- Thumbnails (bundled): `app/src/main/assets/thumbs/`
- Entry: `MainActivity` -> `HomeBrowseFragment`

Notes:
- BuildConfig API fields remain but are unused at runtime
- Admin/upload features are placeholders with no network dependency
- This mode is intended for UI review and offline demos; replace LocalDataSource with real repositories for production
