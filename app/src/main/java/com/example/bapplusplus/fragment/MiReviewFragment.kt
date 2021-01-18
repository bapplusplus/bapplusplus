package com.example.bapplusplus.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.bapplusplus.R
import com.example.bapplusplus.adapter.Bn4ReviewsAdapter
import com.example.bapplusplus.adapter.MiReviewAdapter
import com.example.bapplusplus.data.FBUserInfo
import com.example.bapplusplus.deprecated.ShowMapActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_my_info.*
import kotlinx.android.synthetic.main.activity_my_info.view.*
import kotlinx.android.synthetic.main.fragment_bn4.view.*
import kotlinx.android.synthetic.main.fragment_mi_review.*
import kotlinx.android.synthetic.main.fragment_mi_review.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.HashMap


data class MiReview_Data(
    var content: String,
    var menu: String,
    var timeValue: String,
    var restTitle: String,
    var restCategory: String,
    var restNo: Int,
    var star: Double,
    var reviewCode: String,
    var photoPath: String,
    var timeStamp: Timestamp,
    var displayName: String
)

class MiReviewFragment : Fragment() {

    var myReviewArray = arrayListOf<MiReview_Data>()
    val fbdb = FirebaseFirestore.getInstance()
    val fbauth = FirebaseAuth.getInstance()
    var adapter: MiReviewAdapter? = null
    val reviewListOptionsArray = arrayListOf("최신 순", "오래된 순", "평점 높은 순", "평점 낮은 순")
    //var tvnumview :TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_mi_review, container, false)
        rootView.findViewById<TextView>(R.id.mirv_tv_num).visibility = View.INVISIBLE
        rootView.findViewById<Spinner>(R.id.mirv_spinner).visibility = View.INVISIBLE
        rootView.findViewById<ImageView>(R.id.mirv_sep_line).visibility = View.INVISIBLE
        tvnumview = rootView.findViewById<TextView>(R.id.mirv_tv_num)
        spinnerview = rootView.findViewById<Spinner>(R.id.mirv_spinner)
        tvnoreview = rootView.findViewById<TextView>(R.id.mirv_tv_noreview)

        requireActivity().myinfo_toolbar_title.text = "리뷰 관리"

        adapter = MiReviewAdapter(requireContext(), myReviewArray)
        //rootView.findViewById<RecyclerView>(R.id.bn4_recycler).adapter = adapter
        rootView.mirv_recycler.adapter = adapter
        rootView.mirv_recycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        CoroutineScope(Main).launch {
            //Log.e("BN4", "restNo: "+restNo)
            getMyReviewArray()

            rootView.mirv_tv_num.text = "내가 작성한 리뷰: "+myReviewArray.size.toString()+"건"
            rootView.findViewById<ProgressBar>(R.id.mirv_progressbar).visibility = View.GONE
            rootView.findViewById<TextView>(R.id.mirv_tv_num).visibility = View.VISIBLE
            rootView.findViewById<Spinner>(R.id.mirv_spinner).visibility = View.VISIBLE
            rootView.findViewById<ImageView>(R.id.mirv_sep_line).visibility = View.VISIBLE

            if(myReviewArray.isEmpty()){
                rootView.findViewById<ImageView>(R.id.mirv_sep_line).visibility = View.INVISIBLE
                rootView.mirv_tv_num.visibility = View.INVISIBLE
                rootView.findViewById<RecyclerView>(R.id.mirv_recycler).visibility = View.GONE
                rootView.findViewById<TextView>(R.id.mirv_tv_noreview).visibility = View.VISIBLE
                rootView.findViewById<Spinner>(R.id.mirv_spinner).visibility = View.INVISIBLE
                rootView.findViewById<Spinner>(R.id.mirv_spinner).isEnabled = false
            }
            adapter!!.notifyDataSetChanged()

        }

