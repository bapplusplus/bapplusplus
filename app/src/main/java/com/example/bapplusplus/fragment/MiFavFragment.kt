package com.example.bapplusplus.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bapplusplus.R
import com.example.bapplusplus.adapter.MiFavAdapter
import com.example.bapplusplus.data.FBUserInfo
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_my_info.*
import kotlinx.android.synthetic.main.fragment_mi_fav.view.*
import kotlinx.android.synthetic.main.fragment_mi_review.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

data class MiFav_Data(
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

class MiFavFragment : Fragment() {

    var myFavArray = arrayListOf<MiFav_Data>()
    var adapter: MiFavAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootview = inflater.inflate(R.layout.fragment_mi_fav, container, false)

        mifavtvnum = rootview.findViewById<TextView>(R.id.mifav_tv_num)
        mifavsepline = rootview.findViewById(R.id.mifav_sep_line)
        mifavtvnofav = rootview.findViewById(R.id.mifav_tv_nofav)

        requireActivity().myinfo_toolbar_title.text = "좋아요 관리"

        adapter = MiFavAdapter(requireContext(), myFavArray)
        rootview.mifav_recycler.adapter = adapter
        rootview.mifav_recycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        rootview.mifav_sep_line.visibility = View.GONE
        rootview.mifav_tv_num.visibility = View.GONE
        rootview.mifav_recycler.visibility = View.GONE


        CoroutineScope(Main).launch {
            getFavsData(FBUserInfo.fbauth.currentUser!!.uid)

            if(myFavArray.isEmpty()){
                rootview.mifav_progressbar.visibility = View.GONE
                rootview.mifav_tv_nofav.visibility = View.VISIBLE
            }else{

                rootview.mifav_tv_num.text = "좋아요 항목 "+myFavArray.size.toString()+"개"

                rootview.mifav_progressbar.visibility = View.GONE
                rootview.mifav_sep_line.visibility = View.VISIBLE
                rootview.mifav_tv_num.visibility = View.VISIBLE
                rootview.mifav_recycler.visibility = View.VISIBLE
                adapter!!.notifyDataSetChanged()
            }
        }

        return rootview
    }

    companion object {

        var mifavtvnum: TextView? = null
        var mifavsepline: ImageView? = null
        var mifavtvnofav: TextView? = null

        var fbdb = FirebaseFirestore.getInstance()

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MiFavFragment().apply {
                arguments = Bundle().apply {
                }
            }


        suspend fun likeCancel(basicInfo: MiFav_Data, restNo: Int, uid: String): Boolean{
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
                fbdb!!.collection("tmp7vBasic").document("RestBasic"+restNo.toString())
                    .update("RestFavoritesArray", FieldValue.arrayRemove(uid)).await()
                fbdb!!.collection("AccountGroup").document(uid)
                    .update("MyFavoritesArray", FieldValue.arrayRemove(toDeleteMap)).await()
                return true
            }catch (e: Exception){
                Log.e("MiFav", "Failed to cancelLike: "+e.localizedMessage)
                return false
            }
        }
    }

    suspend fun getFavsData(uid: String): Boolean{
        try{
            myFavArray.clear()
            val doca = fbdb!!.collection("AccountGroup").document(uid).get().await()
            var listGetter = doca.data!!.get("MyFavoritesArray") as List<*>

            if(listGetter.isEmpty()){
                return true
            }

            for(cc in 0..listGetter.size-1){
                var doc = listGetter.get(cc) as HashMap<*, *>
                var map1 = MiFav_Data(
                    (doc.get("RestNo") as Long).toInt(), doc.get("RestCategory").toString(), doc.get("RestTitle").toString(),
                    doc.get("RestCallNum").toString(), doc.get("RestAddressOld").toString(), doc.get("RestAddressRoad").toString(),
                    doc.get("RestOrderTime").toString(), doc.get("RestPosx") as Double, doc.get("RestPosy") as Double)
                myFavArray.add(map1)
            }

            return true
        }catch (e: Exception){
            Log.e("MiFav", "Likearray fail: $uid, "+e.printStackTrace()+", "+e.localizedMessage)
            return false
        }
    }
}