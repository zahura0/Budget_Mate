package com.example.act3test

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.personalfinancetracker.R
import com.example.personalfinancetracker.Transaction
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class UpdateTransactionActivity : AppCompatActivity() {
    private var editingTransaction: Transaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_transaction)

        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_grey))

        editingTransaction = intent.getSerializableExtra("edit_transaction") as? Transaction

        val cardContainer = findViewById<LinearLayout>(R.id.cardContainer)
        val borderView = findViewById<View>(R.id.borderView)
        val typeGroup = findViewById<RadioGroup>(R.id.typeGroup)
        val dateInput = findViewById<EditText>(R.id.dateInput)
        val titleInput = findViewById<EditText>(R.id.titleInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)
        val saveBtn = findViewById<Button>(R.id.saveBtn)
        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)

        val categories = arrayOf("Food", "Transport", "Bills", "Entertainment")
        val adapter = CategorySpinnerAdapter(this, categories)
        categorySpinner.adapter = adapter

        editingTransaction?.let { transaction ->
            titleInput.setText(transaction.title)
            amountInput.setText(String.format("%.2f", transaction.amount))
            dateInput.setText(transaction.date)
            val categoryIndex = categories.indexOf(transaction.category)
            if (categoryIndex >= 0) categorySpinner.setSelection(categoryIndex)
            if (transaction.type == "expense") {
                findViewById<RadioButton>(R.id.expenseRadio).isChecked = true
            } else {
                findViewById<RadioButton>(R.id.incomeRadio).isChecked = true
            }
        }

        fun updateBackgrounds(isExpense: Boolean) {
            val colorRes = if (isExpense) R.color.expense_border else R.color.income_border
            val borderColor = ContextCompat.getColor(this, colorRes)
            val backgroundColor = ContextCompat.getColor(this, R.color.dark_grey)

            val borderDrawable = GradientDrawable().apply {
                setColor(borderColor)
                cornerRadius = 0f
            }
            borderView.background = borderDrawable

            val cardDrawable = GradientDrawable().apply {
                setColor(backgroundColor)
                setStroke(3, borderColor)
                cornerRadius = 35f
            }
            cardContainer.background = cardDrawable
        }

        updateBackgrounds(editingTransaction?.type == "expense")
        typeGroup.setOnCheckedChangeListener { _, checkedId ->
            updateBackgrounds(checkedId == R.id.expenseRadio)
        }

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateInput.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                dateInput.setText(dateFormat.format(calendar.time))
            }, year, month, day).show()
        }

        saveBtn.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val amountText = amountInput.text.toString().trim()
            val category = categorySpinner.selectedItem?.toString()
            val date = dateInput.text.toString().trim()
            val type = if (findViewById<RadioButton>(R.id.expenseRadio).isChecked) "expense" else "income"

            if (title.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (title.length < 3 || title.length > 50) {
                Toast.makeText(this, "Title must be 3–50 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val titlePattern = Pattern.compile("^[a-zA-Z0-9 ,.-]+$")
            if (!titlePattern.matcher(title).matches()) {
                Toast.makeText(this, "Title cannot contain special characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (amountText.isEmpty()) {
                Toast.makeText(this, "Amount cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0 || amount > 1_000_000) {
                Toast.makeText(this, "Amount must be 0–1,000,000", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (amountText.contains(".") && amountText.substringAfter(".").length > 2) {
                Toast.makeText(this, "Amount cannot have more than 2 decimals", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (category == null || category !in categories) {
                Toast.makeText(this, "Select a valid category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (date.isEmpty() || !Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$").matcher(date).matches()) {
                Toast.makeText(this, "Date must be yyyy-MM-dd", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val parsedDate = dateFormat.parse(date)
                if (parsedDate == null || parsedDate.after(Calendar.getInstance().time)) {
                    Toast.makeText(this, "Invalid or future date", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val transaction = Transaction(
                id = editingTransaction?.id ?: System.currentTimeMillis(),
                title = title,
                amount = amount,
                category = category,
                date = date,
                type = type
            )
            val resultIntent = Intent().putExtra("transaction", transaction)
            setResult(RESULT_OK, resultIntent)
            Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}