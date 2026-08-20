# CloudVandana Salesforce CRUD Assignment

React + Spring Boot application for the Associate Software Engineer assignment.

## What this project implements

- Salesforce OAuth 2.0 login through an External Client App.
- Central dropdown for Account, Opportunity, Lead, Contact and Case.
- 5–8 Salesforce fields per object.
- View, create, edit and delete records through Salesforce REST API.
- Infinite-scroll pagination: 20 records per request.
- Server-side OAuth token storage in the Spring HTTP session; the browser never receives the Salesforce access token.
- Ready for GitHub + Render deployment.

## Live deployment

- Frontend (Vercel): https://salesforce-crud-theta.vercel.app
- Backend (Render): https://cloudvandana-salesforce-backend-vcor.onrender.com
- Salesforce OAuth callback: https://cloudvandana-salesforce-backend-vcor.onrender.com/api/auth/callback

The assignment specifically requires CRUD against those five Salesforce standard objects, OAuth 2.0 through an External Client App, 20-record pagination, deployment and a Git repository. See the supplied assignment for the original requirements. 

## Architecture

React (Vite) → Spring Boot REST API → Salesforce OAuth 2.0 → Salesforce REST API

The React app only talks to Spring Boot. Spring Boot handles the Salesforce OAuth callback and REST API calls.

## 1. Create the Salesforce Developer Org

Go to https://developer.salesforce.com/signup and create the Developer Org requested in the assignment.

## 2. Create the Salesforce External Client App

In Salesforce:

1. Open **Setup**.
2. Search for **External Client App Manager** / **External Client Apps**.
3. Choose **New External Client App**.
4. Give it a name such as `CloudVandana Salesforce CRUD`.
5. Enable OAuth.
6. Local callback URL:
   `http://localhost:8080/api/auth/callback`
7. Add API access and refresh/offline access scopes. In the current Salesforce UI the exact scope labels can vary; choose the scopes that grant API access and refresh/offline access.
8. For a server-side Web Server OAuth flow, keep the client secret protected and copy the Consumer Key and Consumer Secret.
9. For the deployed app, add this callback URL as an additional callback URL:
   `https://cloudvandana-salesforce-backend-vcor.onrender.com/api/auth/callback`

Salesforce's current documentation recommends External Client Apps for new integrations. The OAuth callback URL must match the URL used by the application.

## 3. Configure Spring Boot

Create `backend/.env` only for local use, or export these environment variables:

```text
SF_CLIENT_ID=YOUR_CONSUMER_KEY
SF_CLIENT_SECRET=YOUR_CONSUMER_SECRET
SF_LOGIN_URL=https://login.salesforce.com
SF_REDIRECT_URI=http://localhost:8080/api/auth/callback
SF_API_VERSION=66.0
SF_SCOPES=api refresh_token offline_access
FRONTEND_URL=http://localhost:5173
COOKIE_SECURE=false
```

Do not commit the client secret.

## 4. Run backend in VS Code

Recommended development setup: use **VS Code for both backend and frontend**. You do not need STS.

Requirements:
- Java 17+
- Maven 3.9+

Open the repository root in VS Code, then open a terminal:

```powershell
cd backend
java -version
mvn -version
mvn spring-boot:run
```

If `mvn` is not recognized, install Apache Maven and add its `bin` directory to Windows PATH, then restart VS Code.

Backend: `http://localhost:8080`

## 5. Run frontend in VS Code

Requirements: Node.js 20+ recommended.

```bash
cd frontend
npm install
npm run dev
```

Frontend: `http://localhost:5173`

Click **Login with Salesforce** and complete Salesforce authorization.

## 6. Run both services from VS Code

Use two VS Code terminals:

**Terminal 1 — backend**
```powershell
cd backend
mvn spring-boot:run
```

**Terminal 2 — frontend**
```powershell
cd frontend
npm install
npm run dev
```

VS Code also includes `.vscode/tasks.json`. Use **Terminal → Run Task** and select `Spring Boot: Run Backend` or `React: Run Frontend`.

