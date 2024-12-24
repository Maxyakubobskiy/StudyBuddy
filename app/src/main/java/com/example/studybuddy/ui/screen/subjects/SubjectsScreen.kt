package com.example.studybuddy.ui.screen.subjects

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.studybuddy.domain.model.Subject
import com.example.studybuddy.ui.component.EmptyList
import com.example.studybuddy.ui.component.NavigationBarComponent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SubjectScreen(
    navController: NavHostController,
    viewModal: SubjectViewModal = SubjectViewModal()
) {
    val subjectList by viewModal.subjectList.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var subjectName by remember { mutableStateOf("") }
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }

    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Subjects") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                    subjectToEdit = null
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add subject"
                )
            }
        },
        bottomBar = {
            NavigationBarComponent(navController)
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            if (subjectList.isEmpty()) {
                EmptyList("subject")
            } else {
                SubjectList(subjectList, onDeleteSubject = { subject ->
                    viewModal.deleteSubject(subject)
                    scope.launch {

                        val result = snackBarHostState.showSnackbar(
                            message = "Subject is deleted",
                            actionLabel = "Undo",
                            duration = SnackbarDuration.Short
                        )

                        if (result == SnackbarResult.ActionPerformed){
                            viewModal.restoreDeletedSubject()
                        }
                    }
                }, onEditSubject = { subject ->
                    subjectToEdit = subject
                    subjectName = subject.name
                    showDialog = true
                })
            }
        }
    }

    if (showDialog) {
        AddSubjectDialog(
            subjectName = subjectName,
            onSubjectNameChange = { subjectName = it },
            onDismiss = { showDialog = false },
            isAdd = subjectToEdit == null,
            onAddSubject = {
                if (subjectToEdit == null) {
                    viewModal.addSubject(Subject(name = subjectName))
                } else {
                    viewModal.updateSubject(subjectToEdit!!.copy(name = subjectName))
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun AddSubjectDialog(
    subjectName: String,
    onSubjectNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    isAdd: Boolean,
    onAddSubject: () -> Unit
) {
    val context = LocalContext.current

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
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    if (isAdd) "Add new Subject" else "Edit Subject",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = subjectName,
                    onValueChange = onSubjectNameChange,
                    label = { Text("Subject Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onDismiss()
                            onSubjectNameChange("")
                        },
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
                            if (subjectName.isNotBlank()) {
                                onAddSubject()
                                onSubjectNameChange("")
                            } else {
                                Toast.makeText(
                                    context,
                                    "Subject name cannot be empty",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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
fun SubjectList(
    subjectList: List<Subject>,
    onDeleteSubject: (Subject) -> Unit,
    onEditSubject: (Subject) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(subjectList) { subject ->
            SubjectItem(subject, onDeleteSubject, onEditSubject)
        }
    }
}

@Composable
fun SubjectItem(
    subject: Subject,
    onDeleteSubject: (Subject) -> Unit,
    onEditSubject: (Subject) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = subject.name, modifier = Modifier.padding(16.dp))
            Row {
                IconButton(
                    onClick = { onEditSubject(subject) }
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Subject")
                }
                IconButton(
                    onClick = { onDeleteSubject(subject) }
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete Subject",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
