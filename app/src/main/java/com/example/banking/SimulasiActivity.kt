package com.example.banking

import android.annotation.SuppressLint
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "bank.tflite"

    private lateinit var resultText: TextView
    private lateinit var age: EditText
    private lateinit var job: EditText
    private lateinit var marital: EditText
    private lateinit var education: EditText
    private lateinit var month: EditText
    private lateinit var day_of_week: EditText
    private lateinit var duration: EditText
    private lateinit var campaign: EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi)

        resultText = findViewById(R.id.txtResult)
        age = findViewById(R.id.age)
        job = findViewById(R.id.job)
        marital = findViewById(R.id.marital)
        education = findViewById(R.id.education)
        month = findViewById(R.id.month)
        day_of_week = findViewById(R.id.day_of_week)
        duration = findViewById(R.id.duration)
        campaign = findViewById(R.id.campaign)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                age.text.toString(),
                job.text.toString(),
                marital.text.toString(),
                education.text.toString(),
                month.text.toString(),
                day_of_week.text.toString(),
                duration.text.toString(),
                campaign.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Failure"
                }else if (result == 1){
                    resultText.text = "Nonexistent"
                }else if (result == 2){
                    resultText.text = "Success"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(9)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String, input8: String): Int{
        val inputVal = FloatArray(8)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        inputVal[7] = input8.toFloat()
        val output = Array(1) { FloatArray(3) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}