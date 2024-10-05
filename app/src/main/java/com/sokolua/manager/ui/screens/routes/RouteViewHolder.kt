package com.sokolua.manager.ui.screens.routes

import android.view.View
import androidx.core.util.Consumer
import androidx.core.view.isInvisible
import androidx.viewbinding.ViewBinding
import com.sokolua.manager.R
import com.sokolua.manager.data.storage.realm.CustomerRealm
import com.sokolua.manager.data.storage.realm.VisitRealm
import com.sokolua.manager.databinding.EmptyListItemBinding
import com.sokolua.manager.databinding.RouteListHeaderBinding
import com.sokolua.manager.databinding.RouteListItemBinding
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter.ReactiveViewHolder
import com.sokolua.manager.utils.App

class RouteViewHolder<B : ViewBinding>(
    itemView: View,
    private val binding: B,
    private val checkInFun: Consumer<VisitRealm>? = null,
    private val openMapFun: Consumer<CustomerRealm>? = null,
    private val openCallFun: Consumer<CustomerRealm>? = null,
    private val openCustomerFun: Consumer<CustomerRealm>? = null,
) :
    ReactiveViewHolder<RouteListItem>(itemView) {

    override fun setCurrentItem(currentItem: RouteListItem) {
        super.setCurrentItem(currentItem)

        when {
            binding is EmptyListItemBinding -> binding.emptyListText.text =
                App.getStringRes(R.string.routes_no_route)

            currentItem.isHeader && (binding is RouteListHeaderBinding) -> binding.itemHeaderText.text =
                currentItem.headerText

            !currentItem.isHeader && (binding is RouteListItemBinding) -> {
                if (currentItem.customer != null && currentItem.customer.isValid && currentItem.visit != null && currentItem.visit.isValid) {
                    with(binding) {
                        checkInImg.apply {
                            setColorFilter(
                                when {
                                    currentItem.visit.isToSync -> App.getColorRes(R.color.color_orange)
                                    currentItem.visit.isDone -> App.getColorRes(R.color.color_green)
                                    else -> App.getColorRes(R.color.color_red)
                                }
                            )
                            if (currentItem.visit != null && !currentItem.visit.isDone) {
                                setOnClickListener { checkInFun?.accept(currentItem.visit) }
                                itemView.setOnClickListener { checkInFun?.accept(currentItem.visit) }
                            }
                        }
                        if (currentItem.customer != null) {
                            customerNameText.apply {
                                text = currentItem.customer.name
                                setOnClickListener { openCustomerFun?.accept(currentItem.customer) }
                            }
                            mapPinImg.apply {
                                isInvisible = currentItem.customer.address.isEmpty()
                                setOnClickListener { openMapFun?.accept(currentItem.customer) }
                            }
                            callImg.apply {
                                isInvisible = currentItem.customer.phone.isEmpty()
                                setOnClickListener { openCallFun?.accept(currentItem.customer) }
                            }
                        }
                    }
                }
            }

            else -> Unit
        }
    }
}
