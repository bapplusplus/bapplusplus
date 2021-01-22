package com.example.bapplusplus

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bapplusplus.data.FBUserInfo
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_review_upload.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ReviewUploadActivity : AppCompatActivity() {

    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    //private var viewProfile: View? = null
    var pickImageFromAlbum = 0
    var fbstr: FirebaseStorage? = null
    var fbauth: FirebaseAuth? = null
    var fbdb: FirebaseFirestore? = null
    var photoUri: Uri? = null
    var restNo = 0
    var restTitle = ""
    var restCategory = ""
    var myReviewCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_upload)

        val itt = intent
        restNo = itt.getIntExtra("RestNo", 7)
        restTitle = itt.getStringExtra("RestTitle").toString()
        restCategory = itt.getStringExtra("RestCategory").toString()
        myReviewCount = itt.getIntExtra("MyReviewCount", 0).inc()

        fbdb = FirebaseFirestore.getInstance()
        fbauth = FirebaseAuth.getInstance()
        fbstr = FirebaseStorage.getInstance()
        ru_sc_btn_upload.isEnabled = false

        ru_toolbar_title.text = restTitle+" 리뷰 작성"
        val rutoolbar = ru_toolbar as Toolbar
        setSupportActionBar(ru_toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        val oldcolor = ru_sc_edt_writenum.textColors

        ru_sc_edt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //var review_str = ru_sc_edt.text.toString()
                if (ru_sc_edt.text.toString().isEmpty()) {
                    ru_sc_btn_upload.isEnabled = false
                    ru_sc_edt_writenum.setTextColor(oldcolor)
                    ru_sc_edt_writenum.text = ru_sc_edt.text.toString().length.toString() + " / 200"
                } else {
                    ru_sc_btn_upload.isEnabled = true
                    if (ru_sc_edt.text.toString().length >= 200) {
                        ru_sc_edt_writenum.setTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.colorRed1
                            )
                        )
                        ru_sc_edt_writenum.text =
                            ru_sc_edt.text.toString().length.toString() + " / 200"
                    } else {
                        ru_sc_edt_writenum.setTextColor(oldcolor)
                        ru_sc_edt_writenum.text =
                            ru_sc_edt.text.toString().length.toString() + " / 200"
                    }
                }
            }
        })

        ru_sc_btn_photo_one.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // 권한 승인이 안되어 있는 경우
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {     // true: 거부한 적이 있음
                    // 이전에 이미 권한 거부가 있었을 경우 설명 (Anko 라이브러리를 쓰면 편하다)
//                    alert("사진을 표시하려면 외부 저장소 권한이 필요합니다!", "권한이 필요한 이유") {
//                        yesButton {
//                            ActivityCompat.requestPermissions(   // 권한 요청
//                                this@MainActivity,
//                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                                REQUEST_READ_EXTERNAL_STORAGE) // 권한 요청에 대한 분기 처리를 위해
//                            // 만든 적당한 정수 값임
//                        }
//                        noButton {  }
//                    }.show()
                    Toast.makeText(this, "사진을 올리기 위해서 권한 승인이 필요합니다.", Toast.LENGTH_SHORT).show()

                } else {

                    // 이전에 권한 거부가 없었을 경우 권한 요청
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_READ_EXTERNAL_STORAGE)
                }

            } else {
                // 권한이 이미 승인되어 있는 상태
                clickSelect()
            }

            //clickSelect()

        }

        ru_sc_ratingbar_one.onRatingBarChangeListener = OnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (ru_sc_ratingbar_one.rating < 1.0F) {
                ru_sc_ratingbar_one.rating = 1F
            }

            ru_sc_tv_one.text = "내 별점: "+ru_sc_ratingbar_one.rating.toString()
        }

        /*ru_sc_btn_upload.setOnClickListener {
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
        }*/

        ru_sc_btn_upload.setOnClickListener{
            val contentStr = ru_sc_edt.text.toString()
            val menuStr = ru_sc_edt_menu.text.toString()
            val starRating = ru_sc_ratingbar_one.rating.toDouble()
            val uidStr = FBUserInfo.fbuser?.uid
            val dpnameStr = FBUserInfo.fbuser?.displayName
            //val dpnameStr = FBUserInfo.fbuser?.displayName?.get(0)?.plus("***")
            val reviewcodeStr = "rva_"+restNo.toString()+"_"+uidStr+"_"+myReviewCount.toString()
            val photocodeStr = reviewcodeStr+"_pOne.png"
            println("btnupload: " + restNo.toString() + restTitle + " / myrvcount=" + myReviewCount.toString() + " / " + reviewcodeStr)

            if(contentStr.isNullOrEmpty()){
                Toast.makeText(this, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }else{
                //Toast.makeText(this, restNo.toString() + " / " + restTitle + " / " + restCategory, Toast.LENGTH_SHORT).show()
                var uploadDialog = AlertDialog.Builder(this)
                uploadDialog.setTitle("리뷰 등록")
                if(menuStr.isNullOrEmpty()){
                    uploadDialog.setMessage("메뉴가 작성되지 않았습니다. 리뷰를 등록하시겠습니까?")
                }else{
                    uploadDialog.setMessage("리뷰를 등록하시겠습니까?")
                }

                uploadDialog.setPositiveButton(
                    "확인",
                    DialogInterface.OnClickListener { dialogInterface, which ->
                        val timebuttonpressed = Timestamp.now()

                        val reviewdata = hashMapOf(
                            "RestNo" to restNo,
                            "RestTitle" to restTitle,
                            "RestCategory" to restCategory,
                            "ReviewUid" to uidStr,//
                            "ReviewContent" to contentStr,//
                            "ReviewMenu" to menuStr,//
                            "ReviewRating" to starRating,//
                            "ReviewDisplayName" to dpnameStr,
                            "ReviewTime" to timebuttonpressed,
                            if (photoUri == null) {
                                "ReviewPhotoPathOne" to ""
                            } else {
                                "ReviewPhotoPathOne" to "PhotoTest01" + "/" + photocodeStr
                            },
                            "ReviewCode" to reviewcodeStr
                        )

                        /*CoroutineScope(Main).launch {
                        val reviewUploadResult = reviewUploadTest(reviewdata)
                        if(reviewUploadResult){
                            Toast.makeText(applicationContext, "Review Upload Success!", Toast.LENGTH_SHORT).show()
                            if(photoUri != null){
                                if(imageUploadTest(photocodeStr)){
                                    Toast.makeText(applicationContext, "Picture Upload Success!", Toast.LENGTH_SHORT).show()
                                }else{
                                    //image upload fail
                                    Toast.makeText(applicationContext, "Picture Upload Fail...", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else{
                            //review upload fail
                            Toast.makeText(applicationContext, "Review Upload Fail...", Toast.LENGTH_SHORT).show()
                        }
                    }*/

                        CoroutineScope(Main).launch {
                            ru_sc_btn_upload.setText("업로드 중...")
                            ru_sc_btn_upload.isEnabled = false

                            if (newUploadOne(reviewdata)) {
                                //Toast.makeText(applicationContext, "Review Upload Success!", Toast.LENGTH_SHORT).show()
                                if (photoUri != null) {
                                    if (imageUploadTest(photocodeStr)) {
                                        //Toast.makeText(applicationContext, "Picture Upload Success!", Toast.LENGTH_SHORT).show()

                                    } else {
                                        //image upload fail
                                        //Toast.makeText(applicationContext, "Picture Upload Fail...", Toast.LENGTH_SHORT).show()
                                    }
                                    ru_sc_btn_upload.text = "업로드 성공!"
                                    Toast.makeText(this@ReviewUploadActivity, "리뷰가 업로드 되었습니다.", Toast.LENGTH_SHORT).show()
                                    delay(600)
                                    setResult(15)
                                    finish()
                                }else{
                                    ru_sc_btn_upload.text = "업로드 성공!"
                                    Toast.makeText(this@ReviewUploadActivity, "리뷰가 업로드 되었습니다.", Toast.LENGTH_SHORT).show()
                                    delay(600)
                                    setResult(15)
                                    finish()
                                }
                            } else {
                                //Toast.makeText( applicationContext, "Review Upload Fail...", Toast.LENGTH_SHORT ).show()
                            }
                        }
                    })
                uploadDialog.setNegativeButton("취소", null)
                uploadDialog.create()
                uploadDialog.show()

            }
        }

        ru_sc_btn_photodelete.setOnClickListener {
            ru_sc_btn_photo_one.background = getDrawable(R.drawable.outline_grey_rect)
            ru_sc_btn_photo_one.text = "사진 선택"
            photoUri = null
            ru_sc_btn_photodelete.visibility = View.GONE
        }

        /*rusc_btn_test.setOnClickListener {
            val itemer2 = hashMapOf(
                "RestNo" to 96,
                "RestTitle" to "RestTitle",
                "RestPosx" to 37.63,
                "RestFavoritesArray" to arrayListOf<String>()
            )

            fbdb!!.collection("tmp7vBasic").document("TESSE")
                .set(itemer2)
                .addOnSuccessListener {
                    Log.d("TAG", "DocumentSnapshot successfully written!")
                    //count_one++
                }
                .addOnFailureListener {
                        e -> Log.w("TAG", "Error writing document", e) }
        }*/






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
                    ru_sc_btn_photodelete.visibility = View.VISIBLE
                } else {

                }
        }
    }

    suspend fun imageUploadTest(photocode: String): Boolean{
        var imageFileName = photocode
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

    suspend fun reviewUploadTest(reviewdata: HashMap<*, *>?): Boolean{
        return try{
            fbdb!!.collection("tmp7vString").document("RestString" + restNo.toString())
                .update("RestReviewArray", FieldValue.arrayUnion(reviewdata)).await()

            fbdb!!.collection("AccountGroup").document(FBUserInfo.fbuser!!.uid)
                .update("MyReviewArray", FieldValue.arrayUnion(reviewdata)).await()
            return true
        }catch (e: Exception){
            return false
        }
    }

    suspend fun setRatingReviewTest(){
        return try {
            val doc = fbdb!!.collection("tmp7vList").document("bf6c7ba0-537c-11eb-85d6-f96783d0ff1e").get().await()
            var list1 = doc.data!!.get("tmp7vArray") as List<*>
            var found = false
            var indexcc = 0
            var mapfound : HashMap<*, *>?  = null

            while (!found){
                var map1 = list1.get(indexcc) as HashMap<*, *>
                if(map1.get("restNo") == restNo){
                    mapfound = map1
                    found = true
                }else{
                    indexcc++
                }
            }


        }catch (e: Exception){

        }
    }

    suspend fun newUploadOne(reviewdata: HashMap<*, *>?): Boolean{
        return try {
            Log.e("ReviewUp", "Stage: 1")
            val doc = fbdb!!.collection("tmp7vString").document("RestString" + restNo.toString()).get().await()
            Log.e("ReviewUp", "Stage: 1.1" + doc.data)
            var oldReviewNum = doc.data!!.get("RestReviewNum") as Long
            oldReviewNum.toInt()
            Log.e("ReviewUp", "Stage: 1.2 / " + oldReviewNum.toString())
            var oldRatingAvg = doc.getDouble("RestRatingAvg")
            Log.e("ReviewUp", "Stage: 1.3 / " + oldRatingAvg.toString())

            Log.e("ReviewUp", "Stage: 2")
            //arrayRemove 7vlist
            val toDeleteMap = hashMapOf(
                "RestNo" to restNo,
                "RestTitle" to restTitle,
                "RestCategory" to restCategory,
                "RestRatingAvg" to oldRatingAvg,
                "RestReviewNum" to oldReviewNum
            )
            fbdb!!.collection("tmp7vList").document("bf6c7ba0-537c-11eb-85d6-f96783d0ff1e")
                .update("tmp7vArray", FieldValue.arrayRemove(toDeleteMap)).await()
            Log.e("ReviewUp", "Stage: 3")

            var oldArray = doc.data!!.get("RestReviewArray") as List<*>
            var ratingToSum = 0.0
            for(cc in 0..oldArray.size-1){
                var data = oldArray.get(cc) as HashMap<*, *>
                ratingToSum += data.get("ReviewRating") as Double
            }
            Log.d("ReviewUp", "Stage: 4.5, ratingtosum: " + ratingToSum.toString())
            val testval1 = reviewdata?.get("ReviewRating") as Double
            ratingToSum += testval1
            Log.d("ReviewUp", "Stage: 4.6, ratingtosum: " + testval1.toString())
            Log.d("ReviewUp", "Stage: 4.6, ratingtosum: " + ratingToSum.toString())
            ratingToSum = ratingToSum/(oldArray.size+1)
            Log.d("ReviewUp", "Stage: 4.7, ratingtosum: " + ratingToSum.toString())
            //ratingToSum = ((ratingToSum*100).roundToInt()/100).toDouble()
            ratingToSum = Math.round(ratingToSum * 100)/100.0
            Log.d("ReviewUp", "Stage: 4.8, ratingtosum: " + ratingToSum.toString())
            var newReviewNum = oldReviewNum+1
            Log.e(
                "ReviewUp",
                "Stage: 5, ratingtosum: " + ratingToSum.toString() + " / newreviewnum: " + newReviewNum.toString()
            )

            val doc2 = fbdb!!.collection("tmp7vString").document("RestString" + restNo.toString())
            doc2.update("RestReviewArray", FieldValue.arrayUnion(reviewdata)).await()
            doc2.update("RestRatingAvg", ratingToSum).await()
            doc2.update("RestReviewNum", newReviewNum).await()
            Log.e("ReviewUp", "Stage: 7")

            val toAddMap = hashMapOf(
                "RestNo" to restNo,
                "RestTitle" to restTitle,
                "RestCategory" to restCategory,
                "RestRatingAvg" to ratingToSum,
                "RestReviewNum" to newReviewNum
            )

            fbdb!!.collection("tmp7vList").document("bf6c7ba0-537c-11eb-85d6-f96783d0ff1e")
                .update("tmp7vArray", FieldValue.arrayUnion(toAddMap)).await()
            Log.e("ReviewUp", "Stage: 9")
            fbdb!!.collection("AccountGroup").document(FBUserInfo.fbuser!!.uid)
                .update("MyReviewArray", FieldValue.arrayUnion(reviewdata)).await()


            return true
        }catch (e: Exception){
            Log.e("ReviewUp", "Error: " + e.printStackTrace())
            return false
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        //super.onBackPressed()

        var calcelDialog = AlertDialog.Builder(this)
        calcelDialog.setTitle("리뷰 취소").setMessage("리뷰 등록을 취소하시겠습니까?")
        calcelDialog.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, which ->
            CoroutineScope(Dispatchers.Main).launch {
                //Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                //onBackPressed()
                setResult(14)
                finish()
            }
        })
        calcelDialog.setNegativeButton("취소", null)
        calcelDialog.create()
        calcelDialog.show()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                onBackPressed()
                //finish()
                //return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}