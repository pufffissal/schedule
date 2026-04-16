package hu.pufffissal.countdownevents.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import hu.pufffissal.countdownevents.R
import hu.pufffissal.countdownevents.data.Event
import hu.pufffissal.countdownevents.data.SortMode
import hu.pufffissal.countdownevents.time.CountdownParts
import hu.pufffissal.countdownevents.time.countdownParts
import hu.pufffissal.countdownevents.time.secondTicker
import hu.pufffissal.countdownevents.ui.icons.iconByKey
import hu.pufffissal.countdownevents.ui.theme.AccentStripeDefault
import hu.pufffissal.countdownevents.ui.theme.Card
import hu.pufffissal.countdownevents.ui.theme.DeleteRed
import hu.pufffissal.countdownevents.ui.theme.Surface
import hu.pufffissal.countdownevents.ui.theme.TextPrimary
import hu.pufffissal.countdownevents.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun EventsScreen(
    onAdd: () -> Unit,
    onOpenEvent: (Long) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val vm: EventsViewModel = hiltViewModel()
    val settingsVm: SettingsViewModel = hiltViewModel()
    val hapticsOn by settingsVm.hapticsEnabled.collectAsState()
    val ui by vm.ui.collectAsState()

    var sheetEvent by remember { mutableStateOf<Event?>(null) }
    var deleteTarget by remember { mutableStateOf<Event?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text("Események") },
                    navigationIcon = {
                        IconButton(onClick = onOpenSettings) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Beállítások",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    actions = {
                        SortToggleChip(
                            current = ui.sortMode,
                            onToggle = vm::toggleSortMode
                        )
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAdd,
                    shape = RoundedCornerShape(16.dp),
                    containerColor = Color.White,
                    contentColor = Color.Black,
                ) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_input_add),
                        contentDescription = "Hozzáadás"
                    )
                }
            }
        ) { padding ->
            if (ui.events.isEmpty()) {
                EmptyState(modifier = Modifier.padding(padding))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                ) {
                    items(
                        items = ui.events,
                        key = { it.id },
                    ) { item ->
                        SwipeToDeleteEventRow(
                            event = item,
                            onClick = { onOpenEvent(item.id) },
                            onLongClick = { sheetEvent = item },
                            onRequestDelete = { deleteTarget = item }
                        )
                    }
                }
            }
        }

        if (sheetEvent != null) {
            EventActionsBottomSheet(
                event = sheetEvent!!,
                onDismiss = { sheetEvent = null },
                onEdit = {
                    val id = sheetEvent!!.id
                    sheetEvent = null
                    onOpenEvent(id)
                },
                onDelete = {
                    deleteTarget = sheetEvent
                    sheetEvent = null
                }
            )
        }

        val pendingDelete = deleteTarget
        if (pendingDelete != null) {
            DeleteEventConfirmDialog(
                eventName = pendingDelete.name,
                hapticsEnabled = hapticsOn,
                onDismiss = { deleteTarget = null },
                onConfirm = {
                    vm.deleteEvent(pendingDelete.id)
                    deleteTarget = null
                }
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_empty_state),
                contentDescription = null,
                modifier = Modifier.height(128.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Nincs még eseményed",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Nyomd meg a + gombot a hozzáadáshoz.",
                color = Color(0xFF888888)
            )
        }
    }
}

