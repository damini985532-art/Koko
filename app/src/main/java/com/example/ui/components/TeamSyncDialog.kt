package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TeamProfileEntity
import com.example.ui.theme.CateringBorder
import com.example.ui.theme.CateringCardElevated
import com.example.ui.theme.CateringCardNavy
import com.example.ui.theme.CateringDangerRed
import com.example.ui.theme.CateringOrange
import com.example.ui.theme.CateringPackedGreen
import com.example.ui.theme.CateringTextMuted
import com.example.ui.theme.CateringTextPrimary
import com.example.ui.theme.CateringTextSecondary

@Composable
fun TeamSyncDialog(
    teamProfile: TeamProfileEntity?,
    onDismiss: () -> Unit,
    onCreateTeam: (teamName: String, adminName: String) -> Unit,
    onJoinTeam: (code: String, memberName: String) -> Unit,
    onLeaveTeam: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableStateOf(0) }
    var teamNameInput by remember { mutableStateOf("") }
    var adminNameInput by remember { mutableStateOf("") }
    var joinCodeInput by remember { mutableStateOf("") }
    var memberNameInput by remember { mutableStateOf("") }
    var copiedNotice by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = CateringOrange,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "टीम शेयरिंग व एक्सेस",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = CateringTextPrimary
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (teamProfile != null) {
                    // Already in a team
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CateringCardElevated)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "सक्रिय टीम: ${teamProfile.teamName}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = CateringTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "सदस्य: ${teamProfile.memberName} (${if (teamProfile.isAdmin) "एडमिन" else "टीम मेंबर"})",
                                style = MaterialTheme.typography.bodySmall,
                                color = CateringTextSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "टीम इनविटेशन कोड (Invitation Code):",
                                style = MaterialTheme.typography.labelSmall,
                                color = CateringTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CateringCardNavy)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = teamProfile.inviteCode,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 4.sp
                                    ),
                                    color = CateringOrange
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(teamProfile.inviteCode))
                                        copiedNotice = true
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Code",
                                        tint = CateringTextPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            if (copiedNotice) {
                                Text("कोड कॉपी हो गया!", color = CateringPackedGreen, fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "सरल हिंदी निर्देश:\nअपने टीम मेंबर के फोन में 'Catering Display Manager' खोलें और 'कोड से जुड़ें' में यह 6-अंकों का कोड दर्ज करें। किसी भी Google Login की आवश्यकता नहीं है।",
                                style = MaterialTheme.typography.bodySmall,
                                color = CateringTextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onLeaveTeam,
                                colors = ButtonDefaults.buttonColors(containerColor = CateringDangerRed)
                            ) {
                                Text("टीम से बाहर निकलें (Leave Team)", color = Color.White)
                            }
                        }
                    }
                } else {
                    // No team yet - Tabs to Create or Join
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = CateringCardNavy,
                        contentColor = CateringOrange,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = CateringOrange
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("नई टीम बनाएं", color = if (selectedTab == 0) CateringOrange else CateringTextSecondary) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("कोड से जुड़ें", color = if (selectedTab == 1) CateringOrange else CateringTextSecondary) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedTab == 0) {
                        // Create Team Form
                        OutlinedTextField(
                            value = teamNameInput,
                            onValueChange = { teamNameInput = it },
                            label = { Text("टीम / इवेंट का नाम", color = CateringTextMuted) },
                            placeholder = { Text("उदा. Royal Wedding Catering", color = CateringTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CateringOrange,
                                unfocusedBorderColor = CateringBorder,
                                focusedTextColor = CateringTextPrimary,
                                unfocusedTextColor = CateringTextPrimary,
                                focusedContainerColor = CateringCardNavy,
                                unfocusedContainerColor = CateringCardNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = adminNameInput,
                            onValueChange = { adminNameInput = it },
                            label = { Text("आपका नाम (Admin Name)", color = CateringTextMuted) },
                            placeholder = { Text("उदा. Ramesh Supervisor", color = CateringTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CateringOrange,
                                unfocusedBorderColor = CateringBorder,
                                focusedTextColor = CateringTextPrimary,
                                unfocusedTextColor = CateringTextPrimary,
                                focusedContainerColor = CateringCardNavy,
                                unfocusedContainerColor = CateringCardNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (teamNameInput.isNotBlank()) {
                                    onCreateTeam(teamNameInput.trim(), if (adminNameInput.isBlank()) "Admin" else adminNameInput.trim())
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = CateringOrange)
                        ) {
                            Text("यूनिक कोड जनरेट करें", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Join Team Form
                        OutlinedTextField(
                            value = joinCodeInput,
                            onValueChange = { joinCodeInput = it },
                            label = { Text("6-अंकों का इनविटेशन कोड", color = CateringTextMuted) },
                            placeholder = { Text("उदा. 542198", color = CateringTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CateringOrange,
                                unfocusedBorderColor = CateringBorder,
                                focusedTextColor = CateringTextPrimary,
                                unfocusedTextColor = CateringTextPrimary,
                                focusedContainerColor = CateringCardNavy,
                                unfocusedContainerColor = CateringCardNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = memberNameInput,
                            onValueChange = { memberNameInput = it },
                            label = { Text("आपका नाम (Display Name)", color = CateringTextMuted) },
                            placeholder = { Text("उदा. Suresh (Display Crew)", color = CateringTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CateringOrange,
                                unfocusedBorderColor = CateringBorder,
                                focusedTextColor = CateringTextPrimary,
                                unfocusedTextColor = CateringTextPrimary,
                                focusedContainerColor = CateringCardNavy,
                                unfocusedContainerColor = CateringCardNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (joinCodeInput.isNotBlank()) {
                                    onJoinTeam(joinCodeInput.trim(), if (memberNameInput.isBlank()) "Member" else memberNameInput.trim())
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = CateringOrange)
                        ) {
                            Text("टीम में शामिल हों (Join Team)", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("बंद करें (Close)", color = CateringOrange)
            }
        },
        containerColor = CateringCardNavy
    )
}
