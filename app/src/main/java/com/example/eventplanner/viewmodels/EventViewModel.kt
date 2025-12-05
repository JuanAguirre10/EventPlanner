package com.example.eventplanner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventplanner.dao.AuthDao
import com.example.eventplanner.dao.EventDao
import com.example.eventplanner.models.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val eventDao = EventDao()
    private val authDao = AuthDao()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _eventState = MutableStateFlow<EventState>(EventState.Idle)
    val eventState: StateFlow<EventState> = _eventState

    init {
        loadEvents()
    }

    private fun loadEvents() {
        val userId = authDao.getCurrentUser()?.uid
        if (userId == null) {
            println("ERROR: Usuario no autenticado")
            return
        }

        println("Cargando eventos para userId: $userId")

        viewModelScope.launch {
            eventDao.getEventsRealTime(userId).collect { eventList ->
                println("Eventos recibidos: ${eventList.size}")
                eventList.forEach { event ->
                    println("Evento: ${event.title} - ${event.date}")
                }
                _events.value = eventList
            }
        }
    }

    fun createEvent(title: String, date: String, description: String) {
        if (title.isBlank()) {
            _eventState.value = EventState.Error("El título es obligatorio")
            return
        }

        val userId = authDao.getCurrentUser()?.uid ?: return
        val event = Event(
            userId = userId,
            title = title,
            date = date,
            description = description
        )

        _eventState.value = EventState.Loading
        viewModelScope.launch {
            val result = eventDao.createEvent(event)
            _eventState.value = if (result.isSuccess) {
                EventState.Success("Evento creado exitosamente")
            } else {
                EventState.Error(result.exceptionOrNull()?.message ?: "Error al crear evento")
            }
        }
    }

    fun updateEvent(eventId: String, title: String, date: String, description: String) {
        if (title.isBlank()) {
            _eventState.value = EventState.Error("El título es obligatorio")
            return
        }

        val userId = authDao.getCurrentUser()?.uid ?: return
        val event = Event(
            id = eventId,
            userId = userId,
            title = title,
            date = date,
            description = description
        )

        _eventState.value = EventState.Loading
        viewModelScope.launch {
            val result = eventDao.updateEvent(eventId, event)
            _eventState.value = if (result.isSuccess) {
                EventState.Success("Evento actualizado exitosamente")
            } else {
                EventState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar evento")
            }
        }
    }

    fun deleteEvent(eventId: String) {
        _eventState.value = EventState.Loading
        viewModelScope.launch {
            val result = eventDao.deleteEvent(eventId)
            _eventState.value = if (result.isSuccess) {
                EventState.Success("Evento eliminado exitosamente")
            } else {
                EventState.Error(result.exceptionOrNull()?.message ?: "Error al eliminar evento")
            }
        }
    }

    fun resetEventState() {
        _eventState.value = EventState.Idle
    }
}

sealed class EventState {
    object Idle : EventState()
    object Loading : EventState()
    data class Success(val message: String) : EventState()
    data class Error(val message: String) : EventState()
}