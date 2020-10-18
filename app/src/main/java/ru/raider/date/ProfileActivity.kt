package ru.raider.date
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.FileNotFoundException
import java.io.InputStream


class ProfileActivity : AppCompatActivity() {

    private var imageView: ImageView? = null
    val Pick_image = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    fun changeProfilePicture(view: View) {


        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        //Тип получаемых объектов - image:
        photoPickerIntent.type = "image/*"
        //Запускаем переход с ожиданием обратного результата в виде информации об изображении:
        startActivityForResult(photoPickerIntent, Pick_image)


    }

   override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            Pick_image -> if (resultCode == Activity.RESULT_OK) {
                try {

                    //Получаем URI изображения, преобразуем его в Bitmap
                    //объект и отображаем в элементе ImageView нашего интерфейса:
                    val imageUri: Uri? = imageReturnedIntent?.data
                    val imageStream: InputStream? = imageUri?.let {
                        contentResolver.openInputStream(
                            it
                        )
                    }
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    profile_pic?.setImageBitmap(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }
}

