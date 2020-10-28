import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun String.md5(): String {
    return hashString(this, "MD5")
}

fun String.sha256(): String {
    return hashString(this, "SHA-256")
}

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
            .getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
}

fun convertImageUrl(url:String?): String {
    val salt = "damitrotakabratalimvvv"
    val encodedSource = Base64.encodeToString(("$url").toByteArray(), Base64.URL_SAFE)
    val str = "/auto/720/1080/sm/true/$encodedSource"
    val sha256Hmac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec("xersukkkhhrar".toByteArray(), "HmacSHA256")
    sha256Hmac.init(secretKey)
    val signedUrl = Base64.encodeToString(sha256Hmac.doFinal((salt + str).toByteArray()), Base64.URL_SAFE)
    return "https://raiderimgproxy.herokuapp.com/$signedUrl$str"
}