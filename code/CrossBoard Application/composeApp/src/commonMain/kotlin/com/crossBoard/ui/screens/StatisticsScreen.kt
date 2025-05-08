package com.crossBoard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.crossBoard.model.StatsState
import com.crossBoard.utils.CustomColor

@Composable
fun StatisticsScreen(
    stats: StatsState
) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally){
            if(stats.stats.isEmpty()){
                Text("No Stats to show",
                    style = MaterialTheme.typography.h5,
                    color = CustomColor.DarkBrown.value,
                    modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                    )
            }
            else{
                for(stat in stats.stats){
                    Text(stat.matchType, style = MaterialTheme.typography.h5, color = CustomColor.DarkBrown.value)
                    Spacer(Modifier.height(8.dp))
                    Text("Total Games: ${stat.numberOfGames}",
                        style = MaterialTheme.typography.body1,
                        color = CustomColor.LightBrown.value
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Total Wins: ${stat.numberOfWins}",
                        style = MaterialTheme.typography.body1,
                        color = CustomColor.LightBrown.value
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Total Draws: ${stat.numberOfDraws}",
                        style = MaterialTheme.typography.body1,
                        color = CustomColor.LightBrown.value
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Total Losses: ${stat.numberOfLosses}",
                        style = MaterialTheme.typography.body1,
                        color = CustomColor.LightBrown.value
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Win Rate: ${stat.averageWinningRate}",
                        style = MaterialTheme.typography.body1,
                        color = CustomColor.LightBrown.value
                    )
                }
            }
        if (stats.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stats.errorMessage,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}