package com.example.bapplusplus

import android.Manifest
import android.R.attr
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_review_upload.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception


class ReviewUploadActivity : AppCompatActivity() {

    //private var viewProfile: View? = null
    var pickImageFromAlbum = 0
    var fbstr: FirebaseStorage? = null
    var fbauth: FirebaseAuth? = null
    var fbdb: FirebaseFirestore? = null
    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_upload)
        fbdb = FirebaseFirestore.getInstance()
        fbauth = FirebaseAuth.getInstance()
        fbstr = FirebaseStorage.getInstance()


        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        val oldcolor = ru_sc_edt_writenum.textColors

        ru_sc_edt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var review_str = ru_sc_edt.text.toString()
                if (review_str.length >= 200) {
                    ru_sc_edt_writenum.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.colorRed1
                        )
                    )
                    ru_sc_edt_writenum.text = review_str.length.toString() + " / 200"
                } else {
                    ru_sc_edt_writenum.setTextColor(oldcolor)
                    ru_sc_edt_writenum.text = review_str.length.toString() + " / 200"
                }
            }
        })

        ru_sc_btn_photo_one.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
            clickSelect()

        }

        ru_sc_btn_upload.setOnClickListener {
            CoroutineScope(IO).launch {
                val resbool = imageUploadTest()

                withContext(Main){
                    if(resbool){
                        Toast.makeText(applicationContext, "Upload Success", Toast.LENGTH_SHORT).show()
                        var storageRef = fbstr?.reference
                        storageRef?.child("PhotoTest01/imageTestProto2.png")?.downloadUrl?.addOnSuccessListener {
                            Glide.with(applicationContext).load(it).into(ru_sc_img_test)
                            ru_sc_img_test.visibility = View.VISIBLE
                            Toast.makeText(applicationContext, "Download Success: "+it.toString(), Toast.LENGTH_SHORT).show()
                        }?.addOnFailureListener {
                            Toast.makeText(applicationContext, "Download Fail", Toast.LENGTH_SHORT).show()
                        }




                    }else{
                        Toast.makeText(applicationContext, "Upload Fail", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }




    }

    fun clickSelect() {
        //Choose Photo
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            10 ->
                if (resultCode == RESULT_OK) {
                    photoUri = data?.data
                    val inp = contentResolver.openInputStream(data?.data!!)
                    val img = BitmapFactory.decodeStream(inp)
                    inp!!.close()
                    val bitmapdrb = BitmapDrawable(applicationContext.resources, img)

                    ru_sc_btn_photo_one.background = bitmapdrb
                    ru_sc_btn_photo_one.text = ""
                }else{

                }
        }
    }

    suspend fun imageUploadTest(): Boolean{
        var imageFileName = "imageTestProto2.png"
        var storageRef = fbstr?.reference?.child("PhotoTest01")?.child(imageFileName)

        //storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
        //    Toast.makeText(applicationContext, "Upload Success", Toast.LENGTH_SHORT).show()
        //}

        return try {
            val returner = storageRef?.putFile(photoUri!!)?.await()
            return true
        }catch (e: Exception){
            return false
        }

    }

    suspend fun imageDownloadTest(): Boolean{
        var storageRef = fbstr?.reference
        return try {
            val returner = storageRef?.child("PhotoTest01/imageTestProto2.png")?.downloadUrl?.await()
            return true
        }catch (e: Exception){
            return false
        }
    }

}