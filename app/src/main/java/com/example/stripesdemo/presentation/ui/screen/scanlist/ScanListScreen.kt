package com.example.stripesdemo.presentation.ui.screen.scanlist

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stripesdemo.domain.entity.ScanDomainEntity
import com.example.stripesdemo.presentation.exception.errorStringResource
import com.example.stripesdemo.presentation.ui.composables.BackButton
import com.example.stripesdemo.presentation.ui.composables.LoadingBox
import com.example.stripesdemo.presentation.ui.theme.contentSpacing4
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random


@Composable
fun ScanListScreen(
    viewModel: ScanListViewmodel = hiltViewModel(),
    navigateBack: () -> Unit
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
        is ScanListState.Content -> {
            ScanListContent(
                content = state,
                onDeleteScan = { viewModel.add(ScanListEvent.DeleteScan(it)) },
                navigateBack = navigateBack
            )
        }

        ScanListState.Idle -> {
            LoadingBox()
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScanListContent(
    content: ScanListState.Content,
    onDeleteScan: (ScanDomainEntity) -> Unit,
    navigateBack: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Scan List")
                },
                navigationIcon = {
                    BackButton(navigateBack = navigateBack)
                }
            )
        },

        ) {

        Column(
            modifier = Modifier
                .padding(it)

                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier.padding(start = contentSpacing4)
            ) {
                Text(
                    text = "Number of Products: ${content.scans.size}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Total stock: ${content.scans.sumOf { it.count.toInt() }}",
                    style = MaterialTheme.typography.bodySmall
                )
            }


            HorizontalDivider(thickness = 2.dp)

            LazyColumn {

                items(items = content.scans) { scan ->
                    ScanItem(
                        scan = scan,
                        onDeleteScan = onDeleteScan
                    )

                    HorizontalDivider()
                }

            }
        }
    }

}


@Composable
private fun ScanItem(
    scan: ScanDomainEntity,
    onDeleteScan: (ScanDomainEntity) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = contentSpacing4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = scan.barcode,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.width(contentSpacing4))

            Text(
                text = "Count: ${scan.count}",
                style = MaterialTheme.typography.titleSmall
            )
        }

        IconButton(
            onClick = { onDeleteScan(scan) },
        ) {
            Icon(
                Icons.Default.Delete,
                null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }

}


@Composable
@Preview
private fun ScanListScreen() {
    ScanListContent(
        content = ScanListState.Content(generateScans()),
        onDeleteScan = {},
        navigateBack = {}
    )
}


private fun generateScans(): List<ScanDomainEntity> {
    return (0..9).map {
        ScanDomainEntity(
            scanSource = null,
            dateScanned = LocalDateTime.MAX,
            submitted = true,
            barcode =  Random.nextInt(10_000, 100_000).toString(),
            count = (10..99).random().toString(),
            id = UUID.randomUUID().toString()
        )
    }
}