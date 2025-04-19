package com.example.personalfinancetracker

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
import com.example.act3test.CategorySpinnerAdapter
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class AddTransactionActivity : AppCompatActivity() {
    private var editingTransaction: Transaction? = null // Store transaction being edited

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Set the activity background to #131313
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_grey))

        // Check if editing a transaction
        editingTransaction = intent.getSerializableExtra("edit_transaction") as? Transaction

        // Set up the category spinner
        val categories = arrayOf("Food", "Transport", "Bills", "Entertainment")
        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val adapter = com.example.act3test.CategorySpinnerAdapter(this, categories)
        categorySpinner.adapter = adapter

        // Set up views
        val cardContainer = findViewById<LinearLayout>(R.id.cardContainer)
        val borderView = findViewById<View>(R.id.borderView)
        val typeGroup = findViewById<RadioGroup>(R.id.typeGroup)
        val dateInput = findViewById<EditText>(R.id.dateInput)
        val titleInput = findViewById<EditText>(R.id.titleInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)
        val saveBtn = findViewById<Button>(R.id.saveBtn)

        // Update heading based on mode
        val headingText = findViewById<android.widget.TextView>(R.id.transactionHeading)
        headingText.text = if (editingTransaction != null) "Edit Transaction" else "Add Transaction"

        // Prefill fields if editing
        editingTransaction?.let { transaction ->
            titleInput.setText(transaction.title)
            amountInput.setText(transaction.amount.toString())
            dateInput.setText(transaction.date)
            // Set spinner selection
            val categoryIndex = categories.indexOf(transaction.category)
            if (categoryIndex >= 0) categorySpinner.setSelection(categoryIndex)
            // Set radio button
            if (transaction.type == "expense") {
                findViewById<RadioButton>(R.id.expenseRadio).isChecked = true
            } else {
                findViewById<RadioButton>(R.id.incomeRadio).isChecked = true
            }
        }

        // Function to update backgrounds on selected type
        fun updateBackgrounds(isExpense: Boolean) {
            val colorRes = if (isExpense) R.color.expense_border else R.color.income_border
            val borderColor = ContextCompat.getColor(this, colorRes)
            val backgroundColor = ContextCompat.getColor(this, R.color.dark_grey)

            // Update borderView background
            val borderDrawable = GradientDrawable().apply {
                setColor(borderColor)
                cornerRadius = 0f
            }
            borderView.background = borderDrawable

            // Update cardContainer background
            val cardDrawable = GradientDrawable().apply {
                setColor(backgroundColor)
                setStroke(3, borderColor)
                cornerRadius = 35f
            }
            cardContainer.background = cardDrawable

            // Set CardView background
            val cardView = findViewById<androidx.cardview.widget.CardView>(R.id.cardView)
            cardView?.setCardBackgroundColor(backgroundColor)
            cardView?.cardElevation = 6f
        }

        // Set initial background
        updateBackgrounds(
            editingTransaction?.type?.equals("expense") ?: findViewById<RadioButton>(R.id.expenseRadio).isChecked
        )

        // Update background when radio button changes
        typeGroup.setOnCheckedChangeListener { _, checkedId ->
            updateBackgrounds(checkedId == R.id.expenseRadio)
        }

        // Set up date picker
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

        // Handle save button click with validations
        saveBtn.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val amountText = amountInput.text.toString().trim()
            val category = categorySpinner.selectedItem?.toString()
            val date = dateInput.text.toString().trim()
            val type = if (findViewById<RadioButton>(R.id.expenseRadio).isChecked) "expense" else "income"

            // Title validations
            if (title.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (title.length < 3 || title.length > 50) {
                Toast.makeText(this, "Title must be 3–50 characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val titlePattern = Pattern.compile("^[a-zA-Z0-9 ,.-]+$")
            if (!titlePattern.matcher(title).matches()) {
                Toast.makeText(this, "Title cannot contain special characters like @, #, $, etc.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Amount validations
            if (amountText.isEmpty()) {
                Toast.makeText(this, "Amount cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val amount = amountText.toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(this, "Amount must be a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (amount > 1_000_000) {
                Toast.makeText(this, "Amount cannot exceed 1,000,000", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Check for more than 2 decimal places
            if (amountText.contains(".") && amountText.substringAfter(".").length > 2) {
                Toast.makeText(this, "Amount cannot have more than 2 decimal places", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Category validation
            if (category == null || category !in categories) {
                Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Date validations
            if (date.isEmpty()) {
                Toast.makeText(this, "Date cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val datePattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$")
            if (!datePattern.matcher(date).matches()) {
                Toast.makeText(this, "Date must be in yyyy-MM-dd format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val parsedDate = dateFormat.parse(date)
                if (parsedDate == null) {
                    Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val currentDate = Calendar.getInstance().time
                if (parsedDate.after(currentDate)) {
                    Toast.makeText(this, "Date cannot be in the future", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Type validation (redundant but for robustness)
            if (type != "expense" && type != "income") {
                Toast.makeText(this, "Please select a valid transaction type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // All validations passed, create and save transaction
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
            // Show success Toast based on type
            val toastMessage = if (type == "expense") "Expense added successfully" else "Income added successfully"
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}