# services/groq_client.py — Groq API Wrapper
# Author: Poshitha A Kundar (AI Developer 1)
# Day 2 — Groq LLaMA-3.3-70b Integration

import os
from groq import Groq
from dotenv import load_dotenv

# Load environment variables from .env
load_dotenv()


class GroqClient:
    """
    Wrapper for the Groq API.
    Uses LLaMA-3.3-70b-versatile model for AI-powered responses.
    """

    def __init__(self):
        self.api_key = os.getenv("GROQ_API_KEY")
        if not self.api_key:
            raise ValueError("GROQ_API_KEY not found in environment variables. Check .env file.")
        self.client = Groq(api_key=self.api_key)
        self.model = "llama-3.3-70b-versatile"

    def generate_response(self, system_prompt, user_input, max_tokens=1024, temperature=0.7):
        """
        Send a prompt to Groq and get a response.

        Args:
            system_prompt (str): System-level instruction for the AI
            user_input (str): User's question/input
            max_tokens (int): Maximum tokens in response
            temperature (float): Creativity level (0.0 = deterministic, 1.0 = creative)

        Returns:
            dict: {success: bool, response: str, tokens_used: int, error: str|None}
        """
        try:
            chat_completion = self.client.chat.completions.create(
                messages=[
                    {"role": "system", "content": system_prompt},
                    {"role": "user", "content": user_input}
                ],
                model=self.model,
                max_tokens=max_tokens,
                temperature=temperature,
            )

            response_text = chat_completion.choices[0].message.content
            tokens_used = chat_completion.usage.total_tokens if chat_completion.usage else 0

            print(f"[GROQ] Response generated — tokens used: {tokens_used}")

            return {
                "success": True,
                "response": response_text,
                "tokens_used": tokens_used,
                "error": None
            }

        except Exception as e:
            print(f"[GROQ ERROR] {str(e)}")
            return {
                "success": False,
                "response": None,
                "tokens_used": 0,
                "error": f"Groq API error: {str(e)}"
            }

    def generate_with_context(self, system_prompt, context, user_input, max_tokens=1024, temperature=0.7):
        """
        Send a prompt to Groq with additional context (for RAG pipeline).

        Args:
            system_prompt (str): System-level instruction
            context (str): Retrieved context from vector store
            user_input (str): User's question
            max_tokens (int): Maximum tokens
            temperature (float): Creativity level

        Returns:
            dict: Same as generate_response
        """
        augmented_prompt = f"""Context from knowledge base:
{context}

User question: {user_input}

Please use the context above to provide an accurate, detailed response."""

        return self.generate_response(system_prompt, augmented_prompt, max_tokens, temperature)


# --- Singleton instance ---
_groq_client = None


def get_groq_client():
    """Get or create a singleton GroqClient instance."""
    global _groq_client
    if _groq_client is None:
        _groq_client = GroqClient()
    return _groq_client


# --- Test ---
if __name__ == "__main__":
    print("Testing Groq Client...")
    try:
        client = get_groq_client()
        result = client.generate_response(
            system_prompt="You are a helpful assistant.",
            user_input="What is operational risk in banking? Answer in 2 sentences."
        )
        print(f"Success: {result['success']}")
        print(f"Response: {result['response']}")
        print(f"Tokens: {result['tokens_used']}")
    except Exception as e:
        print(f"Error: {e}")
