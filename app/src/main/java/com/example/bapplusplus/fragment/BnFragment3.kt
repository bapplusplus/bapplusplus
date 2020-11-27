package com.example.bapplusplusTemp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bapplusplus.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_bn3.*

data class BN3Info_Rating(
    var stars: Double,
    var uid: String
)

data class BN3Info_Review(
    var content: String,
    var date: String,
    var uid: String
)

class BnFragment3 : Fragment() {
    var RestNo = 0
    var RestTitle = ""
    var RatingArray: ArrayList<BN3Info_Rating> = arrayListOf()
    var ReviewArray: ArrayList<BN3Info_Review> = arrayListOf()
    var get_uid: String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_bn3, container, false)
        val bundle = arguments
        val fbdb = FirebaseFirestore.getInstance()
        val fbauth = FirebaseAuth.getInstance()

        RestNo = bundle?.getInt("RestNo") ?: 0


        fbdb.collection("tmp5vStrings")
            .whereEqualTo("RestNo", RestNo)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents)
                    if (document != null) {
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
                            ReviewArray.add(rat1)
                        }
                        Log.d("TAG", "BN3 DocumentSnapshot data: ${document.data}")

                        for (kk in 0..RatingArray.size-1){
                            println("testing rating S"+RatingArray.get(kk).stars+" / U"+RatingArray.get(kk).uid)
                        }
                        for (kk in 0..ReviewArray.size-1){
                            println("testing review C"+ReviewArray.get(kk).content+" / D"+ReviewArray.get(kk).date+" / U"+ReviewArray.get(kk).uid)
                        }
                    } else {
                        Log.d("TAG", "No such document - Fragment1")
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        get_uid = fbauth.currentUser?.uid ?: ""
        if(get_uid.equals("")){
            bn3_btn_myfav.text = "로그인 되지 않았습니다."
        }else{

        }











        return rootView
    }

}