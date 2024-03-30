package com.toasterofbread.spmp.ui.layout.apppage.searchpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.toasterofbread.composekit.utils.common.getContrasted
import com.toasterofbread.composekit.utils.common.thenIf
import com.toasterofbread.composekit.utils.composable.ShapedIconButton
import com.toasterofbread.composekit.utils.modifier.bounceOnClick
import com.toasterofbread.spmp.resources.getString
import com.toasterofbread.spmp.ui.layout.apppage.mainpage.appTextField
import com.toasterofbread.spmp.ui.layout.nowplaying.NowPlayingExpansionState
import com.toasterofbread.spmp.ui.theme.appHover

internal val SEARCH_BAR_SHAPE: Shape = RoundedCornerShape(20.dp)
internal const val SEARCH_BAR_HEIGHT_DP = 45f
internal const val SEARCH_BAR_V_PADDING_DP = 15f

@Composable
internal fun SearchAppPage.SearchBar(
    modifier: Modifier = Modifier,
    shape: Shape = SEARCH_BAR_SHAPE,
    apply_padding: Boolean = true,
    onFocusChanged: (Boolean) -> Unit
) {
    val focus_requester: FocusRequester = remember { FocusRequester() }
    val expansion: NowPlayingExpansionState = LocalNowPlayingExpansion.current

    LaunchedEffect(Unit) {
        if (expansion.getPage() == 0 && current_results == null && !search_in_progress) {
            focus_requester.requestFocus()
        }
    }

    Row(
        modifier
            .thenIf(apply_padding) {
                padding(vertical = SEARCH_BAR_V_PADDING_DP.dp)
            }
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        BasicTextField(
            value = current_query,
            onValueChange = { current_query = it },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = SEARCH_FIELD_FONT_SIZE,
                color = context.theme.vibrant_accent.getContrasted()
            ),
            modifier = Modifier
                .height(SEARCH_BAR_HEIGHT_DP.dp)
                .weight(1f)
                .appTextField(focus_requester)
                .onFocusChanged {
                    onFocusChanged(it.isFocused)
                },
            decorationBox = { innerTextField ->
                Row(
                    Modifier
                        .background(
                            context.theme.vibrant_accent,
                            shape
                        )
                        .padding(horizontal = 10.dp)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search field
                    Box(Modifier.fillMaxWidth(1f).weight(1f), contentAlignment = Alignment.CenterStart) {

                        // Query hint
                        if (current_query.isEmpty()) {
                            Text(getString("search_entry_field_hint"), fontSize = SEARCH_FIELD_FONT_SIZE, color = context.theme.on_accent)
                        }

                        // Text input
                        innerTextField()
                    }

                    // Clear field button
                    IconButton({ current_query = "" }, Modifier.bounceOnClick().appHover(true)) {
                        Icon(Icons.Filled.Clear, null, Modifier, context.theme.on_accent)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (!search_in_progress) {
                        performSearch()
                    }
                }
            )
        )

        ShapedIconButton(
            { performSearch() },
            IconButtonDefaults.iconButtonColors(
                containerColor = context.theme.vibrant_accent,
                contentColor = context.theme.vibrant_accent.getContrasted()
            ),
            Modifier
                .aspectRatio(1f)
                .bounceOnClick()
                .appHover(true),
            shape = SEARCH_BAR_SHAPE
        ) {
            Icon(Icons.Filled.Search, null)
        }
    }
}
