package com.example.theoraclesplate.ui.viewmodel

import com.example.theoraclesplate.model.Order

sealed class HistoryEvent {
    data class Reorder(val order: Order) : HistoryEvent()
    data class CancelOrder(val orderId: String) : HistoryEvent()
}
