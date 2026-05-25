package com.example.splitpay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import kotlinx.coroutines.delay
import com.example.splitpay.ui.theme.SplitPayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitPayTheme(darkTheme = true, dynamicColor = false) {
                SplitPayApp()
            }
        }
    }
}

private val ScreenBackground = Color(0xFF060C17)
private val CardBackground = Color(0xFF101A2B)
private val CardBorder = Color(0xFF1D2C45)
private val MutedText = Color(0xFF7F8DA8)
private val PrimaryText = Color(0xFFE8EEF9)
private val GreenAccent = Color(0xFF00E6A1)
private val RedAccent = Color(0xFFFF4D8D)

private data class GroupMember(
    val initials: String,
    val name: String,
    val email: String,
    val isCurrentUser: Boolean = false,
    val isAdmin: Boolean = false
)

private data class GroupExpense(
    val icon: String,
    val title: String,
    val subtitle: String,
    val total: String,
    val impact: String,
    val impactPositive: Boolean
)

private data class GroupOverview(
    val key: String,
    val icon: String,
    val title: String,
    val participants: String,
    val totalSpent: String,
    val balance: String,
    val teDevem: String,
    val voceDeve: String
)

private data class DebtTransaction(
    val fromInitials: String,
    val fromName: String,
    val toInitials: String,
    val amount: String,
    val color: Color
)

private fun normalizeGroupKey(groupKey: String): String {
    val normalized = groupKey.trim().lowercase()
    return when (normalized) {
        "viagem floripa", "viagem_floripa" -> "viagem_floripa"
        "republica", "república" -> "republica"
        "aniversario carol", "aniversário carol", "aniversario_carol" -> "aniversario_carol"
        else -> normalized.replace(" ", "_")
    }
}

private fun resolveGroupOverview(groupKey: String): GroupOverview {
    return when (normalizeGroupKey(groupKey)) {
        "viagem_floripa" -> GroupOverview(
            key = "viagem_floripa",
            icon = "🏖",
            title = "Viagem Floripa",
            participants = "4 participantes",
            totalSpent = "R$ 2.060,00",
            balance = "+R$ 641,67",
            teDevem = "R$ 976,67",
            voceDeve = "R$ 0,00"
        )

        "republica" -> GroupOverview(
            key = "republica",
            icon = "🏠",
            title = "República",
            participants = "3 participantes",
            totalSpent = "R$ 330,00",
            balance = "+R$ 10,00",
            teDevem = "R$ 330,00",
            voceDeve = "R$ 0,00"
        )

        "aniversario_carol" -> GroupOverview(
            key = "aniversario_carol",
            icon = "🎉",
            title = "Aniversário Carol",
            participants = "4 participantes",
            totalSpent = "R$ 940,00",
            balance = "+R$ 325,00",
            teDevem = "R$ 940,00",
            voceDeve = "R$ 0,00"
        )

        else -> GroupOverview(
            key = normalizeGroupKey(groupKey),
            icon = "👥",
            title = groupKey,
            participants = "0 participantes",
            totalSpent = "R$ 0,00",
            balance = "R$ 0,00",
            teDevem = "R$ 0,00",
            voceDeve = "R$ 0,00"
        )
    }
}

private fun splitPayGroups(): List<GroupOverview> = listOf(
    resolveGroupOverview("viagem_floripa"),
    resolveGroupOverview("republica"),
    resolveGroupOverview("aniversario_carol")
)

private enum class SplitPayScreen {
    Home,
    Groups,
    NewGroup,
    Notifications,
    GroupDetails,
    NewExpense,
    Debts
}

