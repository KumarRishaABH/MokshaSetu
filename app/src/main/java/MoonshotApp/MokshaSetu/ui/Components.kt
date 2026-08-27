package MoonshotApp.MokshaSetu.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.ui.theme.AmberBg
import MoonshotApp.MokshaSetu.ui.theme.Cream
import MoonshotApp.MokshaSetu.ui.theme.Gold
import MoonshotApp.MokshaSetu.ui.theme.GoldSoft
import MoonshotApp.MokshaSetu.ui.theme.GreenBg
import MoonshotApp.MokshaSetu.ui.theme.GreenOk
import MoonshotApp.MokshaSetu.ui.theme.GreyBg
import MoonshotApp.MokshaSetu.ui.theme.Ink
import MoonshotApp.MokshaSetu.ui.theme.LineC
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.Paper
import MoonshotApp.MokshaSetu.ui.theme.RedAlert
import MoonshotApp.MokshaSetu.ui.theme.RedBg

enum class ChipKind(val bg: Color, val fg: Color) {
    GREEN(GreenBg, GreenOk),
    AMBER(AmberBg, Color(0xFFA67C1A)),
    RED(RedBg, RedAlert),
    NAVY(Cream, Navy),
    GREY(GreyBg, Muted)
}

@Composable
fun DiyaMark(size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val r = this.size.minDimension / 2f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFE9B0), Gold, Color(0xFF7A5A17)),
                center = Offset(r, r * 1.1f),
                radius = r
            ),
            radius = r,
            center = Offset(r, r)
        )
        drawOval(
            brush = Brush.verticalGradient(listOf(Color(0xFFFFF6D8), Color(0xFFFFB638))),
            topLeft = Offset(r * 0.64f, 0f),
            size = this.size.copy(width = r * 0.72f, height = r * 1.05f)
        )
    }
}

@Composable
fun VirasatTopBar(
    title: String,
    subtitle: String,
    showBack: Boolean,
    onBack: () -> Unit = {},
    actionLabel: String? = null,
    onAction: () -> Unit = {}
) {
    Surface(color = Navy) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (showBack) {
                Text(
                    "←",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .noRippleClickable(onBack)
                        .padding(horizontal = 6.dp)
                )
            }
            DiyaMark(22.dp)
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(subtitle, fontSize = 10.sp, letterSpacing = 1.sp, color = GoldSoft)
            }
            if (actionLabel != null) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0x1FFFFFFF),
                    modifier = Modifier.noRippleClickable(onAction)
                ) {
                    Text(
                        actionLabel,
                        color = GoldSoft,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(text: String, kind: ChipKind) {
    Surface(shape = RoundedCornerShape(50), color = kind.bg) {
        Text(
            text,
            color = kind.fg,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        color = Muted,
        modifier = Modifier.padding(start = 2.dp, top = 6.dp, bottom = 10.dp)
    )
}

@Composable
fun InfoCard(
    emoji: String?,
    title: String,
    body: String,
    chip: Pair<String, ChipKind>? = null,
    accentBar: Color? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Paper,
        border = BorderStroke(1.dp, LineC),
        modifier = Modifier
            .fillMaxWidth()
            .let { m -> if (onClick != null) m.noRippleClickable(onClick) else m }
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            if (accentBar != null) {
                Box(Modifier.width(5.dp).fillMaxHeight().background(accentBar))
            }
            Row(
                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                if (emoji != null) {
                    Box(
                        modifier = Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(Cream),
                        contentAlignment = Alignment.Center
                    ) { Text(emoji, fontSize = 18.sp) }
                }
                Column(Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, color = Ink)
                    Text(body, style = MaterialTheme.typography.bodySmall, color = Muted)
                }
                chip?.let { StatusChip(it.first, it.second) }
            }
        }
    }
}

