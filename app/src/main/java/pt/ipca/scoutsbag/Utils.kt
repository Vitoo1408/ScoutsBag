package pt.ipca.scoutsbag

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.TextView
import com.example.scoutsteste1.ScoutActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.util.*
import java.util.regex.Pattern

object Utils {

    /*
        This function returns true if the activity is older than the current date.
     */
    fun outdatedActivity(activity: ScoutActivity): Boolean {

        // Variables
        val activityDate: String = activity.finishDate!!
        val c: Calendar = Calendar.getInstance()

        // Activity Date
        val aDay   = getDay(activityDate).toInt()
        val aMonth = getMonth(activityDate).toInt()
        val aYear  = getYear(activityDate).toInt()

        // Current Date
        val cDay   = c.get(Calendar.DAY_OF_MONTH)
        val cMonth = c.get(Calendar.MONTH) + 1
        val cYear  = c.get(Calendar.YEAR)

        // Check if it is outdated
        return if (cYear > aYear) {
            true
        } else if (cYear == aYear && cMonth > aMonth) {
            true
        } else (cYear == aYear && cMonth == aMonth && cDay > aDay)

    }


    /*
        This function create a Date picker
        @dateTextView = the textView responsible for select the date
        @context = activity that the date picker is in
     */
    fun initDatePicker(dateTextView: TextView, context: Context): DatePickerDialog {

        // After the user finish selecting a date, change the date in the button
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->

                // Add the date to the button
                val date = "$day/${month+1}/$year"
                dateTextView.text = date

                // Call the time pop up window
                val timePickerDialog = initTimePicker(dateTextView, context)
                timePickerDialog.show()
            }

        // Get the data to create the date picker
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        val style: Int = AlertDialog.THEME_HOLO_LIGHT

        // Create and return the date picker
        return DatePickerDialog(context, style, dateSetListener, year, month, day)
    }


    /*
        This function create a Time picker and join the time with the date previous selected
        @dateTextView = the textView responsible for select the date
        @context = activity that the date picker is in
     */
    private fun initTimePicker(dateTextView: TextView, context: Context): TimePickerDialog {

        // After the user finish selecting a time, join the time in the previous string
        val timeSetListener =
            TimePickerDialog.OnTimeSetListener {_, hour, minute ->
                val time = "$hour:$minute"
                "${dateTextView.text} - $time".also { dateTextView.text = it }
            }

        // Get the data to create the time picker
        val cal: Calendar = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR)
        val minute = cal.get(Calendar.MINUTE)
        val style: Int = AlertDialog.THEME_HOLO_LIGHT

        // Create and return the date picker
        return TimePickerDialog(context, style, timeSetListener, hour, minute, true)
    }


    /*
        This function convert mySqlDateTime to an date string
        @mysqlDateTime = mysql value
     */
    fun mySqlDateToString(mysqlDateTime: String): String {

        // Split the string into date
        val strArray = Pattern.compile("T").split(mysqlDateTime)

        // Only return the first element, which is the date
        return strArray[0]
    }


    /*
        This function convert mySqlDateTime to an time string
        @mysqlDateTime = mysql value
     */
    fun mySqlTimeToString(mysqlDateTime: String): String {

        // Split the string into date and time
        val strArray = Pattern.compile("T").split(mysqlDateTime)

        // Split the time into hours, minutes, seconds and millis
        val strArray2 = Pattern.compile(":").split(strArray[1])

        // Only return the hours and the minutes
        return "${strArray2[0]}:${strArray2[1]}"
    }


    /*
        This function convert mySqlDateTime to an date string
        @mysqlDateTime = mysql value
     */
    fun mySqlDateTimeToString(mysqlDateTime: String): String {

        // Get the date and the time
        val date = mySqlDateToString(mysqlDateTime)
        val time = mySqlTimeToString(mysqlDateTime)

        // Return the two
        return "$date - $time"
    }


    /*
        This function convert conventional Date and Time format to a format that mySql accept
        @dateTime = conventional dateTime
     */
    fun dateTimeToMySql(dateTime: String): String {

        // Split the dateTime in date and time
        val strDateTimeArray = Pattern.compile(" - ").split(dateTime, 2)
        val date = strDateTimeArray[0]
        val time = strDateTimeArray[1]

        // Split the Date in day, month and year
        val strDateArray = Pattern.compile("/").split(date, 3)
        val day: String = strDateArray[0]
        val month: String = strDateArray[1]
        val year: String = strDateArray[2]

        // Split the Time in hours and min
        val strTimeArray = Pattern.compile(":").split(time, 2)
        val hour: String = strTimeArray[0]
        val min: String = strTimeArray[1]

        return "$year-$month-$day $hour:$min:00"
    }


    /*
        This function convert default mySqlDate to a European date format
        @date = default date
     */
    fun changeDateFormat(date: String): String {

        // String Array
        val strArray = Pattern.compile("-").split(date)

        // Date variables
        val day: String = strArray[2]
        val month: String = strArray[1]
        val year: String = strArray[0]

        return "$day/$month/$year"
    }


    /*
        This function split the date and return only the day
        @date = default date
     */
    fun getDay(date: String): String {

        // Split the date
        val strArray = Pattern.compile("-").split(date)
        val strArray2 = Pattern.compile("T").split(strArray[2])

        return strArray2[0]
    }


    /*
        This function split the date and return only the month in text
        @date = default date
     */
    fun getMonth(date: String): String {

        // Split the date
        val strArray = Pattern.compile("-").split(date)

        return strArray[1].toString()
    }


    /*
        This function split the date and return only the year in text
        @date = default date
     */
    fun getYear(date: String): String {

        // Split the date
        val strArray = Pattern.compile("-").split(date)

        return strArray[0].toString()
    }


    /*
        This function return the month value in text
        @month = month id
     */
    fun getMonthFormat(month: Int): String {
        return when (month) {
            1 -> "JAN"
            2 -> "FEB"
            3 -> "MAR"
            4 -> "ABR"
            5 -> "MAI"
            6 -> "JUN"
            7 -> "JUL"
            8 -> "AGO"
            9 -> "SET"
            10 -> "OUT"
            11 -> "NOV"
            12 -> "DEC"
            else -> "none"
        }
    }

    /*
        This function returns the path of the selected file
        @filePath = file path
     */
    fun getUriFilePath(context: Context, data: Uri): String? {
        var filePath: String? = null
        val uriPathHelper = URIPathHelper()
        filePath = uriPathHelper.getPath(context, data)
        return filePath
    }

    /*
        This function returns the select file name
        @fileName = file name
     */
    fun getFileName(context: Context, data: Uri): String? {
        var fileName: String? = null
        data?.let { returnUri ->
            context.contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }

        return fileName
    }

    /*
        This function returns the uploaded image URL to S3
        @imageUrl = image URL
     */
    fun uploadImage(filePath: String, imageName: String?, callBack: (String)->Unit) {
        val MEDIA_TYPE_IMAGE = "image/*".toMediaType()
        var imageUrl: String? = null


        // Prepare the from body request
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", imageName, File(filePath)
                .asRequestBody(MEDIA_TYPE_IMAGE))
            .build()

        // Build the request
        val request = Request.Builder()
            .url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/upload")
            .post(requestBody)
            .build()

        // Send the request and verify the response
        OkHttpClient().newCall(request).execute().use { response ->
            if (response.message == "OK") {
                val messageBodyJsonStr = response.body?.string().toString()
                val messageJsonObject = JSONObject(messageBodyJsonStr)
                imageUrl = messageJsonObject.getString("Location")
            }
        }

        callBack(imageUrl!!)
    }
}














