# Virasat Aadhaar Registry (server)

A thin FastAPI service that backs the app's "any 12 digits accepted" Aadhaar mock with a real
server-hosted registry: **FastAPI on Vercel → MongoDB Atlas (free M0)**. Invented demo data only —
never put a real Aadhaar number in here.

```
Android app ──HTTPS──► FastAPI on Vercel ──► MongoDB Atlas M0
                        (4 endpoints,        (collection: aadhaar_registry,
                         seeds on first use,  unique index on aadhaarNumber)
                         MONGODB_URI env var)
```

## Document shape

Collection `aadhaar_registry`:

```json
{
  "aadhaarNumber": "901234567890",
  "holderName": "Anjali Sharma",
  "dob": "14 Aug 1984",
  "address": "Kothrud, Pune, Maharashtra 411038",
  "mobileLast4": "4021",
  "active": true
}
```

Seeded on first use (idempotent — `$setOnInsert`, so redeploying never overwrites records the app
has upserted): the four demo identities from `Fixtures.kt` (901234567890, 784512903366,
562291445521, 330871268814) plus one **inactive** record `999900001111` ("Old Record") so the
rejection path is demoable.

## Endpoints

| Method | Path                | Behaviour                                                        |
|--------|---------------------|------------------------------------------------------------------|
| GET    | `/health`           | `200 {"ok": true, "registry": <count>}` when Atlas is reachable; `503` otherwise |
| GET    | `/aadhaar/{number}` | `200` record, `404` unknown/malformed — never a 500 on unknown    |
| POST   | `/aadhaar`          | Upsert by `aadhaarNumber` (used when the planner registers a nominee) |
| GET    | `/aadhaar`          | List all records (debug)                                          |

CORS is not needed (native app). There is no auth by design: it is a demo registry with invented
data. `pymongo` runs with `maxPoolSize=1` because Vercel serverless instances should keep one
connection each.

## One-time setup: MongoDB Atlas (free M0)

1. Sign up / log in at <https://cloud.mongodb.com>.
2. **Create → Cluster** → pick the free **M0** tier, any region → Create.
3. **Database Access → Add New Database User** → password auth → username `sunnybndl_db_user`
   (or your own) → role *Read and write to any database* → Add User.
4. **Network Access → Add IP Address → Allow access from anywhere** (`0.0.0.0/0`). Vercel
   functions do not have a fixed outbound IP, so the sandbox must be open. Demo data only.
5. **Connect → Drivers → Python** → copy the connection string
   `mongodb+srv://<user>:<password>@cluster0.bah5ota.mongodb.net/?retryWrites=true&w=majority`.
   Treat it as a sandbox secret — it goes into the Vercel env var only, never into the repo.

## Deploy to Vercel

1. Vercel dashboard → **Add New… → Project → Import** this repository → set **Root Directory**
   to `server` (or deploy just this folder). The Python runtime picks up `api/main.py` +
   `api/requirements.txt` automatically.
2. Before the deployment can serve anything: **Project → Settings → Environment Variables →**
   add `MONGODB_URI` = the Atlas connection string from above (all environments). Redeploy after
   adding it — env vars only apply to new deployments.
3. Note the production URL — **use the plain `https://mokshasetu-registry.vercel.app` alias
   (i.e. `<project>.vercel.app`), NOT the `<project>-<teamslug>.vercel.app` deployment URL that
   Vercel echoes back when you deploy.** That longer URL is SSO/deployment-protection gated and
   answers every request with `302` → `vercel.com/sso-api` instead of JSON, which the Android
   app cannot use. It is the value of `ServerConfig.BASE_URL` in the app.

### Verify

```bash
curl -s https://<your-project>.vercel.app/health
# {"ok":true,"registry":5}

curl -s -w '\n%{http_code}\n' https://<your-project>.vercel.app/aadhaar/901234567890   # 200
curl -s -w '\n%{http_code}\n' https://<your-project>.vercel.app/aadhaar/111111111111   # 404
curl -s https://<your-project>.vercel.app/aadhaar                                      # list
curl -s -X POST https://<your-project>.vercel.app/aadhaar \
  -H 'Content-Type: application/json' \
  -d '{"aadhaarNumber":"450000000001","holderName":"Test Nominee"}'
```

## Offline fallback (optional, docker-compose)

Same code, runs on your machine — useful when the laptop and the demo device share a Wi-Fi with
no internet:

```bash
cd server
echo 'MONGODB_URI=mongodb+srv://...' > .env   # gitignored
docker compose up --build                      # serves http://localhost:8000
```

Then point the app at the fallback: in `data/ServerConfig.kt` set
`BASE_URL = "http://10.0.2.2:8000"` — `10.0.2.2` is the emulator's alias for the host machine's
loopback (from a physical device use the laptop's LAN IP instead). Because that is plain HTTP,
the app would also need `android:usesCleartextTraffic="true"` in the manifest while testing
against the fallback; the Vercel HTTPS URL needs no such change. Restore the HTTPS URL
afterwards.