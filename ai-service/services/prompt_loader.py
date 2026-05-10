# services/prompt_loader.py — Prompt Template Management
# Author: Poshitha A Kundar (AI Developer 1)
# Day 5 — Prompt Engineering & Templates

import os

class PromptLoader:
    """Loads and caches system prompts from text files."""
    
    def __init__(self, prompts_dir="prompts"):
        self.prompts_dir = prompts_dir
        self.cache = {}
        
    def get_prompt(self, prompt_name):
        """
        Loads a prompt by name. Caches the result in memory.
        
        Args:
            prompt_name (str): Name of the prompt (e.g., 'describe_prompt')
            
        Returns:
            str: The loaded prompt text
        """
        if prompt_name in self.cache:
            return self.cache[prompt_name]
            
        file_path = os.path.join(self.prompts_dir, f"{prompt_name}.txt")
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                prompt_text = f.read().strip()
                self.cache[prompt_name] = prompt_text
                return prompt_text
        except FileNotFoundError:
            print(f"[ERROR] Prompt file not found: {file_path}")
            # Fallback to a default basic prompt if file is missing
            return "You are SentinelIQ, a helpful AI assistant specialized in operational risk analysis."

# --- Singleton instance ---
_prompt_loader = None

def get_prompt_loader():
    """Get or create a singleton PromptLoader instance."""
    global _prompt_loader
    if _prompt_loader is None:
        _prompt_loader = PromptLoader()
    return _prompt_loader
