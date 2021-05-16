package pt.ipca.scoutsbag

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.widget.Button
import android.widget.EditText
import java.util.*

object Utils {


    /*
        This function create a Date picker
        @dateButton = the button responsible for select the date
        @context = activity that the date picker is in
     */
    fun initDatePicker(dateButton: Button, context: Context): DatePickerDialog {

        // After the user finish selecting a date, change the date in the button
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->

                // Add the date to the button
                val date = "$day/${month+1}/$year"
                dateButton.text = date

                // Call the time pop up window
                val timePickerDialog = initTimePicker(dateButton, context)
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
        @dateButton = the button responsible for select the date
        @context = activity that the date picker is in
     */
    private fun initTimePicker(dateButton: Button, context: Context): TimePickerDialog {

        // After the user finish selecting a time, join the time in the previous string
        val timeSetListener =
            TimePickerDialog.OnTimeSetListener {_, hour, minute ->
                val time = "$hour:$minute"
                "${dateButton.text} | $time".also { dateButton.text = it }
            }

        // Get the data to create the time picker
        val cal: Calendar = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR)
        val minute = cal.get(Calendar.MINUTE)
        val style: Int = AlertDialog.THEME_HOLO_LIGHT

        // Create and return the date picker
        return TimePickerDialog(context, style, timeSetListener, hour, minute, true)
    }

}