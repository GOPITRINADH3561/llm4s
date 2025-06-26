package org.llm4s.llmconnect.provider

import sttp.client4._
import sttp.client4.circe._
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._

import org.llm4s.llmconnect.config.EmbeddingConfig
import org.llm4s.llmconnect.model._

/**
 * OpenAIEmbeddingClient handles embedding generation using OpenAI's embedding API.
 */
object OpenAIEmbeddingClient {

  private val backend = DefaultSyncBackend()

  def getEmbeddings(request: EmbeddingRequest): Either[EmbeddingError, EmbeddingResponse] = {
    val payload: Json = Json.obj(
      "input" -> Json.arr(request.input.map(Json.fromString): _*),
      "model" -> Json.fromString(request.model)
    )

    val response = basicRequest
      .post(uri"${EmbeddingConfig.openAIEndpoint}")
      .body(payload.noSpaces)
      .contentType("application/json")
      .header("Authorization", s"Bearer ${EmbeddingConfig.openAIKey}")
      .response(asStringAlways)
      .send(backend)

    if (response.code.isSuccess) {
      parse(response.body).flatMap(_.hcursor.downField("data").as[Vector[Json]]) match {
        case Right(dataArray) =>
          val vectors = dataArray.flatMap(_.hcursor.get[Seq[Double]]("embedding").toOption)
          Right(EmbeddingResponse(embeddings = vectors.toSeq, model = Some(request.model)))
        case Left(_) =>
          Left(EmbeddingError(Some(response.code.code.toString), "Failed to parse OpenAI embeddings", "openai"))
      }
    } else {
      val errorMsg = parse(response.body)
        .flatMap(_.hcursor.downField("error").get[String]("message"))
        .getOrElse(response.body)

      Left(EmbeddingError(Some(response.code.code.toString), errorMsg, "openai"))
    }
  }
}
