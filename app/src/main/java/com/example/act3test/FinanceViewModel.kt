package com.example.personalfinancetracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FinanceRepository(application)
    private val _transactions = MutableLiveData<List<Transaction>>(emptyList())
    val transactions: LiveData<List<Transaction>> get() = _transactions

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _transactions.postValue(repository.loadTransactions())
        }
    }

    fun addTransaction(transaction: Transaction) {
        val updatedList = (_transactions.value ?: emptyList()) + transaction
        _transactions.postValue(updatedList)
        viewModelScope.launch { repository.saveTransactions(updatedList) }
    }

    fun addTransactions(newTransactions: List<Transaction>) {
        val updatedList = (_transactions.value ?: emptyList()) + newTransactions
        _transactions.postValue(updatedList)
        viewModelScope.launch { repository.saveTransactions(updatedList) }
    }

    fun updateTransaction(updatedTransaction: Transaction) {
        val updatedList = (_transactions.value ?: emptyList()).map {
            if (it.id == updatedTransaction.id) updatedTransaction else it
        }
        _transactions.postValue(updatedList)
        viewModelScope.launch { repository.saveTransactions(updatedList) }
    }

    fun deleteTransaction(transaction: Transaction) {
        val updatedList = (_transactions.value ?: emptyList()).filter { it.id != transaction.id }
        _transactions.postValue(updatedList)
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