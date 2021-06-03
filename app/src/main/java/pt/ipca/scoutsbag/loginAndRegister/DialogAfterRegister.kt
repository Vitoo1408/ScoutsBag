package pt.ipca.scoutsbag.loginAndRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import pt.ipca.scoutsbag.R

class DialogAfterRegister: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.dialog_after_register, container, false)
        rootView.findViewById<Button>(R.id.buttonOk).setOnClickListener {
            dismiss()
            (activity as Register).finish()
        }
        return rootView
    }

}