@Composable
private fun SplitPayApp() {
    var currentScreen by remember { mutableStateOf(SplitPayScreen.Home) }
    var activeGroupKey by remember { mutableStateOf("viagem_floripa") }
    var groupDetailsReturnScreen by remember { mutableStateOf(SplitPayScreen.Home) }

    when (currentScreen) {
        SplitPayScreen.Home -> SplitPayHomeScreen(
            onNewGroupClick = { currentScreen = SplitPayScreen.NewGroup },
            onNotificationsClick = { currentScreen = SplitPayScreen.Notifications },
            onGroupsClick = { currentScreen = SplitPayScreen.Groups },
            onGroupClick = { groupKey ->
                activeGroupKey = groupKey
                groupDetailsReturnScreen = SplitPayScreen.Home
                currentScreen = SplitPayScreen.GroupDetails
            }
        )

        SplitPayScreen.Groups -> GroupsScreen(
            onHomeClick = { currentScreen = SplitPayScreen.Home },
            onGroupsClick = { currentScreen = SplitPayScreen.Groups },
            onGroupClick = { groupKey ->
                activeGroupKey = groupKey
                groupDetailsReturnScreen = SplitPayScreen.Groups
                currentScreen = SplitPayScreen.GroupDetails
            }
        )

        SplitPayScreen.NewGroup -> NewGroupScreen(
            onBackClick = { currentScreen = SplitPayScreen.Home }
        )

        SplitPayScreen.Notifications -> NotificationsScreen(
            onBackClick = { currentScreen = SplitPayScreen.Home }
        )

        SplitPayScreen.GroupDetails -> GroupDetailsScreen(
            groupKey = activeGroupKey,
            onBackClick = { currentScreen = groupDetailsReturnScreen },
            onNewExpenseClick = { currentScreen = SplitPayScreen.NewExpense },
            onViewDebtsClick = { currentScreen = SplitPayScreen.Debts }
        )

        SplitPayScreen.NewExpense -> NewExpenseScreen(
            groupKey = activeGroupKey,
            onCloseClick = { currentScreen = SplitPayScreen.GroupDetails }
        )

        SplitPayScreen.Debts -> DebtsScreen(
            groupKey = activeGroupKey,
            onBackClick = { currentScreen = SplitPayScreen.GroupDetails }
        )
    }
}

