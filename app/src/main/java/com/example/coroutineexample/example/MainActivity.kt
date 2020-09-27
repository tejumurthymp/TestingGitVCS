package com.example.coroutineexample.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import com.example.coroutineexample.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*CoroutineScope(IO).launch {
            fakeApiCall()
        }
        
        button.setOnClickListener {
            CoroutineScope(IO).launch {
                fakeApiCall()
            }
        }*/

        fakeApiRequest()

        Log.d(TAG, "onCreate: called")
    }

    private fun fakeApiRequest(){
        val startTime = System.currentTimeMillis()
        val parentJob = CoroutineScope(IO).launch {
            val job1 = launch {
                val time1 = measureTimeMillis {
                    Log.d(TAG, "fakeApiRequest: job1 ${Thread.currentThread().name}")
                    val result1 = getResult()
                    setTextOnMainThread("Got $result1")
                }
                Log.d(TAG, "fakeApiRequest: completed job1 in $time1 ms.")
            }

            val job2 = launch {
                val time2 = measureTimeMillis {
                    Log.d(TAG, "fakeApiRequest: job2 ${Thread.currentThread().name}")
                    val result2 = getResult2()
                    setTextOnMainThread("Got $result2")
                }
                Log.d(TAG, "fakeApiRequest: completed job2 in $time2 ms.")
            }
        }
        parentJob.invokeOnCompletion {
            Log.d(TAG, "fakeApiRequest: complete parentJob: ${System.currentTimeMillis() - startTime}")
        }
    }

    private suspend fun fakeApiCall(){
        val job = withTimeoutOrNull(6100L){
            val result = getResult()
            setTextOnMainThread(result)
            Log.d(TAG, "fakeApiCall: $result , ${Thread.currentThread().name}")

            val result2 = getResult2()
            setTextOnMainThread(result2)
            Log.d(TAG, "fakeApiCall: $result2 , ${Thread.currentThread().name}")
            
        }//wait

        if(job == null){
            val text = "Cancelling a job which is less 5000 ms"
            Log.e(TAG, "fakeApiCall: Cancelling a job which is less 5000 ms" )
            setTextOnMainThread(text)
        }
    }

    private suspend fun setTextOnMainThread(data: String){
        withContext(Main){
            textView.text = textView.text.toString() + "\n $data"
        }
    }

    private suspend fun getResult():String{
        logThread("getResult")
        delay(3000)
        return RESULT_1
    }

    private suspend fun getResult2():String{
        logThread("getResult2")
        delay(3000)
        return RESULT_2
    }

    private fun logThread(methodName: String){
        Log.d( "logThread: ","$methodName , ${Thread.currentThread().name}" )
    }

}