import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.loginAndRegister.Register

class DialogNoInternet: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.dialog_no_connection, container, false)
        rootView.findViewById<Button>(R.id.buttonOk).setOnClickListener {
            if((activity as DialogNoInternet).context?.let { it1 -> Utils.isOnline(it1) } == true) {
                dismiss()
            }
        }
        return rootView
    }

}