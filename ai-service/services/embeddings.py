# services/embeddings.py — Text Embeddings Generation
# Author: Poshitha A Kundar (AI Developer 1)
# Day 6 — ChromaDB Vector Store Setup

from sentence_transformers import SentenceTransformer

class EmbeddingsClient:
    """Generates embeddings using sentence-transformers."""
    
    def __init__(self, model_name="all-MiniLM-L6-v2"):
        """Initialize the model. Default is a fast, small model ideal for CPU."""
        self.model = SentenceTransformer(model_name)
        
    def get_embedding(self, text):
        """Generate embedding for a single text string."""
        return self.model.encode(text).tolist()

    def get_embeddings(self, texts):
        """Generate embeddings for a list of text strings."""
        return self.model.encode(texts).tolist()

# --- Singleton instance ---
_embeddings_client = None

def get_embeddings_client():
    """Get or create a singleton EmbeddingsClient instance."""
    global _embeddings_client
    if _embeddings_client is None:
        _embeddings_client = EmbeddingsClient()
    return _embeddings_client