        var reviewListSpinnerAdapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item_two, R.id.csp2_item_tv, reviewListOptionsArray)
        rootView.findViewById<Spinner>(R.id.mirv_spinner).adapter = reviewListSpinnerAdapter

        rootView.findViewById<Spinner>(R.id.mirv_spinner).onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //p2 is position
                when(p2){
                    0->{
                        myReviewArray.sortByDescending { data -> data.timeStamp }
                        //myReviewArray.reverse()
                        rootView.mirv_recycler.smoothScrollToPosition(0)
                        adapter!!.notifyDataSetChanged()
                    }
                    1->{
                        myReviewArray.sortBy { data -> data.timeStamp }
                        rootView.mirv_recycler.smoothScrollToPosition(0)
                        adapter!!.notifyDataSetChanged()
                    }
                    2->{
                        myReviewArray.sortByDescending { data -> data.star }
                        //myReviewArray.reverse()
                        rootView.mirv_recycler.smoothScrollToPosition(0)
                        adapter!!.notifyDataSetChanged()
                    }
                    3->{
                        myReviewArray.sortBy { data -> data.star }
                        rootView.mirv_recycler.smoothScrollToPosition(0)
                        adapter!!.notifyDataSetChanged()
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        return rootView
    }

    companion object {
        var fbdb = FirebaseFirestore.getInstance()
        var fbstrref = FirebaseStorage.getInstance().reference
        var tvnumview :TextView? = null
        var spinnerview: Spinner? = null
        var tvnoreview: TextView? = null

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MiReviewFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                    //putString(ARG_PARAM2, param2)
                }
            }

        fun tvNumTextSetter(i: Int){
            tvnumview?.text = "내가 작성한 리뷰: "+i.toString()+"건"
        }

        fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
            var ft: FragmentTransaction = fragmentManager.beginTransaction()
            ft.detach(fragment).attach(fragment).commit()
        }

        suspend fun newDeleteOne(item: MiReview_Data): Boolean{
            return try {
                Log.e("ReviewDel", "Stage: 1")
                val doc = fbdb!!.collection("tmp7vString").document("RestString" + item.restNo.toString()).get().await()
                Log.e("ReviewDel", "Stage: 1.1" + doc.data)
                var oldReviewNum = doc.data!!.get("RestReviewNum") as Long
                oldReviewNum.toInt()
                Log.e("ReviewDel", "Stage: 1.2 / " + oldReviewNum.toString())
                var oldRatingAvg = doc.getDouble("RestRatingAvg")
                Log.e("ReviewDel", "Stage: 1.3 / " + oldRatingAvg.toString())

                Log.e("ReviewDel", "Stage: 2")
                //arrayRemove 7vlist
                val toDeleteMap = hashMapOf(
                    "RestNo" to item.restNo,
                    "RestTitle" to item.restTitle,
                    "RestCategory" to item.restCategory,
                    "RestRatingAvg" to oldRatingAvg,
                    "RestReviewNum" to oldReviewNum
                )
                fbdb!!.collection("tmp7vList").document("bf6c7ba0-537c-11eb-85d6-f96783d0ff1e")
                    .update("tmp7vArray", FieldValue.arrayRemove(toDeleteMap)).await()
                Log.e("ReviewDel", "Stage: 3")

                val toDeleteElement = hashMapOf(
                    "RestNo" to item.restNo,
                    "RestTitle" to item.restTitle,
                    "RestCategory" to item.restCategory,
                    "ReviewUid" to FBUserInfo.fbuser?.uid,//
                    "ReviewContent" to item.content,//
                    "ReviewMenu" to item.menu,//
                    "ReviewRating" to item.star,//
                    "ReviewDisplayName" to item.displayName,
                    "ReviewTime" to item.timeStamp,
                    "ReviewPhotoPathOne" to item.photoPath,
                    "ReviewCode" to item.reviewCode
                )

                fbdb!!.collection("tmp7vString").document("RestString" + item.restNo.toString())
                    .update("RestReviewArray", FieldValue.arrayRemove(toDeleteElement)).await()

                val docnu = fbdb!!.collection("tmp7vString").document("RestString" + item.restNo.toString()).get().await()
                var oldArray = docnu.data!!.get("RestReviewArray") as List<*>
                var ratingToSum = 0.0
                if(oldArray.size == 0){

                }else{
                    for(cc in 0..oldArray.size-1){
                        var data = oldArray.get(cc) as HashMap<*, *>
                        ratingToSum += data.get("ReviewRating") as Double
                    }
                    Log.d("ReviewDel", "Stage: 4.5, ratingtosum: " + ratingToSum.toString())
                    //val testval1 = reviewdata?.get("ReviewRating") as Double
                    //ratingToSum += testval1
                    //Log.d("ReviewUp", "Stage: 4.6, ratingtosum: " + testval1.toString())
                    Log.d("ReviewDel", "Stage: 4.6, ratingtosum: " + ratingToSum.toString())
                    ratingToSum = ratingToSum/(oldArray.size)
                    Log.d("ReviewDel", "Stage: 4.7, ratingtosum: " + ratingToSum.toString())
                    //ratingToSum = ((ratingToSum*100).roundToInt()/100).toDouble()
                    ratingToSum = Math.round(ratingToSum * 100)/100.0
                    Log.d("ReviewDel", "Stage: 4.8, ratingtosum: " + ratingToSum.toString())
                }

                var newReviewNum = oldReviewNum-1
                Log.e(
                    "ReviewDel",
                    "Stage: 5, ratingtosum: " + ratingToSum.toString() + " / newreviewnum: " + newReviewNum.toString()
                )

                val doc2 = fbdb!!.collection("tmp7vString").document("RestString" + item.restNo.toString())
                //doc2.update("RestReviewArray", FieldValue.arrayUnion(reviewdata)).await()
                doc2.update("RestRatingAvg", ratingToSum).await()
                doc2.update("RestReviewNum", newReviewNum).await()
                Log.e("ReviewUp", "Stage: 7")

                val toAddMap = hashMapOf(
                    "RestNo" to item.restNo,
                    "RestTitle" to item.restTitle,
                    "RestCategory" to item.restCategory,
                    "RestRatingAvg" to ratingToSum,
                    "RestReviewNum" to newReviewNum
                )

                fbdb!!.collection("tmp7vList").document("bf6c7ba0-537c-11eb-85d6-f96783d0ff1e")
                    .update("tmp7vArray", FieldValue.arrayUnion(toAddMap)).await()
                Log.e("ReviewDel", "Stage: 9")
                fbdb!!.collection("AccountGroup").document(FBUserInfo.fbuser!!.uid)
                    .update("MyReviewArray", FieldValue.arrayRemove(toDeleteElement)).await()


                if(!item.photoPath.isNullOrEmpty()){
                    fbstrref.child(item.photoPath).delete().await()
                }

                return true
            }catch (e: Exception){
                Log.e("ReviewUp", "Error: " + e.printStackTrace())
                return false
            }
        }

    }

    suspend fun getMyReviewArray(): Boolean{
        return try {
            //Log.e("BN4", "restNo: "+restNo)
            val document = fbdb.collection("AccountGroup").document(FBUserInfo.fbuser!!.uid).get().await()
            myReviewArray.clear()
            //restTitle = document.getString("RestTitle").toString()
            var list2 = document.data!!.get("MyReviewArray") as List<*>
            if(list2.isEmpty()){
                return true
            }

            for(cc in list2.size-1 downTo 0){
                var map1 = list2.get(cc) as HashMap<*, *>
                var rev1 = MiReview_Data(map1.get("ReviewContent").toString(), map1.get("ReviewMenu").toString(),
                    timestampToDateString(map1.get("ReviewTime") as Timestamp), map1.get("RestTitle").toString(), map1.get("RestCategory").toString(),
                    //"1234568", map1.get("ReviewDisplayName").toString(), map1.get("ReviewUid").toString(),
                    map1.get("RestNo").toString().toInt(), map1.get("ReviewRating").toString().toDouble(), map1.get("ReviewCode").toString(),
                    map1.get("ReviewPhotoPathOne").toString(), map1.get("ReviewTime") as Timestamp, map1.get("ReviewDisplayName").toString()
                )
                //my_review_count += rev1.findUidNums(FBUserInfo.fbuser?.uid)
                myReviewArray.add(rev1)
            }
            return true
        }catch (e: Exception){
            Log.e("MiReview", "get rest array fail: "+e.printStackTrace())
            return false
        }
    }

    fun timestampToDateString(t: Timestamp): String{
        val date = t.toDate()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        Log.d("MiReview", sdf.format(date))
        return sdf.format(date)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }

}