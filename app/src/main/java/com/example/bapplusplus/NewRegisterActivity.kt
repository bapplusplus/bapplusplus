package com.example.bapplusplus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_new_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat

class NewRegisterActivity : AppCompatActivity() {
    var fbauth: FirebaseAuth? = null
    var fbdb: FirebaseFirestore? = null
    /*val yearArray_old = arrayOf(1900,1901,1902,1903,1904,1905,1906,1907,1908,1909,1910,
        1911,1912,1913,1914,1915,1916,1917,1918,1919,1920,
        1921,1922,1923,1924,1925,1926,1927,1928,1929,1930,
        1931,1932,1933,1934,1935,1936,1937,1938,1939,1940,
        1941,1942,1943,1944,1945,1946,1947,1948,1949,1950,
        1951,1952,1953,1954,1955,1956,1957,1958,1959,1960,
        1961,1962,1963,1964,1965,1966,1967,1968,1969,1970,
        1971,1972,1973,1974,1975,1976,1977,1978,1979,1980,
        1981,1982,1983,1984,1985,1986,1987,1988,1989,1990,
        1991,1992,1993,1994,1995,1996,1997,1998,1999,2000,
        2001,2002,2003,2004,2005,2006,2007,2008,2009,2010,
        2011,2012,2013,2014,2015,2016,2017,2018,2019,2020)*/
    val monthArray = arrayOf(1,2,3,4,5,6,7,8,9,10,11,12)
    //val yearArray = Array(121, {i-> 1900+i})
    var dayArray: Array<Int> = arrayOf(0)
    var year_sel = 0
    var month_sel =  0
    var day_sel = 0
    var dateStart = false
    var isLeap = true
    var gender_sel = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_register)

        fbauth = FirebaseAuth.getInstance()
        fbdb = FirebaseFirestore.getInstance()

        newr_til_edt_five.isEnabled = false
        newr_til_five.isEnabled = false

        newr_tv_progress.visibility = View.INVISIBLE

        newr_til_edt_four.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(newr_til_edt_four.text.isNullOrEmpty()){
                    newr_tv_pwc_check.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                 if(newr_til_edt_four.text.toString().isEmpty()){
                    newr_tv_pwc_check.visibility = View.INVISIBLE
                } else if(newr_til_edt_four.text.toString() != newr_til_edt_three.text.toString()){
                    newr_tv_pwc_check.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                    newr_tv_pwc_check.text = "×"
                    newr_tv_pwc_check.visibility = View.VISIBLE
                }else if (newr_til_edt_four.text.toString() == newr_til_edt_three.text.toString()){
                    newr_tv_pwc_check.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorGreen1))
                    newr_tv_pwc_check.text = "✓"
                    newr_tv_pwc_check.visibility = View.VISIBLE
                }
            }
        })

        newr_bdate_spin_month.isEnabled = false
        //newr_bdate_spin_day.isEnabled = false
        newr_bdate_edt_day.isEnabled = false
        var monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, monthArray)
        newr_bdate_spin_month.adapter = monthAdapter

        newr_bdate_edt_year.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                dateStart = true
                var year_input = 0
                if(newr_bdate_edt_year.text.toString().isEmpty()){
                    year_input = 0
                    year_sel = 0
                }else{
                    year_input = newr_bdate_edt_year.text.toString().toInt()
                    year_sel = newr_bdate_edt_year.text.toString().toInt()
                    newr_bdate_spin_month.isEnabled = true
                    newr_bdate_edt_day.isEnabled = true
                }
                dateExamine()

                /*if(year_input < 1900 || year_input > 2020){
                    newr_bdate_tv_error.text = "정확한 연도를 입력해 주세요."
                    newr_bdate_tv_error.visibility = View.VISIBLE
                }else{
                    year_sel = year_input
                    newr_bdate_spin_month.isEnabled = true

                    if(newr_bdate_spin_month.isEnabled){
                        setDayArray(year_input, month_sel)
                        monthAdapter.notifyDataSetChanged()
                        if(dayArray.contains(day_sel)){
                            newr_bdate_tv_error.visibility = View.INVISIBLE
                        }else{
                            newr_bdate_tv_error.text = "정확한 날짜를 입력해 주세요."
                        }
                    }
                }*/
            }
        })

        newr_bdate_spin_month.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                month_sel = monthArray[p2]
                //Toast.makeText(applicationContext, "Month: $month_sel", Toast.LENGTH_SHORT).show()
                if(newr_bdate_spin_month.isEnabled == true){
                    newr_bdate_edt_day.isEnabled = true
                }

                //setDayArray(year_sel, month_sel)
                //newr_bdate_edt_day.isEnabled = true
                dateExamine()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        newr_bdate_edt_day.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                var day_input = 0
                if(newr_bdate_edt_day.text.toString().isEmpty()){
                    day_input = 0
                    day_sel = 0
                }else{
                    day_input = newr_bdate_edt_day.text.toString().toInt()
                    day_sel = newr_bdate_edt_day.text.toString().toInt()
                }
                dateExamine()

                /*if(!dayArray.contains(day_input)){
                    newr_bdate_tv_error.text = "정확한 날짜를 입력해 주세요."
                    newr_bdate_tv_error.visibility = View.VISIBLE
                }else{
                    day_sel = day_input
                    if(!(year_sel < 1900 || year_sel > 2020)){
                        newr_bdate_tv_error.visibility = View.INVISIBLE
                    }
                }*/
            }
        })

        newr_gender_rg.setOnCheckedChangeListener { radioGroup, checkedId ->
            when(checkedId){
                R.id.newr_gender_rb_m->gender_sel = 1
                R.id.newr_gender_rb_f->gender_sel = 2
            }
        }

        //var yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, yearArray)
        //var monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, monthArray)
        //var dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dayArray)
        //newr_bdate_spin_year.adapter = yearAdapter
        //newr_bdate_spin_month.adapter = monthAdapter

        /*newr_bdate_spin_year.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val origin_year = year_sel
                year_sel = yearArray[p2]
                if(isLeapYear(origin_year) != isLeapYear(year_sel) && month_sel==2 && day_sel==29){
                    //newr_bdate_spin_month.adapter = monthAdapter

                    setDayArray(year_sel, month_sel)
                    newr_bdate_spin_day.adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, dayArray)
                    newr_bdate_spin_day.setSelection(dayArray.size-1)
                }
                //Toast.makeText(applicationContext, "Year: $year_sel", Toast.LENGTH_SHORT).show()
                //newr_bdate_spin_month.adapter = monthAdapter
                newr_bdate_spin_month.isEnabled = true
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        newr_bdate_spin_month.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                month_sel = monthArray[p2]
                //Toast.makeText(applicationContext, "Month: $month_sel", Toast.LENGTH_SHORT).show()
                setDayArray(year_sel, month_sel)
                //newr_bdate_spin_day.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dayArray)
                var dayAdapter2 = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, dayArray)
                //dayAdapter.notifyDataSetChanged()
                newr_bdate_spin_day.adapter = dayAdapter2
                newr_bdate_spin_day.isEnabled = true
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        newr_bdate_spin_day.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                day_sel = dayArray[p2]
                Toast.makeText(applicationContext, "Day: $day_sel", Toast.LENGTH_SHORT).show()

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }*/





        newr_btn.setOnClickListener {
            newr_tv_progress.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
            if (newr_til_edt_one.text.toString().isEmpty()){
                //newr_tv_progress.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                newr_tv_progress.text = "이름을 입력하세요."
                newr_tv_progress.visibility = View.VISIBLE
                newr_til_one.error = "이름은 필수 입력 항목입니다."
            }else if (newr_til_edt_two.text.toString().isEmpty()){
                newr_tv_progress.text = "이메일을 입력하세요."
                newr_tv_progress.visibility = View.VISIBLE
                newr_til_two.error = "이메일은 필수 입력 항목입니다."
            }else if (!newr_til_edt_two.text.toString().contains("@")){
                //newr_tv_progress.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                newr_tv_progress.text = "이메일 형식이 잘못되었습니다."
                newr_tv_progress.visibility = View.VISIBLE
                newr_til_two.error = "이메일 형식이 잘못되었습니다."
            }else if (newr_til_edt_three.text.toString().length < 6){
                newr_tv_progress.text = "비밀번호를 다시 입력하세요 (6자 이상)."
                newr_tv_progress.visibility = View.VISIBLE
                newr_til_three.error = "비밀번호가 잘못되었습니다."
            }else if (!newr_til_edt_four.text.toString().equals(newr_til_edt_three.text.toString())){
                newr_tv_progress.text = "비밀번호를 확인하세요."
                newr_tv_progress.visibility = View.VISIBLE
                newr_til_four.error = "비밀번호와 일치하지 않습니다."
            }else if(newr_til_edt_six.text.toString().length > 0 && newr_til_edt_six.text.toString().length < 10){
                newr_tv_progress.text = "휴대전화 번호를 다시 확인하세요."
                newr_tv_progress.visibility = View.VISIBLE
            }else if(newr_bdate_tv_error.visibility == View.VISIBLE){
                newr_tv_progress.text = "생년월일을 정확히 입력하세요."
                newr_tv_progress.visibility = View.VISIBLE
            }else if(newr_til_edt_seven.text.toString().length > 0 && !newr_til_edt_seven.text.toString().contains("@")){
                newr_tv_progress.text = "추가 이메일 주소를 정확히 입력하세요."
                newr_tv_progress.visibility = View.VISIBLE
            }else{
                newr_tv_progress.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorBlue1))
                newr_tv_progress.text = "가입 중..."
                newr_tv_progress.visibility = View.VISIBLE

                val get_email = newr_til_edt_two.text.toString()
                val get_pw = newr_til_edt_three.text.toString().trim()
                val get_name = newr_til_edt_one.text.toString()
                val get_phone_number = newr_til_edt_six.text.toString().trim()
                val get_second_email = newr_til_edt_seven.text.toString()
                //var bdatevalue = SimpleDateFormat("yyyy-MM-dd")
                val get_bdate = dateToString()
                val get_rdate = Timestamp.now()


                CoroutineScope(Main).launch {
                    val createUserResult = createUserTest(get_email, get_pw)
                    if(createUserResult){
                        //true

                        val new_user = fbauth!!.currentUser
                        val prochareq = UserProfileChangeRequest.Builder().setDisplayName(get_name).build()
                        //prochareq.displayName = get_name
                        fbauth!!.currentUser?.updateProfile(prochareq)

                        val newuserinfo = hashMapOf(
                            "UserEmail" to get_email,
                            "UserName" to get_name,
                            "UID" to new_user?.uid,
                            "UserPhoneNumber" to get_phone_number,
                            "UserSecondEmail" to get_second_email,
                            "MyReviewArray" to arrayListOf<String>(),
                            "MyFavoritesArray" to arrayListOf<String>(),
                            "IsActivated" to false,
                            "IsAdmin" to 0,
                            "UserBirthDate" to get_bdate,
                            "RegisterTime" to get_rdate,
                            "UserGenger" to gender_sel
                        )

                        val saveUserResult = saveUserTest(newuserinfo)

                        if(saveUserResult){
                            FBUserInfo.setLogin(get_email, get_pw)
                            newr_tv_progress.text = "가입 성공!"
                            newr_tv_progress.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorGreen1))
                            delay(1000)
                            setResult(16)
                            finish()
                        }else{
                            newr_tv_progress.text = "정보 등록 실패"
                            newr_tv_progress.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                        }

                    }else{
                        //false
                        newr_tv_progress.text = "이메일 주소를 확인하세요."
                        newr_tv_progress.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                        newr_tv_progress.visibility = View.VISIBLE
                        newr_til_two.error = "이미 등록된 이메일이거나 형식이 잘못되었습니다."
                    }
                }
            }
        }


    }

    suspend private fun createUserTest(email: String, pw: String): Boolean{
        return try {
            fbauth!!.createUserWithEmailAndPassword(email, pw).await()
            return true
        }catch (e: Exception){
            Log.e("CreateUserError", "CreateUserWithEmailAndPAssword Fail"+e.printStackTrace())
            return false
        }
    }

    suspend private fun saveUserTest(data: HashMap<String, Any?>): Boolean{
        return try {
            fbdb!!.collection("AccountGroup").document(data.get("UID").toString()).set(data).await()
            return true
        }catch (e: Exception){
            Log.e("SaveUserError", "SaveUser Fail"+e.printStackTrace())
            return false
        }
    }

    fun setDayArray(year: Int, month: Int){
        if(month == 2){
            if(year%4==0){
                if(year%100==0){
                    if(year%400==0){
                        //leap
                        dayArray = Array(29, {i->i+1})
                    }else{
                        //non-leap
                        dayArray = Array(28, {i->i+1})
                    }
                }else{
                    //leap
                    dayArray = Array(29, {i->i+1})
                }
            }else{
                //non-leap
                dayArray = Array(28, {i->i+1})
            }
        }else if(arrayOf(1,3,5,7,8,10,12).contains(month)){
            dayArray = Array(31, {i->i+1})
        }else{
            dayArray = Array(30, {i->i+1})
        }
    }

    fun isLeapYear(year: Int): Boolean{
        if(year%4==0){
            if(year%100==0){
                if(year%400==0){
                    //leap
                    //isLeap = true
                    return true
                }else{
                    //non-leap
                    //isLeap = false
                    return false
                }
            }else{
                //leap
                //isLeap = true
                return true
            }
        }else{
            //non-leap
            //isLeap = false
            return false
        }
    }

    fun dateExamine(){
        if (dateStart == false){
            return
        }

        if(year_sel < 1900 || year_sel > 2020){
            newr_bdate_tv_error.text = "정확한 연도를 입력해 주세요."
            newr_bdate_tv_error.visibility = View.VISIBLE
        }else{
            setDayArray(year_sel, month_sel)
            if(!dayArray.contains(day_sel)){
                newr_bdate_tv_error.text = "정확한 날짜를 입력해 주세요."
                newr_bdate_tv_error.visibility = View.VISIBLE
            }else{
                newr_bdate_tv_error.visibility = View.INVISIBLE
            }
        }
    }

    fun dateToString(): String{
        //val year_str = year_sel.toString()
        var month_str = ""
        var day_str = ""
        if(month_sel < 10){
            month_str = "0"+month_sel.toString()
        }else{
            month_str = month_sel.toString()
        }

        if(day_sel < 10){
            day_str = "0"+day_sel.toString()
        }else{
            day_str = day_sel.toString()
        }

        return year_sel.toString()+"-"+month_str+"-"+day_str
    }
}