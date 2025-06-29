package org.llm4s.llmconnect

import org.llm4s.llmconnect.config.EmbeddingConfig
import org.llm4s.llmconnect.model.{ EmbeddingRequest, EmbeddingResponse, EmbeddingError }
import org.llm4s.llmconnect.provider.{ OpenAIEmbeddingClient, VoyageAIEmbeddingClient }

/**
 * EmbeddingClient provides unified access to embedding APIs.
 * Supports OpenAI and VoyageAI via simple routing.
 */
object EmbeddingClient {

  /**
   * Get embeddings from OpenAI.
   */
  def getOpenAIEmbedding(input: Seq[String]): Either[EmbeddingError, EmbeddingResponse] = {
    val request = EmbeddingRequest(input, EmbeddingConfig.openAIModel)
    OpenAIEmbeddingClient.getEmbeddings(request)
  }

  /**
   * Get embeddings from VoyageAI.
   */
  def getVoyageEmbedding(input: Seq[String]): Either[EmbeddingError, EmbeddingResponse] = {
    val request = EmbeddingRequest(input, EmbeddingConfig.voyageModel)
    VoyageAIEmbeddingClient.getEmbeddings(request)
  }

  /**
   * Dynamically route by provider name: "openai" or "voyage".
   */
  def get(provider: String, input: Seq[String]): Either[EmbeddingError, EmbeddingResponse] =
    provider.toLowerCase match {
      case "openai" => getOpenAIEmbedding(input)
      case "voyage" => getVoyageEmbedding(input)
      case unknown  => Left(EmbeddingError(None, s"Unsupported provider: $unknown", unknown))
    }
}
