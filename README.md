# NutriScan

NutriScan adalah aplikasi Android cerdas yang dirancang untuk memindai, mengekstraksi, dan menganalisis informasi nilai gizi pada kemasan makanan. Aplikasi ini dikembangkan sebagai bagian dari penelitian publikasi ilmiah, memanfaatkan teknologi Optical Character Recognition (OCR) yang dioptimalkan dengan algoritma *Weighted Levenshtein Distance*, serta integrasi AI untuk memberikan rekomendasi konsumsi berdasarkan profil kesehatan pengguna.

## ✨ Fitur Utama

* **Pemindai Label Gizi (OCR):** Membaca teks pada label informasi nilai gizi menggunakan antarmuka CameraX dan Google ML Kit.
* **Koreksi Teks Cerdas:** Menggunakan *Confusion-Aware Weighted Levenshtein Distance* dan deteksi spasial (koordinat *bounding box*) untuk memperbaiki kesalahan pembacaan mesin OCR (misal: membedakan 'g' dengan '9', 'O' dengan '0').
* **Rekomendasi Berbasis AI:** Terintegrasi dengan **Gemini 2.5 Flash** untuk memberikan analisis dan rekomendasi gizi (AMAN, BATASI, atau HINDARI) secara presisi berdasarkan metrik profil pengguna (BMI, riwayat penyakit, dan tujuan kesehatan).
* **Pelacak Hidrasi:** Fitur *Water Tracker* harian yang interaktif di halaman Beranda.
* **Tips Kesehatan Harian:** Edukasi gizi yang diperbarui secara berkala.
* **Manajemen Riwayat & Profil:** Menyimpan data hasil pindaian, rekomendasi AI, dan pembaruan profil pengguna yang disinkronkan langsung dengan Firebase Firestore.

## 🛠️ Teknologi & Arsitektur

Aplikasi ini dibangun menggunakan *best practices* pengembangan aplikasi Android modern:

* **Bahasa:** Kotlin (Target Java 17)
* **UI Toolkit:** Jetpack Compose (Material 3)
* **Arsitektur:** MVVM (Model-View-ViewModel)
* **Dependency Injection:** Dagger Hilt
* **Asynchronous Programming:** Kotlin Coroutines & Flows
* **Navigasi:** Compose Navigation
* **Machine Learning:** Google ML Kit Text Recognition
* **Generative AI:** Google Generative AI SDK
* **Backend & Database:** Firebase Authentication, Cloud Firestore, Firebase Storage
* **Network & API:** Retrofit & OkHttp (termasuk integrasi Cloudinary API)
* **Image Loading:** Coil
* **Kamera:** AndroidX CameraX

## ⚙️ Prasyarat

* Android Studio (versi terbaru yang mendukung Jetpack Compose)
* Java Development Kit (JDK) 17
* Perangkat Android fisik atau Emulator dengan Minimum SDK 26 (Android 8.0) dan Target SDK 34.

## 🚀 Panduan Instalasi & Konfigurasi

1.  **Clone repositori ini:**
    ```bash
    git clone https://github.com/username/nutriscan-app.git
    ```
2.  **Konfigurasi Firebase:**
    * Buat proyek baru di [Firebase Console](https://console.firebase.google.com/).
    * Daftarkan aplikasi Android (gunakan package name `com.example.nutriscan`).
    * Unduh file konfigurasi `google-services.json`.
    * Letakkan file `google-services.json` tersebut ke dalam direktori `app/` di proyek Anda.
3.  **Konfigurasi API Keys (Gemini):**
    * Dapatkan API Key yang valid dari [Google AI Studio](https://aistudio.google.com/).
    * Buat file bernama `local.properties` di *root* folder proyek (jika belum otomatis terbuat).
    * Tambahkan baris berikut ke dalam file `local.properties`:
        ```properties
        GEMINI_API_KEY=masukkan_api_key_gemini_anda_di_sini
        ```
4.  **Build dan Run:**
    * Buka proyek di Android Studio.
    * Lakukan sinkronisasi Gradle (*Sync Project with Gradle Files*).
    * Jalankan aplikasi (`Shift + F10`) ke emulator atau perangkat fisik Anda.

## 🧠 Algoritma Inti: *Weighted Levenshtein Distance*

Untuk mengatasi tantangan akurasi ekstraksi OCR pada label nutrisi (yang seringkali dipengaruhi oleh lipatan kemasan, pantulan cahaya, atau ukuran font yang sangat kecil), aplikasi ini mengimplementasikan fungsi jarak karakter kustom. 

Algoritma ini menyesuaikan matriks biaya (*confusion cost*), memberikan penalti jarak yang lebih rendah untuk karakter yang terbukti sering tertukar oleh pembacaan OCR (seperti `5` dan `s`, atau `1` dan `l`). Ekstraksi teks ini kemudian digabungkan dengan validasi posisi spasial secara vertikal dan horizontal (memanfaatkan sumbu X dan Y dari *bounding box*) guna memetakan nama field nutrisi secara tepat dengan nilai numeriknya di sebelahnya.

## 👨‍💻 Pengembang

**Ari Fuzzaman** Program Studi Teknik Informatika  
Universitas Muhammadiyah Purwokerto
