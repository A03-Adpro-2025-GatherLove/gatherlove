# Setup PostgreSQL and Application Properties

## Step 1: Install PostgreSQL

If you haven't already, install PostgreSQL on your system. You can download the installer from the official PostgreSQL website: https://www.postgresql.org/download/

## Step 2: Create a Database

Create a new database in PostgreSQL using the following command:
```sql
CREATE DATABASE gatherlove;
```

## Step 3: Configure application-dev.properties

Copy the `.env.example` file to a new file `.env`.


Edit the `.env` file to update the database connection properties:
```properties
DB_URL=jdbc:postgresql://localhost:5432/gatherlove
DB_USERNAME=postgres
DB_PASSWORD="#Yasinbest23"
```
Replace `your_username` and `your_password` with the values you have.

## Step 4: Verify the Configuration

Start the application by using `run.sh` or `run.bat` and verify that it can connect to the database successfully.

That's it! You should now have PostgreSQL and `.env` set up and configured for your application.