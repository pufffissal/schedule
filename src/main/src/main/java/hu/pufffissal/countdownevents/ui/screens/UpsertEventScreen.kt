package hu.pufffissal.countdownevents.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import hu.pufffissal.countdownevents.ui.icons.IconCatalog
import hu.pufffissal.countdownevents.ui.theme.Accent1
import hu.pufffissal.countdownevents.ui.theme.Accent2
import hu.pufffissal.countdownevents.ui.theme.Accent3
import hu.pufffissal.countdownevents.ui.theme.Accent4
import hu.pufffissal.countdownevents.ui.theme.Accent5
import hu.pufffissal.countdownevents.ui.theme.Accent6
import hu.pufffissal.countdownevents.ui.theme.Accent7
import hu.pufffissal.countdownevents.ui.theme.Accent8
import hu.pufffissal.countdownevents.ui.theme.Card
import hu.pufffissal.countdownevents.ui.theme.DeleteRed
import hu.pufffissal.countdownevents.ui.theme.Surface
import hu.pufffissal.countdownevents.ui.theme.TextPrimary
import hu.pufffissal.countdownevents.ui.theme.TextSecondary
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private enum class DateTimePickKind { Event, Reminder }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpsertEventScreen(
    eventId: Long?,
    onDone: () -> Unit,
) {
    val vm: UpsertEventViewModel = hiltViewModel()
    val settingsVm: SettingsViewModel = hiltViewModel()
    val hapticsOn by settingsVm.hapticsEnabled.collectAsState()
    val state by vm.state.collectAsState()
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(eventId) {
        vm.loadIfEdit(eventId)
    }

    val zone = ZoneId.systemDefault()
    val formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd.  HH:mm")
    val dateTimeLabel = state.targetEpochMillis?.let {
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), zone).format(formatter)
    } ?: "Válassz dátumot és időt"

    val accentOptions = listOf(
        null,
        Accent1, Accent2, Accent3, Accent4, Accent5, Accent6, Accent7, Accent8
    )

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pendingLocalDate by remember { mutableStateOf<LocalDate?>(null) }
    var dateTimePickKind by remember { mutableStateOf<DateTimePickKind?>(null) }

    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(state.shakeNonce) {
        if (state.shakeNonce == 0) return@LaunchedEffect
        shakeOffset.snapTo(0f)
        shakeOffset.animateTo(10f, tween(40))
        shakeOffset.animateTo(-10f, tween(40))
        shakeOffset.animateTo(8f, tween(40))
        shakeOffset.animateTo(0f, tween(40))
    }

    var showToolbarDelete by remember { mutableStateOf(false) }
    if (showToolbarDelete) {
        AlertDialog(
            onDismissRequest = { showToolbarDelete = false },
            containerColor = Surface,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Esemény törlése") },
            text = {
                Text(
                    "Biztosan törölni szeretnéd a(z) ${state.name} eseményt? Ez a művelet nem vonható vissza."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (hapticsOn) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        showToolbarDelete = false
                        vm.delete(onDone)
                    }
                ) {
                    Text("Törlés", color = DeleteRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showToolbarDelete = false }) {
                    Text("Mégse", color = TextSecondary)
                }
            }
        )
    }

    if (showDatePicker) {
        val initial = when (dateTimePickKind) {
            DateTimePickKind.Reminder -> state.reminderEpochMillis?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), zone).toLocalDate()
            } ?: LocalDate.now(zone)
            DateTimePickKind.Event, null -> state.targetEpochMillis?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), zone).toLocalDate()
            } ?: LocalDate.now(zone)
        }
        val millis = initial.atStartOfDay(zone).toInstant().toEpochMilli()
        val dateState = rememberDatePickerState(initialSelectedDateMillis = millis)
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
                dateTimePickKind = null
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selected = dateState.selectedDateMillis
                        if (selected != null) {
                            pendingLocalDate = Instant.ofEpochMilli(selected)
                                .atZone(zone)
                                .toLocalDate()
                        }
                        showDatePicker = false
                        showTimePicker = true
                    }
                ) { Text("Tovább", color = TextPrimary) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    dateTimePickKind = null
                }) {
                    Text("Mégse", color = TextSecondary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Surface,
                titleContentColor = TextPrimary,
                headlineContentColor = TextPrimary,
                weekdayContentColor = TextSecondary,
                subheadContentColor = TextSecondary,
                navigationContentColor = TextPrimary,
                yearContentColor = TextPrimary,
                currentYearContentColor = DeleteRed,
                selectedYearContentColor = TextPrimary,
                selectedYearContainerColor = DeleteRed,
                dayContentColor = TextPrimary,
                selectedDayContentColor = TextPrimary,
                selectedDayContainerColor = DeleteRed,
                todayContentColor = DeleteRed,
                todayDateBorderColor = DeleteRed,
            )
        ) {
            DatePicker(state = dateState)
        }
    }

    if (showTimePicker && pendingLocalDate != null) {
        val baseMillis = when (dateTimePickKind) {
            DateTimePickKind.Reminder -> state.reminderEpochMillis ?: state.targetEpochMillis
            DateTimePickKind.Event, null -> state.targetEpochMillis
        }
        val base = baseMillis?.let {
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), zone)
        }
        val timeState = rememberTimePickerState(
            initialHour = base?.hour ?: 12,
            initialMinute = base?.minute ?: 0,
            is24Hour = true,
        )
        AlertDialog(
            onDismissRequest = {
                showTimePicker = false
                pendingLocalDate = null
                dateTimePickKind = null
            },
            containerColor = Surface,
            titleContentColor = TextPrimary,
            title = { Text("Idő") },
            text = {
                TimePicker(state = timeState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val ld = pendingLocalDate!!
                        val zdt = ZonedDateTime.of(
                            ld,
                            LocalTime.of(timeState.hour, timeState.minute),
                            zone
                        )
                        val epoch = zdt.toInstant().toEpochMilli()
                        when (dateTimePickKind) {
                            DateTimePickKind.Reminder -> vm.setReminderEpochMillis(epoch)
                            DateTimePickKind.Event, null -> vm.setTargetEpochMillis(epoch)
                        }
                        showTimePicker = false
                        pendingLocalDate = null
                        dateTimePickKind = null
                    }
                ) { Text("OK", color = TextPrimary) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showTimePicker = false
                    pendingLocalDate = null
                    dateTimePickKind = null
                }) { Text("Mégse", color = TextSecondary) }
            }
        )
    }

    val canSave = state.name.isNotBlank() && state.targetEpochMillis != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEdit) "Szerkesztés" else "Hozzáadás") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    if (state.canDelete) {
                        Text(
                            text = "Törlés",
                            color = DeleteRed,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clickable { showToolbarDelete = true }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .graphicsLayer { translationX = shakeOffset.value }
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FieldLabel("Esemény neve")
            TextInput(
                value = state.name,
                placeholder = "Pl. Utazás, Vizsga, Szülinap…",
                error = state.nameError,
                underlineError = state.nameError != null,
                onValueChange = vm::setName
            )

            FieldLabel("Dátum és idő")
            val dateBorder = if (state.targetMissing) DeleteRed else Color.Transparent
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface)
                    .border(1.dp, dateBorder, RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        dateTimePickKind = DateTimePickKind.Event
                        showDatePicker = true
                    }
                    .padding(14.dp)
            ) {
                Text("Dátum és idő", color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Text(dateTimeLabel, color = TextPrimary)
            }

            FieldLabel("Ikon")
            IconPicker(
                selectedKey = state.iconKey,
                onSelect = vm::setIconKey
            )

            FieldLabel("Akcent szín (opcionális)")
            AccentPicker(
                selectedArgb = state.accentArgb,
                options = accentOptions.map { it?.value?.toInt() },
                onSelect = vm::setAccent
            )

            FieldLabel("Emlékeztető (opcionális)")
            val reminderText = state.reminderEpochMillis?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), zone).format(formatter)
            } ?: "Nincs"
            ReminderRow(
                enabled = state.reminderEpochMillis != null,
                label = reminderText,
                onToggle = { enabled ->
                    val target = state.targetEpochMillis
                    if (!enabled) {
                        vm.setReminderEpochMillis(null)
                    } else if (target != null) {
                        val suggested = (target - 3_600_000L)
                            .coerceAtLeast(System.currentTimeMillis() + 60_000L)
                        vm.setReminderEpochMillis(suggested)
                    }
                },
                onPickCustom = {
                    dateTimePickKind = DateTimePickKind.Reminder
                    showDatePicker = true
                },
                onSet1h = {
                    val target = state.targetEpochMillis ?: return@ReminderRow
                    vm.setReminderEpochMillis(
                        (target - 3_600_000L).coerceAtLeast(System.currentTimeMillis() + 60_000L)
                    )
                },
                onSet1d = {
                    val target = state.targetEpochMillis ?: return@ReminderRow
                    vm.setReminderEpochMillis(
                        (target - 86_400_000L).coerceAtLeast(System.currentTimeMillis() + 60_000L)
                    )
                }
            )

            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    vm.save(
                        onSuccess = {
                            if (hapticsOn) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            onDone()
                        },
                        onInvalid = { }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canSave) Color.White else Color(0xFF444444),
                    contentColor = if (canSave) Color.Black else Color(0xFF888888),
                    disabledContainerColor = Color(0xFF444444),
                    disabledContentColor = Color(0xFF888888),
                ),
                shape = RoundedCornerShape(14.dp),
                enabled = !state.saving
            ) {
                Text(
                    text = if (state.saving) "Mentés…" else "Mentés",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, color = TextSecondary, fontWeight = FontWeight.Medium)
}