@Composable
private fun SortToggleChip(
    current: SortMode,
    onToggle: () -> Unit,
) {
    Text(
        text = when (current) {
            SortMode.BY_DEADLINE -> "Legközelebbi"
            SortMode.BY_CREATED -> "Létrehozás"
        },
        color = Color.White,
        modifier = Modifier
            .padding(end = 8.dp)
            .background(Surface, RoundedCornerShape(10.dp))
            .clickable(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventActionsBottomSheet(
    event: Event,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = event.name,
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            SheetActionRow(text = "Szerkesztés", onClick = onEdit)
            SheetActionRow(text = "Törlés", color = DeleteRed, onClick = onDelete)
            SheetActionRow(text = "Mégse", color = TextSecondary, onClick = onDismiss)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SheetActionRow(
    text: String,
    onClick: () -> Unit,
    color: Color = TextPrimary,
) {
    Text(
        text = text,
        color = color,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp)
    )
}

@Composable
private fun DeleteEventConfirmDialog(
    eventName: String,
    hapticsEnabled: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = { Text("Esemény törlése") },
        text = {
            Text(
                "Biztosan törölni szeretnéd a(z) $eventName eseményt? Ez a művelet nem vonható vissza."
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (hapticsEnabled) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onConfirm()
                }
            ) {
                Text("Törlés", color = DeleteRed, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Mégse", color = TextSecondary)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SwipeToDeleteEventRow(
    event: Event,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onRequestDelete: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val offset = remember { Animatable(0f) }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val rowWidthPx = with(density) { maxWidth.toPx() }.coerceAtLeast(1f)
        val threshold = rowWidthPx * 0.5f
        val reveal = (-offset.value / rowWidthPx).coerceIn(0f, 1f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(DeleteRed.copy(alpha = reveal))
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 22.dp)
                        .size(28.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(offset.value.roundToInt(), 0) }
                    .pointerInput(rowWidthPx) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    val next = (offset.value + dragAmount).coerceIn(-rowWidthPx, 0f)
                                    offset.snapTo(next)
                                }
                            },
                            onDragCancel = {
                                scope.launch { offset.animateTo(0f, tween(220)) }
                            },
                            onDragEnd = {
                                scope.launch {
                                    if (-offset.value >= threshold) {
                                        onRequestDelete()
                                    }
                                    offset.animateTo(0f, tween(220))
                                }
                            }
                        )
                    }
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick,
                    ),
            ) {
                EventCardContent(event = event)
            }
        }
    }
}

@Composable
private fun EventCardContent(event: Event) {
    val now by secondTicker().collectAsState(initial = System.currentTimeMillis())
    val parts = countdownParts(now, event.targetEpochMillis)
    val accent = event.accentArgb?.let { Color(it) } ?: AccentStripeDefault
    val ringAccent = event.accentArgb?.let { Color(it) } ?: TextPrimary
    val totalSpan = (event.targetEpochMillis - event.createdAtEpochMillis).coerceAtLeast(1L)
    val elapsed = (now - event.createdAtEpochMillis).coerceIn(0L, totalSpan)
    val progress = elapsed.toFloat() / totalSpan.toFloat()

    var pulseDown by remember { mutableStateOf(false) }
    var skipFirstPulse by remember { mutableStateOf(true) }
    LaunchedEffect(parts.secondsAbs, parts.isCompleted) {
        if (parts.isCompleted) {
            pulseDown = false
            return@LaunchedEffect
        }
        if (skipFirstPulse) {
            skipFirstPulse = false
            return@LaunchedEffect
        }
        pulseDown = true
        delay(450)
        pulseDown = false
    }
    val secAlpha by animateFloatAsState(
        targetValue = if (parts.isCompleted || !pulseDown) 1f else 0.6f,
        animationSpec = tween(450),
        label = "secondsPulse"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Card, RoundedCornerShape(14.dp))
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(accent)
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EventIconWithRing(
                progress = progress,
                accent = ringAccent,
                iconVector = iconByKey(event.iconKey) ?: Icons.Outlined.Event
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                if (parts.isCompleted) {
                    Text("Befejezett", color = TextSecondary)
                } else {
                    CountdownAnnotated(
                        parts = parts,
                        secondsAlpha = secAlpha
                    )
                }
            }
        }
    }
}

@Composable
private fun CountdownAnnotated(
    parts: CountdownParts,
    secondsAlpha: Float,
) {
    val tnumStyle = SpanStyle(
        color = TextPrimary,
        fontFeatureSettings = "tnum",
        fontWeight = FontWeight.Medium,
    )
    val text = buildAnnotatedString {
        withStyle(tnumStyle) {
            append("${parts.daysAbs}n ${parts.hoursAbs}ó ${parts.minutesAbs}p ")
        }
        withStyle(
            tnumStyle.copy(color = TextPrimary.copy(alpha = secondsAlpha))
        ) {
            append("${parts.secondsAbs}mp")
        }
    }
    Text(text = text, fontSize = 15.sp)
}

@Composable
private fun EventIconWithRing(
    progress: Float,
    accent: Color,
    iconVector: ImageVector,
) {
    val track = Color(0xFF444444)
    val size = 52.dp
    val stroke = 2.dp
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        val strokePx = with(LocalDensity.current) { stroke.toPx() }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sweep = 360f * progress.coerceIn(0f, 1f)
            val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)
            drawArc(
                color = track,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
        Icon(
            imageVector = iconVector,
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}
