package com.example.personalfinancetracker

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.DecimalFormat

class AnalysisActivity : AppCompatActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var totalIncomeText: TextView
    private lateinit var totalExpenseText: TextView
    private lateinit var budgetText: TextView
    private lateinit var availableFundsText: TextView
    private lateinit var analysisText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)

        val viewModel: FinanceViewModel by viewModels()
        analysisText = findViewById(R.id.analysisText)
        pieChart = findViewById(R.id.pieChart)

        // Initialize TextViews
        totalIncomeText = findViewById(R.id.totalIncomeText)
        totalExpenseText = findViewById(R.id.totalExpenseText)
        budgetText = findViewById(R.id.budgetText)
        availableFundsText = findViewById(R.id.availableFundsText)

        // Set custom font
        val poppinsMedium = ResourcesCompat.getFont(this, R.font.poppins_medium)
        analysisText.typeface = poppinsMedium

        viewModel.transactions.observe(this) { transactions ->
            val spending = viewModel.getCategorySpending()
            val budget = viewModel.getBudget()

            val icons = mapOf(
                "Food" to "🍽️",
                "Transport" to "🚗",
                "Bills" to "📑",
                "Entertainment" to "🎮"
            )

            val summaryText = SpannableStringBuilder("📊 Category-wise Spending:\n\n")
            val decimalFormat = DecimalFormat("#,###.00")

            spending.entries.forEach { (category, amount) ->
                val emoji = icons[category] ?: "💰"
                val formattedAmount = decimalFormat.format(amount)
                val categoryLine = "$emoji $category: LKR $formattedAmount\n"

                val start = summaryText.length
                summaryText.append(categoryLine)
                val end = summaryText.length

                // Highlight only the numeric amount (after "LKR ")
                val amountStart = summaryText.indexOf(formattedAmount, start)
                if (amountStart != -1) {
                    summaryText.setSpan(
                        ForegroundColorSpan(Color.YELLOW),
                        amountStart,
                        amountStart + formattedAmount.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    summaryText.setSpan(
                        StyleSpan(android.graphics.Typeface.BOLD),
                        amountStart,
                        amountStart + formattedAmount.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            analysisText.text = summaryText

            // Calculate totals
            val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
            val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
            val availableFunds = budget + totalIncome - totalExpense

            // Format amounts with thousands separator
            val formattedBudget = decimalFormat.format(budget)
            val formattedIncome = decimalFormat.format(totalIncome)
            val formattedExpense = decimalFormat.format(totalExpense)
            val formattedAvailable = decimalFormat.format(availableFunds)

            // Color setup
            val incomeColor = ContextCompat.getColor(this, R.color.income_border)
            val expenseColor = ContextCompat.getColor(this, R.color.expense_border)
            val budgetColor = ContextCompat.getColor(this, R.color.warning)
            val availableFundsColor = if (availableFunds >= 0) Color.GREEN else Color.RED

            // Create styled text for each metric
            fun createStyledText(prefix: String, amount: String, color: Int): SpannableString {
                val fullText = "$prefix LKR $amount"
                val spannable = SpannableString(fullText)
                val amountStart = fullText.indexOf(amount)
                if (amountStart != -1) {
                    spannable.setSpan(
                        ForegroundColorSpan(color),
                        amountStart,
                        amountStart + amount.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannable.setSpan(
                        StyleSpan(android.graphics.Typeface.BOLD),
                        amountStart,
                        amountStart + amount.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                return spannable
            }

            // Set styled text for each card
            budgetText.text = createStyledText("💰 Monthly Budget:", formattedBudget, budgetColor)
            totalIncomeText.text = createStyledText("💵 Total Income:", formattedIncome, incomeColor)
            totalExpenseText.text = createStyledText("💸 Total Expense:", formattedExpense, expenseColor)
            availableFundsText.text = createStyledText("⚖️ Available Funds:", formattedAvailable, availableFundsColor)

            // Pie chart setup
            val categoryColors = mapOf(
                "Food" to ContextCompat.getColor(this, R.color.highlight_food_pie),
                "Transport" to ContextCompat.getColor(this, R.color.highlight_transport_pie),
                "Bills" to ContextCompat.getColor(this, R.color.highlight_bills_pie),
                "Entertainment" to ContextCompat.getColor(this, R.color.highlight_entertainment_pie)
            )

            val entries = mutableListOf<PieEntry>()
            val colors = mutableListOf<Int>()

            spending.forEach { (category, amount) ->
                entries.add(PieEntry(amount.toFloat(), category))
                colors.add(categoryColors[category] ?: ColorTemplate.MATERIAL_COLORS.random())
            }

            val dataSet = PieDataSet(entries, "").apply {
                this.colors = colors
                sliceSpace = 3f
                valueTextColor = Color.WHITE
                valueTextSize = 14f
            }

            pieChart.apply {
                data = PieData(dataSet)
                setUsePercentValues(false)
                setEntryLabelColor(Color.WHITE)
                setEntryLabelTextSize(12f)
                setCenterText("Spending\nBreakdown")
                setCenterTextSize(18f)
                setCenterTextColor(Color.WHITE)
                setHoleColor(ContextCompat.getColor(this@AnalysisActivity, R.color.dark_grey))
                description.isEnabled = false
                legend.isEnabled = true
                legend.textColor = Color.WHITE
                animateY(1000)
                invalidate()
            }
        }
    }
}