@Composable
fun SplitPayHomeScreen(
    onNewGroupClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onGroupClick: (String) -> Unit
) {
    var selectedGroupKey by remember { mutableStateOf("aniversario_carol") }
    val homeGroups = splitPayGroups()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        topBar = { HomeTopBar(onNotificationsClick = onNotificationsClick) },
        bottomBar = {
            BottomBar(
                selectedScreen = SplitPayScreen.Home,
                onHomeClick = {},
                onGroupsClick = onGroupsClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            BalanceCard()
            Spacer(modifier = Modifier.height(22.dp))

            SectionTitle(title = "PENDÊNCIAS")
            Spacer(modifier = Modifier.height(10.dp))
            PendingItem(name = "Ana Lima -> Voce", amount = "R$ 453,33")
            PendingItem(name = "Beatriz Nunes -> Voce", amount = "R$ 240,00")
            PendingItem(name = "Rafael Costa -> Voce", amount = "R$ 148,33")

            Spacer(modifier = Modifier.height(22.dp))
            SectionTitle(title = "GRUPOS")
            Spacer(modifier = Modifier.height(10.dp))
            GroupItem(
                icon = homeGroups[0].icon,
                title = homeGroups[0].title,
                subtitle = "${homeGroups[0].participants} - ${homeGroups[0].totalSpent} total",
                selected = selectedGroupKey == "viagem_floripa",
                onClick = {
                    selectedGroupKey = "viagem_floripa"
                    onGroupClick("viagem_floripa")
                }
            )
            GroupItem(
                icon = homeGroups[1].icon,
                title = homeGroups[1].title,
                subtitle = "${homeGroups[1].participants} - ${homeGroups[1].totalSpent} total",
                selected = selectedGroupKey == "republica",
                onClick = {
                    selectedGroupKey = "republica"
                    onGroupClick("republica")
                }
            )
            GroupItem(
                icon = homeGroups[2].icon,
                title = homeGroups[2].title,
                subtitle = "${homeGroups[2].participants} - ${homeGroups[2].totalSpent} total",
                selected = selectedGroupKey == "aniversario_carol",
                onClick = {
                    selectedGroupKey = "aniversario_carol"
                    onGroupClick("aniversario_carol")
                }
            )
            NewGroupButton(onClick = onNewGroupClick)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GroupsScreen(
    onHomeClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onGroupClick: (String) -> Unit
) {
    val groups = splitPayGroups()
    var searchQuery by remember { mutableStateOf("") }
    val filteredGroups = remember(searchQuery, groups) {
        if (searchQuery.isBlank()) {
            groups
        } else {
            groups.filter { group ->
                group.title.contains(searchQuery, ignoreCase = true) ||
                    group.participants.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        topBar = { GroupsTopBar() },
        bottomBar = {
            BottomBar(
                selectedScreen = SplitPayScreen.Groups,
                onHomeClick = onHomeClick,
                onGroupsClick = onGroupsClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF111824))
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = PrimaryText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Buscar grupos",
                                tint = MutedText,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Buscar grupos...",
                                        color = MutedText,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                innerTextField()
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (filteredGroups.isEmpty()) {
                Text(
                    text = "Nenhum grupo encontrado.",
                    color = MutedText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 12.dp)
                )
            } else {
                filteredGroups.forEach { group ->
                    GroupOverviewCard(
                        item = group,
                        onClick = { onGroupClick(group.key) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun GroupsTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScreenBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(33.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF2F5BFF), Color(0xFF1DEBAE))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "↗", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Grupos", color = PrimaryText, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
            }

            Box(
                modifier = Modifier
                    .size(33.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF101827))
                    .border(1.dp, Color(0xFF25344D), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificacoes",
                    tint = PrimaryText,
                    modifier = Modifier.size(17.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 5.dp, end = 5.dp)
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(GreenAccent)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = Color(0xFF1A263A))
    }
}

@Composable
private fun GroupOverviewCard(item: GroupOverview, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF17263D)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.icon, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        color = PrimaryText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "›", color = MutedText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.participants,
                    color = Color(0xFF8A97AF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Total gasto", color = Color(0xFF8A97AF), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = item.totalSpent, color = PrimaryText, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(Color(0xFF24344C))
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Seu saldo", color = Color(0xFF8A97AF), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = item.balance, color = GreenAccent, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun NewGroupScreen(onBackClick: () -> Unit) {
    val emojiOptions = listOf("✈", "🏠", "🍻", "🎉", "🏖", "🎮", "🍕", "🚗", "💼", "🎬", "🤸", "📚")
    val members = remember {
        mutableStateListOf(
            GroupMember(initials = "Y", name = "Voce", email = "voce@email.com", isCurrentUser = true, isAdmin = true),
            GroupMember(initials = "AL", name = "Ana Lima", email = "ana@email.com"),
            GroupMember(initials = "BN", name = "Beatriz Nunes", email = "beatriz@email.com"),
            GroupMember(initials = "RC", name = "Rafael Costa", email = "rafael@email.com")
        )
    }
    var selectedEmoji by remember { mutableStateOf(emojiOptions.first()) }
    var groupName by remember { mutableStateOf("") }
    var selectedMembers by remember { mutableStateOf(setOf("ana@email.com", "beatriz@email.com", "rafael@email.com")) }
    var showAddParticipantForm by remember { mutableStateOf(false) }
    var newParticipantName by remember { mutableStateOf("") }
    var newParticipantEmail by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        topBar = { NewGroupTopBar(onBackClick = onBackClick) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            SectionTitle(title = "EMOJI DO GRUPO")
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (rowStart in emojiOptions.indices step 6) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        val rowItems = emojiOptions.subList(rowStart, rowStart + 6)
                        rowItems.forEach { emoji ->
                            val isSelected = selectedEmoji == emoji
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF17263D))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) GreenAccent else CardBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedEmoji = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))
            SectionTitle(title = "NOME DO GRUPO")
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0E141F))
                    .border(1.dp, Color(0xFF0D8676), RoundedCornerShape(18.dp))
                    .padding(horizontal = 16.dp, vertical = 15.dp)
            ) {
                BasicTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = PrimaryText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (groupName.isEmpty()) {
                            Text(
                                text = "Ex: Viagem, Casa, Amigos...",
                                color = Color(0xFF4F5B70),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(26.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle(title = "PARTICIPANTES (${members.size})")
                Text(
                    text = "+ Adicionar",
                    color = GreenAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { showAddParticipantForm = !showAddParticipantForm }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (showAddParticipantForm) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFF0E141F))
                                .border(1.dp, Color(0xFF0D8676), RoundedCornerShape(18.dp))
                                .padding(horizontal = 16.dp, vertical = 15.dp)
                        ) {
                            BasicTextField(
                                value = newParticipantName,
                                onValueChange = { newParticipantName = it },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = PrimaryText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { innerTextField ->
                                    if (newParticipantName.isEmpty()) {
                                        Text(
                                            text = "Nome",
                                            color = Color(0xFF4F5B70),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFF0E141F))
                                .border(1.dp, Color(0xFF0D8676), RoundedCornerShape(18.dp))
                                .padding(horizontal = 16.dp, vertical = 15.dp)
                        ) {
                            BasicTextField(
                                value = newParticipantEmail,
                                onValueChange = { newParticipantEmail = it },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = PrimaryText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { innerTextField ->
                                    if (newParticipantEmail.isEmpty()) {
                                        Text(
                                            text = "Email",
                                            color = Color(0xFF4F5B70),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val canAddParticipant = newParticipantName.isNotBlank() && newParticipantEmail.isNotBlank()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (canAddParticipant) Color(0xFF047243) else Color(0xFF0E141F))
                                .clickable(enabled = canAddParticipant) {
                                    val trimmedName = newParticipantName.trim()
                                    val trimmedEmail = newParticipantEmail.trim().lowercase()
                                    if (trimmedName.isNotBlank() && trimmedEmail.isNotBlank()) {
                                        val initials = trimmedName
                                            .split(" ")
                                            .filter { it.isNotBlank() }
                                            .take(2)
                                            .joinToString("") { it.first().uppercase() }

                                        members.add(
                                            GroupMember(
                                                initials = if (initials.isBlank()) "NP" else initials,
                                                name = trimmedName,
                                                email = trimmedEmail
                                            )
                                        )
                                        selectedMembers = selectedMembers + trimmedEmail
                                        newParticipantName = ""
                                        newParticipantEmail = ""
                                        showAddParticipantForm = false
                                    }
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Adicionar Pessoa",
                                color = if (canAddParticipant) Color(0xFF04150F) else Color(0xFF7A859A),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            members.forEach { member ->
                val selected = member.isCurrentUser || selectedMembers.contains(member.email)
                ParticipantRow(
                    member = member,
                    selected = selected,
                    onClick = {
                        if (!member.isCurrentUser) {
                            selectedMembers = if (selectedMembers.contains(member.email)) {
                                selectedMembers - member.email
                            } else {
                                selectedMembers + member.email
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF047243))
                    .clickable(enabled = groupName.isNotBlank()) { }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Criar Grupo",
                    color = Color(0xFF04150F),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun NewGroupTopBar(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScreenBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF161E2B))
                    .border(1.dp, Color(0xFF23334B), CircleShape)
                    .clickable(onClick = onBackClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = PrimaryText,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Criar Grupo", color = PrimaryText, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = Color(0xFF1A263A))
    }
}

@Composable
private fun ParticipantRow(member: GroupMember, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111720))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (selected) Color(0xFF0D8676) else Color(0xFF243249),
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable(enabled = !member.isCurrentUser, onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (member.isCurrentUser) Color(0xFF0A5B45) else Color(0xFF17263D)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = member.initials,
                        color = if (member.isCurrentUser) GreenAccent else Color(0xFF9AA8C2),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = member.name,
                        color = PrimaryText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = member.email,
                        color = Color(0xFF7A859A),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (member.isAdmin) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF17263D))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Admin",
                        color = Color(0xFF5C6679),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color(0xFF3A465A), CircleShape)
                ) {
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(GreenAccent)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupDetailsScreen(
    groupKey: String,
    onBackClick: () -> Unit,
    onNewExpenseClick: () -> Unit,
    onViewDebtsClick: () -> Unit
) {
    val group = resolveGroupOverview(groupKey)

    val expenses = listOf(
        GroupExpense(
            icon = "🏠",
            title = "Aluguel da casa",
            subtitle = "Voce pagou - 2025-05-14",
            total = "R$ 1.200,00",
            impact = "+R$ 900,00",
            impactPositive = true
        ),
        GroupExpense(
            icon = "🛒",
            title = "Mercado",
            subtitle = "Ana pagou - 2025-05-15",
            total = "R$ 340,00",
            impact = "-R$ 85,00",
            impactPositive = false
        ),
        GroupExpense(
            icon = "⛵",
            title = "Passeio de barco",
            subtitle = "Rafael pagou - 2025-05-16",
            total = "R$ 520,00",
            impact = "-R$ 173,33",
            impactPositive = false
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        topBar = {
            GroupDetailsTopBar(
                groupIcon = group.icon,
                groupName = group.title,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GroupSummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Total",
                    value = group.totalSpent,
                    valueColor = PrimaryText
                )
                GroupSummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Te devem",
                    value = group.teDevem,
                    valueColor = GreenAccent
                )
                GroupSummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Voce deve",
                    value = group.voceDeve,
                    valueColor = RedAccent
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            SectionTitle(title = "PARTICIPANTES")
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ParticipantChip(initials = "Y", name = "Voce", highlighted = true, avatarColor = Color(0xFF0A5B45))
                ParticipantChip(initials = "AL", name = "Ana", highlighted = false, avatarColor = Color(0xFF2A2E6F))
                ParticipantChip(initials = "RC", name = "Rafael", highlighted = false, avatarColor = Color(0xFF612446))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ParticipantChip(initials = "BN", name = "Beatriz", highlighted = false, avatarColor = Color(0xFF635014))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF00D995))
                        .clickable(onClick = onNewExpenseClick)
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+  Nova despesa",
                        color = Color(0xFF04150F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF101827))
                        .border(1.dp, Color(0xFF2B3D5A), RoundedCornerShape(16.dp))
                        .clickable(onClick = onViewDebtsClick)
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⤴  Ver dividas",
                        color = Color(0xFFF0F5FF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            SectionTitle(title = "DESPESAS")
            Spacer(modifier = Modifier.height(10.dp))

            expenses.forEach { expense ->
                ExpenseItem(item = expense)
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun DebtsScreen(groupKey: String, onBackClick: () -> Unit) {
    val group = resolveGroupOverview(groupKey)

    val transactions = listOf(
        DebtTransaction(
            fromInitials = "BN",
            fromName = "Beatriz",
            toInitials = "Y",
            amount = "R$ 385,00",
            color = Color(0xFFA98318)
        ),
        DebtTransaction(
            fromInitials = "AL",
            fromName = "Ana",
            toInitials = "Y",
            amount = "R$ 218,33",
            color = Color(0xFF5850C9)
        ),
        DebtTransaction(
            fromInitials = "RC",
            fromName = "Rafael",
            toInitials = "Y",
            amount = "R$ 38,33",
            color = Color(0xFFC43963)
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        topBar = {
            DebtsTopBar(
                groupName = group.title,
                groupIcon = group.icon,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = "3 TRANSAÇÕES PARA ZERAR O GRUPO",
                color = Color(0xFF6E7F9C),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            transactions.forEach { transaction ->
                DebtTransactionCard(item = transaction)
            }
        }
    }
}

@Composable
private fun DebtsTopBar(groupName: String, groupIcon: String, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScreenBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF161E2B))
                    .border(1.dp, Color(0xFF23334B), CircleShape)
                    .clickable(onClick = onBackClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = PrimaryText,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Dívidas",
                    color = PrimaryText,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "$groupIcon $groupName",
                    color = MutedText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))
        Divider(color = Color(0xFF1A263A))
    }
}

@Composable
private fun DebtTransactionCard(item: DebtTransaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(item.color.copy(alpha = 0.18f))
                            .border(1.dp, item.color.copy(alpha = 0.7f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.fromInitials,
                            color = item.color,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "→", color = MutedText, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0B3A3A))
                        .border(1.dp, Color(0xFF0D8676), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.toInitials,
                        color = GreenAccent,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${item.fromName} → Você",
                color = Color(0xFF90A1BE),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.amount,
                color = GreenAccent,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun NewExpenseScreen(groupKey: String, onCloseClick: () -> Unit) {
    val group = resolveGroupOverview(groupKey)
    val categoryOptions = listOf("🍕", "💻", "🏠", "🍺", "⚡", "🧃", "🎬", "💰", "✈", "🧾", "💊", "🔧")
    val participants = listOf(
        GroupMember(initials = "Y", name = "Voce", email = "voce@email.com", isCurrentUser = true),
        GroupMember(initials = "AL", name = "Ana", email = "ana@email.com"),
        GroupMember(initials = "BN", name = "Beatriz", email = "beatriz@email.com"),
        GroupMember(initials = "RC", name = "Rafael", email = "rafael@email.com")
    )

    var selectedCategory by remember { mutableStateOf(categoryOptions.first()) }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var paidBy by remember { mutableStateOf("voce@email.com") }
    var splitWith by remember { mutableStateOf(participants.map { it.email }.toSet()) }

    val canSave = description.isNotBlank() && amount.isNotBlank() && splitWith.isNotEmpty()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        topBar = { NewExpenseTopBar(onCloseClick = onCloseClick) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Grupo: ${group.title}",
                color = Color(0xFF6E7F9C),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            SectionTitle(title = "CATEGORIA")
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (rowStart in categoryOptions.indices step 6) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        val rowItems = categoryOptions.subList(rowStart, rowStart + 6)
                        rowItems.forEach { category ->
                            val isSelected = selectedCategory == category
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF17263D))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) GreenAccent else CardBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedCategory = category },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = category, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle(title = "DESCRIÇÃO")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1F29))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 13.dp)
            ) {
                BasicTextField(
                    value = description,
                    onValueChange = { description = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = PrimaryText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (description.isEmpty()) {
                            Text(
                                text = "Ex: Jantar no restaurante",
                                color = Color(0xFF5F697B),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(22.dp))
            SectionTitle(title = "VALOR (R$)")
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1F29))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 13.dp)
            ) {
                BasicTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = PrimaryText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (amount.isEmpty()) {
                            Text(
                                text = "0,00",
                                color = Color(0xFF5F697B),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(22.dp))
            SectionTitle(title = "QUEM PAGOU")
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                participants.take(3).forEach { person ->
                    ExpensePersonChip(
                        initials = person.initials,
                        name = person.name,
                        selected = paidBy == person.email,
                        showCheck = paidBy == person.email,
                        onClick = { paidBy = person.email }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val person = participants[3]
                ExpensePersonChip(
                    initials = person.initials,
                    name = person.name,
                    selected = paidBy == person.email,
                    showCheck = paidBy == person.email,
                    onClick = { paidBy = person.email }
                )
            }

            Spacer(modifier = Modifier.height(22.dp))
            SectionTitle(title = "DIVIDIR COM")
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                participants.take(3).forEach { person ->
                    ExpensePersonChip(
                        initials = person.initials,
                        name = person.name,
                        selected = splitWith.contains(person.email),
                        onClick = {
                            splitWith = if (splitWith.contains(person.email)) {
                                splitWith - person.email
                            } else {
                                splitWith + person.email
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val person = participants[3]
                ExpensePersonChip(
                    initials = person.initials,
                    name = person.name,
                    selected = splitWith.contains(person.email),
                    onClick = {
                        splitWith = if (splitWith.contains(person.email)) {
                            splitWith - person.email
                        } else {
                            splitWith + person.email
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (canSave) Color(0xFF047243) else Color(0xFF22324A))
                    .clickable(enabled = canSave) { }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Adicionar despesa",
                    color = if (canSave) Color(0xFF04150F) else MutedText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun NewExpenseTopBar(onCloseClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScreenBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF161E2B))
                    .border(1.dp, Color(0xFF23334B), CircleShape)
                    .clickable(onClick = onCloseClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Fechar",
                    tint = PrimaryText,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Nova despesa",
                color = PrimaryText,
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = Color(0xFF1A263A))
    }
}

@Composable
private fun ExpensePersonChip(
    initials: String,
    name: String,
    selected: Boolean,
    showCheck: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF101827))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color(0xFF0D8676) else CardBorder,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (selected) Color(0xFF00D995) else Color(0xFF23324B)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = if (selected) Color(0xFF04150F) else Color(0xFF96A4BF),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            color = if (selected) PrimaryText else Color(0xFFD5DEEC),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        if (showCheck) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "✓",
                color = GreenAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun GroupDetailsTopBar(groupIcon: String, groupName: String, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScreenBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF161E2B))
                    .border(1.dp, Color(0xFF23334B), CircleShape)
                    .clickable(onClick = onBackClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = PrimaryText,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "$groupIcon  $groupName",
                    color = PrimaryText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "4 participantes",
                    color = MutedText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))
        Divider(color = Color(0xFF1A263A))
    }
}

@Composable
private fun GroupSummaryCard(modifier: Modifier, title: String, value: String, valueColor: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                .padding(start = 10.dp, end = 10.dp, top = 9.dp, bottom = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = MutedText, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            //Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = valueColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ParticipantChip(initials: String, name: String, highlighted: Boolean, avatarColor: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF101827))
            .border(
                width = 1.dp,
                color = if (highlighted) Color(0xFF0D8676) else CardBorder,
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(avatarColor),
            contentAlignment = Alignment.Center
        ) {
            Text(text = initials, color = Color(0xFFB6C4DD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            color = if (highlighted) PrimaryText else Color(0xFFD5DEEC),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ExpenseItem(item: GroupExpense) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF17263D)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.icon, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = item.title, color = PrimaryText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = item.subtitle, color = Color(0xFF93A2BE), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = item.total, color = PrimaryText, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = item.impact,
                    color = if (item.impactPositive) GreenAccent else RedAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HomeTopBar(onNotificationsClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScreenBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        HomeHeader(onNotificationsClick = onNotificationsClick)
        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = Color(0xFF1A263A))
    }
}

@Composable
private fun HomeHeader(onNotificationsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(33.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF2F5BFF), Color(0xFF1DEBAE))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "↗", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "SplitPay", color = PrimaryText, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
        }

        Box(
            modifier = Modifier
                .size(33.dp)
                .clip(CircleShape)
                .background(Color(0xFF101827))
                .border(1.dp, Color(0xFF25344D), CircleShape)
                .clickable(onClick = onNotificationsClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notificacoes",
                tint = PrimaryText,
                modifier = Modifier.size(17.dp)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 5.dp, end = 5.dp)
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(GreenAccent)
            )
        }
    }
}

@Composable
private fun NotificationsScreen(onBackClick: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        topBar = { NotificationsTopBar(onBackClick = onBackClick) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            NotificationCard(
                icon = "📄",
                title = "Nova despesa adicionada",
                description = "Ana Lima adicionou \"Jantar\" no grupo Viagem Floripa",
                date = "15/01/2024 18:30",
                unread = true
            )
            NotificationCard(
                icon = "💸",
                title = "Pagamento recebido",
                description = "Beatriz Nunes pagou R$ 240,00",
                date = "15/01/2024 14:20",
                unread = true
            )
            NotificationCard(
                icon = "⏰",
                title = "Lembrete de pagamento",
                description = "Rafael Costa ainda nao pagou R$ 148,33",
                date = "14/01/2024 10:00",
                unread = false
            )
        }
    }
}

@Composable
private fun NotificationsTopBar(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScreenBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF161E2B))
                        .border(1.dp, Color(0xFF23334B), CircleShape)
                        .clickable(onClick = onBackClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = PrimaryText,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Notificações", color = PrimaryText, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
            }

            Text(
                text = "Marcar todas",
                color = GreenAccent,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = Color(0xFF1A263A))
    }
}

