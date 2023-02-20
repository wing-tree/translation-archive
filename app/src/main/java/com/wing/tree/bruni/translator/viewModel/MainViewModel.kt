package com.wing.tree.bruni.translator.viewModel

import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient.ProductType
import com.wing.tree.bruni.billing.BillingService
import com.wing.tree.bruni.billing.BillingService.Companion.purchased
import com.wing.tree.bruni.core.useCase.getOrDefault
import com.wing.tree.bruni.translator.data.billing.ProductId
import com.wing.tree.bruni.translator.domain.model.History
import com.wing.tree.bruni.translator.domain.useCase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    charactersUseCase: CharactersUseCase,
    translateUseCase: TranslateUseCase,
    private val adsRemovedUseCase: AdsRemovedUseCase,
    private val billingService: BillingService,
    private val clearAllHistoriesUseCase: ClearAllHistoriesUseCase,
    private val sourceUseCase: SourceUseCase,
    private val targetUseCase: TargetUseCase
) : TranslatorViewModel(
    charactersUseCase = charactersUseCase,
    translateUseCase = translateUseCase,
    sourceUseCase = sourceUseCase,
    targetUseCase = targetUseCase
) {
    val adsRemoved = adsRemovedUseCase.get().map { it.getOrDefault(false) }

    init {
        viewModelScope.launch {
            with(billingService) {
                for (purchase in purchases) {
                    val value = purchase.orNull() ?: continue

                    if (value.purchased) {
                        processPurchase(value).orNull()?.let {
                            if (ProductId.remove_ads in it.products) {
                                adsRemovedUseCase.put(true)
                            }
                        }
                    }
                }
            }
        }
    }

    fun clearAllHistories() {
        viewModelScope.launch(ioDispatcher) {
            clearAllHistoriesUseCase()
        }
    }

    fun queryPurchases(@ProductType productType: String) {
        billingService.queryPurchases(productType)
    }

    fun translateHistory(history: History) {
        viewModelScope.launch {
            sourceUseCase.put(history.source)
            targetUseCase.put(history.target)

            sourceText.update { history.sourceText }
        }
    }
}
