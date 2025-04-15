package com.example.personalfinancetracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.util.regex.Pattern

class SettingsActivity : AppCompatActivity() {

    private val MAX_BUDGET = 1_000_000.00
    private val DECIMAL_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$") // Up to 2 decimals

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val viewModel: FinanceViewModel by viewModels()
        val budgetInput = findViewById<EditText>(R.id.budgetInput)
        val saveBtn = findViewById<Button>(R.id.saveBudgetBtn)

        saveBtn.setOnClickListener {
            val budgetText = budgetInput.text.toString().trim()

            if (budgetText.isEmpty()) {
                showToast("Budget cannot be empty")
                return@setOnClickListener
            }

            val budget = budgetText.toDoubleOrNull()
            if (budget == null) {
                showToast("Budget must be a valid number")
                return@setOnClickListener
            }

            if (budget <= 0) {
                showToast("Budget must be greater than 0")
                return@setOnClickListener
            }

            if (budget > MAX_BUDGET) {
                showToast("Budget cannot exceed 1,000,000")
                return@setOnClickListener
            }

            if (!DECIMAL_PATTERN.matcher(budgetText).matches()) {
                showToast("Budget must be a valid number with up to 2 decimal places")
                return@setOnClickListener
            }

            // Format to exactly two decimal places
            val formattedBudget = DecimalFormat("0.00").format(budget)

            // Save budget as Float and show nicely formatted toast
            viewModel.setBudget(formattedBudget.toFloat())
            showToast("Budget set to LKR $formattedBudget")
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
