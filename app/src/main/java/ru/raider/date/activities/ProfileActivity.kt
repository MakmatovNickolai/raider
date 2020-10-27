package ru.raider.date.activities
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageUploadFileOptions
import kotlinx.android.synthetic.main.activity_profile.*
import ru.raider.date.R
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream


class ProfileActivity : AppCompatActivity() {
    val pickImage = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    fun changeProfilePicture(view: View) {

        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, pickImage)

    }

   override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            pickImage -> if (resultCode == Activity.RESULT_OK) {
                try {
                    val imageUri: Uri? = imageReturnedIntent?.data
                    val path = getPath(imageUri!!)
                    val selectedImageFile = File(path)

                    val imageStream: InputStream? = imageUri?.let {
                        contentResolver.openInputStream(
                            it
                        )
                    }
                    val selectedImage = BitmapFactory.decodeStream(imageStream)

                    profile_pic?.setImageBitmap(selectedImage)

                    val options = StorageUploadFileOptions.builder()
                        .accessLevel(StorageAccessLevel.PROTECTED)
                        .build()

                    val exampleFile = File(applicationContext.filesDir, "ExampleKey")

                    exampleFile.writeText("Example file contents")
                    Amplify.Storage.uploadFile(
                        "ExampleKey.jpg",
                        selectedImageFile,
                        { result ->
                            Log.i(
                                "MyAmplifyApp",
                                "Successfully uploaded: " + result.key
                            )
                        },
                        { error -> Log.e("MyAmplifyApp", "Upload failed", error) }
                    )
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
   }

    private fun getPath(imageUri:Uri): String {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? =
                contentResolver.query(imageUri, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(column_index)
            }
            cursor.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }
}

