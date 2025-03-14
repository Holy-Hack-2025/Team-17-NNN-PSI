import cv2
import numpy as np
import os
import keyboard
from deepface import DeepFace
from sklearn.metrics.pairwise import cosine_similarity

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

def main():
    known_faces = load_known_faces()
    
    # Start webcam for live detection
    cap = cv2.VideoCapture(0, cv2.CAP_DSHOW)  # Use DirectShow backend to improve compatibility
    if not cap.isOpened():
        print("Error: Could not open webcam. Trying alternative methods...")
        cap = cv2.VideoCapture(0)  # Try default backend
    
    if not cap.isOpened():
        print("Final Error: Webcam not accessible. Exiting program.")
        return
    
    face_net = cv2.dnn.readNetFromCaffe("deploy.prototxt", "res10_300x300_ssd_iter_140000.caffemodel")
    
    print("Press 'q' to exit the program.")
    
    while True:
        ret, frame = cap.read()
        if not ret:
            print("Error: Could not read frame from webcam. Retrying...")
            cap = cv2.VideoCapture(0)
            continue
        
        frame = cv2.flip(frame, 1)  # Flip the frame horizontally for a better experience
        faces = detect_faces(frame, face_net)
        
        for (x, y, x2, y2) in faces:
            face_image = frame[y:y2, x:x2]
            test_embedding = get_face_embedding(face_image)
            name = recognize_face(test_embedding, known_faces)
            cv2.rectangle(frame, (x, y), (x2, y2), (0, 255, 0), 2)
            cv2.putText(frame, name, (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 255, 0), 2)
        
        cv2.imshow("Live Face Recognition", frame)
        cv2.waitKey(1)  
        if keyboard.is_pressed("q"):
            print("Exit command received. Closing application...")
            break
    
    cap.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    main()
