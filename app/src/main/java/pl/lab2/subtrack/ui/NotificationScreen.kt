package pl.lab2.subtrack.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.lab2.subtrack.R
import pl.lab2.subtrack.data.local.entities.NotificationHistory
import pl.lab2.subtrack.models.NotificationItem
import pl.lab2.subtrack.ui.components.SubscriptionIcon

// ============================================================================
// GŁÓWNY EKRAN POWIADOMIEŃ
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    notificationViewModel: NotificationViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val notifications by notificationViewModel.notifications.collectAsState()
    val hasUnread by notificationViewModel.hasUnreadNotifications.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.notifications_title),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    if (hasUnread) {
                        IconButton(onClick = { notificationViewModel.markAllAsRead() }) {
                            Icon(
                                imageVector = Icons.Default.ClearAll,
                                contentDescription = stringResource(id = R.string.mark_all_as_read_desc)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.notifications_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = notifications,
                    key = { it.id }
                ) { notification ->

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                                notificationViewModel.deleteNotification(notification.id)
                                true
                            } else {
                                false
                            }
                        },
                        positionalThreshold = { distance -> distance * 0.4f }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = true,
                        enableDismissFromEndToStart = true,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Transparent)
                            )
                        },
                        content = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent)
                            ) {
                                NotificationItemRow(
                                    notification = notification,
                                    onMarkReadClick = { notificationViewModel.markAsRead(notification) }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

// ============================================================================
// ELEMENT LISTY (KARTA POWIADOMIENIA)
// ============================================================================

@Composable
fun NotificationItemRow(
    notification: NotificationHistory,
    onMarkReadClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val timestampText = when {
        System.currentTimeMillis() - notification.timestamp < 60 * 60 * 1000 -> stringResource(id = R.string.time_today)
        System.currentTimeMillis() - notification.timestamp < 30 * 60 * 60 * 1000 -> stringResource(id = R.string.time_yesterday)
        System.currentTimeMillis() - notification.timestamp < 54 * 60 * 60 * 1000 -> stringResource(id = R.string.time_2_days_ago)
        else -> stringResource(id = R.string.time_3_days_ago)
    }

    val containerColor = if (!notification.isRead) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    val strokeColor = if (!notification.isRead) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!notification.isRead) {
                    onMarkReadClick()
                }
                isExpanded = !isExpanded
            },
        color = containerColor,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(width = 1.dp, color = strokeColor)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                SubscriptionIcon(
                    serviceName = notification.serviceName,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (!notification.isRead) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (!notification.isRead) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = if (isExpanded) TextOverflow.Clip else TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = timestampText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    maxLines = 1
                )

                if (!notification.isRead) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

// ============================================================================
// POWIADOMIENIA SYSTEMOWE
// ============================================================================

fun showSystemNotification(context: Context, notificationItem: NotificationItem) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "subtrack_notifications"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            context.getString(R.string.system_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.system_channel_desc)
        }
        notificationManager.createNotificationChannel(channel)
    }

    val formattedPrice = context.getString(R.string.price_format, notificationItem.price)

    val contentText = if (notificationItem.isTrial) {
        context.getString(R.string.dynamic_msg_trial, notificationItem.subscriptionName)
    } else {
        context.getString(
            R.string.dynamic_msg_payment,
            notificationItem.daysLeft,
            formattedPrice,
            notificationItem.subscriptionName
        )
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(context.getString(R.string.app_name))
        .setContentText(contentText)
        .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    notificationManager.notify(notificationItem.id.hashCode(), builder.build())
}