## 7. How CRUD works

After login:

1. Select Account / Opportunity / Lead / Contact / Case.
2. Spring Boot builds a SOQL query with a controlled field list.
3. Salesforce returns 20 records.
4. React renders them in a table.
5. Scrolling to the bottom requests the next page.
6. Create sends `POST /sobjects/{Object}`.
7. Update sends `PATCH /sobjects/{Object}/{Id}`.
8. Delete sends `DELETE /sobjects/{Object}/{Id}`.

## 8. GitHub

Create one repository containing both folders:

```text
cloudvandana-salesforce-crud/
├── backend/
├── frontend/
├── README.md
└── .gitignore
```

Then:

```bash
git init
git add .
git commit -m "CloudVandana Salesforce CRUD assignment"
git branch -M main
git remote add origin YOUR_GITHUB_REPOSITORY_URL
git push -u origin main
```

## 9. Deploy on Render

### Backend

Create a **Web Service** from the GitHub repository.

- Root Directory: `backend`
- Environment: Docker
- Dockerfile: `backend/Dockerfile`
- Port: `8080`
- Add environment variables:

```text
SF_CLIENT_ID=...
SF_CLIENT_SECRET=...
SF_LOGIN_URL=https://login.salesforce.com
SF_REDIRECT_URI=https://cloudvandana-salesforce-backend-vcor.onrender.com/api/auth/callback
SF_API_VERSION=66.0
SF_SCOPES=api refresh_token offline_access
FRONTEND_URL=https://salesforce-crud-theta.vercel.app
COOKIE_SECURE=true
```

### Frontend

Create a **Static Site** from the same repository.

- Root Directory: `frontend` (the live frontend is deployed on Vercel)
- Build Command: `npm install && npm run build`
- Publish Directory: `dist`
- Environment variable:

```text
VITE_API_BASE_URL=https://cloudvandana-salesforce-backend-vcor.onrender.com/api
```

The Vercel deployment uses the committed `frontend/.env.production` value. Confirm that the Vercel project root is `frontend`, then update the Salesforce External Client App callback URL to exactly match the backend callback URL above.

Render provides free Static Sites and free Web Services for testing, with free web services spinning down after inactivity. This is suitable for an assignment/demo but not production.

## 10. Final test checklist

- [ ] Salesforce Developer Org created
- [ ] External Client App created
- [ ] OAuth callback URL matches backend URL exactly
- [ ] Salesforce API + refresh/offline scopes configured
- [ ] Login works
- [ ] Account loads 20 records
- [ ] Opportunity loads 20 records
- [ ] Lead loads 20 records
- [ ] Contact loads 20 records
- [ ] Case loads 20 records
- [ ] Scroll loads another page
- [ ] View works
- [ ] Create works
- [ ] Update works
- [ ] Delete works
- [ ] GitHub repository is public/shared as requested
- [x] Frontend deployed at https://salesforce-crud-theta.vercel.app
- [x] Backend deployed at https://cloudvandana-salesforce-backend-vcor.onrender.com
- [ ] Updated resume is ready for submission

## Important security note

Never put the Salesforce client secret or access token in React/Vite environment variables. They belong on the Spring Boot server. The React application uses the server's session cookie and never receives the Salesforce access token.


## Current Salesforce OAuth note

Salesforce currently recommends **External Client Apps** for new integrations. In Salesforce Setup, use **External Client App Manager → New External Client App**, enable OAuth, configure the callback URL, and obtain the Consumer Key/Secret from the app settings. The callback URL configured in Salesforce must exactly match `SF_REDIRECT_URI`. See the official Salesforce documentation:
- https://developer.salesforce.com/docs/platform/hosted-mcp-servers/guide/create-external-client-app.html
- https://developer.salesforce.com/docs/platform/connect-rest-api/guide/intro_using_oauth.html

For deployment, the Render frontend is a Static Site and the Spring Boot backend is a Web Service. Keep `SF_CLIENT_SECRET` server-side and never commit it to GitHub.
