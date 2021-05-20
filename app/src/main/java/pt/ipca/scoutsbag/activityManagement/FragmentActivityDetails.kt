package pt.ipca.scoutsbag.activityManagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.scoutsteste1.ScoutActivity
import org.json.JSONObject
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils

class FragmentActivityDetails : Fragment() {

    // Global variables
    lateinit var activity: ScoutActivity

    lateinit var textViewName: TextView
    lateinit var textViewDescription: TextView
    lateinit var textViewStartDate: TextView
    lateinit var textViewEndDate: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the selected activity
        arguments?.let {
            val activityJsonStr = it.getString("activity")
            val activityJson = JSONObject(activityJsonStr)
            activity = ScoutActivity.fromJson(activityJson)
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_activity_details, container, false)

        // Send the objects in the view to the variables
        textViewName = rootView.findViewById(R.id.textView_activityDetails_name)
        textViewDescription = rootView.findViewById(R.id.textView_activityDetails_description)
        textViewStartDate = rootView.findViewById(R.id.textView_activityDetails_StartDate)
        textViewEndDate = rootView.findViewById(R.id.textView_activityDetails_EndDate)

        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fill the text Views
        textViewName.text = activity.nameActivity
        textViewDescription.text = activity.activityDescription
        textViewStartDate.text = "${Utils.mySqlDateToString(activity.startDate.toString())} - ${Utils.mySqlTimeToString(activity.startDate.toString())}"
        textViewEndDate.text = "${Utils.mySqlDateToString(activity.finishDate.toString())} - ${Utils.mySqlTimeToString(activity.finishDate.toString())}"

    }

}