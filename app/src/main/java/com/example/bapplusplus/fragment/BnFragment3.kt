package com.example.bapplusplusTemp.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bapplusplus.Bn3ReviewsAdapter
import com.example.bapplusplus.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_bn3.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class BN3Info_Rating(
    var stars: Double,
    var uid: String
)

data class BN3Info_Review(
    var content: String,
    var date: String,
    var uid: String
){
    fun finder():Int{
        return date.toInt()
    }

    fun findUidNums(uid: String): Int{
        var to_return = 0
        if(this.uid == uid){
            to_return = 1
        }
        println("finduidnums: "+ to_return)
        return to_return
    }
}

class BnFragment3 : Fragment() {
    var RestNo = 0
    var RestTitle = ""
    var RatingArray: ArrayList<BN3Info_Rating> = arrayListOf()
    var RatingArraySe: ArrayList<String> = arrayListOf()
    var ReviewArray: ArrayList<BN3Info_Review> = arrayListOf()
    var ReviewArrayToUpdate: ArrayList<BN3Info_Review> = arrayListOf()
    var get_uid: String =""
    var get_documentId = ""
    var my_review_num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_bn3, container, false)
        rootView.bn3_btn_myfav.isEnabled = false
        rootView.bn3_btn_writerv.isEnabled = false
        val bundle = arguments
        val fbdb = FirebaseFirestore.getInstance()
        val fbauth = FirebaseAuth.getInstance()
        val fbuser = fbauth.currentUser
        val user_uid = fbuser?.uid!!
        get_uid = fbauth.currentUser?.uid ?: ""

        RestNo = bundle?.getInt("RestNo") ?: 0

        val adapter = Bn3ReviewsAdapter(requireContext(), ReviewArray, user_uid)

        fbdb.collection("tmp5vStrings")
            .whereEqualTo("RestNo", RestNo)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents)
                    if (document != null) {
                        println("document id is: "+document.id)
                        get_documentId = document.id
                        my_review_num = 0
                        RestTitle = document.get("RestTitle").toString()
                        var list1 = document.getData()!!.get("RestRating") as List<*>
                        var list2 = document.getData()!!.get("RestReview") as List<*>
                        for (kk in 0..list1.size-1){
                            var map1 = list1.get(kk) as HashMap<*, *>
                            var rat1 = BN3Info_Rating(map1.get("stars").toString().toDouble(), map1.get("uid").toString())
                            RatingArray.add(rat1)
                        }
                        for (kk in 0..list2.size-1){
                            var map1 = list2.get(kk) as HashMap<*, *>
                            var rat1 = BN3Info_Review(map1.get("content").toString(), map1.get("date").toString(), map1.get("uid").toString())
                            my_review_num = my_review_num + rat1.findUidNums(get_uid)
                            ReviewArray.add(rat1)
                        }
                        Log.d("TAG", "BN3 DocumentSnapshot data: ${document.data}")

                        for (kk in 0..RatingArray.size-1){
                            RatingArraySe.add(RatingArray.get(kk).uid)
                            println("testing rating S"+RatingArray.get(kk).stars+" / U"+RatingArray.get(kk).uid)
                        }
                        for (kk in 0..RatingArraySe.size-1){
                            println("Rating Array Se test: U: "+RatingArraySe.get(kk))
                        }
                        for (kk in 0..ReviewArray.size-1){
                            println("testing review C"+ReviewArray.get(kk).content+" / D"+ReviewArray.get(kk).date+" / U"+ReviewArray.get(kk).uid)
                        }

                        if(my_review_num > 0){
                            rootView.bn3_txt_rv.text = "내가 작성한 리뷰: "+my_review_num+"개"
                        }else{
                            rootView.bn3_txt_rv.text = "첫 리뷰를 작성해 주세요"
                        }
                    } else {
                        Log.d("TAG", "No such document - Fragment1")
                    }
                adapter.notifyDataSetChanged()


            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }



        /*if(get_uid.equals("")){
            rootView.bn3_btn_myfav.text = "로그인 되지 않았습니다"
            rootView.bn3_txt_rt.text = "로그인 후 별점을 줄 수 있습니다"
            rootView.bn3_txt_rv.text = "로그인 후 리뷰를 작성할 수 있습니다"
        }else{
            //RatingArray.indexOf(BN3Info_Rating(0.0 , get_uid))
            var uid_index = RatingArraySe.indexOf(get_uid)
            if(uid_index != -1){
                rootView.bn3_txt_rt.text = "내가 준 평점:"
                rootView.bn3_ratingbar.rating = RatingArray.get(uid_index).stars.toFloat()
            }else{
                rootView.bn3_txt_rt.text = "평점을 매겨주세요"
            }
        }*/




        Handler().postDelayed({
            if(get_uid.equals("")){
                rootView.bn3_btn_myfav.text = "로그인 되지 않았습니다"
                rootView.bn3_txt_rt.text = "로그인 후 별점을 줄 수 있습니다"
                rootView.bn3_txt_rv.text = "로그인 후 리뷰를 작성할 수 있습니다"
            }else{
                rootView.bn3_btn_myfav.isEnabled = true
                rootView.bn3_btn_writerv.isEnabled = true
                //RatingArray.indexOf(BN3Info_Rating(0.0 , get_uid))
                var uid_index = RatingArraySe.indexOf(get_uid)
                if(uid_index != -1){
                    println("RatingArray found! "+uid_index)
                    rootView.bn3_txt_rt.text = "내가 준 별점"
                    rootView.bn3_ratingbar.rating = RatingArray.get(uid_index).stars.toFloat()
                }else{
                    println("RatingArray not found "+uid_index)
                    rootView.bn3_txt_rt.text = "별점을 매겨주세요"
                }
            }

            println("ReviewArray size "+ReviewArray.size)
            if(ReviewArray.size == 0){
                println("ReviewArray size is zero "+ReviewArray.size)
                rootView.bn3_rcv.visibility = View.INVISIBLE
                rootView.bn3_tv_norvsyet.visibility = View.VISIBLE
            }
            else{
                println("ReviewArray size is not zero "+ReviewArray.size)
                //rootView.bn3_rcv.visibility = View.VISIBLE
                rootView.bn3_tv_norvsyet.visibility = View.GONE
            }
        }, 600)

        rootView.bn3_const.setOnClickListener {
            hideKeyboard()
        }

        rootView.bn3_btn_writerv.setOnClickListener {
            Toast.makeText(requireContext(), "New Review Clicked", Toast.LENGTH_SHORT).show()
            rootView.bn3_btn_writerv.isEnabled = false
            rootView.bn3_tv_norvsyet.visibility = View.GONE
            rootView.bn3_rcv.visibility = View.GONE
            rootView.bn3_makerv_const.visibility = View.VISIBLE

            /*rootView.bn3_makerv_btn_cancel.setOnClickListener {
                rootView.bn3_makerv_edt_input.text.clear()
                rootView.bn3_makerv_const.visibility = View.GONE
                rootView.bn3_rcv.visibility = View.VISIBLE
                rootView.bn3_btn_writerv.isEnabled = true
            }*/

            /*rootView.bn3_makerv_btn_up.setOnClickListener {
                if(rootView.bn3_makerv_edt_input.text.isEmpty()){
                    rootView.bn3_makerv_edt_input.hint = "내용을 입력해 주세요"
                }else{
                    fbdb.collection("tmp5vStrings").document(get_documentId)
                        .get()
                        .addOnSuccessListener { document ->
                            //RestTitle = document.get("RestTitle").toString()
                            //var docid = document.id
                            ReviewArrayToUpdate.clear()
                            var list2 = document.getData()!!.get("RestReview") as List<*>
                            for (kk in 0..list2.size-1){
                                var map1 = list2.get(kk) as HashMap<*, *>
                                var rat1 = BN3Info_Review(map1.get("content").toString(), map1.get("date").toString(), map1.get("uid").toString())
                                ReviewArrayToUpdate.add(rat1)
                            }
                            var mDateFor = SimpleDateFormat("yyyy-MM-dd")
                            var mDateStr = mDateFor.format(Date())
                            Log.d("Date Tag", mDateStr)
                            ReviewArrayToUpdate.add(BN3Info_Review(rootView.bn3_makerv_edt_input.text.toString(), mDateStr, get_uid))
                            Log.d("TAGrty", "BN3 DocumentSnapshot data: ${document.data}")

                            for (kk in 0..ReviewArray.size-1){
                                println("testing review C"+ReviewArray.get(kk).content+" / D"+ReviewArray.get(kk).date+" / U"+ReviewArray.get(kk).uid)
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("TAG", "get failed with ", exception)
                        }

                    Handler().postDelayed({
                        fbdb.collection("tmp5vStrings").document(get_documentId)
                            //.update("ValTest", "Right?")
                            .update("RestReview", ReviewArrayToUpdate)
                            .addOnSuccessListener { Log.d("Update", "DocumentSnapshot successfully updated!") }
                            .addOnFailureListener { e -> Log.w("Update", "Error updating document", e) }
                    }, 1000)

                    rootView.bn3_makerv_edt_input.text.clear()
                    rootView.bn3_makerv_const.visibility = View.GONE
                    rootView.bn3_rcv.visibility = View.VISIBLE
                    rootView.bn3_btn_writerv.isEnabled = true
                }

            }*/

            /*fbdb.collection("tmp5vStrings").document(get_documentId)
                .get()
                .addOnSuccessListener { document ->
                    //RestTitle = document.get("RestTitle").toString()
                    var docid = document.id
                    ReviewArrayToUpdate.clear()
                    var list2 = document.getData()!!.get("RestReview") as List<*>
                    for (kk in 0..list2.size-1){
                        var map1 = list2.get(kk) as HashMap<*, *>
                        var rat1 = BN3Info_Review(map1.get("content").toString(), map1.get("date").toString(), map1.get("uid").toString())
                        ReviewArrayToUpdate.add(rat1)
                    }
                    var mDateFor = SimpleDateFormat("yyyy-MM-dd")
                    var mDateStr = mDateFor.format(Date())
                    Log.d("Date Tag", mDateStr)
                    ReviewArrayToUpdate.add(BN3Info_Review("content by trial - "+ReviewArrayToUpdate.size, mDateStr, get_uid))
                    Log.d("TAGrty", "BN3 DocumentSnapshot data: ${document.data}")

                    for (kk in 0..ReviewArray.size-1){
                        println("testing review C"+ReviewArray.get(kk).content+" / D"+ReviewArray.get(kk).date+" / U"+ReviewArray.get(kk).uid)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }

            Handler().postDelayed({
                fbdb.collection("tmp5vStrings").document(get_documentId)
                    //.update("ValTest", "Right?")
                    .update("RestReview", ReviewArrayToUpdate)
                    .addOnSuccessListener { Log.d("Update", "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w("Update", "Error updating document", e) }
            }, 1000)*/

//            Handler().postDelayed({
//                Log.d("TAG RATU", ReviewArrayToUpdate.toString())
//                fdb_getdata(fbdb, get_uid, adapter)
//            }, 1000)
//
//
//            rootView.bn3_tv_norvsyet.visibility = View.GONE
//            if(rootView.bn3_rcv.visibility != View.VISIBLE){
//                rootView.bn3_rcv.visibility = View.VISIBLE
//            }

        }

        rootView.bn3_makerv_btn_cancel.setOnClickListener {
            rootView.bn3_makerv_edt_input.text.clear()
            if(ReviewArray.size > 0){
                rootView.bn3_tv_norvsyet.visibility = View.GONE
            }else{
                rootView.bn3_tv_norvsyet.visibility = View.VISIBLE
            }
            rootView.bn3_makerv_const.visibility = View.GONE
            rootView.bn3_rcv.visibility = View.VISIBLE
            rootView.bn3_btn_writerv.isEnabled = true
        }

        rootView.bn3_makerv_btn_up.setOnClickListener {
            if(rootView.bn3_makerv_edt_input.text.isEmpty()){
                rootView.bn3_makerv_edt_input.hint = "내용을 입력해 주세요"
            }else{
                fbdb.collection("tmp5vStrings").document(get_documentId)
                    .get()
                    .addOnSuccessListener { document ->
                        //RestTitle = document.get("RestTitle").toString()
                        //var docid = document.id
                        ReviewArrayToUpdate.clear()
                        var list2 = document.getData()!!.get("RestReview") as List<*>
                        for (kk in 0..list2.size-1){
                            var map1 = list2.get(kk) as HashMap<*, *>
                            var rat1 = BN3Info_Review(map1.get("content").toString(), map1.get("date").toString(), map1.get("uid").toString())
                            ReviewArrayToUpdate.add(rat1)
                        }
                        var mDateFor = SimpleDateFormat("yyyy-MM-dd")
                        var mDateStr = mDateFor.format(Date())
                        Log.d("Date Tag", mDateStr)
                        ReviewArrayToUpdate.add(BN3Info_Review(rootView.bn3_makerv_edt_input.text.toString(), mDateStr, get_uid))
                        Log.d("TAGrty", "BN3 DocumentSnapshot data: ${document.data}")

                        for (kk in 0..ReviewArray.size-1){
                            println("testing review C"+ReviewArray.get(kk).content+" / D"+ReviewArray.get(kk).date+" / U"+ReviewArray.get(kk).uid)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TAG", "get failed with ", exception)
                    }

                Handler().postDelayed({
                    fbdb.collection("tmp5vStrings").document(get_documentId)
                        //.update("ValTest", "Right?")
                        .update("RestReview", ReviewArrayToUpdate)
                        .addOnSuccessListener { Log.d("Update", "DocumentSnapshot successfully updated!") }
                        .addOnFailureListener { e -> Log.w("Update", "Error updating document", e) }

                    Handler().postDelayed({
                        Log.d("TAG RATU", ReviewArrayToUpdate.toString())
                        fdb_getdata(fbdb, get_uid, adapter)

                        println("mmmmm222 "+ my_review_num)
                        Handler().postDelayed({
                            my_review_num = 0
                            for(kk in 0..ReviewArray.size-1){
                                my_review_num = my_review_num + ReviewArray[kk].findUidNums(get_uid)
                            }
                            if(ReviewArray.size > 0){
                                println("Gone? "+ ReviewArray.size)
                                rootView.bn3_tv_norvsyet.visibility = View.GONE
                            }else{
                                println("Visible? "+ ReviewArray.size)
                                rootView.bn3_tv_norvsyet.visibility = View.VISIBLE
                            }
                            //rootView.bn3_tv_norvsyet.visibility = View.GONE
                            rootView.bn3_makerv_edt_input.text.clear()
                            rootView.bn3_makerv_const.visibility = View.GONE
                            rootView.bn3_rcv.visibility = View.VISIBLE
                            rootView.bn3_btn_writerv.isEnabled = true
                            Log.d("Inmain", "my_review_num in main is: "+my_review_num+" / uid: "+get_uid)
                            if(my_review_num > 0){
                                rootView.bn3_txt_rv.text = "내가 작성한 리뷰: "+my_review_num+"개"
                            }else{
                                rootView.bn3_txt_rv.text = "첫 리뷰를 작성해 주세요"
                            }
                        }, 400)
                    }, 800)
                }, 800)




            }

        }

        rootView.bn3_rcv.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        rootView.bn3_rcv.adapter = adapter

        return rootView
    }

    fun fdb_getdata(fbdb: FirebaseFirestore, uid: String, adapter: Bn3ReviewsAdapter){
        //var my_review_num = 0
        fbdb.collection("tmp5vStrings")
            .whereEqualTo("RestNo", RestNo)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents)
                    if (document != null) {
                        println("document id is: "+document.id)
                        get_documentId = document.id
                        RestTitle = document.get("RestTitle").toString()
                        ReviewArray.clear()
                        //var list1 = document.getData()!!.get("RestRating") as List<*>
                        var list2 = document.getData()!!.get("RestReview") as List<*>
//                        for (kk in 0..list1.size-1){
//                            var map1 = list1.get(kk) as HashMap<*, *>
//                            var rat1 = BN3Info_Rating(map1.get("stars").toString().toDouble(), map1.get("uid").toString())
//                            RatingArray.add(rat1)
//                        }
                        for (kk in 0..list2.size-1){
                            var map1 = list2.get(kk) as HashMap<*, *>
                            var rat1 = BN3Info_Review(map1.get("content").toString(), map1.get("date").toString(), map1.get("uid").toString())
                            my_review_num = my_review_num + rat1.findUidNums(uid)
                            println("mmmmm "+ my_review_num)
                            ReviewArray.add(rat1)
                        }
                        Log.d("TAG", "BN3 DocumentSnapshot data: ${document.data}")

//                        for (kk in 0..RatingArray.size-1){
//                            RatingArraySe.add(RatingArray.get(kk).uid)
//                            println("testing rating S"+RatingArray.get(kk).stars+" / U"+RatingArray.get(kk).uid)
//                        }
//                        for (kk in 0..RatingArraySe.size-1){
//                            println("Rating Array Se test: U: "+RatingArraySe.get(kk))
//                        }
                        for (kk in 0..ReviewArray.size-1){
                            println("testing review C"+ReviewArray.get(kk).content+" / D"+ReviewArray.get(kk).date+" / U"+ReviewArray.get(kk).uid)
                        }


                    } else {
                        Log.d("TAG", "No such document - Fragment1")

                    }
                adapter.notifyDataSetChanged()

            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)

            }
        //Thread.sleep(700)
        //Log.d("Infunc", "my_review_num in func is: "+my_review_num+" / uid: "+uid)
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}