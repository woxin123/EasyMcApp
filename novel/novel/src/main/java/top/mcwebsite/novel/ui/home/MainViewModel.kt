package top.mcwebsite.novel.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _bottomNavigationStatus = MutableStateFlow(true)

    val bottomNavigationStatus = _bottomNavigationStatus.asStateFlow()


    fun changeBottomNavigationStatus(isShow: Boolean) {
        viewModelScope.launch {
            _bottomNavigationStatus.emit(isShow)
        }
    }
}