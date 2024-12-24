package com.example.studybuddy.ui.screen.homework

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.studybuddy.domain.model.Homework
import com.example.studybuddy.enums.HomeworkStatus
import com.example.studybuddy.ui.component.NavigationBarComponent
import com.example.studybuddy.ui.screen.subjects.SubjectViewModal
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkItemScreen(
    homeworkId: Long,
    navController: NavHostController,
    viewModal: HomeworkViewModal = HomeworkViewModal(),
    subjectViewModal: SubjectViewModal = SubjectViewModal()
) {
    val homeworkItem by viewModal.getHomeworkById(homeworkId).collectAsState(initial = null)
    val subjectList by subjectViewModal.subjectList.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Homework Details") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    if (homeworkItem != null) {
                        IconButton(onClick = {
                            viewModal.deleteHomework(homeworkItem!!)
                            scope.launch {

                                val result = snackBarHostState.showSnackbar(
                                    message = "Homework is deleted",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short
                                )

                                if (result == SnackbarResult.ActionPerformed){
                                    viewModal.restoreDeletedHomework()
                                }
                                navController.navigateUp()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Homework",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
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
                    Icons.Filled.Edit,
                    contentDescription = "Edit homework"
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
            if (homeworkItem != null) {
                HomeworkDetailsContent(homework = homeworkItem!!)
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (showDialog && homeworkItem != null) {
        EditHomeworkDialog(
            homework = homeworkItem!!,
            subjectList = subjectList.map { subject -> subject.name },
            onDismiss = { showDialog = false },
            onUpdateHomework = { updatedHomework ->
                viewModal.updateHomework(updatedHomework)
                showDialog = false
            }
        )
    }
}


@Composable
fun EditHomeworkDialog(
    homework: Homework,
    subjectList: List<String>,
    onDismiss: () -> Unit,
    onUpdateHomework: (Homework) -> Unit
) {
    var selectedSubject by remember { mutableStateOf(homework.subject) }
    var description by remember { mutableStateOf(homework.description) }
    var selectedStatus by remember { mutableStateOf(homework.status) }
    var dueDate by remember { mutableStateOf(homework.dueDate) }

    val statusOptions = listOf("To Do", "In Progress", "Completed")

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
                Text("Edit Homework", style = MaterialTheme.typography.titleMedium)
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
                    initialDateTime = dueDate,
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
                            onUpdateHomework(
                                homework.copy(
                                    subject = selectedSubject,
                                    description = description,
                                    dueDate = dueDate,
                                    status = selectedStatus
                                )
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSecondary,
                            contentColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun HomeworkDetailsContent(homework: Homework) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
    val formattedDueDate = homework.dueDate.format(formatter)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Subject: ${homework.subject}",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Due Date: $formattedDueDate",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Status: ${homework.status}",
            style = MaterialTheme.typography.bodyLarge,
            color = if (homework.status == HomeworkStatus.COMPLETED.displayName) Color.Green else Color.Red
        )

        Spacer(modifier = Modifier.height(16.dp))

        if(homework.description.isNotEmpty()){
        Text(
            text = "Description:",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = homework.description,
            style = MaterialTheme.typography.bodyMedium
        )
        }
    }
}