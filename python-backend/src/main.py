from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from src.openai_client import router as ai_router
from src.config import ALLOWED_ORIGINS

app = FastAPI(
    title="NSPACE Python API",
    description="KI Python Backend for NSPACE Portfolio",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=ALLOWED_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
async def root():
    return {"message": "NSPACE Python API is running", "version": "1.0.0"}

@app.get("/health")
async def health():
    return {"status": "healthy", "service": "python-backend"}

app.include_router(ai_router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)