@Composable
private fun NotificationCard(
    icon: String,
    title: String,
    description: String,
    date: String,
    unread: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF17263D)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(text = title, color = PrimaryText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(text = description, color = MutedText, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = date, color = Color(0xFF5F6673), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }

            if (unread) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(GreenAccent)
                )
            }
        }
    }
}

@Composable
private fun BalanceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF083C3D), Color(0xFF16263F), Color(0xFF261B45))
                    )
                )
                .border(1.dp, Color(0xFF0D8676), RoundedCornerShape(16.dp))
                .padding(17.dp)
        ) {
            Column {
                Text(text = "Saldo geral", color = MutedText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "+R$ 976,67", color = GreenAccent, fontSize = 31.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.widthIn(min = 65.dp)) {
                        Text(text = "te devem", color = MutedText, fontSize = 13.sp, lineHeight = 13.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "R$ 976,67",
                            color = GreenAccent,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .width(1.dp)
                            .height(36.dp)
                            .background(Color(0xFF2B3C57))
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "você deve", color = MutedText, fontSize = 12.sp, lineHeight = 12.sp)
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "R$ 0,00",
                            color = RedAccent,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFF6E7F9C),
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.6.sp,
        fontSize = 15.sp
    )
}

@Composable
private fun PendingItem(name: String, amount: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(29.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0C2F35)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "↙", color = GreenAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = name, color = PrimaryText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(text = amount, color = GreenAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GroupItem(
    icon: String,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val isActive = selected || hovered
    val borderColor = if (isActive) Color(0xFF0D8676) else CardBorder
    val arrowColor = if (isActive) GreenAccent else MutedText

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .hoverable(interactionSource)
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                .border(1.dp, borderColor, RoundedCornerShape(19.dp))
                .padding(horizontal = 15.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(39.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF17263D)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = title,
                        color = PrimaryText,
                        fontSize = 13.sp,
                        lineHeight = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = MutedText,
                        fontSize = 11.sp,
                        lineHeight = 11.sp
                    )
                }
            }
            Text(text = "›", color = arrowColor, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun NewGroupButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    var navigateRequested by remember { mutableStateOf(false) }
    val isActive = pressed || navigateRequested
    val borderColor = if (isActive) Color(0xFF0D8676) else Color(0xFF22324A)
    val textColor = if (isActive) GreenAccent else MutedText

    LaunchedEffect(navigateRequested) {
        if (navigateRequested) {
            delay(120)
            navigateRequested = false
            onClick()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { navigateRequested = true }
            )
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+  Novo grupo",
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BottomBar(
    selectedScreen: SplitPayScreen,
    onHomeClick: () -> Unit,
    onGroupsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0F1728))
            .border(1.dp, Color(0xFF22324A), RoundedCornerShape(18.dp))
            .padding(vertical = 8.dp, horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomItem(icon = "⌂", title = "Inicio", selected = selectedScreen == SplitPayScreen.Home, onClick = onHomeClick)
            BottomItem(icon = "◫", title = "Grupos", selected = selectedScreen == SplitPayScreen.Groups, onClick = onGroupsClick)
            BottomItem(icon = "$", title = "Dividas", selected = false, onClick = {})
            BottomItem(icon = "↺", title = "Historico", selected = false, onClick = {})
            BottomItem(icon = "◠", title = "Perfil", selected = false, onClick = {})
        }
    }
}

@Composable
private fun BottomItem(icon: String, title: String, selected: Boolean, onClick: () -> Unit) {
    val itemBackground = if (selected) Color(0xFF0F493F) else Color.Transparent
    val itemColor = if (selected) GreenAccent else MutedText

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(itemBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, color = itemColor, fontSize = 15.sp, textAlign = TextAlign.Center)
        Text(text = title, color = itemColor, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun SplitPayHomePreview() {
    SplitPayTheme(darkTheme = true, dynamicColor = false) {
        SplitPayHomeScreen(onNewGroupClick = {}, onNotificationsClick = {}, onGroupsClick = {}, onGroupClick = {})
    }
}