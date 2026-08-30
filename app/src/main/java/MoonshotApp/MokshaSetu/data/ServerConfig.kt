package MoonshotApp.MokshaSetu.data

object ServerConfig {
    // The production alias for the deployed registry. Use the plain "<project>.vercel.app"
    // URL, NOT the "<project>-<team-slug>.vercel.app" deployment URL that Vercel returns on
    // deploy: that one is SSO-protected and answers requests with 302 -> vercel.com/sso-api,
    // which the JSON HTTP client cannot follow. Only useful for testing the offline compose
    // fallback: swap to http://10.0.2.2:8000 on the emulator (physical device: laptop LAN IP).
    const val BASE_URL = "https://mokshasetu-registry.vercel.app"
}