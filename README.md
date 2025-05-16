# GatherLove
- (2306152134) Allan Kwek
- (2306244942) Abhiseka Susanto
- (2306275954) Ida Made Revindra Dikta Mahendra
- (2306215154) Raden Ahmad Yasin Mahendra
- (2306152102) Janssen Benedict

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
DB_USERNAME=your_username
DB_PASSWORD=your_password
```
Replace `your_username` and `your_password` with the values you have.

## Step 4: Verify the Configuration

Start the application by using `run.sh` or `run.bat` and verify that it can connect to the database successfully.

That's it! You should now have PostgreSQL and `.env` set up and configured for your application.

# Software Architecture

## Current Architecture
Pada pengembangan GatherLove, kami memilih arsitektur monolitik karena beberapa alasan utama:

### 1. Kemudahan Pengembangan
Semua modul berada dalam satu basis kode. Ini memudahkan setup proyek, penulisan pengujian, serta proses perbaikan bug.

### 2. Skala yang Sesuai
Saat ini target pengguna GatherLove masih berada pada skala kecil hingga menengah. Dengan volume trafik dan kompleksitas yang terkelola, arsitektur monolitik sudah mencukupi tanpa menambah overhead komunikasi antar services.

### 3. Performa Internal Cepat
Karena seluruh komponen berjalan dalam satu proses, komunikasi antar-modul terjadi secara langsung (in-memory), mengurangi latensi dibandingkan bila melalui protokol jaringan.

Ini adalah visualisasi dari arsitektur proyek ini

### System Context Diagram
![System Context Diagram](static/img/DIAGRAM%20TUTORIAL%209%20B-System%20Context%20Diagram.jpg)

### Container Diagram
![Container Diagram](static/img/DIAGRAM%20TUTORIAL%209%20B-Container%20Diagram%20(Current).jpg)

### Deployment Diagram
![Deployment Diagram](static/img/DIAGRAM%20TUTORIAL%209%20B-Deployment%20Diagram.jpg)
