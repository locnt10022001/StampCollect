package com.stampcollect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stampcollect.data.repository.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {
    
    protected fun <T> wrapFlowInResource(flow: Flow<T>): Flow<Resource<T>> {
        return flow
            .map { data -> Resource.Success(data) as Resource<T> }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e)) }
    }

    protected fun <T> executeWithResource(
        stateFlow: MutableStateFlow<Resource<T>>,
        action: suspend () -> T
    ) {
        viewModelScope.launch {
            stateFlow.value = Resource.Loading()
            try {
                val result = action()
                stateFlow.value = Resource.Success(result)
            } catch (e: Exception) {
                stateFlow.value = Resource.Error(e)
            }
        }
    }

    fun delayAction(millis: Long, onNext: () -> Unit) {
        viewModelScope.launch {
            delay(millis)
            onNext()
        }
    }
}
