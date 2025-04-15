package org.example.project

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

    suspend fun getMatchByVersion(matchId: Int, version: Int): Either<String, MatchOutput> {
        val response = try {
            client.get("$baseUrl$matchId/$version"){
                contentType(ContentType.Application.Json)
            }
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

    suspend fun enterMatch(userToken: String, gameType: String): Either<String, MatchOutput> {
        val response = try {
            client.post(
                urlString = "$baseUrl/match/$gameType",
            ){
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $userToken")
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

    suspend fun forfeitMatch(userToken: String, matchId: Int): Either<String, MatchOutput> {
        val response = try {
            client.post(
                urlString = "$baseUrl/match/$matchId/forfeit",
            ){
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $userToken")
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
        userToken: String,
        matchId: Int,
        version: Int,
        moveInput: MoveInput,
    ): Either<String, MatchPlayedOutput>{
        val response = try {
            client.post(
                urlString = "$baseUrl/match/$matchId/version/$version/play",
            ){
                contentType(ContentType.Application.Json)
                setBody(moveInput)
                header(HttpHeaders.Authorization, "Bearer $userToken")
            }
        }
        catch (e: SerializationException) {
            return Either.Left(e.message ?: "Serialization exception")
        }
        catch (e: UnresolvedAddressException) {
            return Either.Left(e.message ?: "No internet connection")
        }

        return if (response.status.value in 200 .. 299){
            val move = response.body<MatchPlayedOutput>()
            Either.Right(move)
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }
}