package org.llm4s.llmconnect.provider

import sttp.client4._
import sttp.client4.circe._
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._

import java.util.Base64
import java.nio.{ByteBuffer, ByteOrder}
import scala.util.Try

import org.llm4s.llmconnect.config.EmbeddingConfig
import org.llm4s.llmconnect.model._

/**
 * VoyageAIEmbeddingClient handles embedding generation using VoyageAI's API.
 * Docs: https://docs.voyageai.com
 */
object VoyageAIEmbeddingClient {

  private val backend = DefaultSyncBackend()

  /**
   * Decode base64-encoded float32 (little-endian) string into sequence of doubles.
   */
  private def decodeBase64ToFloatArray(base64Str: String): Seq[Double] = {
    Try {
      val bytes = Base64.getDecoder.decode(base64Str)
      val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
      val count = bytes.length / 4
      (0 until count).map(_ => buffer.getFloat.toDouble)
    }.getOrElse(Seq.empty)
  }

  def getEmbeddings(request: EmbeddingRequest): Either[EmbeddingError, EmbeddingResponse] = {
    val payload: Json = Json.obj(
      "input" -> Json.arr(request.input.map(Json.fromString): _*),
      "model" -> Json.fromString(EmbeddingConfig.voyageModel),
      "encoding_format" -> Json.fromString("base64")
    )

    val response = basicRequest
      .post(uri"${EmbeddingConfig.voyageEndpoint}")
      .body(payload.noSpaces)
      .contentType("application/json")
      .header("Authorization", s"Bearer ${EmbeddingConfig.voyageKey}")
      .response(asStringAlways)
      .send(backend)

    if (response.code.isSuccess) {
      parse(response.body).flatMap(_.hcursor.downField("data").as[Vector[Json]]) match {
        case Right(dataArray) =>
          val vectors = dataArray.flatMap(_.hcursor.get[String]("embedding").toOption.map(decodeBase64ToFloatArray))
          Right(EmbeddingResponse(embeddings = vectors.toSeq, model = Some(request.model)))
        case Left(_) =>
          Left(EmbeddingError(Some(response.code.code.toString), "Failed to parse Voyage embeddings", "voyage"))
      }
    } else {
      val errorMsg = parse(response.body)
        .flatMap(_.hcursor.downField("error").get[String]("message"))
        .getOrElse(response.body)

      Left(EmbeddingError(Some(response.code.code.toString), errorMsg, "voyage"))
    }
  }
}
