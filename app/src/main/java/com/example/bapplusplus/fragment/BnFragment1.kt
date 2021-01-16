package com.example.bapplusplusTemp.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.example.bapplusplus.InfoTemp
import com.example.bapplusplus.R
import com.example.bapplusplus.RestInfoTemp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_bn1.*

class BnFragment1 : Fragment() {

    var RestNo = 0
    var get_posx = 0.0
    var get_posy = 0.0
    var RestTitle = ""
    var RestRoadAddress = ""
    var RestCallNum = ""
    var RestCategory = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootview = inflater.inflate(R.layout.fragment_bn1, container, false)
        // Inflate the layout for this fragment
        ////val progressDialog = ProgressDialog(context)
        //progressDialog.setMessage("ProgressDialog running...")
        ////progressDialog.setCancelable(true)
        ////progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        ////progressDialog.show()
        val bundle = arguments
        //val infoTemp = bundle?.getParcelable<RestInfoTemp>("infotemp")
        RestNo = bundle?.getInt("RestNo") ?: 0
        //RestTitle = bundle?.getString("RestTitle").toString()
        //RestRoadAddress = bundle?.getString("RestRoadAddress").toString()
        //RestCallNum = bundle?.getString("RestCallNum").toString()
        //RestCategory = bundle?.getString("RestCategory").toString()
        val fbdb = FirebaseFirestore.getInstance()


        /*fbdb.collection("tmp7vBasic")
            .whereEqualTo("RestNo", RestNo)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents)
                    if (document != null) {
                        Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                        RestTitle = document.getString("RestTitle").toString()
                        RestRoadAddress = document.getString("RestRoadAddress").toString()
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

                        progressDialog.dismiss()
                    } else {
                        Log.d("TAG", "No such document - Fragment1")
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }*/

        fbdb.collection("tmp7vBasic").document("RestBasic"+RestNo.toString()).get()
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
            }

//        rootview.findViewById<TextView>(R.id.bn1_title).text = bundle?.getString("RestTitle").toString()
//        rootview.findViewById<TextView>(R.id.bn1_category).text = bundle?.getString("RestCategory").toString()
//        rootview.findViewById<TextView>(R.id.bn1_address).text = bundle?.getString("RestRoadAddress").toString()
//        if(RestCallNum.equals("")){
//            rootview.findViewById<TextView>(R.id.bn1_call).text = bundle?.getString("RestCallNum").toString()
//        }else{
//            rootview.findViewById<TextView>(R.id.bn1_call).text = bundle?.getString("RestCallNum").toString()
//        }
        rootview.findViewById<TextView>(R.id.bn1_time).text = "Unavailable"
        rootview.findViewById<TextView>(R.id.bn1_delivery).text = "Unavailable"

        return rootview
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}