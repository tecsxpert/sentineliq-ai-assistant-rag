# services/vector_store.py — ChromaDB Integration
# Author: Poshitha A Kundar (AI Developer 1)
# Day 6 — Vector Store Setup

import os
import chromadb
from chromadb.config import Settings
from services.embeddings import get_embeddings_client

class VectorStore:
    """Wrapper for ChromaDB operations."""
    
    def __init__(self, collection_name="risk_knowledge"):
        # Setup ChromaDB client (persistent local storage for now)
        persist_directory = os.path.join(os.path.dirname(os.path.dirname(__file__)), "chroma_data")
        
        self.client = chromadb.PersistentClient(path=persist_directory)
        self.collection_name = collection_name
        self.embeddings_client = get_embeddings_client()
        
        # Get or create collection
        self.collection = self.client.get_or_create_collection(name=self.collection_name)

    def add_document(self, doc_id, text, metadata=None):
        """
        Adds a single document to the vector store.
        """
        if metadata is None:
            metadata = {}
            
        embedding = self.embeddings_client.get_embedding(text)
        
        self.collection.add(
            ids=[doc_id],
            embeddings=[embedding],
            documents=[text],
            metadatas=[metadata]
        )
        return True

    def query(self, query_text, n_results=3):
        """
        Search for documents similar to the query.
        """
        query_embedding = self.embeddings_client.get_embedding(query_text)
        
        results = self.collection.query(
            query_embeddings=[query_embedding],
            n_results=n_results
        )
        
        # Format results
        if results and results['documents'] and results['documents'][0]:
            return [
                {
                    "id": results['ids'][0][i],
                    "text": results['documents'][0][i],
                    "metadata": results['metadatas'][0][i] if results['metadatas'] else {},
                    "distance": results['distances'][0][i] if 'distances' in results else None
                }
                for i in range(len(results['documents'][0]))
            ]
        return []

# --- Singleton instance ---
_vector_store = None

def get_vector_store():
    """Get or create a singleton VectorStore instance."""
    global _vector_store
    if _vector_store is None:
        _vector_store = VectorStore()
    return _vector_store
