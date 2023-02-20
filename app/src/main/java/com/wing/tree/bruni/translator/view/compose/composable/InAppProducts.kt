package com.wing.tree.bruni.translator.view.compose.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wing.tree.bruni.core.constant.EMPTY
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.extension.float
import com.wing.tree.bruni.translator.extension.paddingHorizontal
import com.wing.tree.bruni.translator.model.InAppProduct
import com.wing.tree.bruni.translator.view.compose.ui.theme.Typography
import com.wing.tree.bruni.translator.view.state.InAppProductsState

@Composable
internal fun InAppProductsScreen(
    inAppProductsState: InAppProductsState,
    onItemClick: (InAppProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    when(inAppProductsState) {
        InAppProductsState.Loading -> {
            // TODO : Implement
        }
        is InAppProductsState.Content -> InAppProducts(
            inAppProducts = inAppProductsState.inAppProducts,
            onItemClick = onItemClick,
            modifier = modifier
        )
        is InAppProductsState.Error -> {
            // TODO : Implement
        }
    }
}

@Composable
private fun InAppProduct(
    inAppProduct: InAppProduct,
    onItemClick: (InAppProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable {
                onItemClick(inAppProduct)
            }
            .padding(
                start = 16.dp,
                top = 8.dp,
                end = 24.dp,
                bottom = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val productDetails = inAppProduct.productDetails

        inAppProduct.imageResource?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )
        }

        Column(
            modifier = Modifier
                .paddingHorizontal(16.dp)
                .weight(ONE.float)
        ) {
            Text(
                text = productDetails.name,
                color = colorScheme.onSurface,
                style = Typography.bodyLarge
            )

            Text(
                text = productDetails.description,
                color = colorScheme.onSurfaceVariant,
                style = Typography.bodyMedium
            )
        }

        val formattedPrice = productDetails
            .oneTimePurchaseOfferDetails
            ?.formattedPrice
            ?: EMPTY

        Text(
            text = formattedPrice,
            color = colorScheme.onSurfaceVariant,
            style = Typography.labelSmall
        )
    }
}

@Composable
private fun InAppProducts(
    inAppProducts: List<InAppProduct>,
    onItemClick: (InAppProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(
            items = inAppProducts,
            key = { it.key }
        ) { inAppProduct ->
            InAppProduct(
                inAppProduct = inAppProduct,
                onItemClick = onItemClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            )
        }
    }
}
