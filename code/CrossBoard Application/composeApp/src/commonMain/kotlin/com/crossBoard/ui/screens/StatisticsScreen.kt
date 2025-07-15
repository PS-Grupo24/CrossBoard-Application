package com.crossBoard.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.crossBoard.httpModel.MatchStats
import com.crossBoard.model.StatsState
import com.crossBoard.utils.CustomColor
import kotlin.math.roundToInt

/**
 * Screen responsible for displaying the match statistics of a user.
 * @param stats The match statistics.
 */
/**
 * A modern, clean screen for displaying user statistics.
 * It handles loading, empty, and error states gracefully.
 */
@Composable
fun StatisticsScreen(
    stats: StatsState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (stats.stats.isEmpty()) {
            Text(
                text = "No Statistics to Show",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        "Your Statistics",
                        style = MaterialTheme.typography.h4,
                        color = CustomColor.DarkBrown.value,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(stats.stats) { stat ->
                    StatCard(stat = stat)
                }
            }
        }
        if (stats.errorMessage != null) {
            Text(
                text = stats.errorMessage,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

/**
 * A Card composable that neatly displays the statistics for a single match type.
 * @param stat The UserStat object to display.
 */
@Composable
fun StatCard(stat: MatchStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stat.matchType,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = CustomColor.DarkBrown.value
            )
            Spacer(Modifier.height(4.dp))
            Divider()
            Spacer(Modifier.height(16.dp))

            StatRow(
                icon = Icons.Default.Casino,
                label = "Total Games",
                value = stat.numberOfMatches.toString()
            )
            StatRow(
                icon = Icons.Default.EmojiEvents,
                iconTint = Color(0xFFFFD700),
                label = "Wins",
                value = stat.numberOfWins.toString()
            )
            StatRow(
                icon = Icons.Default.Handshake,
                label = "Draws",
                value = stat.numberOfDraws.toString()
            )
            StatRow(
                icon = Icons.Default.Cancel,
                iconTint = MaterialTheme.colors.error,
                label = "Losses",
                value = stat.numberOfLosses.toString()
            )
            val winRatePercentage = stat.averageWinningRate * 100.0

            val roundedPercentage = (winRatePercentage * 10).roundToInt() / 10.0

            val formattedWinRate = if (roundedPercentage % 1.0 == 0.0) {
                roundedPercentage.toInt().toString()
            } else {
                roundedPercentage.toString()
            }
            StatRow(
                icon = Icons.Default.TrendingUp,
                iconTint = Color(0xFF32CD32),
                label = "Win Rate",
                value = "$formattedWinRate%"
            )
        }
    }
}

/**
 * A single row for displaying a statistic, with an icon, label, and value.
 * @param icon The ImageVector for the icon.
 * @param label The text label for the stat.
 * @param value The numerical value of the stat.
 * @param iconTint The color for the icon.
 */
@Composable
private fun StatRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.body1,
            color = CustomColor.LightBrown.value
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            color = CustomColor.DarkBrown.value
        )
    }
}