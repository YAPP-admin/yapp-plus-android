package com.yapp.plus.data.attendance.team

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.util.encodeBase64
import java.security.MessageDigest
import java.time.Instant

class TeamPhotoRemoteDataSource(
    private val client: HttpClient,
    private val uploadClient: HttpClient,
) : TeamPhotoDataSource {
    override suspend fun uploadPhoto(
        sessionId: Long,
        filename: String,
        contentType: String,
        bytes: ByteArray,
    ): String {
        val checksum = MessageDigest.getInstance("SHA-256").digest(bytes).encodeBase64()
        val reservation =
            client
                .post("sessions/$sessionId/team-attendance-photos/presign") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        TeamPhotoPresignRequest(
                            filename,
                            contentType,
                            bytes.size.toLong(),
                            checksum,
                        ),
                    )
                }.body<TeamPhotoPresignResponse>()
        require(
            reservation.imageKey.isNotBlank() &&
                Instant.parse(reservation.expiresAt).isAfter(Instant.now()),
        )
        val url = Url(reservation.uploadUrl)
        require(url.protocol.name == "https" && url.host.isNotBlank())
        val response =
            uploadClient.put(reservation.uploadUrl) {
                reservation.signedHeaders.forEach { (key, value) -> headers.append(key, value) }
                setBody(bytes)
            }
        check(response.status.value in 200..299)
        return reservation.imageKey
    }

    override suspend fun submitPhoto(
        sessionId: Long,
        imageKey: String,
        membershipIds: Set<Long>,
    ): TeamPhotoSubmissionData {
        val response =
            client.post("sessions/$sessionId/team-attendance-photos") {
                contentType(ContentType.Application.Json)
                setBody(TeamPhotoSubmitRequest(imageKey, membershipIds))
            }
        check(response.status == HttpStatusCode.Created)
        val submitted = response.body<TeamPhotoSubmitResponse>()
        return TeamPhotoSubmissionData(submitted.photoId, submitted.submittedAt, submitted.status)
    }
}
