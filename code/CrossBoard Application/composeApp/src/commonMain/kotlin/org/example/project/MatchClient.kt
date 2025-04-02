package org.example.project

import domain.Move
import httpModel.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.network.*
import kotlinx.serialization.SerializationException
import util.Either

private const val baseUrl = "http://127.0.0.1:8080"

class MatchClient(
    private val client: HttpClient
) {



    suspend fun enterMatch(userId: Int, gameType: String): Either<String, MatchOutput> {
        val response = try {
            client.post(
                urlString = "$baseUrl/match/user/$userId",
            ){
                contentType(ContentType.Application.Json)
                setBody(MatchCreationInput(gameType))
            }
        }
        catch (e: SerializationException) {
            return Either.Left(e.message ?: "Serialization exception")
        }
        catch (e: UnresolvedAddressException) {
            return Either.Left(e.message ?: "No internet connection")
        }

        return if (response.status.value in 200 .. 299){
            val match = response.body<MatchOutput>()
            Either.Right(match)
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }

    suspend fun forfeitMatch(userId: Int, matchId: Int): Either<String, MatchOutput> {
        val response = try {
            client.put(
                urlString = "$baseUrl/match/$matchId/forfeit/$userId}",
            ){
                contentType(ContentType.Application.Json)
            }
        }
        catch (e: SerializationException) {
            return Either.Left(e.message ?: "Serialization exception")
        }
        catch (e: UnresolvedAddressException) {
            return Either.Left(e.message ?: "No internet connection")
        }

        return if (response.status.value in 200 .. 299){
            val forfeitedMatch = response.body<MatchOutput>()
            Either.Right(forfeitedMatch)
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }

    suspend fun getMatch(matchId: Int): Either<String, MatchOutput>{
        val response = try {
            client.get(
                urlString = "$baseUrl/match/$matchId"
            )
        }
        catch (e: UnresolvedAddressException) {
            return Either.Left(e.message ?: "No internet connection")
        }
        catch (e: Exception) {
            return Either.Left(e.cause?.message ?: e.message ?: "Something went wrong")
        }

        return if (response.status.value in 200 .. 299){
            val match = response.body<MatchOutput>()
            Either.Right(match)
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }

    suspend fun playMatch(
        userId: Int,
        matchId: Int,
        player:String,
        row: Int,
        column: Char
    ): Either<String, MoveOutput>{
        val moveInput = TicTacToeMoveInput(player, "$row$column")
        val moveInputString = clientJson.encodeToString(MoveInput.serializer(),moveInput)
        println("Sending JSON String: $moveInputString")
        val response = try {
            client.put(
                urlString = "$baseUrl/match/$matchId/play/$userId",
            ){
                contentType(ContentType.Application.Json)
                setBody(moveInputString)
            }
        }
        catch (e: SerializationException) {
            return Either.Left(e.message ?: "Serialization exception")
        }
        catch (e: UnresolvedAddressException) {
            return Either.Left(e.message ?: "No internet connection")
        }

        return if (response.status.value in 200 .. 299){
            val move = response.body<MoveOutput>()
            Either.Right(move)
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }
}