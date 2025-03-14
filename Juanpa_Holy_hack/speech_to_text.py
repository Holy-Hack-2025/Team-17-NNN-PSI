import speech_recognition as sr

def transcribe_audio(filename):
    # initialize the recognizer
    r = sr.Recognizer()

    # open the file
    with sr.AudioFile(filename) as source:
        # listen for the data (load audio to memory)
        audio_data = r.record(source)
        # recognize (convert from speech to text)
        text = r.recognize_google(audio_data)
    
    return text

# Example usage
filename = "Learn_English_Naturally.wav"
text = transcribe_audio(filename)
print(text)
