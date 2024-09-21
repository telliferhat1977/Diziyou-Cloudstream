version = 1

cloudstream {
    authors     = listOf("yusiqo", "keyiflerolsun")
    language    = "tr"
    description = "Üşenmeden Dextv Den Canlı olarak film çeken eklenti, lordun elinden"

    /**
     * Status int as the following:
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta only
    **/
    status  = 1 // will be 3 if unspecified
    tvTypes = listOf("Movie")
    iconUrl = "https://play-lh.googleusercontent.com/Ua0EjcLzLHg3Z1uWJU5VkDt6tnoWdXXTJJBXX3T0uRCTJCs74B1X2Y0jO5TFX0XGEjjD=s75-rw"
}