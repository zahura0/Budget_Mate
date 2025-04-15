package com.example.personalfinancetracker

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FinanceRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("FinancePrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    suspend fun saveTransactions(transactions: List<Transaction>) = withContext(Dispatchers.IO) {
        val json = gson.toJson(transactions)
        prefs.edit().putString("transactions", json).apply()
    }

    suspend fun loadTransactions(): List<Transaction> = withContext(Dispatchers.IO) {
        val json = prefs.getString("transactions", null) ?: return@withContext emptyList()
        gson.fromJson(json, Array<Transaction>::class.java).toList()
    }

    fun saveBudget(budget: Float) {
        prefs.edit().putFloat("budget", budget).apply()
    }

    fun getBudget(): Float = prefs.getFloat("budget", 0f)
}