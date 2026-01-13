package com.example.stripesdemo.presentation.ui.screen.gscan
import com.example.presentation.R
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoNotTouch
import androidx.compose.material.icons.outlined.PanToolAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stripesdemo.domain.entity.enums.PickListMode
import com.example.stripesdemo.domain.entity.enums.pickListModeCommands
import com.example.stripesdemo.presentation.exception.errorStringResource
import com.example.stripesdemo.presentation.ui.composables.BackButton
import com.example.stripesdemo.presentation.ui.composables.LoadingBox
import com.example.stripesdemo.presentation.ui.theme.contentSpacing1
import com.example.stripesdemo.presentation.ui.theme.contentSpacing4
import com.example.stripesdemo.presentation.utils.createQrCode

@Composable
fun AdvancedSettingsScreen(
    navigateBack: () -> Unit,
    viewModel: AdvancedSettingsViewModel = hiltViewModel(),
) {


    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.errorFlow.collect { error ->
            Toast.makeText(
                context,
                context.resources.errorStringResource(error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is AdvancedSettingsState.Content -> AdvancedSettingsContent(
            content = state,
            navigateBack = navigateBack
        )

        AdvancedSettingsState.Idle -> {
            LoadingBox()
        }
    }


}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdvancedSettingsContent(
    navigateBack: () -> Unit,
    content: AdvancedSettingsState.Content,

    ) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.advanced_settings))
                },
                navigationIcon = { BackButton(navigateBack = navigateBack) },
                actions = {

                    Icon(
                        if (content.isConnected) Icons.Outlined.PanToolAlt else Icons.Outlined.DoNotTouch,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = null,
                        modifier = Modifier.padding(contentSpacing4)
                    )
                }
            )
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(it)
        ) {

            PickListMode()
        }

    }
}


@Composable
private fun PickListMode() {
    Column {
        Text(
            text = stringResource(R.string.pick_list_mode),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(start = contentSpacing4)
        )

            PickListMode.entries
                .toTypedArray()
                .onEach { mode ->
                        PickListModeQrCodeCommand(mode)
            }

    }
}



@Composable
private fun PickListModeQrCodeCommand(
    mode: PickListMode,
) {

    Column(
       modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = mode.value,
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(contentSpacing1))

        Image(
            bitmap = createQrCode(pickListModeCommands.getValue(mode)).asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
                .align(Alignment.CenterHorizontally)


        )
    }

}


@Composable
@Preview
private fun AdvancedSettingsContentPreview() {
    AdvancedSettingsContent(
        navigateBack = {},
        content = AdvancedSettingsState.Content(
            isConnected = true
        )
    )
}
