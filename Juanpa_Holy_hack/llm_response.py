import requests
import json
from text_to_speechJP import text_to_speech

def generate_content_with_gemini(prompt):
    """
    Sends a request to the Gemini API to generate content and reads it aloud in English.

    Args:
        prompt: The text prompt to send to the model.
    """
    api_key = "AIzaSyBLRSIpuUA7sTGnlZaUyPe6fq-jFhTQkoE"  # Replace with your actual API key
    url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={api_key}"
    headers = {'Content-Type': 'application/json'}
    data = {
        "contents": [{
            "parts": [{"text": prompt}]
        }]
    }

    try:
        response = requests.post(url, headers=headers, data=json.dumps(data))
        response.raise_for_status()
        result = response.json()
        generated_text = result['candidates'][0]['content']['parts'][0]['text']
        
        return generated_text

    except requests.exceptions.RequestException as e:
        print(f"Error during request: {e}")
        return None
    except (KeyError, IndexError, TypeError) as e:
        print(f"Error parsing response: {e}. Raw response: {response.text}")
        return None


prompt = "how much is 8 + 9? Answer in less than 15 words"

text_to_speech(generate_content_with_gemini(prompt))
