package com.example.bapplusplus.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.bapplusplus.adapter.Bn4ReviewsAdapter
import com.example.bapplusplus.data.FBUserInfo
import com.example.bapplusplus.R
import com.example.bapplusplus.ReviewUploadActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_bn4.*
import kotlinx.android.synthetic.main.fragment_bn4.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class BN4Info_Review(
    var content: String,
    var menu: String,
    var timeValue: String,
    var displayName: String,
    var uid: String,
    var star: Double,
    var reviewCode: String,
    var photoPath: String,
    var timeStamp: Timestamp
){
    fun findUidNums(uid: String?): Int{
        var to_return = 0
        if(this.uid == uid){
            to_return = 1
        }
        println("finduidnums: "+ to_return)
        return to_return
    }
}



class BnFragment4 : Fragment() {
    var restNo = 0
    var restTitle = ""
    var restCategory =""
    var my_review_count = 0
    var ratingValue = 0.0
    var restReviewArray = arrayListOf<BN4Info_Review>()
    val fbdb = FirebaseFirestore.getInstance()
    val fbauth = FirebaseAuth.getInstance()
    var adapter: Bn4ReviewsAdapter? = null
    val reviewListOptionsArray = arrayListOf<String>("최신 순", "오래된 순", "평점 높은 순", "평점 낮은 순")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_bn4, container, false)
        val bundle = arguments
        restNo = bundle?.getInt("RestNo") ?: 0
        restTitle = bundle?.getString("RestTitle") ?: ""
        restCategory = bundle?.getString("RestCategory") ?: ""
        rootView.findViewById<RatingBar>(R.id.bn4_ratingbar_star).rating = 4.3F
        rootView.findViewById<TextView>(R.id.bn4_tv_star).text = rootView.findViewById<RatingBar>(R.id.bn4_ratingbar_star).rating.toString()

        //initial status
        rootView.findViewById<RatingBar>(R.id.bn4_ratingbar_star).visibility = View.INVISIBLE
        rootView.findViewById<TextView>(R.id.bn4_tv_star).visibility = View.INVISIBLE
        rootView.findViewById<TextView>(R.id.bn4_tv_reviewnum).visibility = View.INVISIBLE
        //rootView.findViewById<Spinner>(R.id.bn4_spinner).visibility = View.INVISIBLE
        rootView.findViewById<Button>(R.id.bn4_btn_writereview).visibility = View.GONE
        rootView.findViewById<ConstraintLayout>(R.id.bn4_const_spinandbtn).visibility = View.GONE
        rootView.findViewById<ImageView>(R.id.bn4_img_line1).visibility = View.INVISIBLE
        rootView.findViewById<ImageView>(R.id.bn4_img_line2).visibility = View.INVISIBLE



        adapter = Bn4ReviewsAdapter(requireContext(), restReviewArray)
        //rootView.findViewById<RecyclerView>(R.id.bn4_recycler).adapter = adapter
        rootView.bn4_recycler.adapter = adapter
        rootView.bn4_recycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))


        rootView.findViewById<Button>(R.id.bn4_btn_writereview).setOnClickListener {
            if(!FBUserInfo.loginState){
                bn4_tv_reviewnum.text = "UserNull"
            }else{
                val intent = Intent(requireContext(), ReviewUploadActivity::class.java)
                intent.putExtra("RestTitle", restTitle)
                intent.putExtra("RestNo", restNo)
                intent.putExtra("RestCategory", restCategory)
                intent.putExtra("MyReviewCount", my_review_count)
                println("my_review_count: "+my_review_count)
                startActivityForResult(intent, 15)
            }
        }

        CoroutineScope(Main).launch {
            Log.e("BN4", "restNo: "+restNo)
            getRestReviewArray(restNo)

            rootView.findViewById<ProgressBar>(R.id.bn4_progressbar).visibility = View.GONE
            val getdoc = getDocument()
            rootView.findViewById<RatingBar>(R.id.bn4_ratingbar_star).rating = getdoc!!.get("RestRatingAvg")!!.toString().toFloat()
            //rootView.findViewById<RatingBar>(R.id.bn4_ratingbar_star).rating = (3.37).toFloat()
            rootView.findViewById<TextView>(R.id.bn4_tv_star).text = rootView.findViewById<RatingBar>(R.id.bn4_ratingbar_star).rating.toString()
            rootView.findViewById<TextView>(R.id.bn4_tv_reviewnum).text = "리뷰 "+getdoc!!.get("RestReviewNum").toString()+"건"

            rootView.findViewById<RatingBar>(R.id.bn4_ratingbar_star).visibility = View.VISIBLE
            rootView.findViewById<TextView>(R.id.bn4_tv_star).visibility = View.VISIBLE
            rootView.findViewById<TextView>(R.id.bn4_tv_reviewnum).visibility = View.VISIBLE
            rootView.findViewById<ConstraintLayout>(R.id.bn4_const_spinandbtn).visibility = View.VISIBLE
            rootView.findViewById<Spinner>(R.id.bn4_spinner).visibility = View.VISIBLE
            rootView.findViewById<ImageView>(R.id.bn4_img_line1).visibility = View.VISIBLE
            rootView.findViewById<ImageView>(R.id.bn4_img_line2).visibility = View.VISIBLE

            if(FBUserInfo.loginState){
                rootView.findViewById<Button>(R.id.bn4_btn_writereview).visibility = View.VISIBLE
            }

            if(restReviewArray.isEmpty()){
                rootView.findViewById<RecyclerView>(R.id.bn4_recycler).visibility = View.GONE
                rootView.findViewById<TextView>(R.id.bn4_tv_noreview).visibility = View.VISIBLE
            }
            adapter!!.notifyDataSetChanged()

        }

        var reviewListSpinnerAdapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item_two, R.id.csp2_item_tv, reviewListOptionsArray)
        rootView.findViewById<Spinner>(R.id.bn4_spinner).adapter = reviewListSpinnerAdapter

        rootView.findViewById<Spinner>(R.id.bn4_spinner).onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //p2 is position
                when(p2){
                    0->{
                        restReviewArray.sortBy { data -> data.timeStamp }
                        restReviewArray.reverse()
                        adapter!!.notifyDataSetChanged()
                    }
                    1->{
                        restReviewArray.sortBy { data -> data.timeStamp }
                        adapter!!.notifyDataSetChanged()
                    }
                    2->{
                        restReviewArray.sortBy { data -> data.star }
                        restReviewArray.reverse()
                        adapter!!.notifyDataSetChanged()
                    }
                    3->{
                        restReviewArray.sortBy { data -> data.star }
                        adapter!!.notifyDataSetChanged()
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        return rootView
    }

    suspend fun getDocument(): DocumentSnapshot?{
        val document = fbdb.collection("tmp7vString").document("RestString"+restNo.toString()).get().await()
        return document
    }

    suspend fun getRestReviewArray(restNo: Int): Boolean{
        return try {
            //Log.e("BN4", "restNo: "+restNo)
            val document = fbdb.collection("tmp7vString").document("RestString"+restNo.toString()).get().await()
            restReviewArray.clear()
            //restTitle = document.getString("RestTitle").toString()
            var list2 = document.data!!.get("RestReviewArray") as List<*>
            if(list2.isEmpty()){
                return true
            }

            for(cc in list2.size-1 downTo 0){
                var map1 = list2.get(cc) as HashMap<*, *>
                var rev1 = BN4Info_Review(map1.get("ReviewContent").toString(), map1.get("ReviewMenu").toString(),
                    timestampToDateString(map1.get("ReviewTime") as Timestamp), map1.get("ReviewDisplayName").toString(), map1.get("ReviewUid").toString(),
                    //"1234568", map1.get("ReviewDisplayName").toString(), map1.get("ReviewUid").toString(),
                    map1.get("ReviewRating").toString().toDouble(), map1.get("ReviewCode").toString(), map1.get("ReviewPhotoPathOne").toString(),
                    map1.get("ReviewTime") as Timestamp)
                my_review_count += rev1.findUidNums(FBUserInfo.fbuser?.uid)
                restReviewArray.add(rev1)
            }
            return true
        }catch (e: Exception){
            Log.e("BN4", "get rest array fail: "+e.printStackTrace())
            return false
        }
    }

    fun longstrToString(longstr: String): String{
        val timestampLong = longstr.toLong()
        val date = Date(timestampLong)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.timeZone = TimeZone.getTimeZone("GMT+9")
        Log.d("BN4", sdf.format(date))
        return sdf.format(date)
    }

    fun timestampToDateString(t: Timestamp): String{
        val date = t.toDate()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        Log.d("BN4", sdf.format(date))
        return sdf.format(date)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            15->{
                CoroutineScope(Main).launch {
                    //bn4_recycler.visibility = View.INVISIBLE

                    //getRestReviewArray(restNo)
                    //adapter!!.notifyDataSetChanged()

                    //bn4_recycler.visibility = View.VISIBLE
                }
                refreshFragment(this, requireActivity().supportFragmentManager)
            }
        }
    }

    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }




}