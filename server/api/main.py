import os
import threading

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from pymongo import ASCENDING, MongoClient

MONGODB_URI = os.environ.get("MONGODB_URI", "")
DB_NAME = os.environ.get("REGISTRY_DB", "virasat")
COLLECTION_NAME = "aadhaar_registry"

SEED_RECORDS = [
    {"aadhaarNumber": "901234567890", "holderName": "Anjali Sharma", "dob": "14 Aug 1984",
     "address": "Kothrud, Pune, Maharashtra 411038", "mobileLast4": "4021", "active": True},
    {"aadhaarNumber": "784512903366", "holderName": "Rohan Sharma", "dob": "02 Nov 1991",
     "address": "Kothrud, Pune, Maharashtra 411038", "mobileLast4": "3366", "active": True},
    {"aadhaarNumber": "562291445521", "holderName": "Meera Sharma", "dob": "03 Mar 1958",
     "address": "Kothrud, Pune, Maharashtra 411038", "mobileLast4": "4521", "active": True},
    {"aadhaarNumber": "330871268814", "holderName": "Aarav Sharma", "dob": "11 Jun 2013",
     "address": "Kothrud, Pune, Maharashtra 411038", "mobileLast4": "8814", "active": True},
    {"aadhaarNumber": "999900001111", "holderName": "Old Record", "dob": "01 Jan 1950",
     "address": "Untraceable, India", "mobileLast4": "1111", "active": False},
]

RECORD_FIELDS = ("aadhaarNumber", "holderName", "dob", "address", "mobileLast4", "active")

_lock = threading.Lock()
_collection = None

app = FastAPI(title="Virasat Aadhaar Registry")


class AadhaarDoc(BaseModel):
    aadhaarNumber: str
    holderName: str
    dob: str = ""
    address: str = ""
    mobileLast4: str = "0000"
    active: bool = True


def _registry_collection():
    global _collection
    with _lock:
        if _collection is None:
            if not MONGODB_URI:
                raise HTTPException(status_code=503, detail="MONGODB_URI is not configured on the server")
            try:
                client = MongoClient(MONGODB_URI, maxPoolSize=1, serverSelectionTimeoutMS=5000)
                collection = client[DB_NAME][COLLECTION_NAME]
                collection.create_index([("aadhaarNumber", ASCENDING)], unique=True)
                _seed(collection)
            except HTTPException:
                raise
            except Exception as exc:
                raise HTTPException(status_code=503, detail=f"registry database unavailable: {exc}")
            _collection = collection
        return _collection


def _seed(collection):
    for record in SEED_RECORDS:
        collection.update_one(
            {"aadhaarNumber": record["aadhaarNumber"]},
            {"$setOnInsert": dict(record)},
            upsert=True,
        )


def _public(document):
    return {field: document.get(field) for field in RECORD_FIELDS}


@app.get("/health")
def health():
    try:
        count = _registry_collection().count_documents({})
    except HTTPException:
        raise
    except Exception as exc:
        raise HTTPException(status_code=503, detail=f"registry database unavailable: {exc}")
    return {"ok": True, "registry": count}


@app.get("/aadhaar")
def list_records():
    return [_public(document) for document in _registry_collection().find({})]


@app.get("/aadhaar/{number}")
def get_record(number: str):
    digits = "".join(ch for ch in number if ch.isdigit())
    if len(digits) != 12:
        raise HTTPException(status_code=404, detail="record not found")
    document = _registry_collection().find_one({"aadhaarNumber": digits})
    if document is None:
        raise HTTPException(status_code=404, detail="record not found")
    return _public(document)


@app.post("/aadhaar")
def upsert_record(record: AadhaarDoc):
    if len(record.aadhaarNumber) != 12 or not record.aadhaarNumber.isdigit():
        raise HTTPException(status_code=400, detail="aadhaarNumber must be exactly 12 digits")
    _registry_collection().replace_one(
        {"aadhaarNumber": record.aadhaarNumber},
        record.model_dump(),
        upsert=True,
    )
    return {"ok": True, "record": record.model_dump()}