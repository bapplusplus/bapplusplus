package com.example.bapplusplusTemp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.bapplusplus.R
import com.example.bapplusplus.data.FBUserInfo
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.favlistnu_cell.view.*
import kotlinx.android.synthetic.main.fragment_bn1.*
import kotlinx.android.synthetic.main.fragment_bn1.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*


data class Bn1Info_Data(
    var restNo: Int,
    var restCategory: String,
    var restTitle: String,
    var restCallNum: String? = "",
    var restAddressOld: String? = "",
    var restAddressRoad: String? = "",
    var restOrderTime: String? = "",
    var restPosx: Double,
    var restPosy: Double
)

class BnFragment1 : Fragment() {

    companion object{

    }

    val fbdb = FirebaseFirestore.getInstance()
    var likesArray = arrayListOf<String>()
    var basicInfo: Bn1Info_Data? = null
    var isMyLike = false

    var RestNo = 0
    var get_posx = 0.0
    var get_posy = 0.0
    var RestTitle = ""
    var RestRoadAddress = ""
    var RestCallNum = ""
    var RestCategory = ""
    var RestRatingAvg = 1.7
    var RestReviewNum = -2


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootview = inflater.inflate(R.layout.fragment_bn1, container, false)
        // Inflate the layout for this fragment

        val bundle = arguments
        RestNo = bundle?.getInt("RestNo") ?: 0
        isMyLike = bundle?.getBoolean("isMyLike") ?: false
        RestRatingAvg = bundle?.getDouble("RestRatingAvg") ?: 1.6
        RestReviewNum = bundle?.getInt("RestReviewNum") ?: -3


        //val fbdb = FirebaseFirestore.getInstance()

        /*fbdb.collection("tmp7vBasic").document("RestBasic"+RestNo.toString()).get()
            .addOnSuccessListener { document->
                Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                RestTitle = document.getString("RestTitle").toString()
                RestRoadAddress = document.getString("RestAddressRoad").toString()
                RestCallNum = document.getString("RestCallNum").toString()
                RestCategory = document.getString("RestCategory").toString()
                //get_posx = document.getDouble("RestPosx")!!
                //get_posy = document.getDouble("RestPosy")!!
                rootview.findViewById<TextView>(R.id.bn1_title).text = RestTitle
                rootview.findViewById<TextView>(R.id.bn1_category).text = RestCategory
                rootview.findViewById<TextView>(R.id.bn1_address).text = RestRoadAddress
                if(RestCallNum.equals("")){
                    rootview.findViewById<TextView>(R.id.bn1_call).text = "Unavailable"
                }else{
                    rootview.findViewById<TextView>(R.id.bn1_call).text = RestCallNum
                }

                //progressDialog.dismiss()
                rootview.findViewById<ProgressBar>(R.id.bn1_pbar_initial).visibility = View.GONE
            }.addOnFailureListener { exception ->
                Log.d("bn1", "get failed with ", exception)
            }*/

        //new
        rootview.bn1_title.visibility = View.GONE
        rootview.bn1_category.visibility = View.GONE
        rootview.bn1_tv_reviewnum.visibility = View.GONE
        rootview.bn1_tv_rating.visibility = View.GONE
        rootview.bn1_ratingbar.visibility = View.GONE
        rootview.bn1_like_const.visibility = View.GONE
        rootview.bn1_img_repres.visibility = View.GONE
        rootview.bn1_line_sep.visibility = View.GONE
        rootview.bn1_call.visibility = View.GONE
        rootview.bn1_call_img.visibility = View.GONE
        rootview.bn1_address.visibility = View.GONE
        rootview.bn1_address_img.visibility = View.GONE
        rootview.bn1_time.visibility = View.GONE
        rootview.bn1_time_img.visibility = View.GONE

