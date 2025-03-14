import cv2
import numpy as np
import sys
import os
import keyboard
from deepface import DeepFace
from sklearn.metrics.pairwise import cosine_similarity

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from Juanpa_Holy_hack.llm_response import generate_content_with_gemini 
from Juanpa_Holy_hack.text_to_speechJP import text_to_speech

def detect_faces(image, face_net):
    h, w = image.shape[:2]
    blob = cv2.dnn.blobFromImage(image, scalefactor=1.0, size=(300, 300), 
                                 mean=(104.0, 177.0, 123.0), swapRB=False, crop=False)
    face_net.setInput(blob)
    detections = face_net.forward()
    
    faces = []
    for i in range(detections.shape[2]):
        confidence = detections[0, 0, i, 2]
        if confidence > 0.5:
            box = detections[0, 0, i, 3:7] * np.array([w, h, w, h])
            (x, y, x2, y2) = box.astype("int")
            faces.append((x, y, x2, y2))
    
    return faces

def get_face_embedding(face_image):
    embedding = DeepFace.represent(face_image, model_name="Facenet", enforce_detection=False)[0]["embedding"]
    return np.array(embedding)

def recognize_face(face_embedding, known_faces, threshold=0.5):
    best_match = None
    best_similarity = -1  
    
    for name, known_embedding in known_faces.items():
        similarity = cosine_similarity([face_embedding], [known_embedding])[0][0]
        if similarity > best_similarity and similarity > threshold:
            best_similarity = similarity
            best_match = name
    
    return best_match if best_match else "Unknown"

def load_known_faces(directory="known_faces"):
    known_faces = {}
    face_net = cv2.dnn.readNetFromCaffe("deploy.prototxt", "res10_300x300_ssd_iter_140000.caffemodel")
    
    for filename in os.listdir(directory):
        if filename.endswith(".jpg") or filename.endswith(".jpeg"):
            path = os.path.join(directory, filename)
            name = os.path.splitext(filename)[0]
            image = cv2.imread(path)
            faces = detect_faces(image, face_net)
            
            if len(faces) > 0:
                x, y, x2, y2 = faces[0]
                face_crop = image[y:y2, x:x2]
                known_faces[name] = get_face_embedding(face_crop)
                print(f"Face embedding stored for {name}.")
            else:
                print(f"No face detected in {filename}.")
    
    return known_faces

def recognize_faces_continuous():
    known_faces = load_known_faces()
    prev_name="Unknown"
    cap = cv2.VideoCapture(0, cv2.CAP_DSHOW)
    if not cap.isOpened():
        print("Error: Could not open webcam. Trying alternative methods...")
        cap = cv2.VideoCapture(0)
    
    if not cap.isOpened():
        print("Final Error: Webcam not accessible. Exiting program.")
        return
    
    face_net = cv2.dnn.readNetFromCaffe("deploy.prototxt", "res10_300x300_ssd_iter_140000.caffemodel")
    print("Press 'q' to exit the program.")
    
    detected_names = []
    while True:
        ret, frame = cap.read()
        if not ret:
            print("Error: Could not read frame from webcam. Retrying...")
            cap = cv2.VideoCapture(0)
            continue
        
        frame = cv2.flip(frame, 1)
        faces = detect_faces(frame, face_net)
        
        for (x, y, x2, y2) in faces:
            face_image = frame[y:y2, x:x2]
            test_embedding = get_face_embedding(face_image)
            name = recognize_face(test_embedding, known_faces)
            detected_names.append(name)
            if prev_name!=name and name!="Unknown":
                prompt="Simply Introduce"+name+"he is your friend, in one line"
                text_to_speech(generate_content_with_gemini(prompt))
                print(name)
            prev_name=name
            cv2.rectangle(frame, (x, y), (x2, y2), (0, 255, 0), 2)
            cv2.putText(frame, name, (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 255, 0), 2)
        
        cv2.imshow("Live Face Recognition", frame)
        cv2.waitKey(30)  
        if keyboard.is_pressed("q"):
            print("Exit command received. Detected names: ", detected_names)
            break
    
    cap.release()
    cv2.destroyAllWindows()
    return detected_names

def main():
    detected_people = recognize_faces_continuous()
    print("Final detected names:", detected_people)
    return detected_people

if __name__ == "__main__":
    detected_names = main()
    print("Final detected people:", detected_names)
