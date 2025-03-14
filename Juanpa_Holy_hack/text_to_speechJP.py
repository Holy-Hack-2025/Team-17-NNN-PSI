import requests
import json
import pyttsx3
from pyt2s.services import stream_elements

def generate_content_with_gemini(api_key, prompt):
    """
    Sends a request to the Gemini API to generate content and reads it aloud in English.

    Args:
        api_key: Your Gemini API key.
        prompt: The text prompt to send to the model.
    """
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

        # # Text-to-speech with English voice
        # engine = pyttsx3.init()
        # voices = engine.getProperty('voices')

        # # Find an English voice (you might need to adjust this based on your system)
        # english_voice = None
        # for voice in voices:
        #     if 'english' in voice.name.lower(): #checking if the word english is in the voice name.
        #         english_voice = voice
        #         break

        # if english_voice:
        #     engine.setProperty('voice', english_voice.id)
        # else:
        #     print("No English voice found. Using default voice.")

        # engine.say(generated_text)
        # engine.runAndWait()
        
        stream_elements.requestTTS(generated_text, stream_elements.Voice.Russell.value)


        return generated_text

    except requests.exceptions.RequestException as e:
        print(f"Error during request: {e}")
        return None
    except (KeyError, IndexError, TypeError) as e:
        print(f"Error parsing response: {e}. Raw response: {response.text}")
        return None

# Example usage:
api_key = "AIzaSyBLRSIpuUA7sTGnlZaUyPe6fq-jFhTQkoE"  # Replace with your actual API key
prompt = "WHy is Mexico better than Greece?"

generated_text = generate_content_with_gemini(api_key, prompt)

if generated_text:
    print(generated_text)