        CoroutineScope(Main).launch {
            if(getBasicData(RestNo)){

                rootview.bn1_tv_reviewnum.text = "리뷰 "+RestReviewNum.toString()+"건"
                rootview.bn1_tv_rating.text = RestRatingAvg.toString()
                rootview.bn1_ratingbar.rating = RestRatingAvg.toFloat()
                rootview.bn1_title.text = basicInfo!!.restTitle
                rootview.bn1_category.text = basicInfo!!.restCategory
                if(basicInfo!!.restCallNum.isNullOrEmpty()){
                    rootview.bn1_call.text = "번호정보 없음"
                }else{
                    rootview.bn1_call.text = basicInfo!!.restCallNum
                }

                //rootview.bn1_time.text = basicInfo!!.rest
                //rootview.bn1_delivery.text = basicInfo!!.rest
                rootview.bn1_address.text = "지번 주소: "+basicInfo!!.restAddressOld
                rootview.bn1_time.text = basicInfo!!.restAddressRoad
                rootview.bn1_tv_like.text = "좋아요  "+likesArray.size.toString()

                rootview.bn1_img_repres.setImageResource(
                    when (basicInfo!!.restCategory) {
                        "한식" -> R.drawable.korean_240_cut
                        "일식" -> R.drawable.sushi_240_cut
                        "중식" -> R.drawable.chinese_240_n
                        "분식" -> R.drawable.toppokki_240_cut
                        "카페/찻집" -> R.drawable.coffee_240_cut
                        "경양식" -> R.drawable.cutlet_240_cut
                        "김밥(도시락)" -> R.drawable.gimbap_240_cut
                        "피자" -> R.drawable.pizza_240_cut
                        "패스트푸드" -> R.drawable.burger_240_cut
                        "치킨" -> R.drawable.chicken_240_cut
                        "호프/치킨" -> R.drawable.hof_240_cut
                        "정종/대포집/소주방" -> R.drawable.glass_240_cut
                        "패밀리레스토랑" -> R.drawable.spaghetti_240_cut
                        else -> R.drawable.pizza_240_cut
                    }
                )

                if(FBUserInfo.fbauth.currentUser != null){
                    rootview.bn1_like_const.isEnabled = true
                    if(likesArray.contains(FBUserInfo.fbauth.currentUser!!.uid)){
                        isMyLike = true
                        rootview.bn1_img_like.setImageDrawable(
                            resources.getDrawable(
                                R.drawable.red_heart_one,
                                null
                            )
                        )
                    }
                }else{
                    rootview.bn1_like_const.isEnabled = false
                }

                rootview.bn1_pbar_initial.visibility = View.GONE

                rootview.bn1_title.visibility = View.VISIBLE
                rootview.bn1_category.visibility = View.VISIBLE
                rootview.bn1_tv_reviewnum.visibility = View.VISIBLE
                rootview.bn1_tv_rating.visibility = View.VISIBLE
                rootview.bn1_ratingbar.visibility = View.VISIBLE
                rootview.bn1_like_const.visibility = View.VISIBLE
                rootview.bn1_img_repres.visibility = View.VISIBLE
                rootview.bn1_line_sep.visibility = View.VISIBLE
                rootview.bn1_call.visibility = View.VISIBLE
                rootview.bn1_call_img.visibility = View.VISIBLE
                rootview.bn1_address.visibility = View.VISIBLE
                rootview.bn1_address_img.visibility = View.VISIBLE
                rootview.bn1_time.visibility = View.VISIBLE
                rootview.bn1_time_img.visibility = View.VISIBLE


            }
        }

        rootview.bn1_call.setOnClickListener {
            if(!basicInfo!!.restCallNum.isNullOrEmpty()){
                val ctt = Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+basicInfo!!.restCallNum))
                startActivity(ctt)
            }
        }



