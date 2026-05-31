# Transliterator Backend (transliterator-py)

[![Python](https://img.shields.io/badge/Python-3.8%2B-blue.svg?style=flat-square&logo=python)](https://www.python.org)
[![FastAPI](https://img.shields.io/badge/FastAPI-0.100%2B-009688.svg?style=flat-square&logo=fastapi)](https://fastapi.tiangolo.com)
[![Render](https://img.shields.io/badge/Deployment-Render-brightgreen.svg?style=flat-square)](https://render.com)

A lightweight script transliteration microservice powered by the **Aksharamukha** transliteration engine. It provides high-performance API endpoints to convert Indian and other scripts (e.g., Devanagari ➔ Kannada, Roman ➔ Devanagari).

This backend contains two server configurations:
1.  **FastAPI (`trans.py`)**: The primary production-ready API service. Features Pydantic schemas, auto-generated OpenAPI documentation, and asynchronous handling.
2.  **Flask (`transliterate.py`)**: A legacy implementation kept for reference or simple deployment setups.

---

## 📁 Repository Structure

*   `trans.py` — The active entrypoint utilizing **FastAPI**. It configures CORS middleware to allow origins (e.g., mobile apps, web previewers) and implements the `/transliterate` endpoint.
*   `transliterate.py` — A **Flask** variant that falls back to port `5000` or reads the environment port.
*   `transliterator.ipynb` — A Jupyter notebook containing experiments, benchmarks, and prototype tests for Aksharamukha conversions.
*   `requirements.txt` — Lists base dependencies. *Note:* Make sure to install `fastapi` and `uvicorn` manually if you run the primary service, or use the instructions below.

---

## ⚡ Setup & Local Development

### Prerequisites
*   Python 3.8 or higher.
*   Virtual environment tool (`venv`).

### Installation

1.  **Clone or navigate to this directory:**
    ```bash
    cd transliterator-py
    ```

2.  **Set up and activate a virtual environment:**
    *   **Windows (PowerShell):**
        ```powershell
        python -m venv .venv
        .venv\Scripts\Activate.ps1
        ```
    *   **macOS / Linux:**
        ```bash
        python -m venv .venv
        source .venv/bin/activate
        ```

3.  **Install dependencies:**
    ```bash
    pip install -r requirements.txt
    # Install FastAPI and ASGI server (Uvicorn)
    pip install fastapi uvicorn
    ```

---

## 🚀 Running the Servers

### 1. FastAPI (Recommended)
FastAPI offers speed, automatic endpoint documentation, and clean validation.
```bash
uvicorn trans:app --host 0.0.0.0 --port 8000 --reload
```
*   **API Root:** `http://localhost:8000/`
*   **Swagger UI (Interactive Docs):** `http://localhost:8000/docs`
*   **ReDoc (Static Docs):** `http://localhost:8000/redoc`

### 2. Flask (Legacy)
```bash
python transliterate.py
```
Runs a standard development server at `http://localhost:5000/`.

---

## 🔌 API Documentation

### GET `/`
Returns a simple JSON welcome message indicating server health.
*   **Response:**
    ```json
    {
      "message": "Aksharamukha Transliteration API"
    }
    ```

### POST `/transliterate`
Performs script transliteration on the provided payload.

#### Request Headers
`Content-Type: application/json`

#### Request Body Schema
Using Pydantic validation:
| Field | Type | Description | Example |
| :--- | :--- | :--- | :--- |
| `source` | `string` | The source script name (case-sensitive as per Aksharamukha specs) | `"Devanagari"` |
| `target` | `string` | The target script name (case-sensitive as per Aksharamukha specs) | `"Kannada"` |
| `text` | `string` | The raw text to convert | `"नमस्ते"` |

#### Request Example (cURL)
```bash
curl -X POST http://localhost:8000/transliterate \
  -H "Content-Type: application/json" \
  -d '{"source": "Devanagari", "target": "Kannada", "text": "नमस्ते"}'
```

#### Response Example
```json
{
  "result": "ನಮಸ್ತೇ"
}
```

---

## ☁️ Deployment

The service is pre-configured for hosting on PaaS providers like **Render** or **Heroku**.

### Render Configuration
*   **Service Type:** Web Service
*   **Build Command:** `pip install -r requirements.txt && pip install fastapi uvicorn`
*   **Start Command (FastAPI):** `uvicorn trans:app --host 0.0.0.0 --port $PORT`
*   **Start Command (Flask):** `python transliterate.py` (The script binds to `$PORT` via `os.environ.get('PORT', 5000)`).
*   **Active Deployed URL:** `https://transliterator.onrender.com/transliterate`

---

## 🧪 Testing and Future Roadmap

- [ ] **Automated Script Integration Tests:** Write python unit tests checking conversion matches for high-priority language combinations.
- [ ] **Containerization:** Add a standard multi-stage `Dockerfile` to guarantee runtime environments.
- [ ] **Production-grade WSGI/ASGI Servers:** Configure `gunicorn` with `uvicorn` workers for production deployments.
- [ ] **Request Logging:** Add custom middleware to trace payload metadata (character count, source/target languages) without logging sensitive input text.

