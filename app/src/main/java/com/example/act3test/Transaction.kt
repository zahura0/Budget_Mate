package com.example.personalfinancetracker

import java.io.Serializable

data class Transaction(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val amount: Double,
    val category: String,
    val date: String,
    val type: String = "expense"
) : Serializable