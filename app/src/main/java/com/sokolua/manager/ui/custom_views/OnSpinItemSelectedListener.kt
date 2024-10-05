package com.sokolua.manager.ui.custom_views

import android.view.View
import android.widget.AdapterView

class OnSpinItemSelectedListener(
    val itemSelected: () -> Unit
) : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        itemSelected()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}
