import os
import sys
import subprocess

# Suppress pygame startup message
def suppress_pygame_message():
    # Redirect stdout to null
    sys.stdout = open(os.devnull, 'w')

suppress_pygame_message()  # Call the function to suppress

import pygame
from pyt2s.services import stream_elements

def text_to_speech(text):
    data = stream_elements.requestTTS(text, stream_elements.Voice.Russell.value)

    # Initialize pygame mixer
    pygame.mixer.init()

    # Save the audio to a temporary file
    with open("temp.mp3", "wb") as f:
        f.write(data)

    pygame.mixer.music.load("temp.mp3")
    pygame.mixer.music.play()

    print("Playing audio... Press 'q' to stop.")

    # Keep the program running to listen for the stop command
    while pygame.mixer.music.get_busy():
        if input() == "q":
            pygame.mixer.music.stop()
            print("Audio stopped.")
            break

