# Environment variables example

Create a local `.env` file (do **not** commit it) and fill values as needed:

```bash
# App
APP_URL=http://localhost:8080
UPLOAD_DIR=./uploads
SPRING_PROFILES_ACTIVE=dev

# Database (use the docker compose db)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/jobnest_db
SPRING_DATASOURCE_USERNAME=jobnest
SPRING_DATASOURCE_PASSWORD=secret123

# Admin bootstrap (optional)
APP_ADMIN_AUTO_CREATE=false
APP_ADMIN_PASSWORD=

# Stripe (optional for local)
STRIPE_API_KEY=
STRIPE_PUBLISHABLE_KEY=
STRIPE_WEBHOOK_SECRET=
STRIPE_SUCCESS_URL=http://localhost:8080/payment/success
STRIPE_CANCEL_URL=http://localhost:8080/payment/cancel

# Mail (optional for local)
MAIL_USERNAME=
MAIL_PASSWORD=

# Remember-me keys (set strong values in prod)
REMEMBER_ME_KEY=dev-remember-me-key
REMEMBER_ME_ADMIN_KEY=dev-admin-remember-me-key
```

