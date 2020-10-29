import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Patterns
import android.widget.EditText
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

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
    })
}

fun EditText.validate(message: String, validator: (String) -> Boolean): Boolean {
    this.afterTextChanged {
        this.error = if (validator(it)) null else message
    }
    var errorMessage:String? = if (validator(this.text.toString())) null else message
    this.error = errorMessage
    return errorMessage == null
}

fun String.isValidEmail(): Boolean
        = this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()