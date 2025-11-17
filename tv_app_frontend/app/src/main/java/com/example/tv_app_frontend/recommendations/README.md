# Recommendations Integration

Primary approach: fetch recommendations via backend endpoint `/tv/recommendations`.

Client fallback (optional):
- Use TvProvider/Channels to publish recommended workouts to Home screen rows.
- The RecommendationPublisher BroadcastReceiver triggers building recommendations from local history and favorites when backend is unavailable.

Configuration:
- Feature flag: BuildConfig.FEATURE_RECOMMENDATIONS
- Implement actual channel creation when integrating with system recommendations.
