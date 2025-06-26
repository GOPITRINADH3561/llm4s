package org.llm4s.llmconnect.config


object EmbeddingConfig {

  val openAIKey: String = sys.env.getOrElse("OPENAI_API_KEY", {
    println("⚠️ OPENAI_API_KEY is not set. Using placeholder key.")
    "invalid-key"
  })

  val voyageKey: String = sys.env.getOrElse("VOYAGE_API_KEY", {
    println("⚠️ VOYAGE_API_KEY is not set. Using placeholder key.")
    "invalid-key"
  })

  val openAIEndpoint: String = "https://api.openai.com/v1/embeddings"
  val voyageEndpoint: String = "https://api.voyageai.com/v1/embeddings"
  val openAIModel: String = "text-embedding-3-small"
  val voyageModel: String = "voyage-2"
}
