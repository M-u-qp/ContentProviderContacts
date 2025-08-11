package com.example.testcontentprovider.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.testcontentprovider.AddDeleteContact.addContact
import com.example.testcontentprovider.Contact
import com.example.testcontentprovider.ContactPermissionCheck
import com.example.testcontentprovider.GetContacts.fetchContacts
import com.example.testcontentprovider.utils.ExtToast.toast
import com.example.testcontentprovider.utils.RandomContactColor.getRandomColorForContact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    onContactClick: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val contacts = remember { mutableStateListOf<Contact>() }
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredContacts = remember(contacts, searchQuery) {
        if (searchQuery.isBlank()) {
            contacts
        } else {
            contacts.filter { contact ->
                contact.name.contains(searchQuery, ignoreCase = true) ||
                        contact.phone?.contains(searchQuery) == true
            }
        }
    }

    fun refreshContacts() {
        contacts.clear()
        contacts.addAll(fetchContacts(context))
    }

    ContactPermissionCheck(
        onGranted = {
            refreshContacts()
        },
        onDenied = {
            context.toast("Нужны разрешения контактов")
        }
    )
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = {},
                        expanded = false,
                        onExpandedChange = {},
                        placeholder = { Text("Поиск контактов") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, "Очистить поиск")
                                }
                            }
                        }
                    )
                },
                expanded = false,
                onExpandedChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 8.dp),
                shape = SearchBarDefaults.inputFieldShape,
                tonalElevation = SearchBarDefaults.TonalElevation,
                shadowElevation = SearchBarDefaults.ShadowElevation,
                windowInsets = SearchBarDefaults.windowInsets,
                content = { },
            )

            CreateNewContactRow(onCreateClick = { showAddDialog = true })

            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(filteredContacts) { contact ->
                    ContactItem(
                        contact = contact,
                        onContactClick = { onContactClick(contact) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddContactDialog(
            context = context,
            onDismiss = { showAddDialog = false },
            onAdd = { name, phone, email ->
                addContact(context, name, phone, email)
                contacts.clear()
                contacts.addAll(fetchContacts(context))
                showAddDialog = false
            }
        )
    }
}

@Composable
fun CreateNewContactRow(
    onCreateClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable(onClick = onCreateClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = "Создать контакт",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ContactItem(
    contact: Contact,
    onContactClick: () -> Unit
) {
    val contactColor = remember(contact.name) {
        getRandomColorForContact(contact.name)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onContactClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(contactColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.name.take(1).uppercase(),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = contact.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AddContactDialog(
    context: Context,
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val nameError by remember(name) {
        derivedStateOf {
            if (name.isBlank()) {
                "Имя не может быть пустым"
            } else {
                null
            }
        }
    }
    val phoneError by remember(phone) {
        derivedStateOf {
            if (phone.isNotBlank() && !phone.matches(Regex("^[0-9+\\- ]*$"))) {
                "Только цифры, +, - и пробелы "
            } else {
                null
            }
        }
    }

    val emailError by remember {
        derivedStateOf {
            if (email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches()
            ) {
                "Неверный формат email"
            } else {
                null
            }
        }
    }

    val isFormValid by remember(nameError, phoneError, emailError) {
        derivedStateOf {
            nameError == null && phoneError == null && emailError == null && name.isNotBlank()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить новый контакт") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя*") },
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it) } },
                    singleLine = true
                )
                TextField(
                    value = phone,
                    onValueChange = {
                        if (it.length <= 20) {
                            phone = it
                        }
                    },
                    isError = phoneError != null,
                    supportingText = { phoneError?.let { Text(it) } },
                    label = { Text("Номер") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Электронная почта") },
                    isError = emailError != null,
                    supportingText = { emailError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_CONTACTS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        onAdd(name, phone, email)
                        context.toast("Контакт $name добавлен")
                    } else {
                        context.toast("Требуются разрешения на добавление контактов")
                    }
                }, enabled = isFormValid
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}