@Composable
private fun ReminderRow(
    enabled: Boolean,
    label: String,
    onToggle: (Boolean) -> Unit,
    onPickCustom: () -> Unit,
    onSet1h: () -> Unit,
    onSet1d: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface, RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Értesítés", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(label, color = TextSecondary)
            }
            Switch(checked = enabled, onCheckedChange = onToggle)
        }
        if (enabled) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SmallChip("1 óra", onClick = onSet1h)
                SmallChip("1 nap", onClick = onSet1d)
                SmallChip("Egyéni", onClick = onPickCustom)
            }
        }
    }
}

@Composable
private fun SmallChip(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = TextPrimary,
        modifier = Modifier
            .background(Card, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Composable
private fun TextInput(
    value: String,
    placeholder: String,
    error: String?,
    underlineError: Boolean,
    onValueChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface, RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = if (error != null) DeleteRed else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(14.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(placeholder, color = TextSecondary)
                    }
                    inner()
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(if (underlineError) DeleteRed else Color.Transparent)
        )
        if (error != null) {
            Text(error, color = DeleteRed, fontSize = 13.sp)
        }
    }
}

@Composable
private fun IconPicker(
    selectedKey: String?,
    onSelect: (String?) -> Unit,
) {
    val scroll = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scroll),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        IconCatalog.forEach { opt ->
            val selected = opt.key == selectedKey
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Card)
                        .border(
                            2.dp,
                            if (selected) Color.White else Color.Transparent,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelect(opt.key) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = opt.image,
                        contentDescription = opt.key,
                        tint = TextPrimary
                    )
                }
                if (selected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
    val noneSelected = selectedKey == null
    Spacer(Modifier.height(8.dp))
    val dashColor = if (noneSelected) Color.White else TextSecondary
    Text(
        text = "Nincs ikon",
        color = TextSecondary,
        modifier = Modifier
            .drawBehind {
                val r = 12.dp.toPx()
                drawRoundRect(
                    color = dashColor,
                    topLeft = Offset.Zero,
                    size = size,
                    cornerRadius = CornerRadius(r, r),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                    )
                )
            }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .clickable { onSelect(null) }
    )
}

@Composable
private fun AccentPicker(
    selectedArgb: Int?,
    options: List<Int?>,
    onSelect: (Int?) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEach { argb ->
            val isNone = argb == null
            val color = argb?.let { Color(it) } ?: Color(0xFF2A2A2A)
            val selected = argb == selectedArgb
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (selected) 3.dp else 0.dp,
                            color = if (selected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onSelect(argb) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isNone) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val stroke = 3.dp.toPx()
                            drawLine(
                                color = DeleteRed,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, 0f),
                                strokeWidth = stroke
                            )
                        }
                    }
                }
            }
        }
    }
}
