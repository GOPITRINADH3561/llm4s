package org.llm4s.samples.embeddingsupport

import org.llm4s.llmconnect.EmbeddingClient
import org.llm4s.llmconnect.model._

object EmbeddingExample extends App {

  val inputs = Seq("Hello world!", "Gopi is working on GSoC")

  println("🔵 OpenAI Embeddings:")
  EmbeddingClient.getOpenAIEmbedding(inputs) match {
    case Right(response) =>
      println(s"✅ Received ${response.embeddings.length} vectors:")
      response.embeddings.foreach { vec =>
        println("→ " + vec.take(5).mkString(", ") + ", ...")
      }
    case Left(error) =>
      println(s"❌ OpenAI embedding error: ${error.message}")
  }

  println("\n🟠 VoyageAI Embeddings:")
  EmbeddingClient.getVoyageEmbedding(inputs) match {
    case Right(response) =>
      println(s"✅ Received ${response.embeddings.length} vectors:")
      response.embeddings.foreach { vec =>
        println("→ " + vec.take(5).mkString(", ") + ", ...")
      }
    case Left(error) =>
      println(s"❌ VoyageAI embedding error: ${error.message}")
  }
}
