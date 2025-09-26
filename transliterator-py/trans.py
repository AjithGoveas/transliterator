import os
from fastapi import FastAPI,Request
from fastapi.middleware.cors import CORSMiddleware
from aksharamukha import transliterate
from pydantic import BaseModel


class scriptSchema(BaseModel):
	source: str
	target: str
	text: str


app = FastAPI()

app.add_middleware(
	CORSMiddleware,
	allow_origins=["*"],
	allow_credentials=True,
	allow_methods=["*"],
	allow_headers=["*"],
	)


@app.get("/")
async def root():
	return {'message': 'Aksharamukha Transliteration API'}


@app.post("/transliterate")
async def translit(request: scriptSchema):

	if not request:
		return {'error': 'No JSON data provided'}, 400

	result = transliterate.process(request.source, request.target, request.text)

	return {'result': result}
