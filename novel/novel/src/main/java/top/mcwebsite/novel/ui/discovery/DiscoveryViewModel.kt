package top.mcwebsite.novel.ui.discovery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DiscoveryViewModel : ViewModel() {

    val searchContent = MutableLiveData<String>()

    val backVisibleState = MutableLiveData(false)

    val searchBtnVisibleState = MutableLiveData(false)

    private val _backEvent = MutableLiveData<Unit>()
    val backEvent : LiveData<Unit> = _backEvent

    fun showOrHideSearchView(show: Boolean) {
        backVisibleState.value = show
        searchBtnVisibleState.value = show
    }

    fun back() {
        _backEvent.value = Unit
        showOrHideSearchView(false)
    }

}