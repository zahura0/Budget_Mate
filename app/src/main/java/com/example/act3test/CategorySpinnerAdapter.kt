package com.example.act3test

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.personalfinancetracker.R

class CategorySpinnerAdapter(
    context: Context,
    private val categories: Array<String>
) : ArrayAdapter<String>(context, R.layout.spinner_item, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.spinner_item, parent, false
        )
        val textView = view.findViewById<TextView>(android.R.id.text1)
        if (textView != null) {
            val category = categories.getOrNull(position) ?: "Unknown"
            textView.text = category

            // Apply category highlight background for selected item
            val categoryBackground = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 0f // No round corners
                setPadding(20, 10, 20, 10) // Increased for spaciousness
                setStroke(1, ContextCompat.getColor(context, R.color.grey_800)) // Match dropdown border
            }
            val colorResId = when (category) {
                "Food" -> R.color.highlight_food
                "Transport" -> R.color.highlight_transport
                "Bills" -> R.color.highlight_bills
                "Entertainment" -> R.color.highlight_entertainment
                else -> R.color.grey_600
            }
            try {
                val color = ContextCompat.getColor(context, colorResId)
                categoryBackground.setColor(color)
                textView.background = categoryBackground
                textView.invalidate()
                Log.d("CategorySpinnerAdapter", "getView: Set $category with color $colorResId for position $position")
            } catch (e: Exception) {
                Log.e("CategorySpinnerAdapter", "Failed to set color for $category: $colorResId in getView", e)
            }
        } else {
            Log.e("CategorySpinnerAdapter", "TextView null in spinner_item for position $position")
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.spinner_dropdown_item, parent, false
        )
        val textView = view.findViewById<TextView>(android.R.id.text1)
        if (textView != null) {
            val category = categories.getOrNull(position) ?: "Unknown"
            textView.text = category

            // Apply category highlight background
            val categoryBackground = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 0f // No round corners
                setPadding(20, 10, 20, 10) // Increased for spaciousness
                setStroke(1, ContextCompat.getColor(context, R.color.grey_800)) // Subtle border
            }
            val colorResId = when (category) {
                "Food" -> R.color.highlight_food
                "Transport" -> R.color.highlight_transport
                "Bills" -> R.color.highlight_bills
                "Entertainment" -> R.color.highlight_entertainment
                else -> R.color.grey_600
            }
            try {
                val color = ContextCompat.getColor(context, colorResId)
                categoryBackground.setColor(color)
                textView.background = categoryBackground
                textView.invalidate()
                Log.d("CategorySpinnerAdapter", "getDropDownView: Set $category with color $colorResId for position $position")
            } catch (e: Exception) {
                Log.e("CategorySpinnerAdapter", "Failed to set color for $category: $colorResId in getDropDownView", e)
            }
        } else {
            Log.e("CategorySpinnerAdapter", "TextView null in spinner_dropdown_item for position $position")
        }
        return view
    }
}