@Composable
fun MoneyCard(
    emoji: String,
    institution: String,
    maskedId: String,
    amount: String,
    footer: String,
    footerColor: Color = Muted,
    chip: Pair<String, ChipKind>? = null,
    accentBar: Color? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Paper,
        border = BorderStroke(1.dp, LineC),
        modifier = Modifier
            .fillMaxWidth()
            .let { m -> if (onClick != null) m.noRippleClickable(onClick) else m }
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            if (accentBar != null) {
                Box(Modifier.width(5.dp).fillMaxHeight().background(accentBar))
            }
            Column(Modifier.padding(14.dp).fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    Box(
                        modifier = Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(Cream),
                        contentAlignment = Alignment.Center
                    ) { Text(emoji, fontSize = 18.sp) }
                    Column(Modifier.weight(1f)) {
                        Text(institution, style = MaterialTheme.typography.titleMedium, color = Ink)
                        Text(maskedId, style = MaterialTheme.typography.bodySmall, color = Muted)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            amount,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Navy
                        )
                        chip?.let { Spacer(Modifier.height(4.dp)); StatusChip(it.first, it.second) }
                    }
                }
                if (footer.isNotBlank()) {
                    Spacer(Modifier.height(9.dp))
                    Text(footer, fontSize = 10.5.sp, lineHeight = 15.sp, color = footerColor)
                }
                if (trailing != null) {
                    Spacer(Modifier.height(10.dp))
                    trailing()
                }
            }
        }
    }
}

@Composable
fun ScoreRing(percent: Int, label: String) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(112.dp)) {
        Canvas(Modifier.size(112.dp)) {
            val stroke = Stroke(width = 13f, cap = StrokeCap.Butt)
            drawArc(Color(0xFFE9E4D6), -90f, 360f, false, style = stroke)
            drawArc(Gold, -90f, 360f * percent / 100f, false, style = stroke)
        }
        Surface(shape = CircleShape, color = Paper) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 12.dp)
            ) {
                Text(
                    "$percent%",
                    fontFamily = FontFamily.Serif,
                    fontSize = 26.sp,
                    color = Navy,
                    fontWeight = FontWeight.Bold
                )
                Text(label, fontSize = 9.sp, letterSpacing = 0.5.sp, color = Muted)
            }
        }
    }
}

@Composable
fun GoldButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Gold,
            contentColor = Navy,
            disabledContainerColor = LineC,
            disabledContentColor = Muted
        ),
        modifier = Modifier.fillMaxWidth().height(48.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun OutlineButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, if (enabled) Navy else LineC),
        modifier = Modifier
            .fillMaxWidth()
            .let { m -> if (enabled) m.noRippleClickable(onClick) else m }
    ) {
        Text(
            text,
            color = if (enabled) Navy else Muted,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp)
        )
    }
}

@Composable
fun DashedActionCard(title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Paper,
        border = BorderStroke(1.dp, LineC),
        modifier = Modifier.fillMaxWidth().noRippleClickable(onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
            Text(title, color = Navy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Muted, fontSize = 11.sp)
        }
    }
}

@Composable
fun BannerCard(bg: Color, text: String, textColor: Color = Ink) {
    Surface(shape = RoundedCornerShape(12.dp), color = bg) {
        Text(
            text,
            color = textColor,
            fontSize = 11.sp,
            lineHeight = 17.sp,
            modifier = Modifier.padding(12.dp).fillMaxWidth()
        )
    }
}

@Composable
fun EmptyStateCard(title: String, body: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = GreyBg,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy)
            Text(body, fontSize = 10.5.sp, lineHeight = 15.sp, color = Muted)
        }
    }
}

@Composable
fun VirasatTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        enabled = enabled,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        supportingText = supportingText?.let { { Text(it, fontSize = 10.sp, lineHeight = 14.sp) } },
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun SecretRow(label: String, secret: String, revealLabel: String, hideLabel: String) {
    var revealed by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, fontSize = 9.5.sp, letterSpacing = 0.6.sp, color = Muted)
            Text(
                if (revealed) secret else "•".repeat(secret.length.coerceIn(6, 14)),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = Ink
            )
        }
        Surface(
            shape = RoundedCornerShape(50),
            color = Cream,
            modifier = Modifier.noRippleClickable { revealed = !revealed }
        ) {
            Text(
                if (revealed) hideLabel else revealLabel,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = Navy,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun CheckRow(text: String, done: Boolean, active: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (done) GreenOk else if (active) Gold else Color(0xFFCFD4E0))
        ) {
            Text(
                if (done) "✓" else "…",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text,
            fontSize = 11.5.sp,
            fontWeight = if (done || active) FontWeight.SemiBold else FontWeight.Normal,
            color = if (done || active) Ink else Muted
        )
    }
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}