//        rootview.findViewById<TextView>(R.id.bn1_title).text = bundle?.getString("RestTitle").toString()
//        rootview.findViewById<TextView>(R.id.bn1_category).text = bundle?.getString("RestCategory").toString()
//        rootview.findViewById<TextView>(R.id.bn1_address).text = bundle?.getString("RestRoadAddress").toString()
//        if(RestCallNum.equals("")){
//            rootview.findViewById<TextView>(R.id.bn1_call).text = bundle?.getString("RestCallNum").toString()
//        }else{
//            rootview.findViewById<TextView>(R.id.bn1_call).text = bundle?.getString("RestCallNum").toString()
//        }
        //rootview.findViewById<TextView>(R.id.bn1_time).text = "Unavailable"

        rootview.bn1_like_const.setOnClickListener {
            //Toast.makeText(requireContext(), "Like View Clicked", Toast.LENGTH_SHORT).show()
            if(isMyLike){
                CoroutineScope(Main).launch {
                    rootview.bn1_like_const.isEnabled = false
                    likeCancel(RestNo, FBUserInfo.fbauth.currentUser!!.uid)
                    getLikesList(RestNo)

                    rootview.bn1_img_like.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.heart_outline,
                            null
                        )
                    )
                    rootview.bn1_tv_like.text = "좋아요  "+likesArray.size.toString()
                    isMyLike = false
                    Toast.makeText(requireContext(), "좋아요를 취소하였습니다.", Toast.LENGTH_SHORT).show()
                    rootview.bn1_like_const.isEnabled = true

                }
            }else{
                CoroutineScope(Main).launch {
                    rootview.bn1_like_const.isEnabled = false
                    //likeAdd(RestNo, FBUserInfo.fbauth.currentUser!!.uid)
                    likeAdd(RestNo, FBUserInfo.fbauth.currentUser!!.uid)

                    getLikesList(RestNo)
                    rootview.bn1_img_like.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.red_heart_one,
                            null
                        )
                    )
                    rootview.bn1_tv_like.text = "좋아요  "+likesArray.size.toString()
                    isMyLike = true
                    Toast.makeText(requireContext(), "좋아요 목록에 등록되었습니다.", Toast.LENGTH_SHORT).show()
                    rootview.bn1_like_const.isEnabled = true
                }
            }
        }

        return rootview
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    suspend fun getBasicData(restNo: Int): Boolean{
        return try{
            val doc = fbdb!!.collection("tmp7vBasic").document("RestBasic" + restNo.toString()).get().await()
            basicInfo = Bn1Info_Data(
                (doc.get("RestNo") as Long).toInt(),
                doc.get("RestCategory").toString(),
                doc.get("RestTitle").toString(),
                doc.get("RestCallNum").toString(),
                doc.get("RestAddressOld").toString(),
                doc.get(
                    "RestAddressRoad"
                ).toString(),
                doc.get("RestOrderTime").toString(),
                doc.get("RestPosx") as Double,
                doc.get("RestPosy") as Double
            )
            //likesArray = doc.data?.get("RestReviewArray") as Array<String?>
            getLikesList(restNo)
            getStringInfo(restNo)
            return true
        }catch (e: Exception){
            Log.e("BN1", "No such Doc: $restNo " + e.printStackTrace() + ", " + e.localizedMessage)
            return false
        }
    }

    suspend fun getLikesList(restNo: Int): Boolean{
        return try {
            val doc = fbdb!!.collection("tmp7vBasic").document("RestBasic" + restNo.toString()).get().await()
            likesArray.clear()
            var listGetter = doc.data!!.get("RestFavoritesArray") as List<*>

            if(listGetter.isEmpty()){
                return true
            }

            for(cc in 0..listGetter.size-1){
                var map1 = listGetter.get(cc).toString()
                if (map1 != null) {
                    likesArray.add(map1)
                }
            }

            return true
        }catch (e: Exception){
            Log.e(
                "BN1",
                "Likearray fail: $restNo " + e.printStackTrace() + ", " + e.localizedMessage
            )
            return false
        }
    }

    suspend fun getStringInfo(restNo: Int): Boolean{
        return try{
            val doc = fbdb!!.collection("tmp7vString").document("RestString" + restNo.toString()).get().await()
            RestRatingAvg = doc.get("RestRatingAvg").toString().toDouble()
            RestReviewNum = (doc.get("RestReviewNum") as Long).toInt()

            return true
        }catch (e: Exception){
            Log.e(
                "BN1",
                "getstringinfo fail: $restNo " + e.printStackTrace() + ", " + e.localizedMessage
            )
            return false
        }
    }

    suspend fun likeCancel(restNo: Int, uid: String): Boolean{
        return try {
            val toDeleteMap = hashMapOf(
                "RestNo" to basicInfo!!.restNo,
                "RestCategory" to basicInfo!!.restCategory,
                "RestTitle" to basicInfo!!.restTitle,
                "RestCallNum" to basicInfo!!.restCallNum,
                "RestAddressOld" to basicInfo!!.restAddressOld,
                "RestAddressRoad" to basicInfo!!.restAddressRoad,
                "RestOrderTime" to basicInfo!!.restOrderTime,
                "RestPosx" to basicInfo!!.restPosx,
                "RestPosy" to basicInfo!!.restPosy,
            )
            fbdb!!.collection("tmp7vBasic").document("RestBasic" + restNo.toString())
                .update("RestFavoritesArray", FieldValue.arrayRemove(uid)).await()
            fbdb!!.collection("AccountGroup").document(uid)
                .update("MyFavoritesArray", FieldValue.arrayRemove(toDeleteMap)).await()
            return true
        }catch (e: Exception){
            Log.e("BN1", "Failed to cancelLike: " + e.localizedMessage)
            return false
        }
    }

    suspend fun likeAdd(restNo: Int, uid: String): Boolean{
        return try {
            val toAddMap = hashMapOf(
                "RestNo" to basicInfo!!.restNo,
                "RestCategory" to basicInfo!!.restCategory,
                "RestTitle" to basicInfo!!.restTitle,
                "RestCallNum" to basicInfo!!.restCallNum,
                "RestAddressOld" to basicInfo!!.restAddressOld,
                "RestAddressRoad" to basicInfo!!.restAddressRoad,
                "RestOrderTime" to basicInfo!!.restOrderTime,
                "RestPosx" to basicInfo!!.restPosx,
                "RestPosy" to basicInfo!!.restPosy,
            )
            fbdb!!.collection("tmp7vBasic").document("RestBasic" + restNo.toString())
                .update("RestFavoritesArray", FieldValue.arrayUnion(uid)).await()
            fbdb!!.collection("AccountGroup").document(uid)
                .update("MyFavoritesArray", FieldValue.arrayUnion(toAddMap)).await()
            return true
        }catch (e: Exception){
            Log.e("BN1", "Failed to addLike: " + e.localizedMessage)
            return false
        }
    }

    fun refreshFragment() {
        var ft: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        ft.detach(this).attach(this).commit()
    }

}