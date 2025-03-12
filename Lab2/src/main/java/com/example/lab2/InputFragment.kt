package com.example.lab2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import android.graphics.Color
import android.widget.Toast

class InputFragment : Fragment() {

    private lateinit var colorRadioGroup: RadioGroup
    private lateinit var inputText: EditText
    private lateinit var okButton: Button
    private lateinit var cancelButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        colorRadioGroup = view.findViewById(R.id.colorRadioGroup)
        inputText = view.findViewById(R.id.inputText)
        okButton = view.findViewById(R.id.okButton)
        cancelButton = view.findViewById(R.id.cancelButton)

        okButton.setOnClickListener {
            val text = inputText.text.toString()
            val selectedColorId = colorRadioGroup.checkedRadioButtonId
            val selectedColor = when (selectedColorId) {
                R.id.redRadioButton -> Color.RED
                R.id.greenRadioButton -> Color.GREEN
                R.id.blueRadioButton -> Color.BLUE
                else -> null
            }

            if (text.isEmpty() || selectedColor == null) {
                // Якщо не введено текст або не вибрано колір, показуємо повідомлення
                Toast.makeText(requireContext(), "Будь ласка, введіть текст та виберіть колір", Toast.LENGTH_SHORT).show()
            } else {
                val resultFragment = ResultFragment.newInstance(text, selectedColor)
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainer, resultFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }

        cancelButton.setOnClickListener {
            inputText.text.clear()
            colorRadioGroup.clearCheck()
        }
    }
}