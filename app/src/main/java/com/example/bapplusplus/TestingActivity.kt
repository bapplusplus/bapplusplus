package com.example.bapplusplus

import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_testing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.io.Serializable

class TestingActivity : AppCompatActivity() {

    var fbdb : FirebaseFirestore? = null
    var count_one = 0
    var testArray: ArrayList<HashMap<String, Serializable>> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)

        fbdb = FirebaseFirestore.getInstance()

        tst_btn_one.setOnClickListener {
            val assetManager: AssetManager = applicationContext.resources.assets
            val inputStream= assetManager.open("restlist_210111_tmp7vBasic.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val userinfo = JSONObject(jsonString)
            val jsonArray = userinfo.optJSONArray("tmp7vBasic")

            //var i = 0
            for(i in 0..jsonArray.length()-1){
                val jsonObject = jsonArray.getJSONObject(i)

                val PointStatus = jsonObject.get("PointStatus")
                val RestCallNum: String = jsonObject.getString("RestCallNum")
                val RestCategory: String = jsonObject.getString("RestCategory")
                //val RestFavoritesArray: ArrayList<String> = jsonObject.get("RestFavoritesArray") as ArrayList<String>
                val RestFavoritesArray = arrayListOf<String>()
                val RestNo: Int = jsonObject.getInt("RestNo")
                val RestOldAddress: String = jsonObject.getString("RestOldAddress")
                val RestOrderTime: String = jsonObject.getString("RestOrderTime")
                val RestPosx: Double = jsonObject.getDouble("RestPosx")
                val RestPosy: Double = jsonObject.getDouble("RestPosy")
                val RestRoadAddress: String = jsonObject.getString("RestRoadAddress")
                val RestTitle: String = jsonObject.getString("RestTitle")

                println(RestNo.toString()+", "+RestTitle +", "+RestCategory+", "+RestFavoritesArray.toString()+RestFavoritesArray.size)
                val itemer = hashMapOf(
                    "RestNo" to RestNo,
                    "RestTitle" to RestTitle,
                    "RestCategory" to RestCategory,
                    "RestAddressRoad" to RestRoadAddress,
                    "RestAddressOld" to RestOldAddress,
                    "RestOrderTime" to RestOrderTime,
                    "RestCallNum" to RestCallNum,
                    "RestPosx" to RestPosx,
                    "RestPosy" to RestPosy,
                    "RestFavoritesArray" to RestFavoritesArray
                )
                //testArray.add(itemer)

                CoroutineScope(Main).launch {
                    val tt = uploadTestGamma(RestNo.toString(), itemer)
                    if(tt == true){
                        println("success upl i: "+i.toString())
                    }else {
                        println("failure upl i: "+i.toString())
                    }
                }

                /*fbdb.collection("tmp7vBasic").document(RestNo.toString())
                        .set(itemer).addOnSuccessListener {
                            Log.d("TAG", "DocumentSnapshot successfully written!")
                            println("success! " +i.toString()+", "+RestNo+", "+RestTitle)

                        }.addOnFailureListener {
                            e -> Log.w("TAG", "Error writing document", e)
                            println("failure- " +i.toString()+", "+RestNo+", "+RestTitle)

                        }*/


            }

            Toast.makeText(applicationContext, "ArrayDone: "+testArray.size, Toast.LENGTH_SHORT).show()


            /*GlobalScope.launch {
                val boolres = writeFsTest(RestNo, jsonObject, itemer)

                withContext(Main){
                    if(boolres == true){
                        //i++
                        println("cortry success, "+i.toString())
                    }else{
                        //i++
                        println("cortry fail, "+i.toString())
                    }

                }
            }*/
            /*for(i in 0..testArray.size-1){
                /*CoroutineScope(Main).launch {
                    var ttt = writeFsTestS(testArray.get(i))
                    println("successs S2")
                }*/
                fbdb.collection("tmp7vBasic").document(testArray.get(i).get("RestNo").toString())
                        .set(testArray.get(i))
                        .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
            }*/

        }

        tst_btn_two.setOnClickListener {
            val assetManager: AssetManager = applicationContext.resources.assets
            val inputStream= assetManager.open("restlist_210111_tmp7vBasic.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val userinfo = JSONObject(jsonString)
            val jsonArray = userinfo.optJSONArray("tmp7vBasic")

            //var i = 0
            for(i in 0..jsonArray.length()-1){
                val jsonObject = jsonArray.getJSONObject(i)

                //val PointStatus = jsonObject.get("PointStatus")
                //val RestCallNum: String = jsonObject.getString("RestCallNum")
                val RestCategory: String = jsonObject.getString("RestCategory")
                //val RestFavoritesArray: ArrayList<String> = jsonObject.get("RestFavoritesArray") as ArrayList<String>
                val RestFavoritesArray = arrayListOf<String>()
                val RestNo: Int = jsonObject.getInt("RestNo")
                //val RestOldAddress: String = jsonObject.getString("RestOldAddress")
                //val RestOrderTime: String = jsonObject.getString("RestOrderTime")
                //val RestPosx: Double = jsonObject.getDouble("RestPosx")
                //val RestPosy: Double = jsonObject.getDouble("RestPosy")
                //val RestRoadAddress: String = jsonObject.getString("RestRoadAddress")
                val RestTitle: String = jsonObject.getString("RestTitle")

                println(RestNo.toString()+", "+RestTitle +", "+RestCategory+", "+RestFavoritesArray.toString()+RestFavoritesArray.size)
                val itemer = hashMapOf(
                    "RestNo" to RestNo,
                    "RestTitle" to RestTitle,
                    "RestReviewArray" to RestFavoritesArray,
                    "RestRatingAvg" to 0, //added
                    "RestReviewNum" to 0  //added
                )
                //testArray.add(itemer)

                CoroutineScope(Main).launch {
                    val tt = uploadTestDelta(RestNo.toString(), itemer)
                    if(tt == true){
                        println("success upl i: "+i.toString())
                    }else {
                        println("failure upl i: "+i.toString())
                    }
                }

            }

            Toast.makeText(applicationContext, "ArrayDone: "+testArray.size, Toast.LENGTH_SHORT).show()
        }

        tst_btn_arrayunion.setOnClickListener {
            val toup = hashMapOf(
                "one" to "a",
                "two" to 37,
                "three" to false
            )
            fbdb!!.collection("tmp6vTest").document("tmp6vDoc")
                .update("6vArray", FieldValue.arrayUnion(toup))
        }

        tst_btn_arrayremove_str.setOnClickListener {
            fbdb!!.collection("tmp6vTest").document("tmp6vDoc")
                .update("6vArray", FieldValue.arrayRemove("6vtwo"))
        }

        tst_btn_arrayremove_map.setOnClickListener {
            val torm = hashMapOf(
                "one" to "a",
                "two" to 37,
                "three" to false
            )
            fbdb!!.collection("tmp6vTest").document("tmp6vDoc")
                .update("6vArray", FieldValue.arrayRemove(torm))
        }




    }

    suspend fun uploadTestGamma(did: String, itemer: HashMap<String, Serializable>): Boolean{
        return try{
            fbdb!!.collection("tmp7vBasic").document("RestBasic"+did).set(itemer).await()
            return true
        }catch (e: Exception){
            return false
        }
    }

    suspend fun uploadTestDelta(did: String, itemer: HashMap<String, Serializable>): Boolean{
        return try{
            fbdb!!.collection("tmp7vString").document("RestString"+did).set(itemer).await()
            return true
        }catch (e: Exception){
            return false
        }
    }
}