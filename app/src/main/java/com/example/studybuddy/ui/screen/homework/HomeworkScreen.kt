package com.example.studybuddy.ui.screen.homework

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.studybuddy.domain.model.Homework
import com.example.studybuddy.ui.component.EmptyList
import com.example.studybuddy.ui.component.NavigationBarComponent
import com.example.studybuddy.ui.screen.subjects.SubjectViewModal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import com.example.studybuddy.enums.HomeworkStatus
import com.example.studybuddy.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeworkScreen(
    navController: NavHostController,
    viewModal: HomeworkViewModal = HomeworkViewModal(),
    subjectViewModal: SubjectViewModal = SubjectViewModal()
) {
    val homeworkList by viewModal.homeworkList.collectAsState(initial = emptyList())
    val subjectList by subjectViewModal.subjectList.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Homework") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add homework"
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
            if (homeworkList.isEmpty()) {
                EmptyList("homework")
            } else {
                HomeworkList(homeworkList, navController)
            }
        }
    }
    if (showDialog) {
        AddHomeworkDialog(
            subjectList = subjectList.map { subject -> subject.name },
            onDismiss = { showDialog = false },
            onAddHomework = { homework ->
                viewModal.addHomework(homework)
                showDialog = false
            }
        )
    }
}


@Composable
fun AddHomeworkDialog(
    subjectList: List<String>,
    onDismiss: () -> Unit,
    onAddHomework: (Homework) -> Unit
) {
    var selectedSubject by remember { mutableStateOf(subjectList.firstOrNull() ?: "") }
    var description by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(HomeworkStatus.TO_DO.displayName) }
    var dueDate by remember { mutableStateOf<LocalDateTime?>(null) }

    val statusOptions = HomeworkStatus.getDisplayNames()

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
                Text("Add Homework", style = MaterialTheme.typography.titleMedium)
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

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Due Date", style = MaterialTheme.typography.bodySmall)
                DateTimePicker(
                    initialDateTime = dueDate ?: LocalDateTime.now(),
                    onDateTimeSelected = { dueDate = it }
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Status", style = MaterialTheme.typography.bodySmall)
                DropdownSelector(
                    items = statusOptions,
                    selectedItem = selectedStatus,
                    onItemSelected = { selectedStatus = it }
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
                            if (selectedSubject.isNotEmpty() && dueDate != null) {
                                onAddHomework(
                                    Homework(
                                        subject = selectedSubject,
                                        description = description,
                                        dueDate = dueDate!!,
                                        status = selectedStatus
                                    )
                                )
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSecondary,
                            contentColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Add")
                    }
                }
            }
        }
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
            onDismissRequest = {
                expanded = false
            },
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
fun DateTimePicker(
    initialDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    val context = LocalContext.current

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(initialDateTime) }

    selectedDate = dueDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    selectedTime = dueDate.format(DateTimeFormatter.ofPattern("HH:mm"))

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            dueDate = dueDate.withYear(year).withMonth(month + 1).withDayOfMonth(day)
            selectedDate = dueDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            onDateTimeSelected(dueDate)
        },
        dueDate.year,
        dueDate.monthValue - 1,
        dueDate.dayOfMonth
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            dueDate = dueDate.withHour(hour).withMinute(minute)
            selectedTime = dueDate.format(DateTimeFormatter.ofPattern("HH:mm"))
            onDateTimeSelected(dueDate)
        },
        dueDate.hour,
        dueDate.minute,
        true
    )

    Column(horizontalAlignment = Alignment.Start) {
        TextButton(onClick = { datePickerDialog.show() }) {
            Text("Date: $selectedDate", color = MaterialTheme.colorScheme.tertiary)
        }
        TextButton(onClick = { timePickerDialog.show() }) {
            Text("Time: $selectedTime", color = MaterialTheme.colorScheme.tertiary)
        }
    }
}


@Composable
fun HomeworkList(homeworkList: List<Homework>, navController: NavHostController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(homeworkList) { homework ->
            HomeworkItem(homework, navController)
        }
    }
}

@Composable
fun HomeworkItem(homework: Homework, navController: NavHostController) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
    val formattedDueDate = homework.dueDate.format(formatter)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate(Screen.HomeworkItem.route + "/${homework.id}")
            }
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = homework.subject,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Due Date: $formattedDueDate",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Status: ${homework.status}",
                style = MaterialTheme.typography.bodySmall,
                color = if (homework.status == HomeworkStatus.COMPLETED.displayName) Color.Green else Color.Red
            )
        }
    }
}
