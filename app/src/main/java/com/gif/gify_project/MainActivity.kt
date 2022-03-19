package com.gif.gify_project

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class MainActivity : AppCompatActivity() {
    private lateinit var gifAdapter: GifAdapter
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title="Giphy Searching Engine"
        gifAdapter = GifAdapter(mutableListOf())

        //Assign ID to values
        val rwGifs: RecyclerView = findViewById(R.id.rwGifs)
        val etSearch: EditText = findViewById(R.id.etSearchGif)
        //Assign variables
        var gifList = listOf<Gif>()
        var pagTotalCount = 0
        var pagCount = 0
        var pagOffset = 0
        var queueDone = true
        //Add gifAdapter as recycleView Adapter
        rwGifs.adapter = gifAdapter
        rwGifs.layoutManager = GridLayoutManager(this, 2)//Make a grid
        val layoutManager = rwGifs.layoutManager as GridLayoutManager//Get layout manager


        //Retrofit builder
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.giphy.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //instance for interface
        val apiCall: APICall = retrofit.create(APICall::class.java)

        //Search function
        fun searchFunction(pagination:Int){
            val searchPhrase = etSearch.text.toString()

            if(searchPhrase.isNotEmpty() && queueDone){
                queueDone = false
                val call : Call<GifHolder?>? = apiCall.getData(searchPhrase, pagination)

                call?.enqueue(object : Callback<GifHolder?> {
                    override fun onResponse(call: Call<GifHolder?>, response: Response<GifHolder?>) {
                        if (response.isSuccessful){

                            val tempList:List<Gif> = response.body()!!.data
                            pagTotalCount = response.body()!!.pagination.total_count
                            pagCount = response.body()!!.pagination.count
                            pagOffset = response.body()!!.pagination.offset

                            //Add new items to list
                            if(pagOffset == 0){
                                gifList = response.body()!!.data
                            }
                            else{
                                gifList = gifList.plus(tempList)
                            }
                            //Give items to Adapter
                            gifAdapter = GifAdapter(gifList as MutableList<Gif>)

                            rwGifs.adapter = gifAdapter
                            queueDone = true //Open Q on Success
                            layoutManager.scrollToPosition(pagOffset)//Scroll back to items
                            return
                        }
                        else{
                            queueDone = true //Open Q in case of error
                            return
                        }
                    }

                    override fun onFailure(call: Call<GifHolder?>, t: Throwable) {
                        queueDone = true //Open Q in case of error
                        return
                    }
                })
            }
        }
        //Check if User has scrolled to end
        rwGifs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                if ((newState == RecyclerView.SCROLL_STATE_IDLE) && (lastVisiblePosition + 1 == pagCount+pagOffset) && (lastVisiblePosition+1 < pagTotalCount)) {
                    searchFunction(pagCount+pagOffset)

                }
            }
        })

        //Check when text changes and start searching
        etSearch.addTextChangedListener(object : TextWatcher {
            val handler = Handler()
            var runnable: Runnable?=null

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(start+1 != count){ //  ull pointer Error
                    handler.removeCallbacks(runnable!!)
                }
            }
            override fun afterTextChanged(s: Editable?) {
                runnable = Runnable {
                    // After 0.5s of inactivity start searching
                    searchFunction(0)
                }
                handler.postDelayed(runnable!!, 500)
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        })


    }
}

