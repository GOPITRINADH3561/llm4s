package org.llm4s.llmconnect.model

/**
 * EmbeddingRequest represents the input structure required by
 * embedding providers like OpenAI and VoyageAI.
 *
 * @param input A sequence of input texts to be embedded.
 * @param model The model name (e.g., "text-embedding-3-small", "voyage-2").
 */
case class EmbeddingRequest(
  input: Seq[String],
  model: String
)
