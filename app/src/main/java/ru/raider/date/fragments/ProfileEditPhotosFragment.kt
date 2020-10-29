package ru.raider.date.fragments

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_profile_edit_photos.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.R
import ru.raider.date.activities.LoginActivity
import ru.raider.date.adapter_models.ProfileImageItem
import ru.raider.date.network_models.SimpleResponse
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class ProfileEditPhotosFragment : Fragment() {
    private val pickImage = 1
    var photoPosition = 0
    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_edit_photos, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val images = arguments?.getStringArray(PHOTO_URLS)?.toList()!!
        for (i in 0..8) {
            if (images.size > i) {
                adapter.add(ProfileImageItem(images[i], null))
            } else {
                adapter.add(ProfileImageItem("", null))
            }
        }
        adapter.setOnItemClickListener { item, view ->
            if (item is ProfileImageItem) {
                if (item.url.isNullOrEmpty()) {
                    photoPosition = adapter.getAdapterPosition(item)
                    val photoPickerIntent = Intent(Intent.ACTION_PICK)
                    photoPickerIntent.type = "image/*"
                    startActivityForResult(photoPickerIntent, pickImage)
                } else {
                    // загрузить новое или установить главным
                    // TODO: 29.10.2020  
                }
            }
        }
        idProfileEditPhotosRecycler.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // убрать иконку меню из верхнего бара
        // TODO: 29.10.2020  
        return
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
                        activity?.contentResolver?.openInputStream(
                            it
                        )
                    }
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    adapter.remove(adapter.getItem(photoPosition) as ProfileImageItem)
                    adapter.add(photoPosition, ProfileImageItem("", selectedImage))


                    val options = StorageUploadFileOptions.builder()
                        .accessLevel(StorageAccessLevel.PROTECTED)
                        .build()

                    val exampleFile = File(activity?.applicationContext?.filesDir, "ExampleKey")
/*
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

 */
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getPath(imageUri: Uri): String {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? =
            activity?.contentResolver?.query(imageUri, proj, null, null, null)
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

    companion object {
        private const val PHOTO_URLS = "PHOTO_URLS"
        fun newInstance(images: List<String>): ProfileEditPhotosFragment {
            val fragment = ProfileEditPhotosFragment()
            val bundle = Bundle().apply {
                putStringArray(PHOTO_URLS, images.toTypedArray())
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}