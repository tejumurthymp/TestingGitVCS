package com.example.coroutineexample.example2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.example.coroutineexample.R
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000
    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        job_button.setOnClickListener {
            if(!::job.isInitialized)
                initJob()
            job_progressBar.startJobOrCancel(job)
        }

        Log.i(TAG, "onCreate: hello it is info variable")
    }

    private fun initJob(){
        Log.i(TAG, "initJob: called")
        job_button.text = "Start Job #1"
        job_text.text = ""
        job = Job()
        job.invokeOnCompletion { throwable ->
            throwable?.message.let {
                var msg = it
                if(msg.isNullOrBlank()){
                    msg = "Unknown cancellation error."
                }
                println("$job was cancelled. Reason: $msg")
                showToast(msg)
            }
        }
        job_progressBar.max = PROGRESS_MAX
        job_progressBar.progress = PROGRESS_START
    }

    fun ProgressBar.startJobOrCancel(job: Job){
        if(this.progress > 0){
            println("$job is already active. Cancelling...")
            Log.i(TAG, "startJobOrCancel: cancelling...")
            resetJob()
        }else{
            job_button.text = "Cancel job #1"
            CoroutineScope(IO + job).launch {
                println("coroutine $this is activated with job $job")

                for(i in PROGRESS_START..PROGRESS_MAX){
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                withContext(Main){
                    job_text.text = "Job is complete"
                    //Log.d(TAG, "startJobOrCancel: ")
                }
                Log.d(TAG, "startJobOrCancel: called")
            }

        }
    }

    private fun resetJob(){
        if(job.isActive || job.isCompleted){
            job.cancel(CancellationException("Resetting Job"))
        }
        initJob()
    }

    fun showToast(text: String){
        GlobalScope.launch(Main){
            Toast.makeText(this@MainActivity,text,Toast.LENGTH_SHORT).show()
        }

        /*GlobalScope.launch(IO) {
            Toast.makeText(this@MainActivity,text,Toast.LENGTH_SHORT).show()
        }*/
    }
}