package com.example.studybuddy.ui.screen.schedule

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.studybuddy.domain.model.ScheduleEntry
import com.example.studybuddy.enums.DayOfWeek
import com.example.studybuddy.ui.component.EmptyList
import com.example.studybuddy.ui.component.NavigationBarComponent
import com.example.studybuddy.ui.screen.subjects.SubjectViewModal
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScheduleScreen(
    navController: NavHostController,
    viewModal: ScheduleViewModal = ScheduleViewModal(),
    subjectViewModal: SubjectViewModal = SubjectViewModal()
) {
    val scheduleEntryList by viewModal.scheduleEntryList.collectAsState(initial = emptyList())
    val scheduleByDay = scheduleEntryList.groupBy { DayOfWeek.valueOf(it.dayOfWeek.uppercase()) }
    val subjectList by subjectViewModal.subjectList.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<ScheduleEntry?>(null) }

    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Schedule") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                    editingEntry = null
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add schedule entry"
                )
            }
        },
        bottomBar = { NavigationBarComponent(navController) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            if (scheduleEntryList.isEmpty()) {
                EmptyList("schedule")
            } else {
                ScheduleList(scheduleByDay,
                    onEditEntry = { entry ->
                        editingEntry = entry
                        showDialog = true
                    },
                    onDeleteEntry = { entry ->
                        viewModal.deleteScheduleEntry(entry)
                        scope.launch {

                            val result = snackBarHostState.showSnackbar(
                                message = "Entry is deleted",
                                actionLabel = "Undo",
                                duration = SnackbarDuration.Short
                            )

                            if (result == SnackbarResult.ActionPerformed){
                                viewModal.restoreDeletedSchedule()
                            }
                        }
                    })
            }
        }
    }
    if (showDialog) {
        AddScheduleEntryDialog(
            subjectList = subjectList.map { subject -> subject.name },
            initialEntry = editingEntry,
            onDismiss = {
                showDialog = false
                editingEntry = null
            },
            onSave = { scheduleEntry ->
                if (editingEntry != null) {
                    viewModal.updateScheduleEntry(
                        editingEntry!!.copy(
                            subject = scheduleEntry.subject,
                            startTime = scheduleEntry.startTime,
                            endTime = scheduleEntry.endTime,
                            dayOfWeek = scheduleEntry.dayOfWeek,
                            link = scheduleEntry.link
                        )
                    )
                } else {
                    viewModal.addScheduleEntry(scheduleEntry)
                }
                showDialog = false
                editingEntry = null
            }
        )
    }
}

@Composable
fun AddScheduleEntryDialog(
    subjectList: List<String>,
    initialEntry: ScheduleEntry? = null,
    onDismiss: () -> Unit,
    onSave: (ScheduleEntry) -> Unit
) {
    val context = LocalContext.current
    var selectedSubject by remember {
        mutableStateOf(
            initialEntry?.subject ?: subjectList.firstOrNull() ?: ""
        )
    }
    var startTime by remember { mutableStateOf(initialEntry?.startTime ?: LocalTime.of(9, 0)) }
    var endTime by remember { mutableStateOf(initialEntry?.endTime ?: LocalTime.of(10, 0)) }
    var dayOfWeek by remember {
        mutableStateOf(
            initialEntry?.dayOfWeek ?: DayOfWeek.MONDAY.displayName
        )
    }
    var link by remember { mutableStateOf(initialEntry?.link ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = if (initialEntry != null) "Edit Schedule Entry" else "Add New Schedule Entry",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Subject", style = MaterialTheme.typography.bodySmall)
                if (subjectList.isEmpty()) {
                    Text(
                        text = "No subjects available. Please add subjects first.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    DropdownSelector(
                        items = subjectList,
                        selectedItem = selectedSubject,
                        onItemSelected = { selectedSubject = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text("Start Time", style = MaterialTheme.typography.bodySmall)
                TimePickerField("Start Time", startTime) { selectedTime ->
                    startTime = selectedTime
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text("End Time", style = MaterialTheme.typography.bodySmall)
                TimePickerField("End Time", endTime) { selectedTime ->
                    endTime = selectedTime
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text("Day of the Week", style = MaterialTheme.typography.bodySmall)
                DropdownSelector(
                    items = DayOfWeek.entries.map { it.displayName },
                    selectedItem = dayOfWeek,
                    onItemSelected = { selected -> dayOfWeek = selected }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Link", style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = { Text("Enter link") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSecondary,
                            contentColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (selectedSubject.isNotEmpty() && selectedSubject.isNotEmpty()) {
                                if (link.isEmpty() || isValidUrl(link)) {
                                    onSave(
                                        ScheduleEntry(
                                            subject = selectedSubject,
                                            startTime = startTime,
                                            endTime = endTime,
                                            dayOfWeek = dayOfWeek,
                                            link = link
                                        )
                                    )
                                    onDismiss()
                                }else{
                                    Toast.makeText(
                                        context,
                                        "Invalid URL format",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSecondary,
                            contentColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(if (initialEntry != null) "Save" else "Add")
                    }
                }
            }
        }
    }
}

fun isValidUrl(url: String): Boolean {
    return Patterns.WEB_URL.matcher(url).matches()
}

@Composable
fun TimePickerField(
    label: String,
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    val context = LocalContext.current

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            val selectedTime = LocalTime.of(hour, minute)
            onTimeChange(selectedTime)
        },
        time.hour,
        time.minute,
        true
    )

    TextButton(onClick = { timePickerDialog.show() }) {
        Text(
            "$label: ${time.format(DateTimeFormatter.ofPattern("HH:mm"))}",
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ScheduleList(
    scheduleByDay: Map<DayOfWeek, List<ScheduleEntry>>,
    onEditEntry: (ScheduleEntry) -> Unit = {},
    onDeleteEntry: (ScheduleEntry) -> Unit = {}
) {
    LazyColumn {
        DayOfWeek.entries.forEach { day ->
            val entries = scheduleByDay[day].orEmpty()

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.medium
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = day.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (entries.isEmpty()) {
                            Text(
                                text = "No classes today",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 8.dp).alpha(0.6f)
                            )
                        } else {
                            entries.forEach { entry ->
                                ScheduleEntryItem(entry, onEditEntry, onDeleteEntry)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleEntryItem(
    entry: ScheduleEntry,
    onEditEntry: (ScheduleEntry) -> Unit = {},
    onDeleteEntry: (ScheduleEntry) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 2.dp, end = 2.dp, bottom = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${entry.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${
                        entry.endTime.format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        )
                    } | ${entry.subject}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { onEditEntry(entry) }
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Entry")
                    }
                    IconButton(
                        onClick = { onDeleteEntry(entry) }
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete Entry",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            if (entry.link?.isNotEmpty() == true) {
                val context = LocalContext.current
                val annotatedLinkString = buildAnnotatedString {
                    append("Open Link")
                    addStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSecondary,
                            textDecoration = TextDecoration.Underline
                        ),
                        start = 0,
                        end = 9
                    )
                    addStringAnnotation(
                        tag = "URL",
                        annotation = entry.link,
                        start = 0,
                        end = 9
                    )
                }

                Text(
                    text = annotatedLinkString,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(entry.link)).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}