package pl.lab2.subtrack.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.lab2.subtrack.R // Upewnij się, że importujesz swój R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // Scaffold to baza naszego ekranu
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Używamy stringResource, żeby obsłużyć wiele języków od początku!
                    Text(text = stringResource(id = R.string.app_name))
                },
                actions = {
                    // Przycisk ustawień po prawej stronie
                    IconButton(onClick = { /* Tu później dodamy nawigację */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings" // To też warto wrzucić do strings.xml później
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        // Tutaj w następnym kroku dodamy listę i saldo na dole
        // innerPadding jest kluczowy, żeby treść nie wchodziła pod TopBar
    }
}