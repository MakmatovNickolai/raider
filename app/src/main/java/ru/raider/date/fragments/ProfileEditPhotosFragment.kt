package ru.raider.date.fragments

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.amplifyframework.core.Amplify
import kotlinx.android.synthetic.main.fragment_profile_edit_photos.*
import ru.raider.date.App
import ru.raider.date.R
import ru.raider.date.adapters.EditPhotosRecyclerAdapter
import sha256
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class ProfileEditPhotosFragment : Fragment() {
    private lateinit var adapter: EditPhotosRecyclerAdapter
    private var photoPosition = 0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_edit_photos, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState == null) {

            initRecyclerView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    private fun initRecyclerView() {

        adapter = EditPhotosRecyclerAdapter(callback = object : EditPhotosRecyclerAdapter.Callback {
            override fun onItemClicked(item: String) {
                itemClicked(item)
            }
        })
        for (i in 0..8) {
            if (App.user.pictureUrls != null && App.user.pictureUrls!!.size > i) {
                adapter.add(App.user.pictureUrls!![i])
            } else {
                adapter.add("")
            }
        }
        idProfileEditPhotosRecycler.adapter = adapter
    }

    private fun itemClicked(item: String) {
        photoPosition = adapter.getPosition(item)
        if (item.isNullOrEmpty()) {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1)
        } else {
            val listItems = arrayOf("Загрузить новое фото с устройства", "Сделать главным", "Удалить")
            val mBuilder = AlertDialog.Builder(requireActivity())
            mBuilder.setTitle("Выберите действие")
                    .setItems(listItems) { dialog, i ->
                        when (i) {
                            0 -> {
                                // загрузить новое
                                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                                photoPickerIntent.type = "image/*"
                                startActivityForResult(photoPickerIntent, 1)
                            }
                            1 -> {
                                // сделать главным

                                App.user.main_picture_url = item
                                val index = App.user.pictureUrls?.indexOf(item)
                                Log.i("DEV", index.toString())
                                Log.i("DEV", App.user.pictureUrls.toString())
                                if (index != 0) {
                                    Collections.swap(App.user.pictureUrls!!, 0, index!!)
                                }
                                Log.i("DEV", App.user.pictureUrls.toString())
                                adapter.swap(0, adapter.getPosition(item))
                            }
                            2 -> {
                                // удалить
                                val picture = App.user.pictureUrls?.find { it -> it == item }
                                App.user.pictureUrls?.remove(picture)
                                adapter.removeAt(photoPosition)
                                adapter.add("")
                            }
                        }
                        dialog.dismiss()
                    }
            mBuilder.setNeutralButton("Отмена") { dialog, _ ->
                dialog.cancel()
            }
            val mDialog = mBuilder.create()
            mDialog.show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        if (resultCode == Activity.RESULT_OK) {
            try {
                val imageUri: Uri = imageReturnedIntent?.data!!
                val path = getPath(imageUri)
                val selectedImageFile = File(path)

                var uniqueId = (UUID.randomUUID().toString() + App.user.email).sha256()
                idApplyPhotoEditChanges.isClickable = false

                Amplify.Storage.uploadFile(
                        "$uniqueId.jpg",
                        selectedImageFile,
                        { result ->
                            Log.i(
                                    "MyAmplifyApp",
                                    "Successfully uploaded: " + result.key
                            )
                            val uploadedPicture = "https://raiders3225357-dev.s3.eu-central-1.amazonaws.com/public/$uniqueId.jpg"
                            if (photoPosition == 0) {
                                App.user.main_picture_url = uploadedPicture
                            }
                            val item = adapter.getPicture(photoPosition)
                            if (!item.isNullOrEmpty()) {
                                App.user.pictureUrls?.remove(item)
                            }
                            adapter.setAt(photoPosition, uploadedPicture)
                            App.user.pictureUrls?.add(uploadedPicture)
                            idApplyPhotoEditChanges.isClickable = true
                        },
                        { error ->
                            Log.e("MyAmplifyApp", "Upload failed", error)
                            idApplyPhotoEditChanges.isClickable = true
                        }
                )
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
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
        fun newInstance(): ProfileEditPhotosFragment {
            val fragment = ProfileEditPhotosFragment()
            val index = App.user.pictureUrls?.indexOf(App.user.main_picture_url)
            Log.i("DEV", index.toString())
            Log.i("DEV", App.user.toString())
            if (index != 0) {
                Collections.swap(App.user.pictureUrls, 0, index!!)
            }
            Log.i("DEV", App.user.toString())
            val bundle = Bundle().apply {
                putStringArray(PHOTO_URLS, App.user.pictureUrls?.toTypedArray())
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}