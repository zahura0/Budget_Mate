package com.example.personalfinancetracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FinanceRepository(application)
    private val _transactions = MutableLiveData<List<Transaction>>(emptyList())
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private var selectedYear: Int? = null
    private var selectedMonth: Int? = null // Month is 0-based (0 = January, 11 = December)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val allTransactions = repository.loadTransactions()
            _transactions.postValue(filterTransactions(allTransactions))
        }
    }

    fun setFilter(year: Int?, month: Int?) {
        selectedYear = year
        selectedMonth = month
        viewModelScope.launch {
            val allTransactions = repository.loadTransactions()
            _transactions.postValue(filterTransactions(allTransactions))
        }
    }

    private fun filterTransactions(transactions: List<Transaction>): List<Transaction> {
        return transactions.filter { transaction ->
            try {
                // Parse the date string (assuming format "yyyy-MM-dd")
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val date = dateFormat.parse(transaction.date) ?: return@filter false
                val calendar = Calendar.getInstance().apply {
                    time = date
                }
                val transactionYear = calendar.get(Calendar.YEAR)
                val transactionMonth = calendar.get(Calendar.MONTH) // 0-based

                val yearMatches = selectedYear?.let { it == transactionYear } ?: true
                val monthMatches = selectedMonth?.let { it == transactionMonth } ?: true

                yearMatches && monthMatches
            } catch (e: Exception) {
                // Log error for debugging
                println("Invalid date format for transaction ${transaction.id}: ${transaction.date}")
                false // Skip transactions with invalid date formats
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        val updatedList = (_transactions.value ?: emptyList()) + transaction
        _transactions.postValue(filterTransactions(updatedList))
        viewModelScope.launch { repository.saveTransactions(updatedList) }
    }

    fun addTransactions(newTransactions: List<Transaction>) {
        val updatedList = (_transactions.value ?: emptyList()) + newTransactions
        _transactions.postValue(filterTransactions(updatedList))
        viewModelScope.launch { repository.saveTransactions(updatedList) }
    }

    fun updateTransaction(updatedTransaction: Transaction) {
        val updatedList = (_transactions.value ?: emptyList()).map {
            if (it.id == updatedTransaction.id) updatedTransaction else it
        }
        _transactions.postValue(filterTransactions(updatedList))
        viewModelScope.launch { repository.saveTransactions(updatedList) }
    }

    fun deleteTransaction(transaction: Transaction) {
        val updatedList = (_transactions.value ?: emptyList()).filter { it.id != transaction.id }
        _transactions.postValue(filterTransactions(updatedList))
        viewModelScope.launch { repository.saveTransactions(updatedList) }
    }

    fun getCategorySpending(): Map<String, Double> {
        return transactions.value?.filter { it.type == "expense" }
            ?.groupBy { it.category }
            ?.mapValues { it.value.sumOf { t -> t.amount } } ?: emptyMap()
    }

    fun setBudget(budget: Float) = repository.saveBudget(budget)
    fun getBudget() = repository.getBudget()
}