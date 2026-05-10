# services/rag_pipeline.py — RAG Orchestration
# Author: Poshitha A Kundar (AI Developer 1)
# Day 7 — RAG Pipeline Integration

from services.groq_client import get_groq_client
from services.vector_store import get_vector_store
from services.prompt_loader import get_prompt_loader

class RAGPipeline:
    """Orchestrates Retrieval-Augmented Generation."""
    
    def __init__(self):
        self.groq_client = get_groq_client()
        self.vector_store = get_vector_store()
        self.prompt_loader = get_prompt_loader()
        
    def _get_context(self, user_input, top_k=3):
        """Retrieves relevant documents from ChromaDB and formats as context."""
        results = self.vector_store.query(user_input, n_results=top_k)
        
        if not results:
            return "No specific context found in knowledge base."
            
        context_parts = []
        for i, res in enumerate(results):
            context_parts.append(f"Document {i+1}:\n{res['text']}")
            
        return "\n\n".join(context_parts)

    def generate_response(self, prompt_name, user_input):
        """
        Executes the full RAG pipeline:
        1. Retrieve context
        2. Load prompt
        3. Generate response with Groq
        """
        # Step 1: Retrieve context
        context = self._get_context(user_input)
        print(f"[RAG] Retrieved context length: {len(context)} chars")
        
        # Step 2: Load prompt
        system_prompt = self.prompt_loader.get_prompt(prompt_name)
        
        # Step 3: Generate response
        result = self.groq_client.generate_with_context(
            system_prompt=system_prompt,
            context=context,
            user_input=user_input
        )
        
        return result

# --- Singleton instance ---
_rag_pipeline = None

def get_rag_pipeline():
    """Get or create a singleton RAGPipeline instance."""
    global _rag_pipeline
    if _rag_pipeline is None:
        _rag_pipeline = RAGPipeline()
    return _rag_pipeline
