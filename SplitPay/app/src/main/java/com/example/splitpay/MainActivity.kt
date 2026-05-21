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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

private enum class SplitPayScreen {
    Home,
    NewGroup,
    Notifications
}

@Composable
private fun SplitPayApp() {
    var currentScreen by remember { mutableStateOf(SplitPayScreen.Home) }

    when (currentScreen) {
        SplitPayScreen.Home -> SplitPayHomeScreen(
            onNewGroupClick = { currentScreen = SplitPayScreen.NewGroup },
            onNotificationsClick = { currentScreen = SplitPayScreen.Notifications }
        )

        SplitPayScreen.NewGroup -> NewGroupScreen(
            onBackClick = { currentScreen = SplitPayScreen.Home }
        )

        SplitPayScreen.Notifications -> NotificationsScreen(
            onBackClick = { currentScreen = SplitPayScreen.Home }
        )
    }
}

@Composable
fun SplitPayHomeScreen(onNewGroupClick: () -> Unit, onNotificationsClick: () -> Unit) {
    var selectedGroup by remember { mutableStateOf("Aniversario Carol") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        topBar = { HomeTopBar(onNotificationsClick = onNotificationsClick) },
        bottomBar = { BottomBar() }
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
                icon = "🏖",
                title = "Viagem Floripa",
                subtitle = "4 pessoas - R$ 2.060,00 total",
                selected = selectedGroup == "Viagem Floripa",
                onClick = { selectedGroup = "Viagem Floripa" }
            )
            GroupItem(
                icon = "🏠",
                title = "Republica",
                subtitle = "3 pessoas - R$ 330,00 total",
                selected = selectedGroup == "Republica",
                onClick = { selectedGroup = "Republica" }
            )
            GroupItem(
                icon = "🎉",
                title = "Aniversario Carol",
                subtitle = "4 pessoas - R$ 940,00 total",
                selected = selectedGroup == "Aniversario Carol",
                onClick = { selectedGroup = "Aniversario Carol" }
            )
            NewGroupButton(onClick = onNewGroupClick)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun NewGroupScreen(onBackClick: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            Text(
                text = "← Voltar",
                color = GreenAccent,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onBackClick)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Novo grupo",
                color = PrimaryText,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tela de criacao de grupo. O clique no botao da Home agora navega para ca.",
                color = MutedText,
                fontSize = 14.sp
            )
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
private fun BottomBar() {
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
            BottomItem(icon = "⌂", title = "Inicio", selected = true)
            BottomItem(icon = "◫", title = "Grupos", selected = false)
            BottomItem(icon = "$", title = "Dividas", selected = false)
            BottomItem(icon = "↺", title = "Historico", selected = false)
            BottomItem(icon = "◠", title = "Perfil", selected = false)
        }
    }
}

@Composable
private fun BottomItem(icon: String, title: String, selected: Boolean) {
    val itemBackground = if (selected) Color(0xFF0F493F) else Color.Transparent
    val itemColor = if (selected) GreenAccent else MutedText

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(itemBackground)
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
        SplitPayHomeScreen(onNewGroupClick = {}, onNotificationsClick = {})
    }
}