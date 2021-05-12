package com.cst.weatherapptest.util.listeners

import android.text.Editable
import android.text.TextWatcher

class SearchInputListener : TextWatcher {
    private var searchTermEntered: (() -> Unit)? = null
    private var searchTermCleared: (() -> Unit)? = null

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(code: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (code.isNullOrEmpty().not()) {
            searchTermEntered?.invoke()
        } else {
            searchTermCleared?.invoke()
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    fun searchTermEntered(func: () -> Unit) {
        searchTermEntered = func
    }

    fun searchTermCleared(func: () -> Unit) {
        searchTermCleared = func
    }
}
