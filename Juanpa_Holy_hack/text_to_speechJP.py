import os
import sys
import subprocess

# Suppress pygame startup message
def suppress_pygame_message():
    # Redirect stdout to null
    sys.stdout = open(os.devnull, 'w')

suppress_pygame_message()  # Call the function to suppress

import pygame # type: ignore
from pyt2s.services import stream_elements # type: ignore

def text_to_speech(text):
    data = stream_elements.requestTTS(text, stream_elements.Voice.Russell.value)
    pygame.mixer.init()
    pygame.display.init() #Initialize the display module
    pygame.display.set_mode((1, 1)) #Creates a hidden window.
    with open("temp.mp3", "wb") as f:
        f.write(data)
    pygame.mixer.music.load("temp.mp3")
    pygame.mixer.music.play()
    print("Playing audio... Press 'q' to stop.")

    while pygame.mixer.music.get_busy():
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.mixer.music.stop()
                sys.exit()
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_q:
                    pygame.mixer.music.stop()
                    sys.exit()
        pygame.time.delay(10)

    print("Audio finished.")
    pygame.mixer.quit()
    pygame.display.quit() #properly quit the display module.
    os.remove("temp.mp3")
    sys.exit()