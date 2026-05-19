import time
import os
import shutil
from fastapi import FastAPI, UploadFile, File, Form
from pydantic import BaseModel
from typing import List
import uvicorn

import analyzer

app = FastAPI(title="SpeechKids AI Service", version="1.0.0")

# Ensure temp directory for audio files
TEMP_DIR = "./temp_audio"
os.makedirs(TEMP_DIR, exist_ok=True)

class PhonemeScoreDto(BaseModel):
    phoneme: str
    accuracy: int

class AiStatusResponse(BaseModel):
    onlineModelName: str
    offlineModelName: str
    aiServiceStatus: str
    averageProcessingTimeMs: int
    gpuAvailable: bool

class AnalyzeResultResponse(BaseModel):
    recognizedText: str
    targetWord: str
    isCorrect: bool
    score: int
    problemPhonemes: List[str]
    phonemeScores: List[PhonemeScoreDto]
    recommendation: str
    processingTimeMs: int
    modelName: str

@app.get("/ai/status", response_model=AiStatusResponse)
def get_status():
    import torch
    gpu_available = torch.cuda.is_available() if hasattr(torch, 'cuda') else False
    status_str = "READY" if analyzer.WHISPER_LOADED else "FALLBACK_MOCK"
    
    return AiStatusResponse(
        onlineModelName="faster-whisper-base" if analyzer.WHISPER_LOADED else "none",
        offlineModelName="mock-asr-v1",
        aiServiceStatus=status_str,
        averageProcessingTimeMs=180, # Stub avg process time
        gpuAvailable=gpu_available
    )

@app.post("/ai/warmup")
def warmup():
    # Model is warmed up during import, but we can do a dummy transcription or log
    return {"status": "success", "message": "Model warmed up and ready"}

@app.post("/ai/analyze", response_model=AnalyzeResultResponse)
async def analyze(
    audio: UploadFile = File(...),
    targetWord: str = Form(...),
    childId: str = Form(...),
    exerciseItemId: str = Form(...)
):
    start_time = time.time()
    
    # Save UploadFile to a temporary local file
    temp_file_path = os.path.join(TEMP_DIR, f"{childId}_{exerciseItemId}_{int(time.time())}.wav")
    with open(temp_file_path, "wb") as buffer:
        shutil.copyfileobj(audio.file, buffer)
        
    try:
        # Run ASR + scoring
        result = analyzer.analyze_speech(temp_file_path, targetWord)
        
        # Cleanup file
        if os.path.exists(temp_file_path):
            os.remove(temp_file_path)
            
        elapsed_ms = int((time.time() - start_time) * 1000)
        
        return AnalyzeResultResponse(
            recognizedText=result["recognizedText"],
            targetWord=result["targetWord"],
            isCorrect=result["isCorrect"],
            score=result["score"],
            problemPhonemes=result["problemPhonemes"],
            phonemeScores=[PhonemeScoreDto(phoneme=ps["phoneme"], accuracy=ps["accuracy"]) for ps in result["phonemeScores"]],
            recommendation=result["recommendation"],
            processingTimeMs=elapsed_ms,
            modelName=result["modelName"]
        )
    except Exception as e:
        if os.path.exists(temp_file_path):
            os.remove(temp_file_path)
        raise e

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8001, reload=True)
