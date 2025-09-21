from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional
import uvicorn

app = FastAPI(
    title="NSPACE Python API",
    description="KI Python Backend for NSPACE Portfolio",
    version="1.0.0"
)

# CORS middleware for frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000", "http://vue-frontend"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Pydantic models
class KIProject(BaseModel):
    id: int
    name: str
    description: str
    status: str
    technology: str

class KIResponse(BaseModel):
    message: str
    projects: Optional[List[KIProject]] = None

# Sample data
ki_projects = [
    KIProject(id=1, name="ML Model 1", description="Machine Learning Model for Data Analysis", status="Active", technology="TensorFlow"),
    KIProject(id=2, name="AI Chatbot", description="Intelligent Chatbot with NLP", status="In Development", technology="OpenAI GPT"),
    KIProject(id=3, name="Data Analysis", description="Automated Data Processing Pipeline", status="Completed", technology="Pandas"),
]

@app.get("/")
async def root():
    return {"message": "NSPACE Python API is running", "version": "1.0.0"}

@app.get("/health")
async def health():
    return {"status": "healthy", "service": "python-backend"}

@app.get("/api/ki/projects", response_model=KIResponse)
async def get_ki_projects():
    return KIResponse(
        message="KI Projects retrieved successfully",
        projects=ki_projects
    )

@app.get("/api/ki/projects/{project_id}", response_model=KIProject)
async def get_ki_project(project_id: int):
    project = next((p for p in ki_projects if p.id == project_id), None)
    if not project:
        return {"error": "Project not found"}
    return project

@app.post("/api/ki/projects", response_model=KIProject)
async def create_ki_project(project: KIProject):
    project.id = len(ki_projects) + 1
    ki_projects.append(project)
    return project

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
