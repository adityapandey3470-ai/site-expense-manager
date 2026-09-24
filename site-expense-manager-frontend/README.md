# Site Expense Manager - Mobile App

React + Vite frontend for the Site Expense Manager backend, built for
wrapping into an Android app via Capacitor and publishing to Play Store.

## Run locally (browser)
```
npm install
npm run dev
```

## Point at your backend
Edit `.env`:
```
VITE_API_BASE_URL=http://localhost:8080
```
- On Android emulator, backend running on your machine is reachable at
  `http://10.0.2.2:8080`, not `localhost`.
- On a real device, use your machine's LAN IP or a deployed backend URL
  (e.g. Railway/Render), same as you did for the MFM project.

## Login
Uses the `/auth/login` endpoint from the Spring Boot backend. Create a
SUPERVISOR user first via `/auth/register`, or a DIRECTOR/OPERATIONS/ACCOUNTS
user via the protected `/admin/users` endpoint once you have a DIRECTOR
account.

## Screens included (MVP)
- Login
- Home (site balance + quick actions + recent requests)
- Requests list (with role-based Forward / Approve / Reject actions)
- New request form
- Mark attendance
- New travel expense
- Summary (ledger breakdown donut chart + full transaction list)
- More (profile + logout)

## Next step: Capacitor -> Android -> Play Store
```
npm install @capacitor/core @capacitor/android
npx cap init "Site Expense Manager" "com.aditya.siteexpensemanager" --web-dir=dist
npm run build
npx cap add android
npx cap sync
npx cap open android
```
Then build a signed AAB from Android Studio (Build > Generate Signed Bundle)
and upload it to Play Console, same flow you used for the MFM app.
